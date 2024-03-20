package pl.auroramc.auth.identity.generator;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.UUID.nameUUIDFromBytes;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import pl.auroramc.auth.account.AccountFacade;
import pl.auroramc.auth.identity.IdentityGenerator;

class MojangIdentityGenerator implements IdentityGenerator {

  private static final String OFFLINE_PLAYER_UUID_PREFIX = "OfflinePlayer:";
  private final AccountFacade accountFacade;

  MojangIdentityGenerator(final AccountFacade accountFacade) {
    this.accountFacade = accountFacade;
  }

  @Override
  public CompletableFuture<UUID> generateIdentity(final String username) {
    return accountFacade
        .getPremiumUniqueIdByUsername(username)
        .thenApply(premiumUniqueId -> getUuid(premiumUniqueId, username));
  }

  private UUID getUuid(final UUID premiumUniqueId, final String username) {
    return Optional.ofNullable(premiumUniqueId).orElseGet(() -> getUuidFromUsername(username));
  }

  private UUID getUuidFromUsername(final String username) {
    return nameUUIDFromBytes((OFFLINE_PLAYER_UUID_PREFIX + username).getBytes(UTF_8));
  }
}
