package pl.auroramc.hoppers.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.integrations.configs.command.CommandMessageSource;
import pl.auroramc.messages.message.MutableMessage;

public class MessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public MutableMessage hopperHasBeenGiven =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Do twojego ekwipunku został dodany lejek przenoszący <#f4a9ba><quantity> <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>przedmiotów.");

  public MutableMessage hopperDisplayName =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Hopper <#7c5058>(<#f4a9ba><quantity><#7c5058>)");

  public CommandMessageSource command = new CommandMessageSource();
}
