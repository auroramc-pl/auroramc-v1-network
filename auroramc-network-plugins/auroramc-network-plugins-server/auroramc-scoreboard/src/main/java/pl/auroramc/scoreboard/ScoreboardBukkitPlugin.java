package pl.auroramc.scoreboard;

import static java.time.Duration.ofSeconds;
import static pl.auroramc.commons.bukkit.BukkitUtils.registerListeners;
import static pl.auroramc.commons.bukkit.BukkitUtils.resolveService;
import static pl.auroramc.commons.bukkit.scheduler.BukkitSchedulerFactory.getBukkitScheduler;
import static pl.auroramc.commons.scheduler.SchedulerPoll.ASYNC;
import static pl.auroramc.messages.message.compiler.BukkitMessageCompiler.getBukkitMessageCompiler;
import static pl.auroramc.scoreboard.ScoreboardConfig.SCOREBOARD_CONFIG_FILE_NAME;
import static pl.auroramc.scoreboard.message.MessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.scoreboard.sidebar.SidebarFacade.getSidebarFacade;
import static pl.auroramc.scoreboard.sidebar.SidebarRenderer.getSidebarRenderer;

import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.util.Set;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.integrations.configs.ConfigFactory;
import pl.auroramc.integrations.configs.serdes.message.SerdesMessages;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.quests.objective.ObjectiveController;
import pl.auroramc.quests.objective.progress.ObjectiveProgressController;
import pl.auroramc.quests.quest.QuestIndex;
import pl.auroramc.quests.quest.observer.QuestObserverFacade;
import pl.auroramc.registry.user.UserFacade;
import pl.auroramc.scoreboard.message.MessageSource;
import pl.auroramc.scoreboard.quest.QuestListener;
import pl.auroramc.scoreboard.quest.QuestSidebarComponent;
import pl.auroramc.scoreboard.sidebar.SidebarFacade;
import pl.auroramc.scoreboard.sidebar.SidebarRenderer;
import pl.auroramc.scoreboard.sidebar.SidebarRenderingTask;
import pl.auroramc.scoreboard.sidebar.component.SidebarComponent;

public class ScoreboardBukkitPlugin extends JavaPlugin {

  private static final String QUESTS_PLUGIN_NAME = "auroramc-quests";

  @Override
  public void onEnable() {
    final ConfigFactory configFactory =
        new ConfigFactory(getDataFolder().toPath(), YamlBukkitConfigurer::new);
    final ScoreboardConfig scoreboardConfig =
        configFactory.produceConfig(ScoreboardConfig.class, SCOREBOARD_CONFIG_FILE_NAME);

    final Scheduler scheduler = getBukkitScheduler(this);

    final MessageSource messageSource =
        configFactory.produceConfig(
            MessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessages());
    final BukkitMessageCompiler messageCompiler = getBukkitMessageCompiler(scheduler);

    final SidebarFacade sidebarFacade = getSidebarFacade();
    final SidebarRenderer sidebarRenderer =
        getSidebarRenderer(
            messageSource,
            messageCompiler,
            sidebarFacade,
            getAvailableComponents(messageSource, messageCompiler));

    registerListeners(this, new ScoreboardListener(sidebarFacade, sidebarRenderer));
    if (hasQuestSupport()) {
      registerListeners(this, new QuestListener(sidebarRenderer));
    }

    if (scoreboardConfig.updatePeriodically) {
      scheduler.schedule(ASYNC, new SidebarRenderingTask(sidebarRenderer), ofSeconds(5));
    }
  }

  private boolean hasQuestSupport() {
    return getServer().getPluginManager().isPluginEnabled(QUESTS_PLUGIN_NAME);
  }

  private Set<SidebarComponent<?>> getAvailableComponents(
      final MessageSource messageSource, final BukkitMessageCompiler messageCompiler) {
    return hasQuestSupport() ? Set.of(getQuestComponent(messageSource, messageCompiler)) : Set.of();
  }

  private SidebarComponent<?> getQuestComponent(
      final MessageSource messageSource, final BukkitMessageCompiler messageCompiler) {
    final UserFacade userFacade = resolveService(getServer(), UserFacade.class);
    final QuestIndex questIndex = resolveService(getServer(), QuestIndex.class);
    final QuestObserverFacade questObserverFacade =
        resolveService(getServer(), QuestObserverFacade.class);
    final ObjectiveController objectiveController =
        resolveService(getServer(), ObjectiveController.class);
    final ObjectiveProgressController objectiveProgressController =
        resolveService(getServer(), ObjectiveProgressController.class);
    return new QuestSidebarComponent(
        messageSource.quest,
        messageCompiler,
        userFacade,
        questIndex,
        questObserverFacade,
        objectiveController,
        objectiveProgressController);
  }
}
