package pl.auroramc.shops;

import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.exists;
import static pl.auroramc.commons.bukkit.BukkitUtils.resolveService;
import static pl.auroramc.commons.bukkit.scheduler.BukkitSchedulerFactory.getBukkitScheduler;
import static pl.auroramc.messages.message.compiler.BukkitMessageCompiler.getBukkitMessageCompiler;
import static pl.auroramc.shops.ShopsConfig.SHOPS_CONFIG_FILE_NAME;
import static pl.auroramc.shops.message.MessageSource.MESSAGE_SOURCE_FILE_NAME;
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
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.commons.bukkit.integration.commands.BukkitCommandsBuilderProcessor;
import pl.auroramc.commons.integration.configs.ConfigFactory;
import pl.auroramc.commons.integration.configs.serdes.SerdesCommons;
import pl.auroramc.commons.integration.configs.serdes.message.SerdesMessages;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.economy.economy.EconomyFacade;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.shops.message.MessageSource;
import pl.auroramc.shops.product.ProductFacade;
import pl.auroramc.shops.shop.ShopCommand;
import pl.auroramc.shops.shop.ShopFacade;

public class ShopsBukkitPlugin extends JavaPlugin {

  private static final String SHOPS_DIRECTORY_NAME = "shops";
  private LiteCommands<CommandSender> commands;

  @Override
  public void onEnable() {
    final ConfigFactory configFactory =
        new ConfigFactory(getDataFolder().toPath(), YamlBukkitConfigurer::new);

    final ShopsConfig shopsConfig =
        configFactory.produceConfig(ShopsConfig.class, SHOPS_CONFIG_FILE_NAME, new SerdesCommons());
    final ShopFacade shopFacade = getShopFacade(getShopsDirectoryPath(), getClassLoader());

    final Scheduler scheduler = getBukkitScheduler(this);

    final MessageSource messageSource =
        configFactory.produceConfig(
            MessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessages());
    final BukkitMessageCompiler messageCompiler = getBukkitMessageCompiler(scheduler);

    final CurrencyFacade currencyFacade = resolveService(getServer(), CurrencyFacade.class);
    final Currency fundsCurrency = getFundsCurrency(currencyFacade, shopsConfig.fundsCurrencyId);

    final EconomyFacade economyFacade = resolveService(getServer(), EconomyFacade.class);
    final ProductFacade productFacade =
        getProductFacade(
            this, scheduler, messageSource.product, messageCompiler, fundsCurrency, economyFacade);

    commands =
        LiteBukkitFactory.builder(getName(), this)
            .extension(new LiteAdventureExtension<>(), configurer -> configurer.miniMessage(true))
            .commands(LiteCommandsAnnotations.of(new ShopCommand(this, shopFacade, productFacade)))
            .selfProcessor(
                new BukkitCommandsBuilderProcessor(messageSource.command, messageCompiler))
            .build();
  }

  @Override
  public void onDisable() {
    commands.unregister();
  }

  private Currency getFundsCurrency(
      final CurrencyFacade currencyFacade, final long fundsCurrencyId) {
    return Optional.ofNullable(currencyFacade.getCurrencyById(fundsCurrencyId))
        .orElseThrow(
            () ->
                new ShopsInstantiationException(
                    "Could not resolve funds currency, make sure that the currency's id is valid."));
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
              .formatted(shopsDirectoryPath.toString()),
          exception);
    }
  }
}
