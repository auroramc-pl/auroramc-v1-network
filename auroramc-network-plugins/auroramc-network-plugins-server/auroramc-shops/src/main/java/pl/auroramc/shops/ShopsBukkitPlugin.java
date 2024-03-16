package pl.auroramc.shops;

import static dev.rollczi.litecommands.bukkit.LiteBukkitMessages.PLAYER_ONLY;
import static dev.rollczi.litecommands.message.LiteMessages.INVALID_USAGE;
import static dev.rollczi.litecommands.message.LiteMessages.MISSING_PERMISSIONS;
import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.exists;
import static pl.auroramc.commons.BukkitUtils.resolveService;
import static pl.auroramc.shops.ShopsConfig.SHOPS_CONFIG_FILE_NAME;
import static pl.auroramc.shops.message.MessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.shops.message.MessageVariableKey.SCHEMATICS_VARIABLE_KEY;
import static pl.auroramc.shops.product.ProductFacade.getProductFacade;
import static pl.auroramc.shops.shop.ShopFacade.getShopFacade;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.adventure.LiteAdventureExtension;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.serdes.SerdesCommons;
import pl.auroramc.commons.config.serdes.message.SerdesMessageSource;
import pl.auroramc.commons.integration.litecommands.MutableMessageResultHandler;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.shops.message.MessageSource;
import pl.auroramc.shops.product.ProductFacade;
import pl.auroramc.shops.shop.ShopCommand;
import pl.auroramc.shops.shop.ShopFacade;

public class ShopsBukkitPlugin extends JavaPlugin {

  private static final String SHOPS_DIRECTORY_NAME = "shops";
  private LiteCommands<CommandSender> commands;

  @Override
  public void onEnable() {
    final ConfigFactory configFactory = new ConfigFactory(
        getDataFolder().toPath(), YamlBukkitConfigurer::new
    );

    final ShopsConfig shopsConfig = configFactory.produceConfig(
        ShopsConfig.class, SHOPS_CONFIG_FILE_NAME, new SerdesCommons()
    );
    final ShopFacade shopFacade = getShopFacade(getShopsDirectoryPath(), getClassLoader());

    final MessageSource messageSource = configFactory.produceConfig(
        MessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessageSource()
    );

    final Logger logger = getLogger();

    final CurrencyFacade currencyFacade = resolveService(getServer(), CurrencyFacade.class);
    final Currency fundsCurrency =
        Optional.ofNullable(currencyFacade.getCurrencyById(shopsConfig.fundsCurrencyId))
            .orElseThrow(() ->
                new ShopsInstantiationException(
                    "Could not resolve funds currency, make sure that the currency's id is valid."
                )
            );
    final EconomyFacade economyFacade = resolveService(getServer(), EconomyFacade.class);
    final ProductFacade productFacade = getProductFacade(
        this, logger, messageSource, fundsCurrency, economyFacade, shopsConfig.priceFormat
    );

    commands = LiteBukkitFactory.builder(getName(), this)
        .extension(new LiteAdventureExtension<>(),
            configurer -> configurer.miniMessage(true)
        )
        .message(INVALID_USAGE,
            context -> messageSource.availableSchematicsSuggestion
                .with(SCHEMATICS_VARIABLE_KEY, context.getSchematic().join("<newline>"))
        )
        .message(MISSING_PERMISSIONS, messageSource.executionOfCommandIsNotPermitted)
        .message(PLAYER_ONLY, messageSource.executionFromConsoleIsUnsupported)
        .commands(
            LiteCommandsAnnotations.of(
                new ShopCommand(
                    this, shopFacade, productFacade
                )
            )
        )
        .result(MutableMessage.class, new MutableMessageResultHandler<>())
        .build();
  }

  @Override
  public void onDisable() {
    commands.unregister();
  }

  private Path getShopsDirectoryPath() {
    final Path shopsDirectoryPath = getDataFolder().toPath().resolve(SHOPS_DIRECTORY_NAME);
    if (exists(shopsDirectoryPath)) {
      return shopsDirectoryPath;
    }

    try {
      return createDirectory(shopsDirectoryPath);
    } catch (final IOException exception) {
      throw new ShopsInstantiationException(
          "Could not create shops directory in %s path, because of unexpected exception."
              .formatted(
                  shopsDirectoryPath.toString()
              ),
          exception
      );
    }
  }
}
