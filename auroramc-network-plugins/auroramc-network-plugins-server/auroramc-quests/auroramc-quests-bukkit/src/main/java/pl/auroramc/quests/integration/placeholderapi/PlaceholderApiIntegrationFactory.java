package pl.auroramc.quests.integration.placeholderapi;

import org.bukkit.plugin.Plugin;
import pl.auroramc.commons.bukkit.integration.ExternalIntegration;
import pl.auroramc.quests.objective.ObjectiveController;
import pl.auroramc.quests.objective.progress.ObjectiveProgressController;
import pl.auroramc.quests.quest.QuestIndex;
import pl.auroramc.quests.quest.observer.QuestObserverFacade;
import pl.auroramc.registry.user.UserFacade;

public final class PlaceholderApiIntegrationFactory {

  private PlaceholderApiIntegrationFactory() {}

  public static ExternalIntegration getPlaceholderApiIntegration(
      final Plugin plugin,
      final UserFacade userFacade,
      final QuestIndex questIndex,
      final QuestObserverFacade questObserverFacade,
      final ObjectiveController objectiveController,
      final ObjectiveProgressController objectiveProgressController) {
    return new PlaceholderApiIntegration(
        plugin,
        plugin.getServer(),
        userFacade,
        questIndex,
        questObserverFacade,
        objectiveController,
        objectiveProgressController);
  }
}
