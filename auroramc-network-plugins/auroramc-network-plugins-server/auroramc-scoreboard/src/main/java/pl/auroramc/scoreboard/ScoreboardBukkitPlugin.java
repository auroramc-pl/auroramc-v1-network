package pl.auroramc.scoreboard;

import static java.time.Duration.ZERO;
import static java.time.Duration.ofSeconds;
import static pl.auroramc.commons.BukkitUtils.getTicksOf;
import static pl.auroramc.commons.BukkitUtils.registerListeners;
import static pl.auroramc.commons.BukkitUtils.resolveService;
import static pl.auroramc.scoreboard.ScoreboardConfig.SCOREBOARD_CONFIG_FILE_NAME;
import static pl.auroramc.scoreboard.message.MutableMessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.scoreboard.sidebar.SidebarFacade.getSidebarFacade;
import static pl.auroramc.scoreboard.sidebar.SidebarRenderer.getSidebarRenderer;

import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.util.Set;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.serdes.message.SerdesMessageSource;
import pl.auroramc.quests.objective.progress.ObjectiveProgressController;
import pl.auroramc.quests.quest.QuestIndex;
import pl.auroramc.quests.quest.observer.QuestObserverFacade;
import pl.auroramc.registry.user.UserFacade;
import pl.auroramc.scoreboard.message.MutableMessageSource;
import pl.auroramc.scoreboard.quest.QuestListener;
import pl.auroramc.scoreboard.quest.QuestSidebarComponent;
import pl.auroramc.scoreboard.sidebar.SidebarFacade;
import pl.auroramc.scoreboard.sidebar.SidebarRenderer;
import pl.auroramc.scoreboard.sidebar.SidebarRenderingTask;
import pl.auroramc.scoreboard.sidebar.component.SidebarComponentKyori;

public class ScoreboardBukkitPlugin extends JavaPlugin {

  private static final String QUESTS_PLUGIN_NAME = "auroramc-quests";

  @Override
  public void onEnable() {
    final ConfigFactory configFactory = new ConfigFactory(getDataFolder().toPath(), YamlBukkitConfigurer::new);

    final ScoreboardConfig scoreboardConfig = configFactory.produceConfig(
        ScoreboardConfig.class, SCOREBOARD_CONFIG_FILE_NAME
    );
    final MutableMessageSource messageSource = configFactory.produceConfig(
        MutableMessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessageSource()
    );

    final Logger logger = getLogger();

    final SidebarFacade sidebarFacade = getSidebarFacade();
    final SidebarRenderer sidebarRenderer = getSidebarRenderer(
        messageSource, sidebarFacade, getAvailableComponents(logger, messageSource)
    );

    registerListeners(this, new ScoreboardListener(sidebarFacade, sidebarRenderer));
    if (hasQuestSupport()) {
      registerListeners(this, new QuestListener(sidebarRenderer));
    }

    if (scoreboardConfig.updatePeriodically) {
      getServer().getScheduler().runTaskTimerAsynchronously(this,
          new SidebarRenderingTask(sidebarRenderer),
          getTicksOf(ZERO),
          getTicksOf(ofSeconds(2))
      );
    }
  }

  private Set<SidebarComponentKyori<?>> getAvailableComponents(final Logger logger, final MutableMessageSource messageSource) {
    return hasQuestSupport()
        ? Set.of(getQuestComponent(logger, messageSource))
        : Set.of();
  }

  private boolean hasQuestSupport() {
    return getServer().getPluginManager().isPluginEnabled(QUESTS_PLUGIN_NAME);
  }

  private SidebarComponentKyori<?> getQuestComponent(final Logger logger, final MutableMessageSource messageSource) {
    final UserFacade userFacade = resolveService(getServer(), UserFacade.class);
    final QuestIndex questIndex = resolveService(getServer(), QuestIndex.class);
    final QuestObserverFacade questObserverFacade = resolveService(getServer(), QuestObserverFacade.class);
    final ObjectiveProgressController objectiveProgressController = resolveService(getServer(), ObjectiveProgressController.class);
    return new QuestSidebarComponent(
        logger, messageSource, userFacade, questIndex, questObserverFacade, objectiveProgressController
    );
  }
}
