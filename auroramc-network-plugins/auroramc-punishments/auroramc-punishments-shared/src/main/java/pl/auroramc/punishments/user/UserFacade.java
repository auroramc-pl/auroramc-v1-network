package pl.auroramc.punishments.user;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserFacade {

  CompletableFuture<User> getUserByUniqueId(final UUID uniqueId);

  CompletableFuture<Void> createUser(final User user);

  CompletableFuture<Void> updateUser(final User user);
}
