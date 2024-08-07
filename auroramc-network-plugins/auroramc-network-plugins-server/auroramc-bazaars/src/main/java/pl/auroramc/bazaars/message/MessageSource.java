package pl.auroramc.bazaars.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.messages.message.MutableMessage;

public class MessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public MutableMessage invalidMerchant =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie możesz stworzyć tabliczki dla innego gracza, upewnij się, czy wpisałeś swoją nazwę poprawnie.");

  public MutableMessage invalidQuantity =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Wskazana przez ciebie ilość jest nieprawidłowa.");

  public MutableMessage invalidPrice =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Wskazana przez ciebie cena jest nieprawidłowa.");

  public MutableMessage bazaarSelfInteraction =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie możesz korzystać z własnego bazaru.");

  public MutableMessage bazaarOutOfStock =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Bazar, z którego próbujesz zakupić przedmioty nie posiada ich wystarczająco na stanie.");

  public MutableMessage bazaarOutOfSpace =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Bazar, do którego próbujesz sprzedać przedmioty nie posiada wystarczająco miejsca.");

  public MutableMessage customerOutOfSpace =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie posiadasz wystarczająco miejsca w ekwipunku, aby to zakupić.");

  public MutableMessage customerOutOfProduct =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie posiadasz wystarczająco przedmiotów, aby to sprzedać.");

  public MutableMessage customerOutOfBalance =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie posiadasz wystarczającej ilości gotówki na zakup tego przedmiotu.");

  public MutableMessage merchantOutOfBalance =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Właściciel bazaru, do którego próbujesz sprzedać przedmioty nie posiada wystarczająco gotówki.");

  public MutableMessage productBought =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Zakupiłeś <#f4a9ba><product> <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>od <#f4a9ba><context.merchant> <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>za <#f4a9ba><currency.@symbol><context.price><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>, które zostały pobrane z twojego konta.");

  public MutableMessage productSold =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Sprzedałeś <#f4a9ba><product> <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>od <#f4a9ba><context.merchant> <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>za <#f4a9ba><currency.@symbol><context.price><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>, które zostały dodane do twojego konta.");
}
