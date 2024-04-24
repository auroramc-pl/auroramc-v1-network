package pl.auroramc.quests.quest.track;

import static org.bukkit.Bukkit.getPlayer;
import static pl.auroramc.commons.scheduler.SchedulerPoll.ASYNC;
import static pl.auroramc.commons.scheduler.SchedulerPoll.SYNC;
import static pl.auroramc.quests.quest.QuestMessageSourcePaths.QUEST_PATH;
import static pl.auroramc.quests.quest.QuestState.COMPLETED;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.quests.objective.Objective;
import pl.auroramc.quests.objective.progress.ObjectiveProgressFacade;
import pl.auroramc.quests.quest.Quest;
import pl.auroramc.quests.quest.QuestMessageSource;
import pl.auroramc.quests.quest.QuestState;
import pl.auroramc.quests.quest.observer.QuestObserver;
import pl.auroramc.quests.quest.observer.QuestObserverFacade;
import pl.auroramc.quests.quest.reward.QuestReward;
import pl.auroramc.registry.user.User;

public class QuestTrackController {

  private final Logger logger;
  private final Scheduler scheduler;
  private final QuestMessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;
  private final QuestTrackFacade questTrackFacade;
  private final QuestObserverFacade questObserverFacade;
  private final ObjectiveProgressFacade objectiveProgressFacade;

  public QuestTrackController(
      final Logger logger,
      final Scheduler scheduler,
      final QuestMessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final QuestTrackFacade questTrackFacade,
      final QuestObserverFacade questObserverFacade,
      final ObjectiveProgressFacade objectiveProgressFacade) {
    this.logger = logger;
    this.scheduler = scheduler;
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
    this.questTrackFacade = questTrackFacade;
    this.questObserverFacade = questObserverFacade;
    this.objectiveProgressFacade = objectiveProgressFacade;
  }

  public Optional<QuestTrack> getQuestByUserIdAndQuestId(final UUID uniqueId, final long questId) {
    return questTrackFacade.getQuestTrackByUniqueIdAndQuestId(uniqueId, questId);
  }

  public CompletableFuture<Void> assignQuest(
      final User user, final Quest quest, final QuestState state) {
    return scheduler
        .run(
            ASYNC,
            () ->
                questTrackFacade.createQuestTrack(
                    user.getUniqueId(),
                    new QuestTrack(user.getId(), quest.getKey().getId(), state)))
        .thenRun(
            () -> {
              for (final Objective<?> objective : quest.getObjectives()) {
                objectiveProgressFacade.resolveObjectiveProgress(
                    user.getId(),
                    quest.getKey().getId(),
                    objective.getKey().getId(),
                    objective.getGoalResolver().resolveGoal());
              }
            });
  }

  public void completeQuest(final User user, final Quest quest) {
    objectiveProgressFacade.deleteObjectiveProgressByUserIdAndQuestId(
        user.getId(), quest.getKey().getId());

    final QuestTrack questTrack =
        questTrackFacade
            .getQuestTrackByUniqueIdAndQuestId(user.getUniqueId(), quest.getKey().getId())
            .orElseThrow(
                () ->
                    new QuestTrackResolvingException(
                        "Could not get quest track for user %s and quest %d"
                            .formatted(user.getUniqueId(), quest.getKey().getId())));
    if (questTrack.isCompleted()) {
      logger.fine(
          "Quest %s is already completed for user %s"
              .formatted(quest.getKey().getName(), user.getUniqueId()));
      return;
    }

    questTrack.setCompleted(true);
    questTrack.setQuestState(COMPLETED);
    questTrackFacade.updateQuestTrack(questTrack);

    Optional.ofNullable(getPlayer(user.getUniqueId()))
        .ifPresent(target -> scheduler.run(SYNC, () -> completeQuestSequence(quest, target)));
  }

  @SuppressWarnings("unchecked")
  private void completeQuestSequence(final Quest quest, final Player player) {
    for (final QuestReward<?> reward : quest.getRewards()) {
      ((QuestReward<Player>) reward).apply(player);
    }

    final QuestObserver questObserver =
        questObserverFacade.findQuestObserverByUniqueId(player.getUniqueId());
    if (questObserver != null
        && Objects.equals(questObserver.getQuestId(), quest.getKey().getId())) {
      questObserver.setQuestId(null);
      questObserverFacade.updateQuestObserver(questObserver);
    }

    messageCompiler
        .compile(messageSource.questHasBeenCompleted.placeholder(QUEST_PATH, quest))
        .deliver(player);
  }
}
