package pl.auroramc.quests.quest.track;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestTrackFacade {

  Optional<QuestTrack> getQuestTrackByUserUniqueIdAndQuestId(
      final UUID userUniqueId, final Long questId);

  List<QuestTrack> getQuestTracksByUserUniqueId(
      final UUID userUniqueId);

  List<QuestTrack> getQuestTracksByUserUniqueId(
      final UUID userUniqueId, final boolean includeCompletedQuests);

  void createQuestTrack(final UUID userUniqueId, final QuestTrack questTrack);

  void updateQuestTrack(final QuestTrack questTrack);
}
