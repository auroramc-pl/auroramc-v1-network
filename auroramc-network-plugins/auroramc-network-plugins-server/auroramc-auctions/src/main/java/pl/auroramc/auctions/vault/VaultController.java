package pl.auroramc.auctions.vault;

import static org.bukkit.Bukkit.getPlayer;
import static pl.auroramc.auctions.vault.VaultMessageSourcePaths.SUBJECT_PATH;
import static pl.auroramc.commons.scheduler.SchedulerPoll.SYNC;
import static pl.auroramc.integrations.item.ItemStackUtils.giveOrDropItemStack;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.auroramc.auctions.vault.item.VaultItem;
import pl.auroramc.auctions.vault.item.VaultItemFacade;
import pl.auroramc.commons.concurrent.CompletableFutureUtils;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.messages.message.compiler.CompiledMessage;
import pl.auroramc.messages.viewer.BukkitViewer;
import pl.auroramc.messages.viewer.Viewer;
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

  public CompletableFuture<List<VaultItem>> searchVaultItems(final UUID uniqueId) {
    return userFacade
        .getUserByUniqueId(uniqueId)
        .thenApply(User::getId)
        .thenApply(vaultItemFacade::getVaultItemsByUserId)
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
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
          "Vault item could not be redeemed, because player is offline.");
    }

    final Viewer viewer = BukkitViewer.wrap(player);
    final CompiledMessage message =
        messageCompiler.compile(
            messageSource.vaultItemRedeemed.placeholder(
                SUBJECT_PATH, vaultItem.getResolvedSubject()));

    return vaultItemFacade
        .deleteVaultItem(vaultItem)
        .thenAccept(state -> viewer.deliver(message))
        .thenCompose(state -> giveOrDropVaultItem(player, vaultItem))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  private CompletableFuture<Void> giveOrDropVaultItem(
      final Player player, final VaultItem vaultItem) {
    return scheduler.run(
        SYNC,
        () -> giveOrDropItemStack(player, ItemStack.deserializeBytes(vaultItem.getSubject())));
  }
}
