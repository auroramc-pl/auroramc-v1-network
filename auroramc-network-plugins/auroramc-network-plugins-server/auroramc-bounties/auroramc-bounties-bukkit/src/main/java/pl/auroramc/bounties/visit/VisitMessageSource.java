package pl.auroramc.bounties.visit;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class VisitMessageSource extends OkaeriConfig {

  public MutableMessage visitDailySummary =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Wykaz sesji z dnia <#f4a9ba><timeframe.minimum><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>:");

  public MutableMessage visitRangeSummary =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Wykaz sesji z okresu <#f4a9ba><timeframe.minimum> <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>- <#f4a9ba><timeframe.maximum><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>:");

  public MutableMessage visitEntry =
      MutableMessage.of(
          "<#7c5058>► (<#f4a9ba><visit.@startTime> <#7c5058>- <#f4a9ba><visit.@ditchTime><#7c5058>) <#7c5058>- <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b><visit.@duration>");

  public MutableMessage noVisits =
      MutableMessage.of("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Brak sesji w podanym okresie.");
}
