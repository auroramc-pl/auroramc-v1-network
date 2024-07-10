package pl.auroramc.economy.balance;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class BalanceMessageSource extends OkaeriConfig {

  public MutableMessage balanceSummaryHeader = MutableMessage.of("<gray>Podsumowanie stanu konta:");

  public MutableMessage balanceSummaryHeaderTargeted =
      MutableMessage.of("<gray>Podsumowanie stanu konta gracza <white><player.displayName><gray>:");

  public MutableMessage balanceSummaryEntry =
      MutableMessage.of(
          "<dark_gray>► <gray><context.currency.@name> <dark_gray>- <white><context.currency.@symbol><context.balance>");
}
