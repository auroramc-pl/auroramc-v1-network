package pl.auroramc.registry.user;

import static java.time.Duration.ofSeconds;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.runAsync;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

class UserService implements UserFacade {

  private final Logger logger;
  private final UserRepository userRepository;
  private final AsyncLoadingCache<UUID, User> userByUniqueId;
  private final AsyncLoadingCache<String, User> userByUsername;

  UserService(final Logger logger, final UserRepository userRepository) {
    this.logger = logger;
    this.userRepository = userRepository;
    this.userByUniqueId =
        Caffeine.newBuilder()
            .expireAfterWrite(ofSeconds(30))
            .buildAsync(userRepository::findUserByUniqueId);
    this.userByUsername =
        Caffeine.newBuilder()
            .expireAfterWrite(ofSeconds(30))
            .buildAsync(userRepository::findUserByUsername);
  }

  @Override
  public CompletableFuture<User> getUserByUniqueId(final UUID uniqueId) {
    return userByUniqueId.get(uniqueId);
  }

  @Override
  public CompletableFuture<User> getUserByUsername(final String username) {
    return userByUsername.get(username);
  }

  @Override
  public CompletableFuture<Void> createUser(final User user) {
    return runAsync(() -> userRepository.createUser(user))
        .thenAccept(
            state -> {
              final CompletableFuture<User> updatedUser = completedFuture(user);
              userByUniqueId.put(user.getUniqueId(), updatedUser);
              userByUsername.put(user.getUsername(), updatedUser);
            })
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Override
  public CompletableFuture<Void> updateUser(final User user) {
    return runAsync(() -> userRepository.updateUser(user))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }
}
