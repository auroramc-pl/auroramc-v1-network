package pl.auroramc.auctions.vault;

import static pl.auroramc.auctions.message.MessageVariableKey.SUBJECT_VARIABLE_KEY;
import static pl.auroramc.commons.BukkitUtils.postToMainThread;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;
import static pl.auroramc.commons.item.ItemStackFormatter.getFormattedItemStack;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Blocking;
import pl.auroramc.auctions.message.MessageSource;
import pl.auroramc.auctions.vault.item.VaultItem;
import pl.auroramc.auctions.vault.item.VaultItemFacade;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

public class VaultController {

  private final Plugin plugin;
  private final Logger logger;
  private final MessageSource messageSource;
  private final UserFacade userFacade;
  private final VaultFacade vaultFacade;
  private final VaultItemFacade vaultItemFacade;

  public VaultController(
      final Plugin plugin,
      final Logger logger,
      final MessageSource messageSource,
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
    final Player player = Bukkit.getPlayer(uniqueId);
    if (player != null) {
      player.sendMessage(
          messageSource.vaultItemReceived
              .with(SUBJECT_VARIABLE_KEY, getFormattedItemStack(subject))
              .compile()
      );
    }

    userFacade.getUserByUniqueId(uniqueId)
        .thenApply(User::getId)
        .thenCompose(vaultFacade::getVaultByUserId)
        .thenApply(Vault::getId)
        .thenApply(vaultId -> new VaultItem(null, vaultId, subject))
        .thenCompose(vaultItemFacade::createVaultItem)
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  CompletableFuture<Void> redeemVaultItem(final UUID uniqueId, final VaultItem vaultItem) {
    final Player player = Bukkit.getPlayer(uniqueId);
    if (player == null) {
      throw new VaultItemRedeemException(
          "Vault item could not be redeemed, because player is Offline."
      );
    }

    player.sendMessage(
        messageSource.vaultItemRedeemed
            .with(SUBJECT_VARIABLE_KEY, getFormattedItemStack(vaultItem.getSubject()))
            .compile()
    );

    return vaultItemFacade.deleteVaultItem(vaultItem)
        .thenAccept(state ->
            player.getInventory()
                .addItem(ItemStack.deserializeBytes(vaultItem.getSubject()))
                .forEach((index, itemStack) ->
                    postToMainThread(plugin,
                        () -> player.getWorld().dropItemNaturally(player.getLocation(), itemStack)
                    )
                )
        )
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }
}
