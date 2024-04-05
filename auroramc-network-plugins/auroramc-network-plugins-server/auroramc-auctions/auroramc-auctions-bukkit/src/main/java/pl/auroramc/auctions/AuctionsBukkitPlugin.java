package pl.auroramc.auctions;

import static dev.rollczi.litecommands.bukkit.LiteBukkitMessages.PLAYER_ONLY;
import static dev.rollczi.litecommands.message.LiteMessages.INVALID_USAGE;
import static dev.rollczi.litecommands.message.LiteMessages.MISSING_PERMISSIONS;
import static java.time.Duration.ofSeconds;
import static moe.rafal.juliet.datasource.HikariPooledDataSourceFactory.produceHikariDataSource;
import static pl.auroramc.auctions.AuctionsConfig.AUCTIONS_CONFIG_FILE_NAME;
import static pl.auroramc.auctions.auction.AuctionFacadeFactory.getAuctionFacade;
import static pl.auroramc.auctions.audience.AudienceFacade.getAudienceFacade;
import static pl.auroramc.auctions.message.MessageFacade.getMessageFacade;
import static pl.auroramc.auctions.message.MutableMessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.auctions.message.MutableMessageVariableKey.SCHEMATICS_PATH;
import static pl.auroramc.auctions.vault.VaultFacade.getVaultFacade;
import static pl.auroramc.auctions.vault.item.VaultItemFacade.getVaultItemFacade;
import static pl.auroramc.commons.BukkitUtils.getTicksOf;
import static pl.auroramc.commons.BukkitUtils.registerListeners;
import static pl.auroramc.commons.BukkitUtils.resolveService;
import static pl.auroramc.commons.config.serdes.juliet.JulietConfig.JULIET_CONFIG_FILE_NAME;
import static pl.auroramc.commons.message.MutableMessage.LINE_SEPARATOR;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.adventure.LiteAdventureExtension;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.util.Optional;
import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;
import moe.rafal.juliet.JulietBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.auctions.auction.AuctionCommand;
import pl.auroramc.auctions.auction.AuctionCompletionTask;
import pl.auroramc.auctions.auction.AuctionController;
import pl.auroramc.auctions.auction.AuctionFacade;
import pl.auroramc.auctions.auction.AuctionSchedulingTask;
import pl.auroramc.auctions.audience.AudienceFacade;
import pl.auroramc.auctions.message.MessageFacade;
import pl.auroramc.auctions.message.MutableMessageSource;
import pl.auroramc.auctions.vault.VaultCommand;
import pl.auroramc.auctions.vault.VaultController;
import pl.auroramc.auctions.vault.VaultFacade;
import pl.auroramc.auctions.vault.item.VaultItemFacade;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.serdes.SerdesCommons;
import pl.auroramc.commons.config.serdes.juliet.JulietConfig;
import pl.auroramc.commons.config.serdes.juliet.SerdesJuliet;
import pl.auroramc.commons.config.serdes.message.SerdesMessageSource;
import pl.auroramc.commons.integration.litecommands.DeliverableMutableMessageResultHandler;
import pl.auroramc.commons.message.delivery.DeliverableMutableMessage;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.registry.user.UserFacade;

public class AuctionsBukkitPlugin extends JavaPlugin {

  private LiteCommands<CommandSender> commands;

  @Override
  public void onEnable() {
    final ConfigFactory configFactory = new ConfigFactory(
        getDataFolder().toPath(),
        YamlBukkitConfigurer::new
    );

    final MutableMessageSource messageSource = configFactory.produceConfig(
        MutableMessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessageSource()
    );

    final JulietConfig julietConfig = configFactory.produceConfig(
        JulietConfig.class, JULIET_CONFIG_FILE_NAME, new SerdesJuliet()
    );
    final Juliet juliet = JulietBuilder.newBuilder()
        .withDataSource(produceHikariDataSource(julietConfig.hikari))
        .build();

    final AuctionsConfig auctionsConfig = configFactory.produceConfig(
        AuctionsConfig.class, AUCTIONS_CONFIG_FILE_NAME, new SerdesCommons()
    );
    final AuctionFacade auctionFacade = getAuctionFacade();

    final Logger logger = getLogger();

    final AudienceFacade audienceFacade = getAudienceFacade(logger, juliet);
    final CurrencyFacade currencyFacade = resolveService(getServer(), CurrencyFacade.class);
    final Currency fundsCurrency =
        Optional.ofNullable(currencyFacade.getCurrencyById(auctionsConfig.fundsCurrencyId))
            .orElseThrow(() ->
                new AuctionsInstantiationException(
                    "Could not resolve currency with id %d."
                        .formatted(
                            auctionsConfig.fundsCurrencyId
                        )
                )
            );

    final EconomyFacade economyFacade = resolveService(getServer(), EconomyFacade.class);

    final UserFacade userFacade = resolveService(getServer(), UserFacade.class);
    final VaultFacade vaultFacade = getVaultFacade(logger, juliet);
    final VaultItemFacade vaultItemFacade = getVaultItemFacade(logger, juliet);
    final VaultController vaultController = new VaultController(
        this,
        logger,
        messageSource,
        userFacade,
        vaultFacade,
        vaultItemFacade
    );

    final MessageFacade messageFacade = getMessageFacade(audienceFacade);

    registerListeners(this,
        new DataValidationListener(
            logger,
            userFacade,
            vaultFacade,
            audienceFacade
        )
    );

    final AuctionController auctionController = new AuctionController(
        logger,
        auctionsConfig,
        auctionFacade,
        messageSource,
        messageFacade,
        economyFacade,
        fundsCurrency,
        vaultController,
        userFacade
    );

    getServer().getScheduler().runTaskTimer(this,
        new AuctionCompletionTask(
            messageSource,
            messageFacade,
            auctionFacade,
            auctionController
        ),
        getTicksOf(ofSeconds(1)),
        getTicksOf(ofSeconds(1))
    );
    getServer().getScheduler().runTaskTimer(this,
        new AuctionSchedulingTask(
            auctionFacade,
            auctionController
        ),
        getTicksOf(ofSeconds(5)),
        getTicksOf(ofSeconds(5))
    );

    commands = LiteBukkitFactory.builder(getName(), this)
        .extension(new LiteAdventureExtension<>(),
            configurer -> configurer.miniMessage(true)
        )
        .message(INVALID_USAGE,
            context -> messageSource.availableSchematicsSuggestion
                .with(SCHEMATICS_PATH, context.getSchematic().join(LINE_SEPARATOR))
        )
        .message(MISSING_PERMISSIONS, messageSource.executionOfCommandIsNotPermitted)
        .message(PLAYER_ONLY, messageSource.executionFromConsoleIsUnsupported)
        .commands(
            LiteCommandsAnnotations.of(
                new VaultCommand(
                    this, messageSource, vaultController
                ),
                new AuctionCommand(
                    logger,
                    audienceFacade,
                    messageSource,
                    economyFacade,
                    fundsCurrency,
                    auctionsConfig,
                    auctionFacade,
                    auctionController
                )
            )
        )
        .result(DeliverableMutableMessage.class, new DeliverableMutableMessageResultHandler<>())
        .build();
  }

  @Override
  public void onDisable() {
    commands.unregister();
  }
}
