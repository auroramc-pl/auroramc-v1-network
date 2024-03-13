package pl.auroramc.quests.integration.placeholderapi;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import pl.auroramc.commons.integration.ExternalIntegrationDelegate;
import pl.auroramc.quests.objective.progress.ObjectiveProgressController;
import pl.auroramc.quests.quest.QuestIndex;
import pl.auroramc.quests.quest.observer.QuestObserverFacade;
import pl.auroramc.registry.user.UserFacade;

class PlaceholderApiIntegration extends ExternalIntegrationDelegate {

  private static final String PLACEHOLDER_API_PLUGIN_NAME = "PlaceholderAPI";
  private final Plugin plugin;
  private final Server server;
  private final UserFacade userFacade;
  private final QuestIndex questIndex;
  private final QuestObserverFacade questObserverFacade;
  private final ObjectiveProgressController objectiveProgressController;

  PlaceholderApiIntegration(
      final Plugin plugin,
      final Server server,
      final UserFacade userFacade,
      final QuestIndex questIndex,
      final QuestObserverFacade questObserverFacade,
      final ObjectiveProgressController objectiveProgressController
  ) {
    super(PLACEHOLDER_API_PLUGIN_NAME);
    this.plugin = plugin;
    this.server = server;
    this.userFacade = userFacade;
    this.questIndex = questIndex;
    this.questObserverFacade = questObserverFacade;
    this.objectiveProgressController = objectiveProgressController;
  }

  @Override
  public void configure() {
    new QuestsPlaceholderExpansion(
        plugin,
        server,
        userFacade,
        questIndex,
        questObserverFacade,
        objectiveProgressController
    ).register();
  }
}
