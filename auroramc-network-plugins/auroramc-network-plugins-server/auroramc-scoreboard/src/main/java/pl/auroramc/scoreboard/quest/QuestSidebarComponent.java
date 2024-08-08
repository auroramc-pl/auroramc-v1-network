//package pl.auroramc.scoreboard.quest;
//
//import static java.util.Collections.emptyList;
//import static java.util.Comparator.comparing;
//import static java.util.Map.Entry.comparingByKey;
//import static pl.auroramc.messages.message.compiler.CompiledMessage.empty;
//import static pl.auroramc.scoreboard.message.ScoreboardMessageSourcePaths.QUEST_PATH;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import org.bukkit.entity.Player;
//import org.jetbrains.annotations.Nullable;
//import pl.auroramc.commons.concurrent.CompletableFutureUtils;
//import pl.auroramc.messages.i18n.BukkitMessageFacade;
//import pl.auroramc.messages.message.MutableMessage;
//import pl.auroramc.messages.message.MutableMessageCollector;
//import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
//import pl.auroramc.messages.message.compiler.CompiledMessage;
//import pl.auroramc.messages.viewer.BukkitViewer;
//import pl.auroramc.messages.viewer.BukkitViewerFacade;
//import pl.auroramc.messages.viewer.Viewer;
//import pl.auroramc.quests.objective.Objective;
//import pl.auroramc.quests.objective.ObjectiveController;
//import pl.auroramc.quests.objective.progress.ObjectiveProgress;
//import pl.auroramc.quests.objective.progress.ObjectiveProgressController;
//import pl.auroramc.quests.quest.Quest;
//import pl.auroramc.quests.quest.QuestIndex;
//import pl.auroramc.quests.quest.observer.QuestObserver;
//import pl.auroramc.quests.quest.observer.QuestObserverFacade;
//import pl.auroramc.registry.user.User;
//import pl.auroramc.registry.user.UserFacade;
//import pl.auroramc.scoreboard.message.ScoreboardMessageSource;
//import pl.auroramc.scoreboard.sidebar.component.SidebarComponent;
//
//public class QuestSidebarComponent implements SidebarComponent<Quest> {
//
//  private final ScoreboardMessageSource messageSource;
//  private final BukkitMessageFacade messageFacade;
//  private final BukkitMessageCompiler messageCompiler;
//  private final BukkitViewerFacade viewerFacade;
//  private final UserFacade userFacade;
//  private final QuestIndex questIndex;
//  private final QuestObserverFacade questObserverFacade;
//  private final ObjectiveController objectiveController;
//  private final ObjectiveProgressController objectiveProgressController;
//
//  public QuestSidebarComponent(
//      final ScoreboardMessageSource messageSource,
//      final BukkitMessageFacade messageFacade,
//      final BukkitMessageCompiler messageCompiler,
//      final BukkitViewerFacade viewerFacade,
//      final UserFacade userFacade,
//      final QuestIndex questIndex,
//      final QuestObserverFacade questObserverFacade,
//      final ObjectiveController objectiveController,
//      final ObjectiveProgressController objectiveProgressController) {
//    this.messageSource = messageSource;
//    this.messageFacade = messageFacade;
//    this.messageCompiler = messageCompiler;
//    this.viewerFacade = viewerFacade;
//    this.userFacade = userFacade;
//    this.questIndex = questIndex;
//    this.questObserverFacade = questObserverFacade;
//    this.objectiveController = objectiveController;
//    this.objectiveProgressController = objectiveProgressController;
//  }
//
//  @Override
//  public List<CompiledMessage> render(final Player player, final @Nullable Quest quest) {
//    if (quest == null) {
//      return emptyList();
//    }
//
//    final BukkitViewer viewer = viewerFacade.getViewerByUniqueId(player.getUniqueId());
//    return getObservedQuest(player, viewer, quest);
//  }
//
//  @Override
//  public List<CompiledMessage> render(final Player player) {
//    return Optional.ofNullable(
//            questObserverFacade.findQuestObserverByUniqueId(player.getUniqueId()))
//        .map(QuestObserver::getQuestId)
//        .map(questIndex::getQuestById)
//        .map(quest -> render(player, quest))
//        .orElse(emptyList());
//  }
//
//  private List<CompiledMessage> getObservedQuest(
//      final Player player, final Viewer viewer, final Quest quest) {
//    final List<CompiledMessage> observedQuest = new ArrayList<>();
//    observedQuest.add(empty());
//    observedQuest.add(
//        messageCompiler.compile(
//            viewer,
//            messageFacade
//                .getMessage(viewer, messageSource.questObserved)
//                .placeholder(QUEST_PATH, quest)));
//    observedQuest.addAll(getQuestObjectives(player, viewer, quest));
//    return observedQuest;
//  }
//
//  private List<CompiledMessage> getQuestObjectives(
//      final Player player, final Viewer viewer, final Quest quest) {
//    final List<CompiledMessage> questObjectives = new ArrayList<>();
//    questObjectives.add(
//        messageCompiler.compile(
//            viewer, messageFacade.getMessage(viewer, messageSource.questRemainingObjectives)));
//    questObjectives.addAll(getQuestObjectives0(player, quest));
//    return questObjectives;
//  }
//
//  private List<CompiledMessage> getQuestObjectives0(final Player player, final Quest quest) {
//    return userFacade
//        .getUserByUniqueId(player.getUniqueId())
//        .thenApply(user -> getMergedQuestObjectives(user, quest))
//        .thenApply(messageCompiler::compileChildren)
//        .thenApply(List::of)
//        .exceptionally(CompletableFutureUtils::delegateCaughtException)
//        .join();
//  }
//
//  private MutableMessage getMergedQuestObjectives(final User user, final Quest quest) {
//    return getMergedQuestObjectives(
//        objectiveProgressController.getUncompletedObjectives(user, quest));
//  }
//
//  private MutableMessage getMergedQuestObjectives(
//      final Map<Objective<?>, ObjectiveProgress> objectivesToObjectiveProgresses) {
//    return objectivesToObjectiveProgresses.entrySet().stream()
//        .sorted(comparingByKey(comparing(objective -> objective.getClass().getSimpleName())))
//        .map(
//            objectiveToObjectiveProgress ->
//                MutableMessage.of(
//                    objectiveController.getQuestObjectiveTemplate(
//                        objectiveToObjectiveProgress.getKey(),
//                        objectiveToObjectiveProgress.getValue())))
//        .collect(MutableMessageCollector.collector());
//  }
//}
