package pl.auroramc.quests.quest;

import java.util.List;
import java.util.UUID;
import pl.auroramc.quests.quest.track.QuestTrack;
import pl.auroramc.quests.quest.track.QuestTrackFacade;

public class QuestController {

  private final QuestIndex questIndex;
  private final QuestTrackFacade questTrackFacade;

  public QuestController(final QuestIndex questIndex, final QuestTrackFacade questTrackFacade) {
    this.questIndex = questIndex;
    this.questTrackFacade = questTrackFacade;
  }

  public List<Quest> getAssignedQuestsByUserUniqueId(final UUID userUniqueId) {
    return questTrackFacade.getQuestTracksByUserUniqueId(userUniqueId, false).stream()
        .map(QuestTrack::getQuestId)
        .map(questIndex::resolveQuest)
        .toList();
  }
}
