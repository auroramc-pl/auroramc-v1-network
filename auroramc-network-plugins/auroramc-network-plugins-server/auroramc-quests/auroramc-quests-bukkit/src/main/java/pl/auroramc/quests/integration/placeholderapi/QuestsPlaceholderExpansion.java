package pl.auroramc.quests.integration.placeholderapi;

import static java.lang.String.join;
import static pl.auroramc.quests.objective.ObjectiveUtils.getQuestObjective;

import java.util.Map;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.quests.objective.Objective;
import pl.auroramc.quests.objective.progress.ObjectiveProgress;
import pl.auroramc.quests.objective.progress.ObjectiveProgressController;
import pl.auroramc.quests.quest.Quest;
import pl.auroramc.quests.quest.QuestIndex;
import pl.auroramc.quests.quest.observer.QuestObserver;
import pl.auroramc.quests.quest.observer.QuestObserverFacade;
import pl.auroramc.registry.user.UserFacade;

class QuestsPlaceholderExpansion extends PlaceholderExpansion {

  public static final String PLACEHOLDER_API_PLUGIN_NAME = "PlaceholderAPI";
  private final Plugin plugin;
  private final Server server;
  private final UserFacade userFacade;
  private final QuestIndex questIndex;
  private final QuestObserverFacade questObserverFacade;
  private final ObjectiveProgressController objectiveProgressController;

  public QuestsPlaceholderExpansion(
      final Plugin plugin,
      final Server server,
      final UserFacade userFacade,
      final QuestIndex questIndex,
      final QuestObserverFacade questObserverFacade,
      final ObjectiveProgressController objectiveProgressController
  ) {
    this.plugin = plugin;
    this.server = server;
    this.userFacade = userFacade;
    this.questIndex = questIndex;
    this.questObserverFacade = questObserverFacade;
    this.objectiveProgressController = objectiveProgressController;
  }

  @Override
  public @Nullable String onPlaceholderRequest(final Player player, @NotNull final String params) {
    if (player == null) {
      return null;
    }

    final QuestObserver questObserver = questObserverFacade.resolveQuestObserverByUniqueId(player.getUniqueId()).join();
    if (questObserver.getQuestId() == null) {
      return null;
    }

    final Quest quest = questIndex.resolveQuest(questObserver.getQuestId());
    if (quest == null) {
      return null;
    }

    return switch (params) {
      case "observed_quest":
        yield quest.getKey().getName();
      case "observed_quest_objectives":
        yield userFacade.getUserByUniqueId(player.getUniqueId())
            .thenApply(user -> objectiveProgressController.getUncompletedObjectives(user, quest))
            .thenApply(this::aggregateQuestObjectives)
            .join();
      default:
        yield null;
    };
  }

  private String aggregateQuestObjectives(
      final Map<Objective<?>, ObjectiveProgress> objectivesToObjectiveProgresses
  ) {
    return objectivesToObjectiveProgresses.entrySet().stream()
        .map(objectiveToObjectiveProgress ->
            getQuestObjective(
                objectiveToObjectiveProgress.getKey(),
                objectiveToObjectiveProgress.getValue()
            )
        )
        .collect(MutableMessage.collector())
        .getTemplate();
  }

  @Override
  public boolean canRegister() {
    return server.getPluginManager().isPluginEnabled(PLACEHOLDER_API_PLUGIN_NAME);
  }

  @Override
  public @NotNull String getIdentifier() {
    return plugin.getPluginMeta().getName();
  }

  @Override
  public @NotNull String getAuthor() {
    return plugin.getPluginMeta().getVersion();
  }

  @Override
  public @NotNull String getVersion() {
    return join(", ", plugin.getPluginMeta().getAuthors());
  }
}
