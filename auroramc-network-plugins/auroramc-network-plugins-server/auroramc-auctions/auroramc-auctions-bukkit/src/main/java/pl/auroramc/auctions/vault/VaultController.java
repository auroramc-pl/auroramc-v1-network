package pl.auroramc.auctions.vault;

import static org.bukkit.Bukkit.getPlayer;
import static pl.auroramc.auctions.message.MutableMessageVariableKey.SUBJECT_VARIABLE_KEY;
import static pl.auroramc.commons.BukkitUtils.appendItemStackOrDropBelow;
import static pl.auroramc.commons.BukkitUtils.postToMainThreadAndNextTick;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;
import static pl.auroramc.commons.item.ItemStackFormatter.getFormattedItemStack;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Blocking;
import pl.auroramc.auctions.message.MutableMessageSource;
import pl.auroramc.auctions.vault.item.VaultItem;
import pl.auroramc.auctions.vault.item.VaultItemFacade;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

public class VaultController {

  private final Plugin plugin;
  private final Logger logger;
  private final MutableMessageSource messageSource;
  private final UserFacade userFacade;
  private final VaultFacade vaultFacade;
  private final VaultItemFacade vaultItemFacade;

  public VaultController(
      final Plugin plugin,
      final Logger logger,
      final MutableMessageSource messageSource,
      final UserFacade userFacade,
      final VaultFacade vaultFacade,
      final VaultItemFacade vaultItemFacade
  ) {
    this.plugin = plugin;
    this.logger = logger;
    this.messageSource = messageSource;
    this.userFacade = userFacade;
    this.vaultFacade = vaultFacade;
    this.vaultItemFacade = vaultItemFacade;
  }

  @Blocking
  Set<VaultItem> searchVaultItems(final UUID uniqueId) {
    return userFacade.getUserByUniqueId(uniqueId)
        .thenApply(User::getId)
        .thenApply(vaultItemFacade::getVaultItemsByUserId)
        .exceptionally(exception -> delegateCaughtException(logger, exception))
        .join();
  }

  public void createVaultItem(final UUID uniqueId, final byte[] subject) {
    final Player player = getPlayer(uniqueId);
    if (player != null) {
      messageSource.vaultItemReceived
          .with(SUBJECT_VARIABLE_KEY, getFormattedItemStack(subject))
          .deliver(player);
    }

    userFacade.getUserByUniqueId(uniqueId)
        .thenApply(User::getId)
        .thenCompose(vaultFacade::getVaultByUserId)
        .thenApply(vault -> new VaultItem(null, vault.getUserId(), vault.getId(), subject))
        .thenAccept(vaultItemFacade::createVaultItem)
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  CompletableFuture<Void> redeemVaultItem(final UUID uniqueId, final VaultItem vaultItem) {
    final Player player = getPlayer(uniqueId);
    if (player == null) {
      throw new VaultItemRedeemException(
          "Vault item could not be redeemed, because player is Offline."
      );
    }

    return vaultItemFacade.deleteVaultItem(vaultItem)
        .thenAccept(state ->
            messageSource.vaultItemRedeemed
                .with(SUBJECT_VARIABLE_KEY, getFormattedItemStack(vaultItem.getSubject()))
                .deliver(player)
        )
        .thenAccept(state ->
            postToMainThreadAndNextTick(
                plugin,
                () -> appendItemStackOrDropBelow(player, ItemStack.deserializeBytes(vaultItem.getSubject()))
            )
        );
  }
}
