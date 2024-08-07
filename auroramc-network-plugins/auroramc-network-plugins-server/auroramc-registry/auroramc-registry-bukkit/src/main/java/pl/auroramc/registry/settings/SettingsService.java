package pl.auroramc.registry.settings;

import static java.time.Duration.ofSeconds;
import static pl.auroramc.commons.scheduler.SchedulerPoll.ASYNC;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.CompletableFuture;
import pl.auroramc.commons.concurrent.CompletableFutureUtils;
import pl.auroramc.commons.scheduler.Scheduler;

class SettingsService implements SettingsFacade {

  private final Scheduler scheduler;
  private final SettingsRepository settingsRepository;
  private final AsyncLoadingCache<Long, Settings> settingsByUserId;

  SettingsService(final Scheduler scheduler, final SettingsRepository settingsRepository) {
    this.scheduler = scheduler;
    this.settingsRepository = settingsRepository;
    this.settingsByUserId =
        Caffeine.newBuilder()
            .expireAfterWrite(ofSeconds(30))
            .buildAsync(settingsRepository::findSettingsByUserId);
  }

  @Override
  public CompletableFuture<Settings> getSettingsByUserId(final Long userId) {
    return settingsByUserId.get(userId);
  }

  @Override
  public CompletableFuture<Void> createSettings(final Settings settings) {
    return scheduler
        .run(ASYNC, () -> settingsRepository.createSettings(settings))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  @Override
  public CompletableFuture<Void> updateSettings(final Settings settings) {
    return scheduler
        .run(ASYNC, () -> settingsRepository.updateSettings(settings))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }
}
