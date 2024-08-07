package pl.auroramc.lobby.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.integrations.configs.command.CommandMessageSource;
import pl.auroramc.messages.message.MutableMessage;

public class MessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public MutableMessage teleportedIntoSpawn =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Zostałeś przeteleportowany na spawn.");

  public MutableMessage lobbyClarification =
      MutableMessage.of(
          """
          <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Znajdujesz się w lobby, które przeznaczone jest do uwierzytelnienia,
          po poprawnym zakończeniu tego procesu, zostaniesz przeniesiony na serwer docelowy.
          Powodzenia!
          """);

  public MutableMessage teleportedFromVoid =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Zostałeś przeteleportowany na spawn, z powodu twojej obecności w otchłani.");

  public CommandMessageSource command = new CommandMessageSource();
}
