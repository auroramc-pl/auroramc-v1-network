package pl.auroramc.registry.observer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import pl.auroramc.commons.concurrent.CompletableFutureUtils;
import pl.auroramc.registry.provider.Provider;
import pl.auroramc.registry.provider.ProviderFacade;
import pl.auroramc.registry.user.UserFacade;

public class ObserverController {

  private final UserFacade userFacade;
  private final ProviderFacade providerFacade;
  private final ObserverFacade observerFacade;

  public ObserverController(
      final UserFacade userFacade,
      final ProviderFacade providerFacade,
      final ObserverFacade observerFacade) {
    this.userFacade = userFacade;
    this.providerFacade = providerFacade;
    this.observerFacade = observerFacade;
  }

  public CompletableFuture<Boolean> toggleNotifications(
      final String providerName, final UUID playerUniqueId) {
    final Provider provider = providerFacade.getProviderByName(providerName);
    return userFacade
        .getUserByUniqueId(playerUniqueId)
        .thenCompose(
            user -> observerFacade.getObserverByProviderIdAndUserId(provider.getId(), user.getId()))
        .thenCompose(this::negateNotificationState)
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  private CompletableFuture<Boolean> negateNotificationState(final Observer observer) {
    final boolean initialState = observer.isEnabled();
    final boolean negatedState = !initialState;
    observer.setEnabled(negatedState);
    return observerFacade.updateObserver(observer).thenApply(state -> negatedState);
  }
}
