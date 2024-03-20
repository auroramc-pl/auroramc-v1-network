package pl.auroramc.quests;

import static dev.rollczi.litecommands.bukkit.LiteBukkitMessages.PLAYER_NOT_FOUND;
import static dev.rollczi.litecommands.bukkit.LiteBukkitMessages.PLAYER_ONLY;
import static dev.rollczi.litecommands.message.LiteMessages.INVALID_USAGE;
import static dev.rollczi.litecommands.message.LiteMessages.MISSING_PERMISSIONS;
import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.exists;
import static java.util.Locale.ROOT;
import static moe.rafal.juliet.datasource.HikariPooledDataSourceFactory.produceHikariDataSource;
import static pl.auroramc.commons.BukkitUtils.registerListeners;
import static pl.auroramc.commons.BukkitUtils.registerServices;
import static pl.auroramc.commons.BukkitUtils.resolveService;
import static pl.auroramc.commons.config.serdes.juliet.JulietConfig.JULIET_CONFIG_FILE_NAME;
import static pl.auroramc.commons.message.MutableMessage.LINE_SEPARATOR;
import static pl.auroramc.quests.integration.placeholderapi.PlaceholderApiIntegrationFactory.producePlaceholderApiIntegration;
import static pl.auroramc.quests.message.MutableMessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.quests.message.MutableMessageVariableKey.SCHEMATICS_VARIABLE_KEY;
import static pl.auroramc.quests.objective.progress.ObjectiveProgressFacadeFactory.getObjectiveProgressFacade;
import static pl.auroramc.quests.quest.QuestFacadeFactory.getQuestFacade;
import static pl.auroramc.quests.quest.QuestIndexFactory.getQuestIndex;
import static pl.auroramc.quests.quest.observer.QuestObserverFacadeFactory.getQuestObserverFacade;
import static pl.auroramc.quests.quest.track.QuestTrackFacadeFactory.getQuestTrackFacade;
import static pl.auroramc.quests.resource.key.ResourceKeyFacadeFactory.getResourceKeyFacade;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.adventure.LiteAdventureExtension;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;
import moe.rafal.juliet.JulietBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.commons.config.serdes.juliet.JulietConfig;
import pl.auroramc.commons.config.serdes.message.SerdesMessageSource;
import pl.auroramc.commons.integration.ExternalIntegration;
import pl.auroramc.commons.integration.ExternalIntegrator;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.serdes.juliet.SerdesJuliet;
import pl.auroramc.commons.event.publisher.EventPublisher;
import pl.auroramc.quests.message.MutableMessageSource;
import pl.auroramc.commons.integration.litecommands.MutableMessageResultHandler;
import pl.auroramc.quests.objective.Objective;
import pl.auroramc.quests.objectives.block.BreakBlockObjectiveHandler;
import pl.auroramc.quests.objectives.travel.DistanceObjective;
import pl.auroramc.quests.objectives.travel.DistanceObjectiveHandler;
import pl.auroramc.quests.objectives.block.PlaceBlockObjectiveHandler;
import pl.auroramc.quests.objective.progress.ObjectiveProgressController;
import pl.auroramc.quests.objective.progress.ObjectiveProgressFacade;
import pl.auroramc.quests.objective.requirement.ObjectiveRequirement;
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
import pl.auroramc.quests.resource.key.ResourceKeyFacade;
import pl.auroramc.registry.user.UserFacade;

public class QuestsBukkitPlugin extends JavaPlugin {

  private static final String QUESTS_DIRECTORY_NAME = "quests";
  private LiteCommands<CommandSender> commands;

  @Override
  public void onEnable() {
    final ConfigFactory configFactory = new ConfigFactory(
        getDataFolder().toPath(), YamlBukkitConfigurer::new);

    final MutableMessageSource messageSource = configFactory.produceConfig(
        MutableMessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessageSource()
    );

    final Logger logger = getLogger();

    final JulietConfig julietConfig = configFactory.produceConfig(
        JulietConfig.class, JULIET_CONFIG_FILE_NAME, new SerdesJuliet()
    );
    final Juliet juliet = JulietBuilder.newBuilder()
        .withDataSource(produceHikariDataSource(julietConfig.hikari))
        .build();

    final ResourceKeyFacade resourceKeyFacade = getResourceKeyFacade(juliet);
    final QuestIndex questIndex = getQuestIndex();
    final QuestFacade questFacade = getQuestFacade(getQuestsDirectoryPath(), getClassLoader());
    final List<Quest> quests = questFacade.discoverQuestDefinitions(getQuestsDirectoryPath());
    resourceKeyFacade.validateResourceKeys(quests);
    initTranslationForObjectivesFromQuests(messageSource, quests);

    questIndex.indexQuests(quests);

    final EventPublisher eventPublisher = new EventPublisher(this);

    final UserFacade userFacade = resolveService(getServer(), UserFacade.class);
    final ObjectiveProgressFacade objectiveProgressFacade = getObjectiveProgressFacade(logger, juliet);
    final QuestObserverFacade questObserverFacade = getQuestObserverFacade(logger, juliet, userFacade);
    final QuestTrackFacade questTrackFacade = getQuestTrackFacade(logger, juliet);
    final QuestController questController = new QuestController(questIndex, questTrackFacade);
    final QuestTrackController questTrackController = new QuestTrackController(
        this, getServer(), messageSource, questTrackFacade, questObserverFacade, objectiveProgressFacade
    );
    final QuestsView questsView = new QuestsView(
        this, logger, messageSource, userFacade, questIndex, questController, questObserverFacade, questTrackController, objectiveProgressFacade, eventPublisher
    );
    final ObjectiveProgressController objectiveProgressController = new ObjectiveProgressController(
        logger, userFacade, questTrackController, objectiveProgressFacade
    );

    registerListeners(this,
        new BreakBlockObjectiveHandler(questController, objectiveProgressController),
        new PlaceBlockObjectiveHandler(questController, objectiveProgressController)
    );

    if (questIndex.resolveQuests().stream()
        .anyMatch(quest -> quest.getObjectives().stream()
        .anyMatch(DistanceObjective.class::isInstance))
    ) {
      registerListeners(
          this, new DistanceObjectiveHandler(questController, objectiveProgressController)
      );
    }

    final ExternalIntegration placeholderApiIntegration = producePlaceholderApiIntegration(
        this, userFacade, questIndex, questObserverFacade, objectiveProgressController
    );
    final ExternalIntegrator externalIntegrator = new ExternalIntegrator(
        Map.of(
            placeholderApiIntegration::isSupportedEnvironment,
            placeholderApiIntegration
        )
    );
    externalIntegrator.configure(getServer());

    registerServices(
        this,
        Set.of(
            userFacade,
            questIndex,
            questFacade,
            questObserverFacade,
            questTrackFacade,
            objectiveProgressFacade,
            objectiveProgressController
        )
    );

    commands = LiteBukkitFactory.builder(getName(), this)
        .extension(new LiteAdventureExtension<>(),
            configurer -> configurer.miniMessage(true)
        )
        .message(INVALID_USAGE,
            context -> messageSource.availableSchematicsSuggestion
                .with(SCHEMATICS_VARIABLE_KEY, context.getSchematic().join(LINE_SEPARATOR))
        )
        .message(MISSING_PERMISSIONS, messageSource.executionOfCommandIsNotPermitted)
        .message(PLAYER_ONLY, messageSource.executionFromConsoleIsUnsupported)
        .message(PLAYER_NOT_FOUND, messageSource.specifiedPlayerIsUnknown)
        .argument(Quest.class, new QuestArgumentResolver<>(messageSource, questIndex))
        .commands(
            LiteCommandsAnnotations.of(
                new QuestsCommand(
                    logger, messageSource, userFacade, questsView, questTrackFacade, questTrackController
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

  private Path getQuestsDirectoryPath() {
    final Path shopsDirectoryPath = getDataFolder().toPath().resolve(QUESTS_DIRECTORY_NAME);
    if (exists(shopsDirectoryPath)) {
      return shopsDirectoryPath;
    }

    try {
      return createDirectory(shopsDirectoryPath);
    } catch (final IOException exception) {
      throw new QuestsInstantiationException(
          "Could not create quests directory in %s path, because of unexpected exception.".formatted(shopsDirectoryPath.toString()),
          exception
      );
    }
  }

  private void initTranslationForObjectivesFromQuests(
      final MutableMessageSource messageSource, final List<Quest> quests) {
    for (final Quest quest : quests) {
      initTranslationForObjectivesAndRequirements(messageSource, quest.getObjectives());
    }
  }

  private void initTranslationForObjectivesAndRequirements(
      final MutableMessageSource messageSource, final List<Objective<?>> objectives) {
    for (final Objective<?> objective : objectives) {
      objective.setMessage(
          getMessageByFieldName(
              messageSource,
              getMessageFieldNameByObjective(objective)
          )
      );
      for (final ObjectiveRequirement requirement : objective.getRequirements()) {
        requirement.setMessage(
            getMessageByFieldName(
                messageSource,
                getMessageFieldNameByRequirement(requirement)
            )
        );
      }
    }
  }

  private MutableMessage getMessageByFieldName(
      final MutableMessageSource messageSource, final String translationFieldName
  ) {
    try {
      return (MutableMessage) messageSource.getClass()
          .getDeclaredField(translationFieldName)
          .get(messageSource);
    } catch (final NoSuchFieldException | IllegalAccessException exception) {
      throw new QuestsInstantiationException(
          "Could not resolve message field by %s field name, because of unexpected exception."
              .formatted(
                  translationFieldName
              ),
          exception);
    }
  }

  private String getMessageFieldNameByObjective(final Objective<?> objective) {
    return getMessageFieldName(objective.getClass());
  }

  private String getMessageFieldNameByRequirement(final ObjectiveRequirement requirement) {
    return getMessageFieldName(requirement.getClass());
  }

  private String getMessageFieldName(final Class<?> clazz) {
    final String nameOfClazzWithoutPackage = clazz.getSimpleName().replace(clazz.getPackageName(), "");
    final String initialCharacterOfClazzLowercase = nameOfClazzWithoutPackage.substring(0, 1).toLowerCase(ROOT);
    final String clazzWithoutInitialCharacter = nameOfClazzWithoutPackage.substring(1);
    return initialCharacterOfClazzLowercase + clazzWithoutInitialCharacter;
  }
}
