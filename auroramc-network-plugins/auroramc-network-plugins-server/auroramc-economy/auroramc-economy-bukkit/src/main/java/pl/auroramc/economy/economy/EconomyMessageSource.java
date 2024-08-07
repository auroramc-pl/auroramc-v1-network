package pl.auroramc.economy.economy;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class EconomyMessageSource extends OkaeriConfig {

  public MutableMessage balanceSet =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Saldo gracza <#f4a9ba><context.player.displayName><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b> zostało ustawione na <#f4a9ba><context.currency.@symbol><context.amount><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>.");

  public MutableMessage balanceDeposited =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Do konta gracza <#f4a9ba><context.player.displayName><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b> zostało dodane <#f4a9ba><context.currency.@symbol><context.amount><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>.");

  public MutableMessage balanceWithdrawn =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Z konta gracza <#f4a9ba><context.player.displayName><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b> zostało odebrane <#f4a9ba><context.currency.@symbol><context.amount><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>.");

  public MutableMessage validationRequiresAmountGreaterThanZero =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Kwota musi być większa od zera.");
}
