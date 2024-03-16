package pl.auroramc.quests.quest.track;

import java.util.List;
import java.util.UUID;

interface QuestTrackRepository {

  List<QuestTrack> getQuestTracksByUniqueId(final UUID uniqueId);

  void createQuestTrack(final QuestTrack questTrack);

  void updateQuestTrack(final QuestTrack questTrack);
}
