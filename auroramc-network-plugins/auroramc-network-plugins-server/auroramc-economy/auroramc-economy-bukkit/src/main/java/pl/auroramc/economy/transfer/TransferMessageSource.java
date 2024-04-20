package pl.auroramc.economy.transfer;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class TransferMessageSource extends OkaeriConfig {

  public MutableMessage transferSent =
      MutableMessage.of(
          "<gray>Wysłałeś przelew do <white>{context.player.displayName}<gray>, <gray>z twojego konta zostało odebrane <white>{context.currency.@symbol}{context.amount}<gray>.");

  public MutableMessage transferReceived =
      MutableMessage.of(
          "<gray>Otrzymałeś przelew od <white>{context.player.displayName}<gray>, do twojego konta zostało dodane <white>{context.currency.@symbol}{context.amount}<gray>.");

  public MutableMessage transferFailed =
      MutableMessage.of(
          "<red>Operacja nie została wykonana, gdyż nie udało się odnaleźć waluty z id pasującym do <yellow>{currency.@id}<red>.");

  public MutableMessage validationRequiresAmountGreaterThanZero =
      MutableMessage.of("<red>Kwota musi być większa od zera.");

  public MutableMessage validationRequiresSpecifyingTarget =
      MutableMessage.of("<red>Musisz określić poprawnego odbiorcę, aby wykonać tę operację.");

  public MutableMessage validationRequiresGreaterAmountOfBalance =
      MutableMessage.of(
          "<red>Nie posiadasz wystarczającej ilości pieniędzy, aby wykonać ten przelew.");
}
