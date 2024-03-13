package pl.auroramc.quests.quest;

import java.nio.file.Path;

public final class QuestFacadeFactory {

  private QuestFacadeFactory() {

  }

  public static QuestFacade getQuestFacade(final Path questsPath, final ClassLoader pluginClassLoader) {
    final QuestService questService = new QuestService(pluginClassLoader);
    questService.discoverQuestDefinitions(questsPath);
    return questService;
  }
}
