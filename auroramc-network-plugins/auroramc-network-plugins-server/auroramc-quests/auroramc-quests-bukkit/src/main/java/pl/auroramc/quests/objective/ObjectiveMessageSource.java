package pl.auroramc.quests.objective;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

/**
 * Fields in that message source are dynamically attached to the {@link Objective} instances.
 *
 * @see Objective#getMessage()
 * @see Objective#setMessage(MutableMessage)
 */
public class ObjectiveMessageSource extends OkaeriConfig {

  public MutableMessage breakBlockObjective =
      MutableMessage.of(
          "<#7c5058>► <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Zniszcz <#f4a9ba><objective.@type> <#7c5058>(<#f4a9ba><progress.@data><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>/<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b><progress.@goal><#7c5058>)");

  public MutableMessage placeBlockObjective =
      MutableMessage.of(
          "<#7c5058>► <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Postaw <#f4a9ba><objective.@type> <#7c5058>(<#f4a9ba><progress.@data><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>/<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b><progress.@goal><#7c5058>)");

  public MutableMessage distanceObjective =
      MutableMessage.of(
          "<#7c5058>► <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Przemierz dystans <#7c5058>(<#f4a9ba><progress.@data><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>/<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b><progress.@goal><#7c5058>)");
}
