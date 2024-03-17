package pl.auroramc.bazaars.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.commons.message.MutableMessage;

public class MessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public MutableMessage invalidMerchant = MutableMessage.of(
      "<red>Nie możesz stworzyć tabliczki dla innego gracza, upewnij się, czy wpisałeś swoją nazwę poprawnie."
  );

  public MutableMessage invalidQuantity = MutableMessage.of(
      "<red>Wskazana przez ciebie ilość jest nieprawidłowa."
  );

  public MutableMessage invalidPrice = MutableMessage.of(
      "<red>Wskazana przez ciebie cena jest nieprawidłowa."
  );

  public MutableMessage bazaarOutOfStock = MutableMessage.of(
      "<red>Bazar, z którego próbujesz zakupić przedmioty nie posiada ich wystarczająco na stanie."
  );

  public MutableMessage bazaarOutOfSpace = MutableMessage.of(
      "<red>Bazar, do którego próbujesz sprzedać przedmioty nie posiada wystarczająco miejsca."
  );

  public MutableMessage customerOutOfSpace = MutableMessage.of(
      "<red>Nie posiadasz wystarczająco miejsca w ekwipunku, aby to zakupić."
  );

  public MutableMessage customerOutOfProduct = MutableMessage.of(
      "<red>Nie posiadasz wystarczająco przedmiotów, aby to sprzedać."
  );

  public MutableMessage customerOutOfBalance = MutableMessage.of(
      "<red>Nie posiadasz wystarczającej ilości gotówki na zakup tego przedmiotu."
  );

  public MutableMessage merchantOutOfBalance = MutableMessage.of(
      "<red>Właściciel bazaru, do którego próbujesz sprzedać przedmioty nie posiada wystarczająco gotówki."
  );

  public MutableMessage productBought = MutableMessage.of(
      "<gray>Zakupiłeś <white>{product} <gray>od <white>{merchant} <gray>za <white>{currency}{price}<gray>, które zostały pobrane z twojego konta."
  );

  public MutableMessage productSold = MutableMessage.of(
      "<gray>Sprzedałeś <white>{product} <gray>od <white>{merchant} <gray>za <white>{currency}{price}<gray>, które zostały dodane do twojego konta."
  );
}
