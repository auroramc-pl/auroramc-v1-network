package pl.auroramc.scoreboard.quest;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.Map.Entry.comparingByKey;
import static pl.auroramc.messages.message.compiler.CompiledMessage.empty;
import static pl.auroramc.scoreboard.quest.QuestMessageSourcePaths.QUEST_PATH;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import pl.auroramc.commons.concurrent.CompletableFutureUtils;
import pl.auroramc.messages.message.MutableMessage;
import pl.auroramc.messages.message.MutableMessageCollector;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.messages.message.compiler.CompiledMessage;
import pl.auroramc.quests.objective.Objective;
import pl.auroramc.quests.objective.ObjectiveController;
import pl.auroramc.quests.objective.progress.ObjectiveProgress;
import pl.auroramc.quests.objective.progress.ObjectiveProgressController;
import pl.auroramc.quests.quest.Quest;
import pl.auroramc.quests.quest.QuestIndex;
import pl.auroramc.quests.quest.observer.QuestObserver;
import pl.auroramc.quests.quest.observer.QuestObserverFacade;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;
import pl.auroramc.scoreboard.sidebar.component.SidebarComponent;

public class QuestSidebarComponent implements SidebarComponent<Quest> {

  private final QuestMessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;
  private final UserFacade userFacade;
  private final QuestIndex questIndex;
  private final QuestObserverFacade questObserverFacade;
  private final ObjectiveController objectiveController;
  private final ObjectiveProgressController objectiveProgressController;

  public QuestSidebarComponent(
      final QuestMessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final UserFacade userFacade,
      final QuestIndex questIndex,
      final QuestObserverFacade questObserverFacade,
      final ObjectiveController objectiveController,
      final ObjectiveProgressController objectiveProgressController) {
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
    this.userFacade = userFacade;
    this.questIndex = questIndex;
    this.questObserverFacade = questObserverFacade;
    this.objectiveController = objectiveController;
    this.objectiveProgressController = objectiveProgressController;
  }

  @Override
  public List<CompiledMessage> render(final Player viewer, final @Nullable Quest quest) {
    if (quest == null) {
      return emptyList();
    }

    return getObservedQuest(viewer, quest);
  }

  @Override
  public List<CompiledMessage> render(final Player viewer) {
    return Optional.ofNullable(
            questObserverFacade.findQuestObserverByUniqueId(viewer.getUniqueId()))
        .map(QuestObserver::getQuestId)
        .map(questIndex::getQuestById)
        .map(quest -> render(viewer, quest))
        .orElse(emptyList());
  }

  private List<CompiledMessage> getObservedQuest(final Player viewer, final Quest quest) {
    final List<CompiledMessage> observedQuest = new ArrayList<>();
    observedQuest.add(empty());
    observedQuest.add(
        messageCompiler.compile(messageSource.observedQuest.placeholder(QUEST_PATH, quest)));
    observedQuest.addAll(getQuestObjectives(viewer, quest));
    return observedQuest;
  }

  private List<CompiledMessage> getQuestObjectives(final Player viewer, final Quest quest) {
    final List<CompiledMessage> questObjectives = new ArrayList<>();
    questObjectives.add(messageCompiler.compile(messageSource.remainingObjectives));
    questObjectives.addAll(getQuestObjectives0(viewer, quest));
    return questObjectives;
  }

  private List<CompiledMessage> getQuestObjectives0(final Player viewer, final Quest quest) {
    return userFacade
        .getUserByUniqueId(viewer.getUniqueId())
        .thenApply(user -> getMergedQuestObjectives(user, quest))
        .thenApply(messageCompiler::compileChildren)
        .thenApply(List::of)
        .exceptionally(CompletableFutureUtils::delegateCaughtException)
        .join();
  }

  private MutableMessage getMergedQuestObjectives(final User user, final Quest quest) {
    return getMergedQuestObjectives(
        objectiveProgressController.getUncompletedObjectives(user, quest));
  }

  private MutableMessage getMergedQuestObjectives(
      final Map<Objective<?>, ObjectiveProgress> objectivesToObjectiveProgresses) {
    return objectivesToObjectiveProgresses.entrySet().stream()
        .sorted(comparingByKey(comparing(objective -> objective.getClass().getSimpleName())))
        .map(
            objectiveToObjectiveProgress ->
                MutableMessage.of(
                    objectiveController.getQuestObjectiveTemplate(
                        objectiveToObjectiveProgress.getKey(),
                        objectiveToObjectiveProgress.getValue())))
        .collect(MutableMessageCollector.collector());
  }
}
