package pl.auroramc.auth.account;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface AccountFacade {

  CompletableFuture<UUID> getPremiumUniqueIdByUsername(final String username);
}
