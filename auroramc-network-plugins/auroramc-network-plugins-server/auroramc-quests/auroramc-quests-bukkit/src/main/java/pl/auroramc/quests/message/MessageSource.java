package pl.auroramc.quests.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.commons.integration.configs.command.CommandMessageSource;
import pl.auroramc.messages.message.MutableMessage;

public class MessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

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

  public MutableMessage titleOfQuestsView = MutableMessage.of("<gray>Dostępne zadania");

  public MutableMessage nameOfNextPageNavigationButton = MutableMessage.of("<gray>Następna strona");

  public MutableMessage nameOfPrevPageNavigationButton =
      MutableMessage.of("<gray>Poprzednia strona");

  public MutableMessage loreOfNextPageNavigationButton =
      MutableMessage.of("<gray>Naciśnij, aby przejść do następnej strony.");

  public MutableMessage loreOfPrevPageNavigationButton =
      MutableMessage.of("<gray>Naciśnij, aby przejść do poprzedniej strony.");

  public MutableMessage observingQuest =
      MutableMessage.of("<gray>Obserwujesz zadanie <white>{quest}<gray>.");

  public MutableMessage breakBlockObjective =
      MutableMessage.of(
          "<dark_gray>► <gray>Zniszcz <white>{type} <dark_gray>(<white>{data}<gray>/<gray>{goal}<dark_gray>)");

  public MutableMessage placeBlockObjective =
      MutableMessage.of(
          "<dark_gray>► <gray>Postaw <white>{type} <dark_gray>(<white>{data}<gray>/<gray>{goal}<dark_gray>)");

  public MutableMessage distanceObjective =
      MutableMessage.of(
          "<dark_gray>► <gray>Przemierz dystans <dark_gray>(<white>{data}<gray>/<gray>{goal}<dark_gray>)");

  public MutableMessage heldItemRequirement =
      MutableMessage.of(
          "<dark_gray>* <gray>Aby wykonać cel, musisz używać przedmiotu <white>{item}");

  public CommandMessageSource command = new CommandMessageSource();
}
