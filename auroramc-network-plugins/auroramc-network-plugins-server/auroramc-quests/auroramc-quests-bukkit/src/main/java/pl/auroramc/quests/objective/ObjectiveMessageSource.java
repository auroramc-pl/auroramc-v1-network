package pl.auroramc.quests.objective;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

/**
 * Fields in that message source are dynamically attached to the {@link Objective}
 * instances.
 * @see Objective#getMessage()
 * @see Objective#setMessage(MutableMessage)
 */
public class ObjectiveMessageSource extends OkaeriConfig {

  public MutableMessage breakBlockObjective =
      MutableMessage.of(
          "<dark_gray>► <gray>Zniszcz <white>{objective.@type} <dark_gray>(<white>{progress.@data}<gray>/<gray>{progress.@goal}<dark_gray>)");

  public MutableMessage placeBlockObjective =
      MutableMessage.of(
          "<dark_gray>► <gray>Postaw <white>{objective.@type} <dark_gray>(<white>{progress.@data}<gray>/<gray>{progress.@goal}<dark_gray>)");

  public MutableMessage distanceObjective =
      MutableMessage.of(
          "<dark_gray>► <gray>Przemierz dystans <dark_gray>(<white>{progress.@data}<gray>/<gray>{progress.@goal}<dark_gray>)");
}
