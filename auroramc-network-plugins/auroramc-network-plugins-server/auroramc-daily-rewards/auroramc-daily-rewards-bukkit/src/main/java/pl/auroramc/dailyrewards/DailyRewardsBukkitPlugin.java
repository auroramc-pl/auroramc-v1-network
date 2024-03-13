package pl.auroramc.dailyrewards;

import static java.lang.String.join;
import static java.time.Duration.ofSeconds;
import static moe.rafal.juliet.datasource.HikariPooledDataSourceFactory.produceHikariDataSource;
import static pl.auroramc.commons.BukkitUtils.getTicksOf;
import static pl.auroramc.commons.BukkitUtils.registerListeners;
import static pl.auroramc.commons.BukkitUtils.resolveService;
import static pl.auroramc.commons.config.serdes.juliet.JulietConfig.JULIET_CONFIG_FILE_NAME;
import static pl.auroramc.commons.duration.DurationFormatterStyle.DEFAULT;
import static pl.auroramc.commons.duration.DurationFormatterStyle.SHORTLY;
import static pl.auroramc.commons.plural.Pluralizers.getPluralizer;
import static pl.auroramc.dailyrewards.DailyRewardsConfig.PLUGIN_CONFIG_FILE_NAME;
import static pl.auroramc.dailyrewards.message.MessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.dailyrewards.visit.VisitFacadeFactory.getVisitFacade;
import static pl.auroramc.nametag.NametagFacade.getNametagFacade;
import static pl.auroramc.nametag.context.NametagContextFacade.getNametagContextFacade;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.adventure.paper.LitePaperAdventureFactory;
import dev.rollczi.litecommands.bukkit.tools.BukkitOnlyPlayerContextual;
import dev.rollczi.litecommands.bukkit.tools.BukkitPlayerArgument;
import dev.rollczi.litecommands.command.permission.RequiredPermissions;
import dev.rollczi.litecommands.schematic.Schematic;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.util.Locale;
import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;
import moe.rafal.juliet.JulietBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.serdes.SerdesCommons;
import pl.auroramc.commons.config.serdes.juliet.JulietConfig;
import pl.auroramc.commons.config.serdes.juliet.SerdesJuliet;
import pl.auroramc.commons.config.serdes.message.SerdesMessageSource;
import pl.auroramc.commons.duration.DurationFormatter;
import pl.auroramc.commons.integration.litecommands.v2.MutableMessageResultHandler;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.dailyrewards.message.MessageSource;
import pl.auroramc.dailyrewards.visit.VisitCommand;
import pl.auroramc.dailyrewards.visit.VisitController;
import pl.auroramc.dailyrewards.visit.VisitFacade;
import pl.auroramc.dailyrewards.visit.VisitListener;
import pl.auroramc.dailyrewards.nametag.NametagListener;
import pl.auroramc.dailyrewards.nametag.NametagUpdateScheduler;
import pl.auroramc.nametag.NametagFacade;
import pl.auroramc.registry.user.UserFacade;

public class DailyRewardsBukkitPlugin extends JavaPlugin {

  private static final Locale POLISH = new Locale("pl");
  private LiteCommands<CommandSender> commands;

  @Override
  public void onEnable() {
    final ConfigFactory configFactory = new ConfigFactory(getDataFolder().toPath(),
        YamlBukkitConfigurer::new);

    final DailyRewardsConfig dailyRewardsConfig = configFactory.produceConfig(
        DailyRewardsConfig.class, PLUGIN_CONFIG_FILE_NAME, new SerdesCommons()
    );
    final DurationFormatter durationFormatter = new DurationFormatter(
        getPluralizer(POLISH), DEFAULT
    );
    final DurationFormatter durationFormatterShortly = new DurationFormatter(
        getPluralizer(POLISH), SHORTLY
    );

    final MessageSource messageSource = configFactory.produceConfig(
        MessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessageSource()
    );

    final Logger logger = getLogger();

    final JulietConfig julietConfig = configFactory.produceConfig(
        JulietConfig.class, JULIET_CONFIG_FILE_NAME, new SerdesJuliet()
    );
    final Juliet juliet = JulietBuilder.newBuilder()
        .withDataSource(produceHikariDataSource(julietConfig.hikari))
        .build();

    final NametagFacade nametagFacade = getNametagFacade(getNametagContextFacade());

    final UserFacade userFacade = resolveService(getServer(), UserFacade.class);
    final VisitFacade visitFacade = getVisitFacade(logger, juliet);
    final VisitController visitController = new VisitController();

    registerListeners(this,
        new VisitListener(logger, userFacade, visitFacade, visitController, dailyRewardsConfig),
        new NametagListener(nametagFacade)
    );

    getServer().getScheduler().runTaskTimer(
        this,
        new NametagUpdateScheduler(
            messageSource,
            nametagFacade,
            visitController,
            durationFormatterShortly
        ),
        getTicksOf(ofSeconds(1)),
        getTicksOf(ofSeconds(1))
    );

    commands = LitePaperAdventureFactory.builder(getServer(), getName())
        .contextualBind(Player.class,
            new BukkitOnlyPlayerContextual<>(
                messageSource.executionFromConsoleIsUnsupported
            )
        )
        .commandInstance(
            new VisitCommand(messageSource, userFacade, visitFacade, durationFormatter)
        )
        .argument(Player.class,
            new BukkitPlayerArgument<>(
                getServer(), messageSource.specifiedPlayerIsUnknown
            )
        )
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
}
