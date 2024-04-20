package pl.auroramc.quests.quest.track;

import moe.rafal.juliet.Juliet;
import pl.auroramc.commons.scheduler.Scheduler;

public final class QuestTrackFacadeFactory {

  private QuestTrackFacadeFactory() {}

  public static QuestTrackFacade getQuestTrackFacade(final Scheduler scheduler, final Juliet juliet) {
    final SqlQuestTrackRepository sqlQuestTrackRepository = new SqlQuestTrackRepository(juliet);
    sqlQuestTrackRepository.createQuestTrackSchemaIfRequired();
    return new QuestTrackService(scheduler, sqlQuestTrackRepository);
  }
}
