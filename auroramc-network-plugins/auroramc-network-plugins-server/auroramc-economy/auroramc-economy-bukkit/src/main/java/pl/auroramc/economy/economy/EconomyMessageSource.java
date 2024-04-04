package pl.auroramc.economy.economy;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class EconomyMessageSource extends OkaeriConfig {

  public MutableMessage balanceSet =
      MutableMessage.of(
          "<gray>Saldo gracza <white>{context.player.displayName}<gray> zostało ustawione na <white>{context.currency.@symbol}{context.amount}<gray>.");

  public MutableMessage balanceDeposited =
      MutableMessage.of(
          "<gray>Do konta gracza <white>{context.player.displayName}<gray> zostało dodane <white>{context.currency.@symbol}{context.amount}<gray>.");

  public MutableMessage balanceWithdrawn =
      MutableMessage.of(
          "<gray>Z konta gracza <white>{context.player.displayName}<gray> zostało odebrane <white>{context.currency.@symbol}{context.amount}<gray>.");

  public MutableMessage validationRequiresAmountGreaterThanZero =
      MutableMessage.of("<red>Kwota musi być większa od zera.");
}
