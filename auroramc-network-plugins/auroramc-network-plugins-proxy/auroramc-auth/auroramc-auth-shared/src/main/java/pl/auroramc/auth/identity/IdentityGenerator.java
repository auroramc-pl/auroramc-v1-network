package pl.auroramc.auth.identity;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IdentityGenerator {

  CompletableFuture<UUID> generateIdentity(final String username);
}
