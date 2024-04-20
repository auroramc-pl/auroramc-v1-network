package pl.auroramc.quests.quest.observer;

import moe.rafal.juliet.Juliet;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.registry.user.UserFacade;

public final class QuestObserverFacadeFactory {

  private QuestObserverFacadeFactory() {}

  public static QuestObserverFacade getQuestObserverFacade(
      final Scheduler scheduler, final Juliet juliet, final UserFacade userFacade) {
    final SqlQuestObserverRepository sqlQuestObserverRepository =
        new SqlQuestObserverRepository(juliet);
    sqlQuestObserverRepository.createQuestObserverSchemaIfRequired();
    return new QuestObserverService(scheduler, userFacade, sqlQuestObserverRepository);
  }
}
