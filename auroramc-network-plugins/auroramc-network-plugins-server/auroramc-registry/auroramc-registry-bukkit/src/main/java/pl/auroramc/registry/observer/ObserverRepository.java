package pl.auroramc.registry.observer;

interface ObserverRepository {

  Observer findObserverByProviderIdAndUserId(final Long providerId, final Long userId);

  void createObserver(final Observer observer);

  void updateObserver(final Observer observer);
}
