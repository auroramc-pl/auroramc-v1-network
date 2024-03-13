package pl.auroramc.registry.user;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserFacade {

  CompletableFuture<User> getUserByUniqueId(final UUID uniqueId);

  CompletableFuture<User> getUserByUsername(final String username);

  CompletableFuture<Void> createUser(final User user);

  CompletableFuture<Void> updateUser(final User user);
}
