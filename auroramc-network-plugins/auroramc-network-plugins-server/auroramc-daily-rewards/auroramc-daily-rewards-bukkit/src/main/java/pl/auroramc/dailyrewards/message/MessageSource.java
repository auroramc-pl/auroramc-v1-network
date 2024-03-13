package pl.auroramc.dailyrewards.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;

public class MessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public String belowName = "<dark_gray>[ <gradient:white:gray><visit_period> <dark_gray>]";

  public String visitDailySummary = "<gray>Wykaz sesji z dnia <white>%s<gray>:";

  public String visitRangeSummary = "<gray>Wykaz sesji z okresu <white>%s <gray>- <white>%s<gray>:";

  public String visitEntry = "<dark_gray>► (<white>%s <dark_gray>- <white>%s<dark_gray>) <dark_gray>- <gray>%s";

  public String noVisits = "<gray>Brak sesji w podanym okresie.";
}
