package pl.auroramc.registry.observer;

import java.util.concurrent.CompletableFuture;

public interface ObserverFacade {

  CompletableFuture<Observer> getObserverByProviderIdAndUserId(
      final Long providerId, final Long userId);

  CompletableFuture<Void> createObserver(final Observer observer);

  CompletableFuture<Void> updateObserver(final Observer observer);
}
