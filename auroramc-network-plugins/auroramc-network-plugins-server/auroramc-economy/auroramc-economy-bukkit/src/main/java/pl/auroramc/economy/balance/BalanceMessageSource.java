package pl.auroramc.economy.balance;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class BalanceMessageSource extends OkaeriConfig {

  public MutableMessage balanceSummaryHeader =
      MutableMessage.of("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Podsumowanie stanu konta:");

  public MutableMessage balanceSummaryHeaderTargeted =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Podsumowanie stanu konta gracza <#f4a9ba><player.displayName><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>:");

  public MutableMessage balanceSummaryEntry =
      MutableMessage.of(
          "<#7c5058>► <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b><context.currency.@name> <#7c5058>- <#f4a9ba><context.currency.@symbol><context.balance>");
}
