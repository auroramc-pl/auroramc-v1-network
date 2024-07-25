package pl.auroramc.cheque.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.integrations.configs.command.CommandMessageSource;
import pl.auroramc.messages.message.MutableMessage;

public class MessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public MutableMessage chequeIssued =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Wystawiłeś czek o wartości <#f4a9ba><context.currency.@symbol><context.amount><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>.");

  public MutableMessage chequeFinalized =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Sfinalizowałeś czek na kwotę <#f4a9ba><context.currency.@symbol><context.amount> <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>wystawiony przez <#f4a9ba><context.issuer.username><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>.");

  public MutableMessage validationRequiresAmountInBounds =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Wprowadzona przez ciebie kwota czeku jest niepoprawna, gdyż musi ona wynosić co najmniej <currency.@symbol><cheque.worth.minimum> oraz co najwyżej <currency.@symbol><cheque.worth.maximum>.");

  public MutableMessage validationRequiresIntegralAndFractionalInBounds =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Wprowadzona przez ciebie kwota czeku jest niepoprawna, gdyż maksymalnie może ona posiadać <integral.length.maximum> liczb przed przecinkiem oraz <fraction.length.maximum> liczb po przecinku.");

  public MutableMessage validationRequiresGreaterAmountOfBalance =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie posiadasz wystarczających środków aby wystawić czek o tej wartości.");

  public MutableMessage titleOfCheque =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Czek <#7c5058>(<#f4a9ba><context.currency.@symbol><context.amount><#7c5058>)");

  public MutableMessage linesOfCheque =
      MutableMessage.of("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Wystawiający: <#f4a9ba><context.issuer.username>");

  public CommandMessageSource command = new CommandMessageSource();
}
