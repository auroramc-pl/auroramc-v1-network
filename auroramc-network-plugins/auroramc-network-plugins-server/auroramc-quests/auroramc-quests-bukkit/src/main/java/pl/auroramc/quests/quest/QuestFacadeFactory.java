package pl.auroramc.quests.quest;

import java.nio.file.Path;

public final class QuestFacadeFactory {

  private QuestFacadeFactory() {}

  public static QuestFacade getQuestFacade(
      final Path questDefinitionsPath, final ClassLoader pluginClassLoader) {
    return new QuestService(pluginClassLoader, questDefinitionsPath);
  }
}
