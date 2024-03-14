package pl.auroramc.auctions;

import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.auroramc.auctions.message.viewer.MessageViewer;
import pl.auroramc.auctions.message.viewer.MessageViewerFacade;
import pl.auroramc.auctions.vault.Vault;
import pl.auroramc.auctions.vault.VaultFacade;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

class DataValidationListener implements Listener {

  private static final CompletableFuture<Void> EMPTY_FUTURE = completedFuture(null);
  private final Logger logger;
  private final UserFacade userFacade;
  private final VaultFacade vaultFacade;
  private final MessageViewerFacade messageViewerFacade;

  public DataValidationListener(
      final Logger logger,
      final UserFacade userFacade,
      final VaultFacade vaultFacade,
      final MessageViewerFacade messageViewerFacade
  ) {
    this.logger = logger;
    this.userFacade = userFacade;
    this.vaultFacade = vaultFacade;
    this.messageViewerFacade = messageViewerFacade;
  }

  @EventHandler
  public void onDataValidation(final PlayerJoinEvent event) {
    userFacade.getUserByUniqueId(event.getPlayer().getUniqueId())
        .thenCompose(user ->
            allOf(
                createVaultIfRequired(user),
                createMessageViewerIfRequired(user)
            )
        )
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private CompletableFuture<Void> createVaultIfRequired(final User user) {
    return vaultFacade.getVaultByUserId(user.getId())
        .thenCompose(vault -> {
          if (vault == null) {
            return vaultFacade.createVault(
                new Vault(null, user.getId())
            );
          }

          return EMPTY_FUTURE;
        });
  }

  private CompletableFuture<Void> createMessageViewerIfRequired(final User user) {
    return messageViewerFacade.getMessageViewerByUserUniqueId(user.getUniqueId())
        .thenCompose(messageViewer -> {
          if (messageViewer == null) {
            return messageViewerFacade.createMessageViewer(
                new MessageViewer(user.getId(), true)
            );
          }

          return EMPTY_FUTURE;
        });
  }
}
