package pl.auroramc.shops.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.commons.message.MutableMessage;

public class MessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public MutableMessage availableSchematicsSuggestion = MutableMessage.of(
      "<red>Poprawne użycie: <yellow><newline>{schematics}"
  );

  public MutableMessage executionOfCommandIsNotPermitted = MutableMessage.of(
      "<red>Nie posiadasz wystarczających uprawnień aby użyć tej komendy."
  );

  public MutableMessage executionFromConsoleIsUnsupported = MutableMessage.of(
      "<red>Nie możesz użyć tej konsoli z poziomu konsoli."
  );

  public MutableMessage productBought = MutableMessage.of(
      "<gray>Zakupiłeś <white>x{quantity} {material} <gray>w zamian za <white>{currency}{amount}<gray>, które zostały pobrane z twojego konta."
  );

  public MutableMessage productCouldNotBeBoughtBecauseOfMissingMoney = MutableMessage.of(
      "<red>Nie posiadasz wystarczającej ilości środków na zakup tego przedmiotu."
  );

  public MutableMessage productCouldNotBeBoughtBecauseOfMissingSpace = MutableMessage.of(
      "<red>Nie posiadasz wystarczająco miejsca w ekwipunku, aby to zakupić."
  );

  public MutableMessage productSold = MutableMessage.of(
      "<gray>Sprzedałeś <white>x{quantity} {material} <gray>w zamian za <white>{currency}{amount}<gray>, które zostały przelane na twoje konto."
  );

  public MutableMessage productCouldNotBeSoldBecauseOfMissingStock = MutableMessage.of(
      "<red>Nie posiadasz wystarczającej ilości tego przedmiotu, aby go sprzedać."
  );

  public MutableMessage purchaseTag = MutableMessage.of(
      "<gray>Cena zakupu: <white>{currency}{price}"
  );

  public MutableMessage purchaseSuggestion = MutableMessage.of(
      "<gray>Naciśnij <white>LPM <gray>aby zakupić ten przedmiot."
  );

  public MutableMessage sellTag = MutableMessage.of(
      "<gray>Cena sprzedaży: <white>{currency}{price}"
  );

  public MutableMessage sellSuggestion = MutableMessage.of(
      "<gray>Naciśnij <white>PPM <gray>aby sprzedać ten przedmiot."
  );
}
