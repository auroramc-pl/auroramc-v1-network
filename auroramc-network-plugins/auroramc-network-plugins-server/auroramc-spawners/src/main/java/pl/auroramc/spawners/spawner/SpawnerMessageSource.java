package pl.auroramc.spawners.spawner;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class SpawnerMessageSource extends OkaeriConfig {

  public MutableMessage spawnerPurchased =
      MutableMessage.of(
          "<gray>Zakupiłeś zmianę typu spawnera na <white>{spawner.creatureType} <gray>za <white>{currency.@symbol}{spawner.price}<gray>, które zostały pobrane z twojego konta.");

  public MutableMessage spawnerPurchaseTag =
      MutableMessage.of("<gray>Cena zakupu: <white>{currency.@symbol}{spawner.price}");

  public MutableMessage spawnerPurchaseSuggestion =
      MutableMessage.of("<gray>Naciśnij <white>LPM <gray>aby zakupić ten przedmiot.");

  public MutableMessage spawnerCouldNotBePurchasedBecauseOfMissingMoney =
      MutableMessage.of(
          "<red>Nie posiadasz wystarczająco pieniędzy, aby zmienić typ spawnera na wybrany przez ciebie.");

  public MutableMessage spawnerCouldNotBePurchasedBecauseOfSameType =
      MutableMessage.of(
          "<red>Nie możesz zmienić spawnera na ten typ, ponieważ już go posiada.");

  public MutableMessage spawnerDisplayName =
      MutableMessage.of(
          "<white>{creatureType}Spawner");
}
