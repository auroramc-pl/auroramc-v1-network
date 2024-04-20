package pl.auroramc.economy.leaderboard;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class LeaderboardMessageSource extends OkaeriConfig {

  public MutableMessage leaderboardHeader =
      MutableMessage.of("<gray>Ranking najbogatszych graczy:");

  public MutableMessage leaderboardFooter =
      MutableMessage.of(
          "<gray>Twoja pozycja:<newline><yellow>{context.position}) <gray>{context.username} <dark_gray>- <white>{context.currency.@symbol}{context.balance}");

  public MutableMessage leaderboardEntry =
      MutableMessage.of(
          "<dark_gray>{context.position}) <gray>{context.username} <dark_gray>- <white>{context.currency.@symbol}{context.balance}");

  public MutableMessage leaderboardEmpty =
      MutableMessage.of(
          "<gray>Nie wystarczająca liczba graczy do skompletowania rankingu najbogatszych graczy dla tej waluty.");
}
