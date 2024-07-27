package pl.auroramc.auctions;

import static java.time.Duration.ofSeconds;
import static moe.rafal.juliet.datasource.hikari.HikariPooledDataSourceFactory.getHikariDataSource;
import static pl.auroramc.auctions.AuctionsConfig.AUCTIONS_CONFIG_FILE_NAME;
import static pl.auroramc.auctions.auction.AuctionFacadeFactory.getAuctionFacade;
import static pl.auroramc.auctions.audience.AudienceFacade.getAudienceFacade;
import static pl.auroramc.auctions.message.MessageFacade.getMessageFacade;
import static pl.auroramc.auctions.message.MessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.auctions.vault.VaultFacade.getVaultFacade;
import static pl.auroramc.auctions.vault.item.VaultItemFacade.getVaultItemFacade;
import static pl.auroramc.commons.bukkit.BukkitUtils.registerListeners;
import static pl.auroramc.commons.bukkit.BukkitUtils.resolveService;
import static pl.auroramc.commons.bukkit.scheduler.BukkitSchedulerFactory.getBukkitScheduler;
import static pl.auroramc.commons.scheduler.SchedulerPoll.ASYNC;
import static pl.auroramc.integrations.configs.juliet.JulietConfig.JULIET_CONFIG_FILE_NAME;
import static pl.auroramc.messages.message.compiler.BukkitMessageCompiler.getBukkitMessageCompiler;

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
import pl.auroramc.auctions.message.MessageSource;
import pl.auroramc.auctions.vault.VaultCommand;
import pl.auroramc.auctions.vault.VaultController;
import pl.auroramc.auctions.vault.VaultFacade;
import pl.auroramc.auctions.vault.item.VaultItemFacade;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.economy.economy.EconomyFacade;
import pl.auroramc.integrations.commands.BukkitCommandsBuilderProcessor;
import pl.auroramc.integrations.configs.ConfigFactory;
import pl.auroramc.integrations.configs.juliet.JulietConfig;
import pl.auroramc.integrations.configs.serdes.SerdesCommons;
import pl.auroramc.integrations.configs.serdes.juliet.SerdesJuliet;
import pl.auroramc.integrations.configs.serdes.message.SerdesMessages;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.registry.user.UserFacade;

public class AuctionsBukkitPlugin extends JavaPlugin {

  private Juliet juliet;
  private LiteCommands<CommandSender> commands;

  @Override
  public void onEnable() {
    final ConfigFactory configFactory =
        new ConfigFactory(getDataFolder().toPath(), YamlBukkitConfigurer::new);

    final AuctionsConfig auctionsConfig =
        configFactory.produceConfig(
            AuctionsConfig.class, AUCTIONS_CONFIG_FILE_NAME, new SerdesCommons());
    final AuctionFacade auctionFacade = getAuctionFacade();

    final Logger logger = getLogger();
    final Scheduler scheduler = getBukkitScheduler(this);

    final MessageSource messageSource =
        configFactory.produceConfig(
            MessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessages());
    final BukkitMessageCompiler messageCompiler = getBukkitMessageCompiler(scheduler);

    final JulietConfig julietConfig =
        configFactory.produceConfig(
            JulietConfig.class, JULIET_CONFIG_FILE_NAME, new SerdesJuliet());
    juliet =
        JulietBuilder.newBuilder().withDataSource(getHikariDataSource(julietConfig.hikari)).build();

    final AudienceFacade audienceFacade = getAudienceFacade(scheduler, juliet);
    final CurrencyFacade currencyFacade = resolveService(getServer(), CurrencyFacade.class);
    final Currency fundsCurrency = getFundsCurrency(currencyFacade, auctionsConfig.fundsCurrencyId);

    final EconomyFacade economyFacade = resolveService(getServer(), EconomyFacade.class);

    final UserFacade userFacade = resolveService(getServer(), UserFacade.class);
    final VaultFacade vaultFacade = getVaultFacade(scheduler, juliet);
    final VaultItemFacade vaultItemFacade = getVaultItemFacade(logger, scheduler, juliet);
    final VaultController vaultController =
        new VaultController(
            scheduler,
            messageSource.vault,
            messageCompiler,
            userFacade,
            vaultFacade,
            vaultItemFacade);

    final MessageFacade messageFacade = getMessageFacade(messageCompiler, audienceFacade);

    registerListeners(this, new DataValidationListener(userFacade, vaultFacade, audienceFacade));

    final AuctionController auctionController =
        new AuctionController(
            auctionsConfig,
            auctionFacade,
            messageSource.auction,
            messageFacade,
            economyFacade,
            fundsCurrency,
            vaultController,
            userFacade);

    scheduler.schedule(
        ASYNC,
        new AuctionCompletionTask(
            messageSource.auction, messageFacade, auctionFacade, auctionController),
        ofSeconds(1));
    scheduler.schedule(
        ASYNC, new AuctionSchedulingTask(auctionFacade, auctionController), ofSeconds(1));

    commands =
        LiteBukkitFactory.builder(getName(), this)
            .extension(new LiteAdventureExtension<>(), configurer -> configurer.miniMessage(true))
            .commands(
                LiteCommandsAnnotations.of(
                    new VaultCommand(
                        this, scheduler, messageSource.vault, messageCompiler, vaultController),
                    new AuctionCommand(
                        audienceFacade,
                        messageSource.auction,
                        messageCompiler,
                        economyFacade,
                        fundsCurrency,
                        auctionsConfig,
                        auctionFacade,
                        auctionController)))
            .selfProcessor(
                new BukkitCommandsBuilderProcessor(messageSource.command, messageCompiler))
            .build();
  }

  @Override
  public void onDisable() {
    juliet.close();
    commands.unregister();
  }

  private Currency getFundsCurrency(
      final CurrencyFacade currencyFacade, final long fundsCurrencyId) {
    return Optional.ofNullable(currencyFacade.getCurrencyById(fundsCurrencyId))
        .orElseThrow(
            () ->
                new AuctionsInstantiationException(
                    "Could not resolve currency with id %d.".formatted(fundsCurrencyId)));
  }
}
