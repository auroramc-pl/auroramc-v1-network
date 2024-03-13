package pl.auroramc.shops;

import static java.lang.String.join;
import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.exists;
import static pl.auroramc.commons.BukkitUtils.resolveService;
import static pl.auroramc.shops.ShopsConfig.SHOPS_CONFIG_FILE_NAME;
import static pl.auroramc.shops.message.MessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.shops.product.ProductFacade.getProductFacade;
import static pl.auroramc.shops.shop.ShopFacade.getShopFacade;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.adventure.paper.LitePaperAdventureFactory;
import dev.rollczi.litecommands.bukkit.tools.BukkitOnlyPlayerContextual;
import dev.rollczi.litecommands.command.permission.RequiredPermissions;
import dev.rollczi.litecommands.schematic.Schematic;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.serdes.SerdesCommons;
import pl.auroramc.commons.config.serdes.message.SerdesMessageSource;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.commons.integration.litecommands.v2.MutableMessageResultHandler;
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

    commands = LitePaperAdventureFactory.builder(getServer(), getName())
        .contextualBind(Player.class,
            new BukkitOnlyPlayerContextual<>(
                messageSource.executionFromConsoleIsUnsupported
            )
        )
        .commandInstance(new ShopCommand(this, shopFacade, productFacade))
        .redirectResult(RequiredPermissions.class, MutableMessage.class,
            context -> messageSource.executionOfCommandIsNotPermitted
        )
        .redirectResult(Schematic.class, MutableMessage.class,
            context -> messageSource.availableSchematicsSuggestion
                .with("schematics", join("<newline>", context.getSchematics()))
        )
        .resultHandler(MutableMessage.class, new MutableMessageResultHandler())
        .register();
  }

  @Override
  public void onDisable() {
    commands.getPlatform().unregisterAll();
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
