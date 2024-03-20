package pl.auroramc.auctions;

import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.auroramc.auctions.audience.Audience;
import pl.auroramc.auctions.audience.AudienceFacade;
import pl.auroramc.auctions.vault.Vault;
import pl.auroramc.auctions.vault.VaultFacade;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

class DataValidationListener implements Listener {

  private static final CompletableFuture<Void> EMPTY_FUTURE = completedFuture(null);
  private final Logger logger;
  private final UserFacade userFacade;
  private final VaultFacade vaultFacade;
  private final AudienceFacade audienceFacade;

  public DataValidationListener(
      final Logger logger,
      final UserFacade userFacade,
      final VaultFacade vaultFacade,
      final AudienceFacade audienceFacade) {
    this.logger = logger;
    this.userFacade = userFacade;
    this.vaultFacade = vaultFacade;
    this.audienceFacade = audienceFacade;
  }

  @EventHandler
  public void onDataValidation(final PlayerJoinEvent event) {
    userFacade
        .getUserByUniqueId(event.getPlayer().getUniqueId())
        .thenCompose(user -> allOf(createVaultIfRequired(user), createAudienceIfRequired(user)))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private CompletableFuture<Void> createVaultIfRequired(final User user) {
    return vaultFacade
        .getVaultByUserId(user.getId())
        .thenCompose(
            vault -> {
              if (vault == null) {
                return vaultFacade.createVault(new Vault(null, user.getId()));
              }

              return EMPTY_FUTURE;
            });
  }

  private CompletableFuture<Void> createAudienceIfRequired(final User user) {
    return audienceFacade
        .getAudienceByUniqueId(user.getUniqueId())
        .thenCompose(
            audience -> {
              if (audience == null) {
                return audienceFacade.createAudience(new Audience(user.getId(), true));
              }

              return EMPTY_FUTURE;
            });
  }
}
