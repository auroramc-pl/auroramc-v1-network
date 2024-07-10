package pl.auroramc.scoreboard.quest;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class QuestMessageSource extends OkaeriConfig {

  public MutableMessage observedQuest =
      MutableMessage.of("<gray>Obserwowane zadanie: <white><quest.@key.@name>");

  public MutableMessage remainingObjectives = MutableMessage.of("<gray>Pozostałe cele:");
}
