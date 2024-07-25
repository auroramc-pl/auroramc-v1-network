package pl.auroramc.bounties.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.bounties.bounty.BountyMessageSource;
import pl.auroramc.bounties.visit.VisitMessageSource;
import pl.auroramc.integrations.configs.command.CommandMessageSource;
import pl.auroramc.messages.message.MutableMessage;

public class MessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public MutableMessage belowName =
      MutableMessage.of("<#7c5058>[ <gradient:white:gray><duration> <#7c5058>]");

  public VisitMessageSource visit = new VisitMessageSource();

  public BountyMessageSource bounty = new BountyMessageSource();

  public CommandMessageSource command = new CommandMessageSource();
}
