package pl.auroramc.quests.quest.observer;

import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;
import pl.auroramc.registry.user.UserFacade;

public final class QuestObserverFacadeFactory {

  private QuestObserverFacadeFactory() {

  }

  public static QuestObserverFacade getQuestObserverFacade(
      final Logger logger,
      final Juliet juliet,
      final UserFacade userFacade
  ) {
    final SqlQuestObserverRepository sqlQuestObserverRepository = new SqlQuestObserverRepository(juliet);
    sqlQuestObserverRepository.createQuestObserverSchemaIfRequired();
    return new QuestObserverService(logger, userFacade, sqlQuestObserverRepository);
  }
}
