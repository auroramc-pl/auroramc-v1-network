package pl.auroramc.registry;

import static java.util.Locale.ENGLISH;
import static moe.rafal.juliet.datasource.hikari.HikariPooledDataSourceFactory.getHikariDataSource;
import static pl.auroramc.commons.bukkit.BukkitUtils.registerFacades;
import static pl.auroramc.commons.bukkit.BukkitUtils.registerListeners;
import static pl.auroramc.commons.bukkit.BukkitUtils.registerServices;
import static pl.auroramc.integrations.configs.juliet.JulietConfig.JULIET_CONFIG_FILE_NAME;
import static pl.auroramc.messages.i18n.BukkitMessageFacade.getBukkitMessageFacade;
import static pl.auroramc.messages.i18n.Message.message;
import static pl.auroramc.messages.viewer.BukkitViewerFacade.getBukkitViewerFacade;
import static pl.auroramc.registry.observer.ObserverFacadeFactory.getObserverFacade;
import static pl.auroramc.registry.provider.ProviderFacadeFactory.getProviderFacade;
import static pl.auroramc.registry.resource.key.ResourceKeyFacadeFactory.getResourceKeyFacade;
import static pl.auroramc.registry.settings.SettingsFacadeFactory.getSettingsFacade;
import static pl.auroramc.registry.user.UserFacadeFactory.getUserFacade;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.adventure.LiteAdventureExtension;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.util.Locale;
import java.util.Set;
import moe.rafal.juliet.Juliet;
import moe.rafal.juliet.JulietBuilder;
import org.bukkit.command.CommandSender;
import pl.auroramc.commons.bukkit.event.BukkitEventPublisher;
import pl.auroramc.integrations.IntegrationsBukkitPlugin;
import pl.auroramc.integrations.commands.BukkitCommandsBuilderProcessor;
import pl.auroramc.integrations.configs.juliet.JulietConfig;
import pl.auroramc.integrations.configs.serdes.juliet.SerdesJuliet;
import pl.auroramc.messages.i18n.BukkitMessageFacade;
import pl.auroramc.messages.viewer.BukkitViewerFacade;
import pl.auroramc.registry.locale.LocaleArgumentResolver;
import pl.auroramc.registry.locale.LocaleCommand;
import pl.auroramc.registry.message.RegistryMessageSource;
import pl.auroramc.registry.observer.ObserverController;
import pl.auroramc.registry.observer.ObserverFacade;
import pl.auroramc.registry.provider.ProviderFacade;
import pl.auroramc.registry.resource.key.ResourceKeyFacade;
import pl.auroramc.registry.settings.SettingsController;
import pl.auroramc.registry.settings.SettingsFacade;
import pl.auroramc.registry.settings.SettingsListener;
import pl.auroramc.registry.user.UserFacade;
import pl.auroramc.registry.user.UserListener;

public class RegistryBukkitPlugin extends IntegrationsBukkitPlugin {

  private static final String REGISTRY_BUNDLE_NAME = "registry_";
  private Juliet juliet;
  private LiteCommands<CommandSender> commands;

  @Override
  public void onStartup() {
    final BukkitViewerFacade viewerFacade = getBukkitViewerFacade(getServer());
    final BukkitMessageFacade messageFacade =
        getBukkitMessageFacade(YamlBukkitConfigurer::new, ENGLISH);
    final RegistryMessageSource registryMessageSource =
        registerMessageSource(messageFacade, RegistryMessageSource.class, REGISTRY_BUNDLE_NAME);
    registerServices(this, Set.of(registerIntegrationsMessageSource(messageFacade)));

    final JulietConfig julietConfig =
        produceConfig(JulietConfig.class, JULIET_CONFIG_FILE_NAME, new SerdesJuliet());
    juliet =
        JulietBuilder.newBuilder().withDataSource(getHikariDataSource(julietConfig.hikari)).build();

    final UserFacade userFacade = getUserFacade(getScheduler(), juliet);
    registerListeners(this, new UserListener(userFacade));

    final SettingsFacade settingsFacade = getSettingsFacade(getScheduler(), juliet);
    final SettingsController settingsController =
        new SettingsController(userFacade, viewerFacade, settingsFacade);
    registerListeners(this, new SettingsListener(viewerFacade, settingsController));

    final ProviderFacade providerFacade = getProviderFacade(juliet);
    final ObserverFacade observerFacade = getObserverFacade(getScheduler(), juliet);
    final ObserverController observerController =
        new ObserverController(userFacade, providerFacade, observerFacade);
    registerServices(this, Set.of(observerController));

    final ResourceKeyFacade resourceKeyFacade = getResourceKeyFacade(juliet);
    registerFacades(
        this,
        Set.of(
            viewerFacade,
            messageFacade,
            settingsFacade,
            providerFacade,
            observerFacade,
            resourceKeyFacade,
            userFacade));

    final BukkitEventPublisher eventPublisher = new BukkitEventPublisher(getScheduler());
    commands =
        LiteBukkitFactory.builder(getName(), this)
            .extension(new LiteAdventureExtension<>(), configurer -> configurer.miniMessage(true))
            .argument(
                Locale.class,
                new LocaleArgumentResolver<>(
                    messageFacade.getAvailableLocales(),
                    message(registryMessageSource.localeNotSupported)))
            .commands(
                LiteCommandsAnnotations.of(
                    new RegistryCommand(getIntegrationsMessageSource(), messageFacade),
                    new LocaleCommand(
                        registryMessageSource, eventPublisher, settingsFacade, settingsController)))
            .selfProcessor(
                new BukkitCommandsBuilderProcessor(
                    getIntegrationsMessageSource(),
                    messageFacade,
                    getMessageCompiler(),
                    viewerFacade))
            .build();
  }

  @Override
  public void onDisable() {
    juliet.close();
    commands.unregister();
  }
}
