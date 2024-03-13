package pl.auroramc.quests.quest.track;

import static java.util.concurrent.CompletableFuture.runAsync;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;
import static pl.auroramc.quests.quest.QuestState.COMPLETED;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

class QuestTrackService implements QuestTrackFacade {

  private final Logger logger;
  private final QuestTrackRepository questTrackRepository;
  private final LoadingCache<UUID, List<QuestTrack>> userUniqueIdToQuestTracks;

  QuestTrackService(final Logger logger, QuestTrackRepository questTrackRepository) {
    this.logger = logger;
    this.questTrackRepository = questTrackRepository;
    this.userUniqueIdToQuestTracks = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofSeconds(20))
        .build(questTrackRepository::getQuestTracksByUserUniqueId);
  }

  @Override
  public Optional<QuestTrack> getQuestTrackByUserUniqueIdAndQuestId(
      final UUID userUniqueId, final Long questId
  ) {
    return getQuestTracksByUserUniqueId(userUniqueId).stream()
        .filter(questTrack -> Objects.equals(questTrack.getQuestId(), questId))
        .findFirst();
  }

  @Override
  public List<QuestTrack> getQuestTracksByUserUniqueId(
      final UUID userUniqueId
  ) {
    return userUniqueIdToQuestTracks.get(userUniqueId);
  }

  @Override
  public List<QuestTrack> getQuestTracksByUserUniqueId(
      final UUID userUniqueId, final boolean includeCompletedQuests
  ) {
    return getQuestTracksByUserUniqueId(userUniqueId).stream()
        .filter(questTrack -> whetherQuestTrackShouldBeIncluded(questTrack, includeCompletedQuests))
        .toList();
  }

  private boolean whetherQuestTrackShouldBeIncluded(
      final QuestTrack questTrack, final boolean includeCompletedQuests
  ) {
    return includeCompletedQuests || questTrack.getQuestState() != COMPLETED;
  }

  @Override
  public void createQuestTrack(final UUID userUniqueId, final QuestTrack questTrack) {
    runAsyncWithExceptionDelegation(() -> questTrackRepository.createQuestTrack(questTrack))
        .thenAccept(state -> userUniqueIdToQuestTracks.invalidate(userUniqueId));
  }

  @Override
  public void updateQuestTrack(final QuestTrack questTrack) {
    runAsyncWithExceptionDelegation(() -> questTrackRepository.updateQuestTrack(questTrack));
  }

  private CompletableFuture<Void> runAsyncWithExceptionDelegation(final Runnable runnable) {
    return runAsync(runnable)
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }
}
