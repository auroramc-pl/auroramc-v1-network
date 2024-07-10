package pl.auroramc.cheque.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.integrations.configs.command.CommandMessageSource;
import pl.auroramc.messages.message.MutableMessage;

public class MessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public MutableMessage chequeIssued =
      MutableMessage.of(
          "<gray>Wystawiłeś czek o wartości <white><context.currency.@symbol><context.amount><gray>.");

  public MutableMessage chequeFinalized =
      MutableMessage.of(
          "<gray>Sfinalizowałeś czek na kwotę <white><context.currency.@symbol><context.amount> <gray>wystawiony przez <white><context.issuer.username><gray>.");

  public MutableMessage validationRequiresAmountInBounds =
      MutableMessage.of(
          "<red>Wprowadzona przez ciebie kwota czeku jest niepoprawna, gdyż musi ona wynosić co najmniej <currency.@symbol><cheque.worth.minimum> oraz co najwyżej <currency.@symbol><cheque.worth.maximum>.");

  public MutableMessage validationRequiresIntegralAndFractionalInBounds =
      MutableMessage.of(
          "<red>Wprowadzona przez ciebie kwota czeku jest niepoprawna, gdyż maksymalnie może ona posiadać <integral.length.maximum> liczb przed przecinkiem oraz <fraction.length.maximum> liczb po przecinku.");

  public MutableMessage validationRequiresGreaterAmountOfBalance =
      MutableMessage.of(
          "<red>Nie posiadasz wystarczających środków aby wystawić czek o tej wartości.");

  public MutableMessage titleOfCheque =
      MutableMessage.of(
          "<gray>Czek <dark_gray>(<white><context.currency.@symbol><context.amount><dark_gray>)");

  public MutableMessage linesOfCheque =
      MutableMessage.of("<gray>Wystawiający: <white><context.issuer.username>");

  public CommandMessageSource command = new CommandMessageSource();
}
