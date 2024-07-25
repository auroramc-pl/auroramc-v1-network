package pl.auroramc.spawners.spawner;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class SpawnerMessageSource extends OkaeriConfig {

  public MutableMessage spawnerPurchased =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Zakupiłeś zmianę typu spawnera na <#f4a9ba><spawner.creatureType> <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>za <#f4a9ba><currency.@symbol><spawner.price><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>, które zostały pobrane z twojego konta.");

  public MutableMessage spawnerPurchaseTag =
      MutableMessage.of("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Cena zakupu: <#f4a9ba><currency.@symbol><spawner.price>");

  public MutableMessage spawnerPurchaseSuggestion =
      MutableMessage.of("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Naciśnij <#f4a9ba>LPM <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>aby zakupić ten przedmiot.");

  public MutableMessage spawnerCouldNotBePurchasedBecauseOfMissingMoney =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie posiadasz wystarczająco pieniędzy, aby zmienić typ spawnera na wybrany przez ciebie.");

  public MutableMessage spawnerCouldNotBePurchasedBecauseOfSameType =
      MutableMessage.of("<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie możesz zmienić spawnera na ten typ, ponieważ już go posiada.");

  public MutableMessage spawnerDisplayName = MutableMessage.of("<#f4a9ba><creatureType>Spawner");
}
