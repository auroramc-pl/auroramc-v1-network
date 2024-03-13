package pl.auroramc.scoreboard.message.quest;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.commons.message.MutableMessage;

public class QuestMessageSource extends OkaeriConfig {

  public MutableMessage observedQuest = MutableMessage.of(
      "<gray>Obserwowane zadanie:"
  );

  public MutableMessage observedQuestName = MutableMessage.of(
      "<white>{quest}"
  );

  public MutableMessage remainingQuestObjectives = MutableMessage.of(
      "<gray>Pozostałe cele:"
  );
}
