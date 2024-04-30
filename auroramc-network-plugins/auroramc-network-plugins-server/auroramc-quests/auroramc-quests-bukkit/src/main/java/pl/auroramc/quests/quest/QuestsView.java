package pl.auroramc.quests.quest;

import static com.github.stefvanschie.inventoryframework.pane.Pane.Priority.LOWEST;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparingInt;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.bukkit.Material.BLACK_STAINED_GLASS_PANE;
import static org.bukkit.Material.PAPER;
import static org.bukkit.enchantments.Enchantment.DURABILITY;
import static org.bukkit.event.inventory.ClickType.LEFT;
import static org.bukkit.event.inventory.ClickType.RIGHT;
import static org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS;
import static pl.auroramc.commons.bukkit.page.navigation.NavigationDirection.BACKWARD;
import static pl.auroramc.commons.bukkit.page.navigation.NavigationDirection.FORWARD;
import static pl.auroramc.commons.bukkit.page.navigation.NavigationUtils.navigate;
import static pl.auroramc.commons.collection.CollectionUtils.merge;
import static pl.auroramc.commons.scheduler.SchedulerPoll.SYNC;
import static pl.auroramc.messages.message.compiler.CompiledMessage.empty;
import static pl.auroramc.messages.message.decoration.MessageDecorations.NO_CURSIVE;
import static pl.auroramc.quests.quest.QuestMessageSourcePaths.QUEST_PATH;
import static pl.auroramc.quests.quest.QuestState.IN_PROGRESS;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import pl.auroramc.commons.CompletableFutureUtils;
import pl.auroramc.commons.External;
import pl.auroramc.commons.bukkit.event.BukkitEventPublisher;
import pl.auroramc.commons.bukkit.item.ItemStackBuilder;
import pl.auroramc.commons.bukkit.page.navigation.NavigationMessageSource;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.messages.message.MutableMessage;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.messages.message.compiler.CompiledMessage;
import pl.auroramc.quests.objective.Objective;
import pl.auroramc.quests.objective.ObjectiveController;
import pl.auroramc.quests.objective.progress.ObjectiveProgress;
import pl.auroramc.quests.objective.progress.ObjectiveProgressFacade;
import pl.auroramc.quests.quest.observer.QuestObservedEvent;
import pl.auroramc.quests.quest.observer.QuestObserverFacade;
import pl.auroramc.quests.quest.track.QuestTrack;
import pl.auroramc.quests.quest.track.QuestTrackController;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

// TODO: Split monster view into multiple segments
public class QuestsView {

  private final Plugin plugin;
  private final Scheduler scheduler;
  private final QuestMessageSource messageSource;
  private final NavigationMessageSource navigationMessageSource;
  private final BukkitMessageCompiler messageCompiler;
  private final UserFacade userFacade;
  private final QuestIndex questIndex;
  private final QuestController questController;
  private final QuestObserverFacade questObserverFacade;
  private final QuestTrackController questTrackController;
  private final ObjectiveController objectiveController;
  private final ObjectiveProgressFacade objectiveProgressFacade;
  private final BukkitEventPublisher eventPublisher;

  public QuestsView(
      final Plugin plugin,
      final Scheduler scheduler,
      final QuestMessageSource messageSource,
      final NavigationMessageSource navigationMessageSource,
      final BukkitMessageCompiler messageCompiler,
      final UserFacade userFacade,
      final QuestIndex questIndex,
      final QuestController questController,
      final QuestObserverFacade questObserverFacade,
      final QuestTrackController questTrackController,
      final ObjectiveController objectiveController,
      final ObjectiveProgressFacade objectiveProgressFacade,
      final BukkitEventPublisher eventPublisher) {
    this.plugin = plugin;
    this.scheduler = scheduler;
    this.messageSource = messageSource;
    this.navigationMessageSource = navigationMessageSource;
    this.messageCompiler = messageCompiler;
    this.userFacade = userFacade;
    this.questIndex = questIndex;
    this.questController = questController;
    this.questObserverFacade = questObserverFacade;
    this.questTrackController = questTrackController;
    this.objectiveController = objectiveController;
    this.objectiveProgressFacade = objectiveProgressFacade;
    this.eventPublisher = eventPublisher;
  }

  public void render(final HumanEntity viewer) {
    final ChestGui questsGui = getQuestsGui(viewer);
    questsGui.show(viewer);
  }

  public @External void requestClickCancelling(final InventoryClickEvent event) {
    event.setCancelled(true);
  }

  public @External void navigateToNextPage(
      final ChestGui questsGui, final PaginatedPane questsPane) {
    navigate(FORWARD, questsGui, questsPane);
  }

  public @External void navigateToPrevPage(
      final ChestGui questsGui, final PaginatedPane questsPane) {
    navigate(BACKWARD, questsGui, questsPane);
  }

  private ChestGui getQuestsGui(final HumanEntity viewer) {
    final ChestGui questsGui =
        new ChestGui(
            6,
            ComponentHolder.of(
                messageCompiler.compile(messageSource.questsViewTitle).getComponent()),
            plugin);
    questsGui.setOnTopClick(this::requestClickCancelling);
    final PaginatedPane questsPane = getQuestsPane(viewer);
    questsGui.addPane(questsPane);
    questsGui.addPane(getBorderPane());
    questsGui.addPane(getNavigationPane(questsGui, questsPane));
    return questsGui;
  }

  private OutlinePane getBorderPane() {
    final OutlinePane borderPane = new OutlinePane(0, 5, 9, 1);
    borderPane.setPriority(LOWEST);
    borderPane.setRepeat(true);
    borderPane.addItem(
        new GuiItem(
            ItemStackBuilder.newBuilder(BLACK_STAINED_GLASS_PANE).displayName(" ").build(),
            plugin));
    return borderPane;
  }

  private PaginatedPane getQuestsPane(final HumanEntity viewer) {
    final List<Quest> assignedQuests =
        questController.getAssignedQuestsByUniqueId(viewer.getUniqueId());

    final PaginatedPane questsPane = new PaginatedPane(0, 0, 9, 5);
    questsPane.populateWithGuiItems(
        questIndex.getQuests().stream()
            .sorted(comparingInt(Quest::getWeight))
            .map(quest -> getQuestIcon(quest, assignedQuests, viewer.getUniqueId()))
            .toList());

    return questsPane;
  }

  private GuiItem getQuestIcon(
      final Quest quest, final List<Quest> assignedQuests, final UUID viewerUniqueId) {
    return new GuiItem(
        getQuestItem(viewerUniqueId, quest, assignedQuests),
        event -> {
          if (event.getClick() == LEFT && whetherQuestCanBeAssigned(viewerUniqueId, quest)) {
            assignQuest((Player) event.getWhoClicked(), quest);
          }

          if (event.getClick() == RIGHT && whetherQuestCanBeTracked(viewerUniqueId, quest)) {
            trackQuest((Player) event.getWhoClicked(), quest);
          }
        },
        plugin);
  }

  private ItemStack getQuestItem(
      final UUID viewerUniqueId, final Quest quest, final List<Quest> assignedQuests) {
    return ItemStackBuilder.newBuilder(quest.getIcon())
        .lore(
            merge(
                Optional.ofNullable(quest.getIcon().lore()).orElse(List.of()),
                List.of(getQuestState(quest, viewerUniqueId)),
                Component[]::new))
        .manipulate(
            quests -> quests.contains(quest),
            assignedQuests,
            icon -> icon.flags(HIDE_ENCHANTS).enchantment(DURABILITY, 1))
        .build();
  }

  private boolean whetherQuestCanBeAssigned(final UUID viewerUniqueId, final Quest quest) {
    return questTrackController
        .getQuestByUserIdAndQuestId(viewerUniqueId, quest.getKey().getId())
        .isEmpty();
  }

  private boolean whetherQuestCanBeTracked(final UUID viewerUniqueId, final Quest quest) {
    return questTrackController
        .getQuestByUserIdAndQuestId(viewerUniqueId, quest.getKey().getId())
        .map(QuestTrack::getQuestState)
        .filter(IN_PROGRESS::equals)
        .isPresent();
  }

  private void assignQuest(final Player viewer, final Quest quest) {
    userFacade
        .getUserByUniqueId(viewer.getUniqueId())
        .thenCompose(user -> questTrackController.assignQuest(user, quest, IN_PROGRESS))
        .thenCompose(state -> scheduler.run(SYNC, () -> render(viewer)))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  private void trackQuest(final Player viewer, final Quest quest) {
    questObserverFacade
        .resolveQuestObserverByUniqueId(viewer.getUniqueId())
        .thenAccept(
            questObserver -> {
              if (questObserver == null
                  || Objects.equals(questObserver.getQuestId(), quest.getKey().getId())) {
                return;
              }

              final Long previousQuestId = questObserver.getQuestId();
              questObserver.setQuestId(quest.getKey().getId());
              questObserverFacade.updateQuestObserver(questObserver);

              final Quest previousQuest = questIndex.getQuestById(previousQuestId);
              final Quest observedQuest = questIndex.getQuestById(questObserver.getQuestId());
              eventPublisher.publish(new QuestObservedEvent(viewer, previousQuest, observedQuest));

              messageCompiler
                  .compile(messageSource.observingQuest.placeholder(QUEST_PATH, quest))
                  .deliver(viewer);
            })
        .thenCompose(state -> scheduler.run(SYNC, () -> render(viewer)))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  private Component[] getQuestState(final Quest quest, final UUID viewerUniqueId) {
    final Optional<QuestTrack> questTrack =
        questTrackController.getQuestByUserIdAndQuestId(viewerUniqueId, quest.getKey().getId());
    return stream(
            questTrack
                .map(
                    track ->
                        switch (track.getQuestState()) {
                          case COMPLETED ->
                              messageCompiler.compileChildren(
                                  messageSource.questIsCompleted, NO_CURSIVE);
                          case IN_PROGRESS -> getQuestObjectivesSummary(quest, viewerUniqueId);
                        })
                .orElse(
                    messageCompiler.compileChildren(messageSource.questCouldBeStarted, NO_CURSIVE)))
        .map(CompiledMessage::getComponent)
        .toArray(Component[]::new);
  }

  private CompiledMessage[] getQuestObjectivesSummary(
      final Quest quest, final UUID viewerUniqueId) {
    final List<CompiledMessage> questObjectives = getQuestObjectives(quest, viewerUniqueId);
    final List<CompiledMessage> entries = new ArrayList<>();
    entries.add(empty());
    entries.addAll(questObjectives);
    entries.add(
        messageCompiler.compile(messageSource.questRequiresCompletionOfAllObjectives, NO_CURSIVE));
    entries.add(empty());
    entries.add(messageCompiler.compile(messageSource.questCouldBeTracked, NO_CURSIVE));
    return entries.toArray(CompiledMessage[]::new);
  }

  private List<CompiledMessage> getQuestObjectives(final Quest quest, final UUID viewerUniqueId) {
    final Map<? extends Objective<?>, ObjectiveProgress> objectiveToObjectiveProgress =
        quest.getObjectives().stream()
            .collect(
                toMap(
                    identity(),
                    objective ->
                        objectiveProgressFacade.getObjectiveProgress(
                            userFacade
                                .getUserByUniqueId(viewerUniqueId)
                                .thenApply(User::getId)
                                .join(),
                            quest.getKey().getId(),
                            objective.getKey().getId())));

    return getQuestObjectivesWithHeader(
        messageSource.questObjectivesHeader,
        objectiveController.getQuestObjectives(
            quest.getObjectives(), objectiveToObjectiveProgress));
  }

  private List<CompiledMessage> getQuestObjectivesWithHeader(
      final MutableMessage header, final List<CompiledMessage> objectives) {
    final List<CompiledMessage> copyOfObjectives = new ArrayList<>(objectives);
    copyOfObjectives.addFirst(messageCompiler.compile(header, NO_CURSIVE));
    return copyOfObjectives;
  }

  private StaticPane getNavigationPane(final ChestGui questsGui, final PaginatedPane questsPane) {
    final StaticPane navigationPane = new StaticPane(0, 5, 9, 1);
    navigationPane.addItem(getNextPageButton(questsGui, questsPane), 3, 0);
    navigationPane.addItem(getPrevPageButton(questsGui, questsPane), 5, 0);
    return navigationPane;
  }

  private GuiItem getNextPageButton(final ChestGui questsGui, final PaginatedPane questsPane) {
    return new GuiItem(
        ItemStackBuilder.newBuilder(PAPER)
            .displayName(
                messageCompiler.compile(navigationMessageSource.nameOfNextPageButton, NO_CURSIVE))
            .lore(
                messageCompiler.compileChildren(
                    navigationMessageSource.loreOfNextPageButton, NO_CURSIVE))
            .build(),
        event -> navigateToNextPage(questsGui, questsPane),
        plugin);
  }

  private GuiItem getPrevPageButton(final ChestGui questsGui, final PaginatedPane questsPane) {
    return new GuiItem(
        ItemStackBuilder.newBuilder(PAPER)
            .displayName(
                messageCompiler.compile(navigationMessageSource.nameOfPrevPageButton, NO_CURSIVE))
            .lore(
                messageCompiler.compileChildren(
                    navigationMessageSource.loreOfPrevPageButton, NO_CURSIVE))
            .build(),
        event -> navigateToPrevPage(questsGui, questsPane),
        plugin);
  }
}
