package pl.auroramc.quests.quest;

import static com.github.stefvanschie.inventoryframework.pane.Pane.Priority.LOWEST;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toMap;
import static org.bukkit.Material.BLACK_STAINED_GLASS_PANE;
import static org.bukkit.Material.PAPER;
import static org.bukkit.enchantments.Enchantment.DURABILITY;
import static org.bukkit.event.inventory.ClickType.LEFT;
import static org.bukkit.event.inventory.ClickType.RIGHT;
import static org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS;
import static pl.auroramc.commons.BukkitUtils.postToMainThread;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;
import static pl.auroramc.commons.collection.CollectionUtils.merge;
import static pl.auroramc.commons.message.MutableMessage.EMPTY_DELIMITER;
import static pl.auroramc.commons.message.MutableMessage.empty;
import static pl.auroramc.commons.message.MutableMessage.newline;
import static pl.auroramc.commons.page.navigation.PageNavigationDirection.BACKWARD;
import static pl.auroramc.commons.page.navigation.PageNavigationDirection.FORWARD;
import static pl.auroramc.commons.page.navigation.PageNavigationUtils.navigate;
import static pl.auroramc.quests.message.MutableMessageVariableKey.QUEST_VARIABLE_KEY;
import static pl.auroramc.quests.quest.QuestState.IN_PROGRESS;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus.Internal;
import pl.auroramc.commons.item.ItemStackBuilder;
import pl.auroramc.commons.event.publisher.EventPublisher;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.commons.message.MutableMessageDecoration;
import pl.auroramc.quests.message.MutableMessageSource;
import pl.auroramc.quests.objective.Objective;
import pl.auroramc.quests.objective.ObjectiveUtils;
import pl.auroramc.quests.objective.progress.ObjectiveProgress;
import pl.auroramc.quests.objective.progress.ObjectiveProgressFacade;
import pl.auroramc.quests.quest.observer.QuestObservedEvent;
import pl.auroramc.quests.quest.observer.QuestObserverFacade;
import pl.auroramc.quests.quest.track.QuestTrack;
import pl.auroramc.quests.quest.track.QuestTrackController;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

public class QuestsView {

  private final Plugin plugin;
  private final Logger logger;
  private final MutableMessageSource messageSource;
  private final UserFacade userFacade;
  private final QuestIndex questIndex;
  private final QuestController questController;
  private final QuestObserverFacade questObserverFacade;
  private final QuestTrackController questTrackController;
  private final ObjectiveProgressFacade objectiveProgressFacade;
  private final EventPublisher eventPublisher;

  public QuestsView(
      final Plugin plugin,
      final Logger logger,
      final MutableMessageSource messageSource,
      final UserFacade userFacade,
      final QuestIndex questIndex,
      final QuestController questController,
      final QuestObserverFacade questObserverFacade,
      final QuestTrackController questTrackController,
      final ObjectiveProgressFacade objectiveProgressFacade,
      final EventPublisher eventPublisher
  ) {
    this.plugin = plugin;
    this.logger = logger;
    this.messageSource = messageSource;
    this.userFacade = userFacade;
    this.questIndex = questIndex;
    this.questController = questController;
    this.questObserverFacade = questObserverFacade;
    this.questTrackController = questTrackController;
    this.objectiveProgressFacade = objectiveProgressFacade;
    this.eventPublisher = eventPublisher;
  }

  private ChestGui getQuestsGui(final HumanEntity viewer) {
    final ChestGui questsGui = new ChestGui(
        6, ComponentHolder.of(messageSource.titleOfQuestsView.compile()), plugin
    );
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
            ItemStackBuilder.newBuilder(BLACK_STAINED_GLASS_PANE)
                .displayName(" ")
                .build(),
            plugin
        )
    );
    return borderPane;
  }

  private PaginatedPane getQuestsPane(final HumanEntity viewer) {
    final List<Quest> assignedQuests = questController.getAssignedQuestsByUniqueId(
        viewer.getUniqueId());

    final PaginatedPane questsPane = new PaginatedPane(0, 0, 9, 5);
    questsPane.populateWithGuiItems(questIndex.resolveQuests().stream()
        .sorted(comparingInt(Quest::getWeight))
        .map(quest -> getQuestIcon(quest, assignedQuests, viewer.getUniqueId()))
        .toList());

    return questsPane;
  }

  private GuiItem getQuestIcon(
      final Quest quest, final List<Quest> assignedQuests, final UUID viewerUniqueId
  ) {
    return new GuiItem(
        getQuestItem(viewerUniqueId, quest, assignedQuests), event -> {
          if (event.getClick() == LEFT && whetherQuestCanBeAssigned(viewerUniqueId, quest)) {
            assignQuest((Player) event.getWhoClicked(), quest);
          }

          if (event.getClick() == RIGHT && whetherQuestCanBeTracked(viewerUniqueId, quest)) {
            trackQuest((Player) event.getWhoClicked(), quest);
          }
        },
        plugin
    );
  }

  private ItemStack getQuestItem(
      final UUID viewerUniqueId, final Quest quest, final List<Quest> assignedQuests
  ) {
    return ItemStackBuilder.newBuilder(quest.getIcon())
        .lore(
            merge(
                Optional.ofNullable(quest.getIcon().lore()).orElse(List.of()),
                List.of(getQuestState(quest, viewerUniqueId)),
                Component[]::new
            )
        )
        .manipulate(
            quests -> quests.contains(quest), assignedQuests,
            icon -> icon.flags(HIDE_ENCHANTS).enchantment(DURABILITY, 1)
        )
        .build();
  }

  private boolean whetherQuestCanBeAssigned(final UUID viewerUniqueId, final Quest quest) {
    return questTrackController.getQuestByUserIdAndQuestId(viewerUniqueId, quest.getKey().getId()).isEmpty();
  }

  private boolean whetherQuestCanBeTracked(final UUID viewerUniqueId, final Quest quest) {
    return questTrackController.getQuestByUserIdAndQuestId(viewerUniqueId, quest.getKey().getId())
        .map(QuestTrack::getQuestState)
        .filter(IN_PROGRESS::equals)
        .isPresent();
  }

  private void assignQuest(final Player viewer, final Quest quest) {
    userFacade.getUserByUniqueId(viewer.getUniqueId())
        .thenCompose(user -> questTrackController.assignQuest(user, quest, IN_PROGRESS))
        .thenAccept(state -> postToMainThread(plugin, () -> render(viewer)))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private void trackQuest(final Player viewer, final Quest quest) {
    questObserverFacade.resolveQuestObserverByUniqueId(viewer.getUniqueId())
        .thenAccept(questObserver -> {
          if (questObserver == null || Objects.equals(questObserver.getQuestId(), quest.getKey().getId())) {
            return;
          }

          final Long previousQuestId = questObserver.getQuestId();
          questObserver.setQuestId(quest.getKey().getId());
          questObserverFacade.updateQuestObserver(questObserver);

          final Quest previousQuest = questIndex.resolveQuest(previousQuestId);
          final Quest observedQuest = questIndex.resolveQuest(questObserver.getQuestId());
          eventPublisher.publish(new QuestObservedEvent(viewer, previousQuest, observedQuest));

          viewer.sendMessage(
              messageSource.observingQuest
                  .with(QUEST_VARIABLE_KEY, quest.getKey().getId())
                  .compile()
          );
        })
        .thenAccept(state -> postToMainThread(plugin, () -> render(viewer)))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private Component[] getQuestState(final Quest quest, final UUID viewerUniqueId) {
    final Optional<QuestTrack> questTrack = questTrackController.getQuestByUserIdAndQuestId(
        viewerUniqueId, quest.getKey().getId()
    );
    return questTrack
        .map(track ->
            switch (track.getQuestState()) {
              case COMPLETED ->
                  messageSource.questIsCompleted;
              case IN_PROGRESS ->
                  newline()
                      .append(
                          getQuestObjectives(quest, viewerUniqueId)
                              .append(messageSource.questRequiresCompletionOfAllObjectives)
                              .append(empty())
                              .append(messageSource.questCouldBeTracked),
                          EMPTY_DELIMITER
                      );
            })
        .orElse(messageSource.questCouldBeStarted)
        .compileChildren(
            MutableMessageDecoration.of(TextDecoration.ITALIC, State.FALSE)
        );
  }

  private MutableMessage getQuestObjectives(final Quest quest, final UUID viewerUniqueId) {
    final Map<? extends Objective<?>, ObjectiveProgress> objectiveToObjectiveProgress =
        quest.getObjectives().stream()
            .collect(toMap(
                objective -> objective,
                objective ->
                    objectiveProgressFacade.getObjectiveProgress(
                        userFacade.getUserByUniqueId(viewerUniqueId)
                            .thenApply(User::getId)
                            .join(),
                        quest.getKey().getId(), objective.getKey().getId()
                    )
                )
            );

    return messageSource.questObjectivesHeader
        .append(
            ObjectiveUtils.getQuestObjectives(quest.getObjectives(), objectiveToObjectiveProgress)
        );
  }

  private StaticPane getNavigationPane(final ChestGui questsGui, final PaginatedPane questsPane) {
    final StaticPane navigationPane = new StaticPane(0, 5, 9, 1);
    navigationPane.addItem(
        new GuiItem(
            ItemStackBuilder.newBuilder(PAPER)
                .displayName(
                    messageSource.nameOfPrevPageNavigationButton
                        .compile(
                            MutableMessageDecoration.of(TextDecoration.ITALIC, State.FALSE)
                        )
                )
                .lore(
                    messageSource.loreOfPrevPageNavigationButton
                        .compileChildren(
                            MutableMessageDecoration.of(TextDecoration.ITALIC, State.FALSE)
                        )
                )
                .build(),
            event -> navigateToPrevPage(questsGui, questsPane),
            plugin
        ),
        3, 0
    );
    navigationPane.addItem(
        new GuiItem(
            ItemStackBuilder.newBuilder(PAPER)
                .displayName(
                    messageSource.nameOfNextPageNavigationButton
                        .compile(
                            MutableMessageDecoration.of(TextDecoration.ITALIC, State.FALSE)
                        )
                )
                .lore(
                    messageSource.loreOfNextPageNavigationButton
                        .compileChildren(
                            MutableMessageDecoration.of(TextDecoration.ITALIC, State.FALSE)
                        )
                )
                .build(),
            event -> navigateToNextPage(questsGui, questsPane),
            plugin
        ),
        5, 0
    );
    return navigationPane;
  }

  public void render(final HumanEntity viewer) {
    final ChestGui questsGui = getQuestsGui(viewer);
    questsGui.show(viewer);
  }

  @Internal
  public void navigateToNextPage(final ChestGui questsGui, final PaginatedPane questsPane) {
    navigate(FORWARD, questsGui, questsPane);
  }

  @Internal
  public void navigateToPrevPage(final ChestGui questsGui, final PaginatedPane questsPane) {
    navigate(BACKWARD, questsGui, questsPane);
  }

  @Internal
  public void requestClickCancelling(final InventoryClickEvent event) {
    event.setCancelled(true);
  }
}
