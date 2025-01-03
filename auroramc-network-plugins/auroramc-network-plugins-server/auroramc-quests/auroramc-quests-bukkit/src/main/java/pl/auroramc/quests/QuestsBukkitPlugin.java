package pl.auroramc.quests;

import static moe.rafal.juliet.datasource.hikari.HikariPooledDataSourceFactory.getHikariDataSource;
import static pl.auroramc.commons.bukkit.BukkitUtils.registerFacades;
import static pl.auroramc.commons.bukkit.BukkitUtils.registerListeners;
import static pl.auroramc.commons.bukkit.BukkitUtils.resolveService;
import static pl.auroramc.commons.bukkit.scheduler.BukkitSchedulerFactory.getBukkitScheduler;
import static pl.auroramc.commons.resource.ResourceUtils.unpackResources;
import static pl.auroramc.integrations.configs.juliet.JulietConfig.JULIET_CONFIG_FILE_NAME;
import static pl.auroramc.messages.message.compiler.BukkitMessageCompiler.getBukkitMessageCompiler;
import static pl.auroramc.quests.QuestsBukkitPluginUtils.initTranslationForObjectivesFromQuests;
import static pl.auroramc.quests.integration.placeholderapi.PlaceholderApiIntegrationFactory.getPlaceholderApiIntegration;
import static pl.auroramc.quests.message.MessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.quests.objective.progress.ObjectiveProgressFacadeFactory.getObjectiveProgressFacade;
import static pl.auroramc.quests.quest.QuestFacadeFactory.getQuestFacade;
import static pl.auroramc.quests.quest.QuestIndexFactory.getQuestIndex;
import static pl.auroramc.quests.quest.observer.QuestObserverFacadeFactory.getQuestObserverFacade;
import static pl.auroramc.quests.quest.track.QuestTrackFacadeFactory.getQuestTrackFacade;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.adventure.LiteAdventureExtension;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;
import moe.rafal.juliet.JulietBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.commons.bukkit.event.BukkitEventPublisher;
import pl.auroramc.commons.bukkit.integration.ExternalIntegration;
import pl.auroramc.commons.bukkit.integration.ExternalIntegrator;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.integrations.commands.BukkitCommandsBuilderProcessor;
import pl.auroramc.integrations.configs.ConfigFactory;
import pl.auroramc.integrations.configs.juliet.JulietConfig;
import pl.auroramc.integrations.configs.serdes.juliet.SerdesJuliet;
import pl.auroramc.integrations.configs.serdes.message.SerdesMessages;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.quests.message.MessageSource;
import pl.auroramc.quests.objective.ObjectiveController;
import pl.auroramc.quests.objective.progress.ObjectiveProgressController;
import pl.auroramc.quests.objective.progress.ObjectiveProgressFacade;
import pl.auroramc.quests.objectives.block.BreakBlockObjectiveHandler;
import pl.auroramc.quests.objectives.block.PlaceBlockObjectiveHandler;
import pl.auroramc.quests.objectives.travel.DistanceObjective;
import pl.auroramc.quests.objectives.travel.DistanceObjectiveHandler;
import pl.auroramc.quests.quest.Quest;
import pl.auroramc.quests.quest.QuestArgumentResolver;
import pl.auroramc.quests.quest.QuestController;
import pl.auroramc.quests.quest.QuestFacade;
import pl.auroramc.quests.quest.QuestIndex;
import pl.auroramc.quests.quest.QuestsCommand;
import pl.auroramc.quests.quest.QuestsView;
import pl.auroramc.quests.quest.observer.QuestObserverFacade;
import pl.auroramc.quests.quest.track.QuestTrackController;
import pl.auroramc.quests.quest.track.QuestTrackFacade;
import pl.auroramc.registry.resource.key.ResourceKeyFacade;
import pl.auroramc.registry.resource.provider.ResourceProvider;
import pl.auroramc.registry.resource.provider.ResourceProviderFacade;
import pl.auroramc.registry.user.UserFacade;

public class QuestsBukkitPlugin extends JavaPlugin {

  private static final String QUESTS_DIRECTORY_NAME = "quests";
  private Juliet juliet;
  private LiteCommands<CommandSender> commands;

  @Override
  public void onEnable() {
    unpackResources(getFile(), getDataFolder(), Set.of(QUESTS_DIRECTORY_NAME), Set.of());

    final ConfigFactory configFactory =
        new ConfigFactory(getDataFolder().toPath(), YamlBukkitConfigurer::new);

    final ResourceProviderFacade resourceProviderFacade =
        resolveService(getServer(), ResourceProviderFacade.class);
    final ResourceProvider resourceProvider =
        resourceProviderFacade.resolveResourceProviderByName(getName());
    final ResourceKeyFacade resourceKeyFacade =
        resolveService(getServer(), ResourceKeyFacade.class);

    final Scheduler scheduler = getBukkitScheduler(this);

    final MessageSource messageSource =
        configFactory.produceConfig(
            MessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessages());
    final BukkitMessageCompiler messageCompiler = getBukkitMessageCompiler(scheduler);

    final Logger logger = getLogger();

    final JulietConfig julietConfig =
        configFactory.produceConfig(
            JulietConfig.class, JULIET_CONFIG_FILE_NAME, new SerdesJuliet());
    juliet =
        JulietBuilder.newBuilder().withDataSource(getHikariDataSource(julietConfig.hikari)).build();

    final QuestIndex questIndex = getQuestIndex();
    final QuestFacade questFacade = getQuestFacade(getQuestsDirectoryPath(), getClassLoader());
    final List<Quest> quests = questFacade.getQuests();
    resourceKeyFacade.validateResourceKeys(resourceProvider, quests);
    initTranslationForObjectivesFromQuests(messageSource, quests);

    questIndex.indexQuests(quests);

    final BukkitEventPublisher eventPublisher = new BukkitEventPublisher(scheduler);

    final UserFacade userFacade = resolveService(getServer(), UserFacade.class);
    final ObjectiveProgressFacade objectiveProgressFacade =
        getObjectiveProgressFacade(scheduler, juliet);
    final QuestObserverFacade questObserverFacade =
        getQuestObserverFacade(scheduler, juliet, userFacade);
    final QuestTrackFacade questTrackFacade = getQuestTrackFacade(scheduler, juliet);
    final QuestController questController = new QuestController(questIndex, questTrackFacade);
    final QuestTrackController questTrackController =
        new QuestTrackController(
            logger,
            scheduler,
            messageSource.quest,
            messageCompiler,
            questTrackFacade,
            questObserverFacade,
            objectiveProgressFacade);

    final ObjectiveController objectiveController = new ObjectiveController(messageCompiler);
    final ObjectiveProgressController objectiveProgressController =
        new ObjectiveProgressController(userFacade, questTrackController, objectiveProgressFacade);

    registerListeners(
        this,
        new BreakBlockObjectiveHandler(questController, objectiveProgressController),
        new PlaceBlockObjectiveHandler(questController, objectiveProgressController));

    if (questIndex.getQuests().stream()
        .anyMatch(
            quest ->
                quest.getObjectives().stream().anyMatch(DistanceObjective.class::isInstance))) {
      registerListeners(
          this, new DistanceObjectiveHandler(questController, objectiveProgressController));
    }

    final QuestsView questsView =
        new QuestsView(
            this,
            scheduler,
            messageSource.quest,
            messageSource.navigation,
            messageCompiler,
            userFacade,
            questIndex,
            questController,
            questObserverFacade,
            questTrackController,
            objectiveController,
            objectiveProgressFacade,
            eventPublisher);

    final ExternalIntegration placeholderApiIntegration =
        getPlaceholderApiIntegration(
            this,
            userFacade,
            questIndex,
            questObserverFacade,
            objectiveController,
            objectiveProgressController);
    final ExternalIntegrator externalIntegrator =
        new ExternalIntegrator(
            Map.of(placeholderApiIntegration::isSupportedEnvironment, placeholderApiIntegration));
    externalIntegrator.configure(getServer());

    registerFacades(
        this,
        Set.of(
            questIndex,
            questFacade,
            questObserverFacade,
            questTrackFacade,
            objectiveController,
            objectiveProgressFacade,
            objectiveProgressController));

    commands =
        LiteBukkitFactory.builder(getName(), this)
            .extension(new LiteAdventureExtension<>(), configurer -> configurer.miniMessage(true))
            .argument(Quest.class, new QuestArgumentResolver<>(messageSource.quest, questIndex))
            .commands(
                LiteCommandsAnnotations.of(
                    new QuestsCommand(
                        messageSource.quest,
                        userFacade,
                        questsView,
                        questTrackFacade,
                        questTrackController)))
            .selfProcessor(
                new BukkitCommandsBuilderProcessor(messageSource.command, messageCompiler))
            .build();
  }

  @Override
  public void onDisable() {
    juliet.close();
    commands.unregister();
  }

  private Path getQuestsDirectoryPath() {
    return getDataFolder().toPath().resolve(QUESTS_DIRECTORY_NAME);
  }
}
