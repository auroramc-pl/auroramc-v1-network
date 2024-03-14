package pl.auroramc.auth.user;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserFacade {

  CompletableFuture<User> getUserByUniqueId(final UUID uniqueId);

  CompletableFuture<User> getUserByUsername(final String username);

  CompletableFuture<User> getUserByEmail(final String email);

  CompletableFuture<User> createUser(final User user);

  CompletableFuture<Void> updateUser(final User user);

  CompletableFuture<Void> deleteUser(final User user);

  User createUserInCache(final User user);

  void deleteUserOfCacheByUniqueId(final UUID uniqueId);
}
