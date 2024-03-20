package pl.auroramc.quests.quest.track;

import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;

public final class QuestTrackFacadeFactory {

  private QuestTrackFacadeFactory() {}

  public static QuestTrackFacade getQuestTrackFacade(final Logger logger, final Juliet juliet) {
    final SqlQuestTrackRepository sqlQuestTrackRepository = new SqlQuestTrackRepository(juliet);
    sqlQuestTrackRepository.createQuestTrackSchemaIfRequired();
    return new QuestTrackService(logger, sqlQuestTrackRepository);
  }
}
