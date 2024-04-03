package pl.auroramc.scoreboard.quest;

import static java.util.Comparator.comparing;
import static java.util.Map.Entry.comparingByKey;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;
import static pl.auroramc.commons.message.MutableMessage.EMPTY_DELIMITER;
import static pl.auroramc.commons.message.MutableMessage.empty;
import static pl.auroramc.commons.message.MutableMessage.newline;
import static pl.auroramc.quests.objective.ObjectiveUtils.getQuestObjectiveTemplate;
import static pl.auroramc.scoreboard.message.MutableMessageVariableKey.QUEST_PATH;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
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
import pl.auroramc.scoreboard.message.MutableMessageSource;
import pl.auroramc.scoreboard.sidebar.component.SidebarComponentKyori;

public class QuestSidebarComponent implements SidebarComponentKyori<Quest> {

  private final Logger logger;
  private final MutableMessageSource messageSource;
  private final UserFacade userFacade;
  private final QuestIndex questIndex;
  private final QuestObserverFacade questObserverFacade;
  private final ObjectiveProgressController objectiveProgressController;

  public QuestSidebarComponent(
      final Logger logger,
      final MutableMessageSource messageSource,
      final UserFacade userFacade,
      final QuestIndex questIndex,
      final QuestObserverFacade questObserverFacade,
      final ObjectiveProgressController objectiveProgressController) {
    this.logger = logger;
    this.messageSource = messageSource;
    this.userFacade = userFacade;
    this.questIndex = questIndex;
    this.questObserverFacade = questObserverFacade;
    this.objectiveProgressController = objectiveProgressController;
  }

  @Override
  public MutableMessage render(final Player viewer, final @Nullable Quest quest) {
    if (quest == null) {
      return empty();
    }

    return empty()
        .append(renderQuestName(quest))
        .append(renderQuestObjectiveHeader())
        .append(renderQuestObjectives(viewer, quest));
  }

  @Override
  public MutableMessage render(final Player viewer) {
    return Optional.ofNullable(
            questObserverFacade.findQuestObserverByUniqueId(viewer.getUniqueId()))
        .map(QuestObserver::getQuestId)
        .map(questIndex::resolveQuest)
        .map(quest -> render(viewer, quest))
        .orElse(empty());
  }

  private MutableMessage renderQuestName(final Quest quest) {
    return newline()
        .append(messageSource.quest.observedQuest, EMPTY_DELIMITER)
        .append(
            messageSource.quest.observedQuestName.with(
                QUEST_PATH, quest.getKey().getName()));
  }

  private MutableMessage renderQuestObjectiveHeader() {
    return newline().append(messageSource.quest.remainingQuestObjectives, EMPTY_DELIMITER);
  }

  private MutableMessage renderQuestObjectives(final Player viewer, final Quest quest) {
    return userFacade
        .getUserByUniqueId(viewer.getUniqueId())
        .thenApply(user -> objectiveProgressController.getUncompletedObjectives(user, quest))
        .thenApply(this::aggregateQuestObjectives)
        .exceptionally(exception -> delegateCaughtException(logger, exception))
        .join();
  }

  private MutableMessage aggregateQuestObjectives(
      final Map<Objective<?>, ObjectiveProgress> objectivesToObjectiveProgresses) {
    return objectivesToObjectiveProgresses.entrySet().stream()
        .sorted(comparingByKey(comparing(objective -> objective.getClass().getSimpleName())))
        .map(
            objectiveToObjectiveProgress ->
                MutableMessage.of(
                    getQuestObjectiveTemplate(
                        objectiveToObjectiveProgress.getKey(),
                        objectiveToObjectiveProgress.getValue())))
        .collect(MutableMessage.collector());
  }
}
