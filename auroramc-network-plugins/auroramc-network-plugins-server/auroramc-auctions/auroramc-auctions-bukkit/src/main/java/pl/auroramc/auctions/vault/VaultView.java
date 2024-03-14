package pl.auroramc.auctions.vault;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static pl.auroramc.auctions.vault.VaultViewUtils.mergeLoreOnItemStack;
import static pl.auroramc.commons.BukkitUtils.postToMainThread;
import static pl.auroramc.commons.page.navigation.PageNavigationDirection.BACKWARD;
import static pl.auroramc.commons.page.navigation.PageNavigationDirection.FORWARD;
import static pl.auroramc.commons.page.navigation.PageNavigationUtils.navigate;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus.Internal;
import pl.auroramc.auctions.vault.item.VaultItem;

class VaultView {

  private final Plugin plugin;
  private final VaultController vaultController;
  private final UUID vaultOwnerUniqueId;
  public ChestGui vaultGui;
  public PaginatedPane vaultItemsPane;

  VaultView(
      final Plugin plugin,
      final VaultController vaultController,
      final UUID vaultOwnerUniqueId
  ) {
    this.plugin = plugin;
    this.vaultController = vaultController;
    this.vaultOwnerUniqueId = vaultOwnerUniqueId;
  }

  public void populateVaultItems(final PaginatedPane requestingPane) {
    vaultItemsPane = requestingPane;
    vaultItemsPane.clear();
    vaultItemsPane.populateWithGuiItems(
        vaultController.searchVaultItems(vaultOwnerUniqueId).stream()
            .map(this::getGuiItemForVaultItem)
            .toList()
    );

    postToMainThread(plugin, () -> vaultGui.update());
  }

  public void requestVaultItemRedeem(final InventoryClickEvent event, final VaultItem vaultItem) {
    vaultController.redeemVaultItem(event.getWhoClicked().getUniqueId(), vaultItem)
        .thenAccept(state -> populateVaultItems(vaultItemsPane));
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
    final ItemStack renderItemStack = mergeLoreOnItemStack(
        originItemStack,
        getAdditionalLoreForVaultItem()
    );
    return new GuiItem(
        renderItemStack,
        event -> requestVaultItemRedeem(event, vaultItem),
        plugin
    );
  }

  private List<Component> getAdditionalLoreForVaultItem() {
    return List.of(
        miniMessage().deserialize(
            "<gray>Naciśnij aby odebrać ten przedmiot."
        )
    );
  }
}
