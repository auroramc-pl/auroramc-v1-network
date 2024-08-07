package pl.auroramc.quests.quest;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class QuestMessageSource extends OkaeriConfig {

  public MutableMessage observingQuest =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Obserwujesz zadanie <#f4a9ba><quest.@key.@id><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>.");

  public MutableMessage questCouldNotBeFound =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Zadanie o nazwie <gradient:#ffad33:#ff8052:#ffdb57:#ff8052:#ffdb57:#ffad33><input> <gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>nie zostało znalezione.");

  public MutableMessage questHasBeenAssigned =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Zadanie <#f4a9ba><quest.@key.@name> <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>zostało przypisane dla <#f4a9ba><user.@username><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>.");

  public MutableMessage questIsAlreadyAssigned =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Zadanie <gradient:#ffad33:#ff8052:#ffdb57:#ff8052:#ffdb57:#ffad33><quest.@key.@name> <gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>jest już przypisane dla <gradient:#ffad33:#ff8052:#ffdb57:#ff8052:#ffdb57:#ffad33><user.@username><gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>.");

  public MutableMessage questIsAlreadyCompleted =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Zadanie <gradient:#ffad33:#ff8052:#ffdb57:#ff8052:#ffdb57:#ffad33><quest.@key.@id> <gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>jest już ukończone przez <gradient:#ffad33:#ff8052:#ffdb57:#ff8052:#ffdb57:#ffad33><user.@username><gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>.");

  public MutableMessage questHasBeenCompleted =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Ukończyłeś zadanie <#f4a9ba><quest.@key.@id><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>.");

  public MutableMessage questIsCompleted =
      MutableMessage.of(
          "<gradient:#ffad33:#ff8052:#ffdb57:#ff8052:#ffdb57:#ffad33>Ukończyłeś już te zadanie.");

  public MutableMessage questRequiresCompletionOfAllObjectives =
      MutableMessage.of(
          "<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>Ukończ wszystkie cele, aby zakończyć to zadanie.");

  public MutableMessage questCouldBeStarted =
      MutableMessage.of(
          "<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>Naciśnij <bold>LPM</bold>, aby rozpocząć zadanie.");

  public MutableMessage questCouldBeTracked =
      MutableMessage.of(
          "<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>Naciśnij <bold>PPM</bold>, aby obserwować zadanie.");

  public MutableMessage questObjectivesHeader =
      MutableMessage.of("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b><bold>Cele:");

  public MutableMessage questsViewTitle =
      MutableMessage.of("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Dostępne zadania");
}
