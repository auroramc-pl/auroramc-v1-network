package pl.auroramc.auctions.vault;

import static org.bukkit.Bukkit.getPlayer;
import static pl.auroramc.auctions.vault.VaultMessageSourcePaths.SUBJECT_PATH;
import static pl.auroramc.commons.scheduler.SchedulerPoll.SYNC;
import static pl.auroramc.integrations.item.ItemStackUtils.giveOrDropItemStack;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Blocking;
import pl.auroramc.auctions.vault.item.VaultItem;
import pl.auroramc.auctions.vault.item.VaultItemFacade;
import pl.auroramc.commons.concurrent.CompletableFutureUtils;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

public class VaultController {

  private final Scheduler scheduler;
  private final VaultMessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;
  private final UserFacade userFacade;
  private final VaultFacade vaultFacade;
  private final VaultItemFacade vaultItemFacade;

  public VaultController(
      final Scheduler scheduler,
      final VaultMessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final UserFacade userFacade,
      final VaultFacade vaultFacade,
      final VaultItemFacade vaultItemFacade) {
    this.scheduler = scheduler;
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
    this.userFacade = userFacade;
    this.vaultFacade = vaultFacade;
    this.vaultItemFacade = vaultItemFacade;
  }

  @Blocking
  Set<VaultItem> searchVaultItems(final UUID uniqueId) {
    return userFacade
        .getUserByUniqueId(uniqueId)
        .thenApply(User::getId)
        .thenApply(vaultItemFacade::getVaultItemsByUserId)
        .exceptionally(CompletableFutureUtils::delegateCaughtException)
        .join();
  }

  public void createVaultItem(final UUID uniqueId, final byte[] subject) {
    final Player player = getPlayer(uniqueId);
    if (player != null) {
      messageCompiler
          .compile(
              messageSource.vaultItemReceived.placeholder(
                  SUBJECT_PATH, ItemStack.deserializeBytes(subject)))
          .deliver(player);
    }

    userFacade
        .getUserByUniqueId(uniqueId)
        .thenApply(User::getId)
        .thenCompose(vaultFacade::getVaultByUserId)
        .thenApply(vault -> new VaultItem(null, vault.getUserId(), vault.getId(), subject))
        .thenCompose(vaultItemFacade::createVaultItem)
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  CompletableFuture<Void> redeemVaultItem(final UUID uniqueId, final VaultItem vaultItem) {
    final Player player = getPlayer(uniqueId);
    if (player == null) {
      throw new VaultItemRedeemException(
          "Vault item could not be redeemed, because player is Offline.");
    }

    return vaultItemFacade
        .deleteVaultItem(vaultItem)
        .thenApply(
            state ->
                messageSource.vaultItemRedeemed.placeholder(
                    SUBJECT_PATH, vaultItem.getResolvedSubject()))
        .thenApply(messageCompiler::compile)
        .thenAccept(message -> message.deliver(player))
        .thenCompose(
            state ->
                scheduler.run(
                    SYNC,
                    () ->
                        giveOrDropItemStack(
                            player, ItemStack.deserializeBytes(vaultItem.getSubject()))));
  }
}
