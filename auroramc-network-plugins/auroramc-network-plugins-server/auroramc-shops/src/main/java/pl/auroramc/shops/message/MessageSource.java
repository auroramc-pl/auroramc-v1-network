package pl.auroramc.shops.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.integrations.configs.command.CommandMessageSource;
import pl.auroramc.shops.product.ProductMessageSource;

public class MessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public CommandMessageSource command = new CommandMessageSource();

  public ProductMessageSource product = new ProductMessageSource();
}
