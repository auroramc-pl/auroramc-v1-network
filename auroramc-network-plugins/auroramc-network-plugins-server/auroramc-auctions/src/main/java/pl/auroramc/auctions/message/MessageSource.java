package pl.auroramc.auctions.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.auctions.auction.AuctionMessageSource;
import pl.auroramc.auctions.vault.VaultMessageSource;
import pl.auroramc.integrations.configs.command.CommandMessageSource;

public class MessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public VaultMessageSource vault = new VaultMessageSource();

  public AuctionMessageSource auction = new AuctionMessageSource();

  public CommandMessageSource command = new CommandMessageSource();
}
