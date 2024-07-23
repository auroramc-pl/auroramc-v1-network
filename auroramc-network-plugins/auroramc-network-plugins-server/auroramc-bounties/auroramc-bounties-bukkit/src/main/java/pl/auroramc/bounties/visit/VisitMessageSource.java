package pl.auroramc.bounties.visit;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class VisitMessageSource extends OkaeriConfig {

  public MutableMessage visitDailySummary =
      MutableMessage.of("<gray>Wykaz sesji z dnia <white><timeframe.minimum><gray>:");

  public MutableMessage visitRangeSummary =
      MutableMessage.of(
          "<gray>Wykaz sesji z okresu <white><timeframe.minimum> <gray>- <white><timeframe.maximum><gray>:");

  public MutableMessage visitEntry =
      MutableMessage.of(
          "<dark_gray>► (<white><visit.@startTime> <dark_gray>- <white><visit.@ditchTime><dark_gray>) <dark_gray>- <gray><visit.@duration>");

  public MutableMessage noVisits = MutableMessage.of("<gray>Brak sesji w podanym okresie.");
}
