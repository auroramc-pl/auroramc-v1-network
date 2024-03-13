package pl.auroramc.quests.quest;

import java.nio.file.Path;
import java.util.List;

public interface QuestFacade {

  List<Quest> discoverQuestDefinitions(final Path traversalPath);
}
