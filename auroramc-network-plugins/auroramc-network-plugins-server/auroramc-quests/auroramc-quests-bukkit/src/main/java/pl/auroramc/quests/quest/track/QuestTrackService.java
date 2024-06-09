package pl.auroramc.quests.quest.track;

import static java.time.Duration.ofSeconds;
import static pl.auroramc.commons.scheduler.SchedulerPoll.ASYNC;
import static pl.auroramc.quests.quest.QuestState.COMPLETED;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import pl.auroramc.commons.concurrent.CompletableFutureUtils;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.commons.scheduler.caffeine.CaffeineExecutor;

class QuestTrackService implements QuestTrackFacade {

  private final Scheduler scheduler;
  private final QuestTrackRepository questTrackRepository;
  private final LoadingCache<UUID, List<QuestTrack>> questTracksByUniqueId;

  QuestTrackService(final Scheduler scheduler, QuestTrackRepository questTrackRepository) {
    this.scheduler = scheduler;
    this.questTrackRepository = questTrackRepository;
    this.questTracksByUniqueId =
        Caffeine.newBuilder()
            .executor(new CaffeineExecutor(scheduler))
            .expireAfterWrite(ofSeconds(20))
            .build(questTrackRepository::getQuestTracksByUniqueId);
  }

  @Override
  public Optional<QuestTrack> getQuestTrackByUniqueIdAndQuestId(
      final UUID uniqueId, final Long questId) {
    return getQuestTracksByUniqueId(uniqueId).stream()
        .filter(questTrack -> Objects.equals(questTrack.getQuestId(), questId))
        .findFirst();
  }

  @Override
  public List<QuestTrack> getQuestTracksByUniqueId(final UUID uniqueId) {
    return questTracksByUniqueId.get(uniqueId);
  }

  @Override
  public List<QuestTrack> getQuestTracksByUniqueId(
      final UUID uniqueId, final boolean includeCompletedQuests) {
    return getQuestTracksByUniqueId(uniqueId).stream()
        .filter(questTrack -> whetherQuestTrackShouldBeIncluded(questTrack, includeCompletedQuests))
        .toList();
  }

  private boolean whetherQuestTrackShouldBeIncluded(
      final QuestTrack questTrack, final boolean includeCompletedQuests) {
    return includeCompletedQuests || questTrack.getQuestState() != COMPLETED;
  }

  @Override
  public void createQuestTrack(final UUID uniqueId, final QuestTrack questTrack) {
    scheduler
        .run(ASYNC, () -> questTrackRepository.createQuestTrack(questTrack))
        .thenAccept(state -> questTracksByUniqueId.invalidate(uniqueId))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  @Override
  public void updateQuestTrack(final QuestTrack questTrack) {
    scheduler
        .run(ASYNC, () -> questTrackRepository.updateQuestTrack(questTrack))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }
}
