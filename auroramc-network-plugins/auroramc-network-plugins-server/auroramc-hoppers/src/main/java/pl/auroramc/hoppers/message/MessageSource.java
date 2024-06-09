package pl.auroramc.hoppers.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.integrations.configs.command.CommandMessageSource;
import pl.auroramc.messages.message.MutableMessage;

public class MessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public MutableMessage hopperHasBeenGiven =
      MutableMessage.of(
          "<gray>Do twojego ekwipunku został dodany lejek przenoszący <white><quantity> <gray>przedmiotów.");

  public MutableMessage hopperDisplayName =
      MutableMessage.of("<gray>Hopper <dark_gray>(<white><quantity><dark_gray>)");

  public CommandMessageSource command = new CommandMessageSource();
}
