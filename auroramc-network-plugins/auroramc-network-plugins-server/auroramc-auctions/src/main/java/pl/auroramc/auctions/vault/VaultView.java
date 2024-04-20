package pl.auroramc.auctions.vault;

import static pl.auroramc.commons.bukkit.item.ItemStackUtils.mergeLore;
import static pl.auroramc.commons.bukkit.page.navigation.NavigationDirection.BACKWARD;
import static pl.auroramc.commons.bukkit.page.navigation.NavigationDirection.FORWARD;
import static pl.auroramc.commons.bukkit.page.navigation.NavigationUtils.navigate;
import static pl.auroramc.commons.scheduler.SchedulerPoll.SYNC;
import static pl.auroramc.messages.message.decoration.MessageDecorations.NO_CURSIVE;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import java.util.UUID;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus.Internal;
import pl.auroramc.auctions.vault.item.VaultItem;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;

class VaultView {

  private final Plugin plugin;
  private final Scheduler scheduler;
  private final VaultMessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;
  private final VaultController vaultController;
  private final UUID vaultOwnerUniqueId;
  public ChestGui vaultGui;
  public PaginatedPane vaultItemsPane;

  VaultView(
      final Plugin plugin,
      final Scheduler scheduler,
      final VaultMessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final VaultController vaultController,
      final UUID vaultOwnerUniqueId) {
    this.plugin = plugin;
    this.scheduler = scheduler;
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
    this.vaultController = vaultController;
    this.vaultOwnerUniqueId = vaultOwnerUniqueId;
  }

  public void populateVaultItems(final PaginatedPane requestingPane) {
    vaultItemsPane = requestingPane;
    vaultItemsPane.clear();
    vaultItemsPane.populateWithGuiItems(
        vaultController.searchVaultItems(vaultOwnerUniqueId).stream()
            .map(this::getGuiItemForVaultItem)
            .toList());

    scheduler.run(SYNC, () -> vaultGui.update());
  }

  public void requestVaultItemRedeem(final InventoryClickEvent event, final VaultItem vaultItem) {
    vaultController
        .redeemVaultItem(event.getWhoClicked().getUniqueId(), vaultItem)
        .thenCompose(state -> scheduler.run(SYNC, () -> populateVaultItems(vaultItemsPane)));
  }

  @Internal
  public void requestClickCancelling(final InventoryClickEvent event) {
    event.setCancelled(true);
  }

  @Internal
  public void navigateToNextPage() {
    navigate(FORWARD, vaultGui, vaultItemsPane);
  }

  @Internal
  public void navigateToPrevPage() {
    navigate(BACKWARD, vaultGui, vaultItemsPane);
  }

  private GuiItem getGuiItemForVaultItem(final VaultItem vaultItem) {
    final ItemStack originItemStack = ItemStack.deserializeBytes(vaultItem.getSubject());
    final ItemStack renderItemStack =
        mergeLore(
            originItemStack,
            messageCompiler.compileChildren(messageSource.vaultItemRedeemSuggestion, NO_CURSIVE));
    return new GuiItem(renderItemStack, event -> requestVaultItemRedeem(event, vaultItem), plugin);
  }
}