package pl.auroramc.scoreboard.quest;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.Map.Entry.comparingByKey;
import static net.kyori.adventure.text.Component.empty;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;
import static pl.auroramc.quests.objective.ObjectiveUtils.getQuestObjective;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
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
import pl.auroramc.scoreboard.message.MessageSource;
import pl.auroramc.scoreboard.sidebar.component.SidebarComponentKyori;

public class QuestSidebarComponent implements SidebarComponentKyori<Quest> {

  private final Logger logger;
  private final MessageSource messageSource;
  private final UserFacade userFacade;
  private final QuestIndex questIndex;
  private final QuestObserverFacade questObserverFacade;
  private final ObjectiveProgressController objectiveProgressController;

  public QuestSidebarComponent(
      final Logger logger,
      final MessageSource messageSource,
      final UserFacade userFacade,
      final QuestIndex questIndex,
      final QuestObserverFacade questObserverFacade,
      final ObjectiveProgressController objectiveProgressController
  ) {
    this.logger = logger;
    this.messageSource = messageSource;
    this.userFacade = userFacade;
    this.questIndex = questIndex;
    this.questObserverFacade = questObserverFacade;
    this.objectiveProgressController = objectiveProgressController;
  }

  @Override
  public List<Component> render(final Player viewer, final @Nullable Quest quest) {
    if (quest == null) {
      return emptyList();
    }

    final List<Component> lines = new ArrayList<>();
    lines.add(empty());
    lines.addAll(renderQuestName(quest));
    lines.addAll(renderQuestObjectiveHeader());
    lines.addAll(renderQuestObjectives(viewer, quest));
    return lines;
  }

  @Override
  public List<Component> render(final Player viewer) {
    return Optional.ofNullable(questObserverFacade.findQuestObserverByUserUniqueId(viewer.getUniqueId()))
        .map(QuestObserver::getQuestId)
        .map(questIndex::resolveQuest)
        .map(quest -> render(viewer, quest))
        .orElse(emptyList());
  }

  private List<Component> renderQuestName(final Quest quest) {
    return Stream.of(
        messageSource.quest.observedQuest,
        messageSource.quest.observedQuestName.with("quest", quest.getKey().getName())
    )
        .map(MutableMessage::into)
        .toList();
  }

  private List<Component> renderQuestObjectiveHeader() {
    return List.of(
        empty(), messageSource.quest.remainingQuestObjectives.into()
    );
  }

  private List<Component> renderQuestObjectives(final Player viewer, final Quest quest) {
    return userFacade.getUserByUniqueId(viewer.getUniqueId())
        .thenApply(user -> objectiveProgressController.getUncompletedObjectives(user, quest))
        .thenApply(this::aggregateQuestObjectives)
        .exceptionally(exception -> delegateCaughtException(logger, exception))
        .join();
  }

  private List<Component> aggregateQuestObjectives(
      final Map<Objective<?>, ObjectiveProgress> objectivesToObjectiveProgresses) {
    return objectivesToObjectiveProgresses.entrySet().stream()
        .sorted(comparingByKey(comparing(objective -> objective.getClass().getSimpleName())))
        .map(objectiveToObjectiveProgress ->
            getQuestObjective(
                objectiveToObjectiveProgress.getKey(),
                objectiveToObjectiveProgress.getValue()
            )
        )
        .flatMap(List::stream)
        .toList();
  }
}
