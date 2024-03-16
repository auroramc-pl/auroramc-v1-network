package pl.auroramc.auth.user;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

class UserService implements UserFacade {

  private final Map<UUID, User> uniqueIdToUser = new ConcurrentHashMap<>();
  private final Map<String, User> usernameToUser = new ConcurrentHashMap<>();
  private final Map<String, User> emailToUser = new ConcurrentHashMap<>();
  private final Logger logger;
  private final UserRepository userRepository;

  UserService(final Logger logger, final UserRepository userRepository) {
    this.logger = logger;
    this.userRepository = userRepository;
  }

  @Override
  public CompletableFuture<User> getUserByUniqueId(final UUID uniqueId) {
    if (uniqueIdToUser.containsKey(uniqueId)) {
      return completedFuture(uniqueIdToUser.get(uniqueId));
    }

    return supplyAsync(() -> userRepository.findUserByUniqueId(uniqueId))
        .thenApply(this::createUserInCache)
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Override
  public CompletableFuture<User> getUserByUsername(final String username) {
    if (usernameToUser.containsKey(username)) {
      return completedFuture(usernameToUser.get(username));
    }

    return supplyAsync(() -> userRepository.findUserByUsername(username))
        .thenApply(this::createUserInCache)
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Override
  public CompletableFuture<User> getUserByEmail(final String email) {
    if (emailToUser.containsKey(email)) {
      return completedFuture(emailToUser.get(email));
    }

    return supplyAsync(() -> userRepository.findUserByEmail(email))
        .thenApply(this::createUserInCache)
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Override
  public CompletableFuture<User> createUser(final User user) {
    return runAsyncWithExceptionDelegation(() -> userRepository.createUser(user))
        .thenApply(state -> user);
  }

  @Override
  public CompletableFuture<Void> updateUser(final User user) {
    return runAsyncWithExceptionDelegation(() -> userRepository.updateUser(user));
  }

  @Override
  public CompletableFuture<Void> deleteUser(final User user) {
    return runAsyncWithExceptionDelegation(() -> userRepository.deleteUser(user))
        .thenAccept(state -> deleteUserOfCacheByUniqueId(user.getUniqueId()));
  }

  @Override
  public User createUserInCache(final User user) {
    if (user != null) {
      uniqueIdToUser.put(user.getUniqueId(), user);
      usernameToUser.put(user.getUsername(), user);
      Optional.ofNullable(user.getEmail()).ifPresent(email -> emailToUser.put(email, user));
    }
    return user;
  }

  @Override
  public void deleteUserOfCacheByUniqueId(final UUID uniqueId) {
    final User user = uniqueIdToUser.get(uniqueId);
    if (user != null) {
      uniqueIdToUser.remove(user.getUniqueId());
      usernameToUser.remove(user.getUsername());
      Optional.ofNullable(user.getEmail()).ifPresent(email -> emailToUser.remove(user.getEmail()));
    }
  }

  private CompletableFuture<Void> runAsyncWithExceptionDelegation(final Runnable runnable) {
    return runAsync(runnable)
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }
}
