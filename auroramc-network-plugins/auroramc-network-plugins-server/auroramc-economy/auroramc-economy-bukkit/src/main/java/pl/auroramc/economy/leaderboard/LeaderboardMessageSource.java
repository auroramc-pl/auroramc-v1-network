package pl.auroramc.economy.leaderboard;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class LeaderboardMessageSource extends OkaeriConfig {

  public MutableMessage leaderboardHeader =
      MutableMessage.of("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Ranking najbogatszych graczy:");

  public MutableMessage leaderboardFooter =
      MutableMessage.of(
          "<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>Twoja pozycja:<newline><#d3a37e><context.position>) <gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894><context.username> <#d3a37e>- <gradient:#f7cf78:#fbd06a:#f4b352:#fbd06a:#f7cf78><context.currency.@symbol><context.balance>");

  public MutableMessage leaderboardEntry =
      MutableMessage.of(
          "<#7c5058><context.position>) <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b><context.username> <#7c5058>- <#f4a9ba><context.currency.@symbol><context.balance>");

  public MutableMessage leaderboardEmpty =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Nie wystarczająca liczba graczy do skompletowania rankingu najbogatszych graczy dla tej waluty.");
}
