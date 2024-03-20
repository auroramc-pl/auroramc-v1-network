package pl.auroramc.quests.quest.track;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestTrackFacade {

  Optional<QuestTrack> getQuestTrackByUniqueIdAndQuestId(final UUID uniqueId, final Long questId);

  List<QuestTrack> getQuestTracksByUniqueId(final UUID uniqueId);

  List<QuestTrack> getQuestTracksByUniqueId(
      final UUID uniqueId, final boolean includeCompletedQuests);

  void createQuestTrack(final UUID uniqueId, final QuestTrack questTrack);

  void updateQuestTrack(final QuestTrack questTrack);
}
