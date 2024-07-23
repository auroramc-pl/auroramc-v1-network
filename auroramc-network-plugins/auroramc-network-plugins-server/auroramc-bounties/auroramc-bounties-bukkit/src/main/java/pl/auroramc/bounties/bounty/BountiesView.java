package pl.auroramc.bounties.bounty;

import static java.lang.Long.compare;
import static java.time.Duration.ofDays;
import static java.time.LocalDate.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Comparator.comparingLong;
import static org.bukkit.Material.CHEST_MINECART;
import static org.bukkit.Material.MINECART;
import static pl.auroramc.bounties.bounty.BountyMessageSourcePaths.DAY_PATH;
import static pl.auroramc.bounties.bounty.BountyMessageSourcePaths.REMAINING_DAYS_PATH;
import static pl.auroramc.bounties.bounty.BountyMessageSourcePaths.REMAINING_TIME_PATH;
import static pl.auroramc.commons.bukkit.page.navigation.NavigationDirection.BACKWARD;
import static pl.auroramc.commons.bukkit.page.navigation.NavigationDirection.FORWARD;
import static pl.auroramc.commons.bukkit.page.navigation.NavigationUtils.navigate;
import static pl.auroramc.integrations.item.ItemStackUtils.mergeLore;
import static pl.auroramc.messages.message.decoration.MessageDecorations.NO_CURSIVE;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import pl.auroramc.bounties.BountyConfig;
import pl.auroramc.bounties.progress.BountyProgress;
import pl.auroramc.commons.External;
import pl.auroramc.commons.view.Variable;
import pl.auroramc.integrations.item.ItemStackBuilder;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.messages.message.compiler.CompiledMessage;

class BountiesView {

  private static final int GREATER = 1;
  private static final int LESSER = -1;
  private static final int EQUALS = 0;
  private static final int INDEX_OFFSET = 1;
  private final Plugin plugin;
  private final BountyConfig bountyConfig;
  private final BountyFacade bountyFacade;
  private final @Variable BountyProgress bountyProgress;
  private final BountyController bountyController;
  private final @Variable Duration aggregatedPlaytime;
  private final BountyMessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;
  public @External ChestGui bountiesGui;
  public @External PaginatedPane bountiesPane;

  BountiesView(
      final Plugin plugin,
      final BountyConfig bountyConfig,
      final BountyFacade bountyFacade,
      final BountyProgress bountyProgress,
      final BountyController bountyController,
      final Duration aggregatedPlaytime,
      final BountyMessageSource messageSource,
      final BukkitMessageCompiler messageCompiler) {
    this.plugin = plugin;
    this.bountyConfig = bountyConfig;
    this.bountyFacade = bountyFacade;
    this.bountyProgress = bountyProgress;
    this.bountyController = bountyController;
    this.aggregatedPlaytime = aggregatedPlaytime;
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
  }

  public @External void requestClickCancelling(final InventoryClickEvent event) {
    event.setCancelled(true);
  }

  public @External void populateBountiesItems(final PaginatedPane requestingPane) {
    bountiesPane = requestingPane;
    bountiesPane.clear();
    bountiesPane.populateWithGuiItems(getBountyItems(bountyFacade.getBounties()));
    bountiesGui.update();
  }

  public @External void navigateToNextPage() {
    navigate(FORWARD, bountiesGui, bountiesPane);
  }

  public @External void navigateToPrevPage() {
    navigate(BACKWARD, bountiesGui, bountiesPane);
  }

  private GuiItem getBountyItem(final Bounty bounty) {
    final ItemStack originItemStack = bounty.getIcon();
    final ItemStack renderItemStack =
        ItemStackBuilder.newBuilder(
                mergeLore(originItemStack, getAdditionalLoreForBountyItem(bounty)))
            .type(isBountyAcquirable(bounty) ? CHEST_MINECART : MINECART)
            .displayName(
                messageCompiler.compile(
                    messageSource.bountyTitle.placeholder(DAY_PATH, bounty.getDay() + INDEX_OFFSET),
                    NO_CURSIVE))
            .build();
    return new GuiItem(
        renderItemStack,
        event -> {
          if (isBountyAcquirable(bounty)) {
            bountyController.acquireBounty(
                (Player) event.getWhoClicked(), bounty, bountyProgress, aggregatedPlaytime);
          }
        },
        plugin);
  }

  private CompiledMessage[] getAdditionalLoreForBountyItem(final Bounty bounty) {
    return List.of(getAvailabilityLoreForBountyItem(bountyProgress.getDay(), bounty))
        .toArray(CompiledMessage[]::new);
  }

  private CompiledMessage getAvailabilityLoreForBountyItem(final long day, final Bounty bounty) {
    final LocalDate today = now();
    return messageCompiler.compile(
        switch (compare(day, bounty.getDay())) {
          case GREATER -> messageSource.pastBounty;
          case LESSER ->
              messageSource.remainingDaysUntilBounty.placeholder(
                  REMAINING_DAYS_PATH, ofDays(bounty.getDay() - day));
          case EQUALS -> {
            if (bountyProgress.getAcquisitionDate().isEqual(today)) {
              yield messageSource.bountyAvailableSinceTomorrow;
            }

            if (aggregatedPlaytime.compareTo(bountyConfig.bountyBuffer) >= 0) {
              yield messageSource.bountyAvailable;
            }

            yield messageSource.remainingTimeUntilBounty.placeholder(
                REMAINING_TIME_PATH, bountyConfig.bountyBuffer.minus(aggregatedPlaytime));
          }
          default ->
              throw new BountiesViewInstantiationException(
                  "Could not compare days remaining till bounty.");
        },
        NO_CURSIVE);
  }

  private boolean isBountyAcquirable(final Bounty bounty) {
    final LocalDate today = now();
    return (bounty.getDay() > bountyProgress.getDay()
        || (bounty.getDay() == bountyProgress.getDay()
            && DAYS.between(bountyProgress.getAcquisitionDate(), today) >= 0));
  }

  private List<GuiItem> getBountyItems(final List<Bounty> bounties) {
    return bounties.stream()
        .sorted(comparingLong(Bounty::getDay))
        .map(this::getBountyItem)
        .toList();
  }
}
