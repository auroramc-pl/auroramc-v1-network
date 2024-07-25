package pl.auroramc.scoreboard.quest;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class QuestMessageSource extends OkaeriConfig {

  public MutableMessage observedQuest =
      MutableMessage.of("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Obserwowane zadanie: <#f4a9ba><quest.@key.@name>");

  public MutableMessage remainingObjectives = MutableMessage.of("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Pozostałe cele:");
}
