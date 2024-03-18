package pl.auroramc.auctions.vault;

import static java.util.Collections.emptyList;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;
import static net.kyori.adventure.text.format.TextDecoration.State.FALSE;
import static pl.auroramc.commons.BukkitUtils.postToMainThread;
import static pl.auroramc.commons.BukkitUtils.postToMainThreadAndNextTick;
import static pl.auroramc.commons.collection.CollectionUtils.merge;
import static pl.auroramc.commons.page.navigation.PageNavigationDirection.BACKWARD;
import static pl.auroramc.commons.page.navigation.PageNavigationDirection.FORWARD;
import static pl.auroramc.commons.page.navigation.PageNavigationUtils.navigate;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus.Internal;
import pl.auroramc.auctions.message.MutableMessageSource;
import pl.auroramc.auctions.vault.item.VaultItem;
import pl.auroramc.commons.item.ItemStackBuilder;

class VaultView {

  private final Plugin plugin;
  private final MutableMessageSource messageSource;
  private final VaultController vaultController;
  private final UUID vaultOwnerUniqueId;
  public ChestGui vaultGui;
  public PaginatedPane vaultItemsPane;

  VaultView(
      final Plugin plugin,
      final MutableMessageSource messageSource,
      final VaultController vaultController,
      final UUID vaultOwnerUniqueId
  ) {
    this.plugin = plugin;
    this.messageSource = messageSource;
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
        .thenAccept(state ->
                postToMainThreadAndNextTick(
                    plugin,
                    () -> populateVaultItems(vaultItemsPane)
                )
        );
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
    final ItemStack renderItemStack = mergeLore(
        originItemStack,
        List.of(
            messageSource.vaultItemRedeemSuggestion
                .compile()
        )
    );
    return new GuiItem(
        renderItemStack,
        event -> requestVaultItemRedeem(event, vaultItem),
        plugin
    );
  }

  private ItemStack mergeLore(
      final ItemStack source, final List<Component> lines
  ) {
    return ItemStackBuilder.newBuilder(source)
        .lore(
            merge(
                Optional.ofNullable(source.lore())
                    .orElse(emptyList()),
                lines.stream()
                    .map(line -> line.decoration(ITALIC, FALSE))
                    .toList(),
                Component[]::new
            )
        )
        .build();
  }
}
