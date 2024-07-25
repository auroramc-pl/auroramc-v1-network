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
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Wprowadzona przez ciebie kwota czeku jest niepoprawna, gdyż musi ona wynosić co najmniej <gradient:#ffad33:#ff8052:#ffdb57:#ff8052:#ffdb57:#ffad33><currency.@symbol><cheque.worth.minimum> <gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>oraz co najwyżej <gradient:#ffad33:#ff8052:#ffdb57:#ff8052:#ffdb57:#ffad33><currency.@symbol><cheque.worth.maximum><gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>.");

  public MutableMessage validationRequiresIntegralAndFractionalInBounds =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Wprowadzona przez ciebie kwota czeku jest niepoprawna, gdyż maksymalnie może ona posiadać <gradient:#ffad33:#ff8052:#ffdb57:#ff8052:#ffdb57:#ffad33><integral.length.maximum> <gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>liczb przed przecinkiem oraz <gradient:#ffad33:#ff8052:#ffdb57:#ff8052:#ffdb57:#ffad33><fraction.length.maximum> <gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>liczb po przecinku.");

  public MutableMessage validationRequiresGreaterAmountOfBalance =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie posiadasz wystarczających środków aby wystawić czek o tej wartości.");

  public MutableMessage titleOfCheque =
      MutableMessage.of(
          "<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>Czek <#d3a37e>(<gradient:#f7cf78:#fbd06a:#f4b352:#fbd06a:#f7cf78><context.currency.@symbol><context.amount><#d3a37e>)");

  public MutableMessage linesOfCheque =
      MutableMessage.of("<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>Wystawiający: <gradient:#f7cf78:#fbd06a:#f4b352:#fbd06a:#f7cf78><context.issuer.username>");

  public CommandMessageSource command = new CommandMessageSource();
}
