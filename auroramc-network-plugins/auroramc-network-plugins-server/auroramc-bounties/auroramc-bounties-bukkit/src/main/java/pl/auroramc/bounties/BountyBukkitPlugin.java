package pl.auroramc.bounties;

import static moe.rafal.juliet.datasource.hikari.HikariPooledDataSourceFactory.getHikariDataSource;
import static pl.auroramc.bounties.BountyConfig.BOUNTY_CONFIG_FILE_NAME;
import static pl.auroramc.bounties.bounty.BountyFacadeFactory.getBountyFacade;
import static pl.auroramc.bounties.message.MessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.bounties.progress.BountyProgressFacadeFactory.getBountyProgressFacade;
import static pl.auroramc.bounties.visit.VisitFacadeFactory.getVisitFacade;
import static pl.auroramc.commons.bukkit.BukkitUtils.registerFacades;
import static pl.auroramc.commons.bukkit.BukkitUtils.registerListeners;
import static pl.auroramc.commons.bukkit.BukkitUtils.resolveService;
import static pl.auroramc.commons.bukkit.scheduler.BukkitSchedulerFactory.getBukkitScheduler;
import static pl.auroramc.commons.resource.ResourceUtils.unpackResources;
import static pl.auroramc.integrations.configs.juliet.JulietConfig.JULIET_CONFIG_FILE_NAME;
import static pl.auroramc.messages.message.compiler.BukkitMessageCompiler.getBukkitMessageCompiler;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.adventure.LiteAdventureExtension;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.nio.file.Path;
import java.util.Set;
import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;
import moe.rafal.juliet.JulietBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.bounties.bounty.BountiesViewRenderer;
import pl.auroramc.bounties.bounty.BountyCommand;
import pl.auroramc.bounties.bounty.BountyController;
import pl.auroramc.bounties.bounty.BountyFacade;
import pl.auroramc.bounties.bounty.BountyListener;
import pl.auroramc.bounties.message.MessageSource;
import pl.auroramc.bounties.progress.BountyProgressFacade;
import pl.auroramc.bounties.visit.VisitCommand;
import pl.auroramc.bounties.visit.VisitController;
import pl.auroramc.bounties.visit.VisitFacade;
import pl.auroramc.bounties.visit.VisitListener;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.integrations.commands.BukkitCommandsBuilderProcessor;
import pl.auroramc.integrations.configs.ConfigFactory;
import pl.auroramc.integrations.configs.juliet.JulietConfig;
import pl.auroramc.integrations.configs.serdes.SerdesCommons;
import pl.auroramc.integrations.configs.serdes.juliet.SerdesJuliet;
import pl.auroramc.integrations.configs.serdes.message.SerdesMessages;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.registry.resource.key.ResourceKeyFacade;
import pl.auroramc.registry.user.UserFacade;

public class BountyBukkitPlugin extends JavaPlugin {

  private static final String BOUNTIES_DIRECTORY_NAME = "bounties";
  private Juliet juliet;
  private LiteCommands<CommandSender> commands;

  @Override
  public void onEnable() {
    unpackResources(getFile(), getDataFolder(), Set.of(BOUNTIES_DIRECTORY_NAME), Set.of());

    final ConfigFactory configFactory =
        new ConfigFactory(getDataFolder().toPath(), YamlBukkitConfigurer::new);
    final BountyConfig bountyConfig =
        configFactory.produceConfig(
            BountyConfig.class, BOUNTY_CONFIG_FILE_NAME, new SerdesCommons());

    final ResourceKeyFacade resourceKeyFacade =
        resolveService(getServer(), ResourceKeyFacade.class);

    final Scheduler scheduler = getBukkitScheduler(this);

    final MessageSource messageSource =
        configFactory.produceConfig(
            MessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessages());
    final BukkitMessageCompiler messageCompiler = getBukkitMessageCompiler(scheduler);

    final JulietConfig julietConfig =
        configFactory.produceConfig(
            JulietConfig.class, JULIET_CONFIG_FILE_NAME, new SerdesJuliet());
    juliet =
        JulietBuilder.newBuilder().withDataSource(getHikariDataSource(julietConfig.hikari)).build();
    final Logger logger = getLogger();

    final UserFacade userFacade = resolveService(getServer(), UserFacade.class);
    final VisitFacade visitFacade = getVisitFacade(scheduler, juliet);
    final VisitController visitController = new VisitController();

    final BountyFacade bountyFacade = getBountyFacade(getClassLoader(), getBountiesDirectoryPath());
    resourceKeyFacade.validateResourceKeys(bountyFacade.getBounties());
    final BountyProgressFacade bountyProgressFacade = getBountyProgressFacade(scheduler, juliet);
    final BountyController bountyController =
        new BountyController(
            scheduler, bountyConfig, bountyProgressFacade, messageSource.bounty, messageCompiler);
    final BountiesViewRenderer bountiesViewRenderer =
        new BountiesViewRenderer(
            this,
            bountyConfig,
            bountyFacade,
            bountyController,
            messageSource.bounty,
            messageCompiler);

    registerFacades(this, Set.of(visitFacade, bountyProgressFacade));

    registerListeners(
        this,
        new VisitListener(logger, userFacade, visitFacade, visitController, bountyConfig),
        new BountyListener(userFacade, bountyProgressFacade));

    commands =
        LiteBukkitFactory.builder(getName(), this)
            .extension(new LiteAdventureExtension<>(), configurer -> configurer.miniMessage(true))
            .commands(
                LiteCommandsAnnotations.of(
                    new BountyCommand(
                        scheduler,
                        userFacade,
                        visitFacade,
                        visitController,
                        bountyProgressFacade,
                        bountiesViewRenderer),
                    new VisitCommand(
                        messageSource.visit, messageCompiler, userFacade, visitFacade)))
            .selfProcessor(
                new BukkitCommandsBuilderProcessor(messageSource.command, messageCompiler))
            .build();
  }

  @Override
  public void onDisable() {
    juliet.close();
    commands.unregister();
  }

  private Path getBountiesDirectoryPath() {
    return getDataFolder().toPath().resolve(BOUNTIES_DIRECTORY_NAME);
  }
}
