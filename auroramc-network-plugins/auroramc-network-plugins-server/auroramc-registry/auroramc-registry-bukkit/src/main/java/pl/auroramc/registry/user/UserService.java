package pl.auroramc.registry.user;

import static java.time.Duration.ofSeconds;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static pl.auroramc.commons.scheduler.SchedulerPoll.ASYNC;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import pl.auroramc.commons.concurrent.CompletableFutureUtils;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.commons.scheduler.caffeine.CaffeineExecutor;

class UserService implements UserFacade {

  private final Scheduler scheduler;
  private final UserRepository userRepository;
  private final AsyncLoadingCache<UUID, User> userByUniqueId;
  private final AsyncLoadingCache<String, User> userByUsername;

  UserService(final Scheduler scheduler, final UserRepository userRepository) {
    this.scheduler = scheduler;
    this.userRepository = userRepository;
    this.userByUniqueId =
        Caffeine.newBuilder()
            .executor(new CaffeineExecutor(scheduler))
            .expireAfterWrite(ofSeconds(30))
            .buildAsync(userRepository::findUserByUniqueId);
    this.userByUsername =
        Caffeine.newBuilder()
            .executor(new CaffeineExecutor(scheduler))
            .expireAfterWrite(ofSeconds(30))
            .buildAsync(userRepository::findUserByUsername);
  }

  @Override
  public CompletableFuture<User> getUserByUniqueId(final UUID uniqueId) {
    return userByUniqueId
        .get(uniqueId)
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  @Override
  public CompletableFuture<User> getUserByUsername(final String username) {
    return userByUsername
        .get(username)
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  @Override
  public CompletableFuture<Void> createUser(final User user) {
    return scheduler
        .run(ASYNC, () -> userRepository.createUser(user))
        .thenAccept(
            state -> {
              final CompletableFuture<User> updatedUser = completedFuture(user);
              userByUniqueId.put(user.getUniqueId(), updatedUser);
              userByUsername.put(user.getUsername(), updatedUser);
            });
  }

  @Override
  public CompletableFuture<Void> updateUser(final User user) {
    return scheduler.run(ASYNC, () -> userRepository.updateUser(user));
  }
}
