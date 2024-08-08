package pl.auroramc.scoreboard;

import static java.time.Duration.ofSeconds;
import static pl.auroramc.commons.bukkit.BukkitUtils.registerListeners;
import static pl.auroramc.commons.bukkit.BukkitUtils.resolveService;
import static pl.auroramc.commons.scheduler.SchedulerPoll.ASYNC;
import static pl.auroramc.scoreboard.ScoreboardConfig.SCOREBOARD_CONFIG_FILE_NAME;
import static pl.auroramc.scoreboard.sidebar.SidebarFacade.getSidebarFacade;
import static pl.auroramc.scoreboard.sidebar.SidebarRenderer.getSidebarRenderer;

import java.util.Set;
import pl.auroramc.integrations.IntegrationsBukkitPlugin;
import pl.auroramc.messages.i18n.BukkitMessageFacade;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.messages.viewer.BukkitViewerFacade;
// import pl.auroramc.quests.objective.ObjectiveController;
// import pl.auroramc.quests.objective.progress.ObjectiveProgressController;
// import pl.auroramc.quests.quest.QuestIndex;
// import pl.auroramc.quests.quest.observer.QuestObserverFacade;
// import pl.auroramc.registry.user.UserFacade;
import pl.auroramc.scoreboard.message.ScoreboardMessageSource;
import pl.auroramc.scoreboard.quest.QuestListener;
// import pl.auroramc.scoreboard.quest.QuestSidebarComponent;
import pl.auroramc.scoreboard.sidebar.SidebarFacade;
import pl.auroramc.scoreboard.sidebar.SidebarRenderer;
import pl.auroramc.scoreboard.sidebar.SidebarRenderingTask;
import pl.auroramc.scoreboard.sidebar.component.SidebarComponent;

public class ScoreboardBukkitPlugin extends IntegrationsBukkitPlugin {

  private static final String SCOREBOARD_BUNDLE_NAME = "scoreboard_";
  private static final String QUESTS_PLUGIN_NAME = "auroramc-quests";

  @Override
  public void onStartup() {
    final BukkitViewerFacade viewerFacade = resolveService(getServer(), BukkitViewerFacade.class);
    final BukkitMessageFacade messageFacade =
        resolveService(getServer(), BukkitMessageFacade.class);

    final ScoreboardConfig scoreboardConfig =
        produceConfig(ScoreboardConfig.class, SCOREBOARD_CONFIG_FILE_NAME);
    final ScoreboardMessageSource messageSource =
        registerMessageSource(messageFacade, ScoreboardMessageSource.class, SCOREBOARD_BUNDLE_NAME);

    final SidebarFacade sidebarFacade = getSidebarFacade();
    final SidebarRenderer sidebarRenderer =
        getSidebarRenderer(
            scoreboardConfig,
            messageSource,
            messageFacade,
            getMessageCompiler(),
            viewerFacade,
            sidebarFacade,
            getAvailableComponents(
                messageSource, messageFacade, getMessageCompiler(), viewerFacade));

    registerListeners(this, new ScoreboardListener(sidebarFacade, sidebarRenderer));
    if (hasQuestSupport()) {
      registerListeners(this, new QuestListener(sidebarRenderer));
    }

    if (scoreboardConfig.updatePeriodically) {
      getScheduler().schedule(ASYNC, new SidebarRenderingTask(sidebarRenderer), ofSeconds(5));
    }
  }

  private boolean hasQuestSupport() {
    return getServer().getPluginManager().isPluginEnabled(QUESTS_PLUGIN_NAME);
  }

  private Set<SidebarComponent<?>> getAvailableComponents(
      final ScoreboardMessageSource messageSource,
      final BukkitMessageFacade messageFacade,
      final BukkitMessageCompiler messageCompiler,
      final BukkitViewerFacade viewerFacade) {
    return Set.of();
    //    return hasQuestSupport()
    //        ? Set.of(getQuestComponent(messageSource, messageFacade, messageCompiler,
    // viewerFacade))
    //        : Set.of();
  }

  //  private SidebarComponent<?> getQuestComponent(
  //      final ScoreboardMessageSource messageSource,
  //      final BukkitMessageFacade messageFacade,
  //      final BukkitMessageCompiler messageCompiler,
  //      final BukkitViewerFacade viewerFacade) {
  //    final UserFacade userFacade = resolveService(getServer(), UserFacade.class);
  //    final QuestIndex questIndex = resolveService(getServer(), QuestIndex.class);
  //    final QuestObserverFacade questObserverFacade =
  //        resolveService(getServer(), QuestObserverFacade.class);
  //    final ObjectiveController objectiveController =
  //        resolveService(getServer(), ObjectiveController.class);
  //    final ObjectiveProgressController objectiveProgressController =
  //        resolveService(getServer(), ObjectiveProgressController.class);
  //    return new QuestSidebarComponent(
  //        messageSource,
  //        messageFacade,
  //        messageCompiler,
  //        viewerFacade,
  //        userFacade,
  //        questIndex,
  //        questObserverFacade,
  //        objectiveController,
  //        objectiveProgressController);
  //  }
}
