package pl.auroramc.registry.observer;

import static java.time.Duration.ofSeconds;
import static pl.auroramc.commons.scheduler.SchedulerPoll.ASYNC;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.CompletableFuture;
import pl.auroramc.commons.concurrent.CompletableFutureUtils;
import pl.auroramc.commons.scheduler.Scheduler;

class ObserverService implements ObserverFacade {

  private final Scheduler scheduler;
  private final ObserverRepository observerRepository;
  private final AsyncLoadingCache<ObserverCompositeKey, Observer> observerByProviderIdAndUserId;

  ObserverService(final Scheduler scheduler, final ObserverRepository observerRepository) {
    this.scheduler = scheduler;
    this.observerRepository = observerRepository;
    this.observerByProviderIdAndUserId =
        Caffeine.newBuilder()
            .expireAfterWrite(ofSeconds(30))
            .buildAsync(
                compositeKey ->
                    observerRepository.findObserverByProviderIdAndUserId(
                        compositeKey.providerId(), compositeKey.userId()));
  }

  @Override
  public CompletableFuture<Observer> getObserverByProviderIdAndUserId(
      final Long providerId, final Long userId) {
    return observerByProviderIdAndUserId
        .get(new ObserverCompositeKey(providerId, userId))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  @Override
  public CompletableFuture<Void> createObserver(final Observer observer) {
    return scheduler
        .run(ASYNC, () -> observerRepository.createObserver(observer))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  @Override
  public CompletableFuture<Void> updateObserver(final Observer observer) {
    return scheduler
        .run(ASYNC, () -> observerRepository.updateObserver(observer))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  private record ObserverCompositeKey(Long providerId, Long userId) {}
}
