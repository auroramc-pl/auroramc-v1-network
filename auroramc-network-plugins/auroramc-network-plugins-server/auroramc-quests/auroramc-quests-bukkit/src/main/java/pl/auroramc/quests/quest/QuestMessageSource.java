package pl.auroramc.quests.quest;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class QuestMessageSource extends OkaeriConfig {

  public MutableMessage observingQuest =
      MutableMessage.of("<gray>Obserwujesz zadanie <white>{quest.@key.@id}<gray>.");

  public MutableMessage questCouldNotBeFound =
      MutableMessage.of("<red>Zadanie o nazwie <yellow>{input} <red>nie zostało znalezione.");

  public MutableMessage questHasBeenAssigned =
      MutableMessage.of(
          "<gray>Zadanie <white>{quest.@key.@name} <gray>zostało przypisane dla <white>{user.@username}<gray>.");

  public MutableMessage questIsAlreadyAssigned =
      MutableMessage.of(
          "<red>Zadanie <yellow>{quest.@key.@name} <red>jest już przypisane dla <yellow>{user.@username}<red>.");

  public MutableMessage questIsAlreadyCompleted =
      MutableMessage.of(
          "<red>Zadanie <yellow>{quest.@key.@id} <red>jest już ukończone przez <yellow>{user.@username}<red>.");

  public MutableMessage questHasBeenCompleted =
      MutableMessage.of("<gray>Ukończyłeś zadanie <white>{quest.@key.@id}<gray>.");

  public MutableMessage questIsCompleted = MutableMessage.of("<yellow>Ukończyłeś już te zadanie.");

  public MutableMessage questRequiresCompletionOfAllObjectives =
      MutableMessage.of("<yellow>Ukończ wszystkie cele, aby zakończyć to zadanie.");

  public MutableMessage questCouldBeStarted =
      MutableMessage.of("<yellow>Naciśnij <bold>LPM</bold>, aby rozpocząć zadanie.");

  public MutableMessage questCouldBeTracked =
      MutableMessage.of("<yellow>Naciśnij <bold>PPM</bold>, aby obserwować zadanie.");

  public MutableMessage questObjectivesHeader = MutableMessage.of("<gray>Cele:");

  public MutableMessage questsViewTitle = MutableMessage.of("<gray>Dostępne zadania");
}
