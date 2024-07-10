package pl.auroramc.shops.product;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class ProductMessageSource extends OkaeriConfig {

  public MutableMessage productBought =
      MutableMessage.of(
          "<gray>Zakupiłeś <white><context.product> <gray>w zamian za <white><context.currency.@symbol><context.price><gray>, które zostały pobrane z twojego konta.");

  public MutableMessage productCouldNotBeBoughtBecauseOfMissingMoney =
      MutableMessage.of(
          "<red>Nie posiadasz wystarczającej ilości środków na zakup tego przedmiotu.");

  public MutableMessage productCouldNotBeBoughtBecauseOfMissingSpace =
      MutableMessage.of("<red>Nie posiadasz wystarczająco miejsca w ekwipunku, aby to zakupić.");

  public MutableMessage productSold =
      MutableMessage.of(
          "<gray>Sprzedałeś <white><context.product> <gray>w zamian za <white><context.currency.@symbol><context.price><gray>, które zostały przelane na twoje konto.");

  public MutableMessage productCouldNotBeSoldBecauseOfMissingStock =
      MutableMessage.of(
          "<red>Nie posiadasz wystarczającej ilości tego przedmiotu, aby go sprzedać.");

  public MutableMessage purchaseTag =
      MutableMessage.of("<gray>Cena zakupu: <white><context.currency.@symbol><context.price>");

  public MutableMessage purchaseSuggestion =
      MutableMessage.of("<gray>Naciśnij <white>LPM <gray>aby zakupić ten przedmiot.");

  public MutableMessage sellTag =
      MutableMessage.of("<gray>Cena sprzedaży: <white><context.currency.@symbol><context.price>");

  public MutableMessage sellSuggestion =
      MutableMessage.of("<gray>Naciśnij <white>PPM <gray>aby sprzedać ten przedmiot.");
}
