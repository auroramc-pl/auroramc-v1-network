package pl.auroramc.auctions;

import static java.util.concurrent.CompletableFuture.allOf;
import static pl.auroramc.commons.concurrent.CompletableFutureUtils.NIL;

import java.util.concurrent.CompletableFuture;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.auroramc.auctions.audience.Audience;
import pl.auroramc.auctions.audience.AudienceFacade;
import pl.auroramc.auctions.vault.Vault;
import pl.auroramc.auctions.vault.VaultFacade;
import pl.auroramc.commons.concurrent.CompletableFutureUtils;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

class DataValidationListener implements Listener {

  private final UserFacade userFacade;
  private final VaultFacade vaultFacade;
  private final AudienceFacade audienceFacade;

  public DataValidationListener(
      final UserFacade userFacade,
      final VaultFacade vaultFacade,
      final AudienceFacade audienceFacade) {
    this.userFacade = userFacade;
    this.vaultFacade = vaultFacade;
    this.audienceFacade = audienceFacade;
  }

  @EventHandler
  public void onDataValidation(final PlayerJoinEvent event) {
    userFacade
        .getUserByUniqueId(event.getPlayer().getUniqueId())
        .thenCompose(user -> allOf(createVaultIfRequired(user), createAudienceIfRequired(user)))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  private CompletableFuture<Void> createVaultIfRequired(final User user) {
    return vaultFacade
        .getVaultByUserId(user.getId())
        .thenCompose(
            vault -> vault == null ? vaultFacade.createVault(new Vault(null, user.getId())) : NIL);
  }

  private CompletableFuture<Void> createAudienceIfRequired(final User user) {
    return audienceFacade
        .getAudienceByUniqueId(user.getUniqueId())
        .thenCompose(
            audience ->
                audience == null
                    ? audienceFacade.createAudience(new Audience(user.getId(), true))
                    : NIL);
  }
}
