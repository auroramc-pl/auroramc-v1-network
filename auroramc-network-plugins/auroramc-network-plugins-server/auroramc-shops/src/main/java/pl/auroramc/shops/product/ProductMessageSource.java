package pl.auroramc.shops.product;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class ProductMessageSource extends OkaeriConfig {

  public MutableMessage productBought =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Zakupiłeś <#f4a9ba><context.product> <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>w zamian za <#f4a9ba><context.currency.@symbol><context.price><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>, które zostały pobrane z twojego konta.");

  public MutableMessage productCouldNotBeBoughtBecauseOfMissingMoney =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie posiadasz wystarczającej ilości środków na zakup tego przedmiotu.");

  public MutableMessage productCouldNotBeBoughtBecauseOfMissingSpace =
      MutableMessage.of("<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie posiadasz wystarczająco miejsca w ekwipunku, aby to zakupić.");

  public MutableMessage productSold =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Sprzedałeś <#f4a9ba><context.product> <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>w zamian za <#f4a9ba><context.currency.@symbol><context.price><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>, które zostały przelane na twoje konto.");

  public MutableMessage productCouldNotBeSoldBecauseOfMissingStock =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie posiadasz wystarczającej ilości tego przedmiotu, aby go sprzedać.");

  public MutableMessage purchaseTag =
      MutableMessage.of("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Cena zakupu: <#f4a9ba><context.currency.@symbol><context.price>");

  public MutableMessage purchaseSuggestion =
      MutableMessage.of("<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>Naciśnij <gradient:#f7cf78:#fbd06a:#f4b352:#fbd06a:#f7cf78><bold>LPM</bold><gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>, aby zakupić ten przedmiot.");

  public MutableMessage sellTag =
      MutableMessage.of("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Cena sprzedaży: <#f4a9ba><context.currency.@symbol><context.price>");

  public MutableMessage sellSuggestion =
      MutableMessage.of("<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>Naciśnij <gradient:#f7cf78:#fbd06a:#f4b352:#fbd06a:#f7cf78><bold>PPM</bold><gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>, aby sprzedać ten przedmiot.");
}
