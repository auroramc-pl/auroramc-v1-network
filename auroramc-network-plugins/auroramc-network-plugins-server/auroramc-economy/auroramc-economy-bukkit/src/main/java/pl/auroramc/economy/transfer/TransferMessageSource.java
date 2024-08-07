package pl.auroramc.economy.transfer;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class TransferMessageSource extends OkaeriConfig {

  public MutableMessage transferSent =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Wysłałeś przelew do <#f4a9ba><context.player.displayName><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>, <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>z twojego konta zostało odebrane <#f4a9ba><context.currency.@symbol><context.amount><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>.");

  public MutableMessage transferReceived =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Otrzymałeś przelew od <#f4a9ba><context.player.displayName><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>, do twojego konta zostało dodane <#f4a9ba><context.currency.@symbol><context.amount><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>.");

  public MutableMessage transferFailed =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Operacja nie została wykonana, gdyż nie udało się odnaleźć waluty z id pasującym do <gradient:#ffad33:#ff8052:#ffdb57:#ff8052:#ffdb57:#ffad33><currency.@id><gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>.");

  public MutableMessage validationRequiresAmountGreaterThanZero =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Kwota musi być większa od zera.");

  public MutableMessage validationRequiresSpecifyingTarget =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Musisz określić poprawnego odbiorcę, aby wykonać tę operację.");

  public MutableMessage validationRequiresGreaterAmountOfBalance =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie posiadasz wystarczającej ilości pieniędzy, aby wykonać ten przelew.");
}
