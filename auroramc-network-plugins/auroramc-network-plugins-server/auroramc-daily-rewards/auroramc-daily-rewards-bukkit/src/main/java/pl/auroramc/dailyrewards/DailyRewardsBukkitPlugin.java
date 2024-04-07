package pl.auroramc.dailyrewards;

import static java.time.Duration.ofSeconds;
import static moe.rafal.juliet.datasource.hikari.HikariPooledDataSourceFactory.getHikariDataSource;
import static pl.auroramc.commons.bukkit.BukkitUtils.registerListeners;
import static pl.auroramc.commons.bukkit.BukkitUtils.resolveService;
import static pl.auroramc.commons.bukkit.scheduler.BukkitSchedulerFactory.getBukkitScheduler;
import static pl.auroramc.commons.config.juliet.JulietConfig.JULIET_CONFIG_FILE_NAME;
import static pl.auroramc.commons.scheduler.SchedulerPoll.ASYNC;
import static pl.auroramc.dailyrewards.DailyRewardsConfig.DAILY_REWARDS_CONFIG_FILE_NAME;
import static pl.auroramc.dailyrewards.message.MessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.dailyrewards.visit.VisitFacadeFactory.getVisitFacade;
import static pl.auroramc.messages.message.compiler.BukkitMessageCompiler.getBukkitMessageCompiler;
import static pl.auroramc.nametag.NametagFacade.getNametagFacade;
import static pl.auroramc.nametag.context.NametagContextFacade.getNametagContextFacade;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.adventure.LiteAdventureExtension;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;
import moe.rafal.juliet.JulietBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.commons.bukkit.integration.litecommands.BukkitCommandsBuilderProcessor;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.juliet.JulietConfig;
import pl.auroramc.commons.config.serdes.SerdesCommons;
import pl.auroramc.commons.config.serdes.juliet.SerdesJuliet;
import pl.auroramc.commons.config.serdes.message.SerdesMessages;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.dailyrewards.message.MessageSource;
import pl.auroramc.dailyrewards.nametag.NametagListener;
import pl.auroramc.dailyrewards.nametag.NametagUpdateScheduler;
import pl.auroramc.dailyrewards.visit.VisitCommand;
import pl.auroramc.dailyrewards.visit.VisitController;
import pl.auroramc.dailyrewards.visit.VisitFacade;
import pl.auroramc.dailyrewards.visit.VisitListener;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.nametag.NametagFacade;
import pl.auroramc.registry.user.UserFacade;

public class DailyRewardsBukkitPlugin extends JavaPlugin {

  private Juliet juliet;
  private LiteCommands<CommandSender> commands;

  @Override
  public void onEnable() {
    final ConfigFactory configFactory =
        new ConfigFactory(getDataFolder().toPath(), YamlBukkitConfigurer::new);
    final DailyRewardsConfig dailyRewardsConfig =
        configFactory.produceConfig(
            DailyRewardsConfig.class, DAILY_REWARDS_CONFIG_FILE_NAME, new SerdesCommons());

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
    final Logger logger = getLogger();

    final NametagFacade nametagFacade = getNametagFacade(getNametagContextFacade());

    final UserFacade userFacade = resolveService(getServer(), UserFacade.class);
    final VisitFacade visitFacade = getVisitFacade(scheduler, juliet);
    final VisitController visitController = new VisitController();

    registerListeners(
        this,
        new NametagListener(nametagFacade),
        new VisitListener(logger, userFacade, visitFacade, visitController, dailyRewardsConfig));

    scheduler.schedule(
        ASYNC,
        new NametagUpdateScheduler(messageSource, messageCompiler, nametagFacade, visitController),
        ofSeconds(1));

    commands =
        LiteBukkitFactory.builder(getName(), this)
            .extension(new LiteAdventureExtension<>(), configurer -> configurer.miniMessage(true))
            .commands(
                LiteCommandsAnnotations.of(
                    new VisitCommand(messageSource, messageCompiler, userFacade, visitFacade)))
            .selfProcessor(
                new BukkitCommandsBuilderProcessor(messageSource.command, messageCompiler))
            .build();
  }

  @Override
  public void onDisable() {
    juliet.close();
    commands.unregister();
  }
}
