package pl.auroramc.auctions;

import static java.lang.String.join;
import static java.time.Duration.ofSeconds;
import static moe.rafal.juliet.datasource.HikariPooledDataSourceFactory.produceHikariDataSource;
import static pl.auroramc.auctions.AuctionsConfig.PLUGIN_CONFIG_FILE_NAME;
import static pl.auroramc.auctions.auction.AuctionFacadeFactory.getAuctionFacade;
import static pl.auroramc.auctions.message.MessageFacade.getMessageFacade;
import static pl.auroramc.auctions.message.MessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.auctions.message.MessageVariableKey.SCHEMATICS_VARIABLE_KEY;
import static pl.auroramc.auctions.message.viewer.MessageViewerFacadeFactory.getMessageViewerFacade;
import static pl.auroramc.auctions.vault.VaultFacadeFactory.getVaultFacade;
import static pl.auroramc.auctions.vault.item.VaultItemFacadeFactory.getVaultItemFacade;
import static pl.auroramc.commons.BukkitUtils.getTicksOf;
import static pl.auroramc.commons.BukkitUtils.registerListeners;
import static pl.auroramc.commons.BukkitUtils.resolveService;
import static pl.auroramc.commons.config.serdes.juliet.JulietConfig.JULIET_CONFIG_FILE_NAME;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.adventure.paper.LitePaperAdventureFactory;
import dev.rollczi.litecommands.bukkit.tools.BukkitOnlyPlayerContextual;
import dev.rollczi.litecommands.command.permission.RequiredPermissions;
import dev.rollczi.litecommands.schematic.Schematic;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.util.Optional;
import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;
import moe.rafal.juliet.JulietBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.auctions.auction.AuctionCommand;
import pl.auroramc.auctions.auction.AuctionCompletionScheduler;
import pl.auroramc.auctions.auction.AuctionController;
import pl.auroramc.auctions.auction.AuctionFacade;
import pl.auroramc.auctions.auction.AuctionListener;
import pl.auroramc.auctions.message.MessageFacade;
import pl.auroramc.auctions.message.MessageSource;
import pl.auroramc.auctions.message.viewer.MessageViewerFacade;
import pl.auroramc.auctions.vault.VaultCommand;
import pl.auroramc.auctions.vault.VaultController;
import pl.auroramc.auctions.vault.VaultFacade;
import pl.auroramc.auctions.vault.item.VaultItemFacade;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.serdes.SerdesCommons;
import pl.auroramc.commons.config.serdes.juliet.JulietConfig;
import pl.auroramc.commons.config.serdes.juliet.SerdesJuliet;
import pl.auroramc.commons.config.serdes.message.SerdesMessageSource;
import pl.auroramc.commons.event.publisher.EventPublisher;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.registry.user.UserFacade;

public class AuctionsBukkitPlugin extends JavaPlugin {

  private LiteCommands<CommandSender> commands;

  @Override
  public void onEnable() {
    final EventPublisher eventPublisher = new EventPublisher(this);

    final ConfigFactory configFactory = new ConfigFactory(
        getDataFolder().toPath(),
        YamlBukkitConfigurer::new
    );

    final MessageSource messageSource = configFactory.produceConfig(
        MessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessageSource()
    );

    final JulietConfig julietConfig = configFactory.produceConfig(
        JulietConfig.class, JULIET_CONFIG_FILE_NAME, new SerdesJuliet()
    );
    final Juliet juliet = JulietBuilder.newBuilder()
        .withDataSource(produceHikariDataSource(julietConfig.hikari))
        .build();

    final AuctionsConfig auctionsConfig = configFactory.produceConfig(
        AuctionsConfig.class, PLUGIN_CONFIG_FILE_NAME, new SerdesCommons()
    );
    final AuctionFacade auctionFacade = getAuctionFacade();
    final AuctionController auctionController = new AuctionController(
        auctionsConfig, auctionFacade, eventPublisher
    );

    final Logger logger = getLogger();

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
        this, logger, messageSource, userFacade, vaultFacade, vaultItemFacade
    );

    final MessageViewerFacade messageViewerFacade = getMessageViewerFacade(logger, juliet);
    final MessageFacade messageFacade = getMessageFacade(messageViewerFacade);

    registerListeners(this,
        new DataValidationListener(
            logger,
            userFacade,
            vaultFacade,
            messageViewerFacade
        ),
        new AuctionListener(
            logger,
            userFacade,
            messageSource,
            messageFacade,
            economyFacade,
            fundsCurrency,
            vaultController,
            auctionController
        )
    );

    getServer().getScheduler().runTaskTimer(this,
        new AuctionCompletionScheduler(
            messageSource, messageFacade, auctionController
        ),
        getTicksOf(ofSeconds(1)),
        getTicksOf(ofSeconds(1))
    );

    commands = LitePaperAdventureFactory.builder(getServer(), getName())
        .contextualBind(Player.class,
            new BukkitOnlyPlayerContextual<>(
                messageSource.executionFromConsoleIsUnsupported
            )
        )
        .commandInstance(new VaultCommand(this, messageSource, vaultController))
        .commandInstance(
            new AuctionCommand(
                logger,
                messageViewerFacade,
                messageSource,
                economyFacade,
                fundsCurrency,
                auctionController,
                eventPublisher
            )
        )
        .redirectResult(RequiredPermissions.class, MutableMessage.class,
            context -> messageSource.executionOfCommandIsNotPermitted
        )
        .redirectResult(Schematic.class, MutableMessage.class,
            context -> messageSource.availableSchematicsSuggestion
                .with(SCHEMATICS_VARIABLE_KEY, join("<newline>", context.getSchematics()))
        )
        .register();
  }

  @Override
  public void onDisable() {
    commands.getPlatform().unregisterAll();
  }
}
