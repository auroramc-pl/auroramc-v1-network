package pl.auroramc.registry.provider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class ProviderService implements ProviderFacade {

  private final ProviderRepository providerRepository;
  private final Map<String, Provider> providerByProviderName;

  ProviderService(final ProviderRepository providerRepository) {
    this.providerRepository = providerRepository;
    this.providerByProviderName = new ConcurrentHashMap<>();
  }

  @Override
  public Provider getOrCreateProviderByName(final String name) {
    final Provider cachedProvider = providerByProviderName.get(name);
    if (cachedProvider != null) {
      return cachedProvider;
    }

    final Provider oldProvider = getProviderByName(name);
    if (oldProvider != null) {
      return cacheAndGetProvider(oldProvider);
    }

    final Provider newProvider = ProviderBuilder.newBuilder().withName(name).build();
    createProvider(newProvider);
    return cacheAndGetProvider(newProvider);
  }

  @Override
  public Provider getProviderByName(final String name) {
    return providerRepository.findProviderByName(name);
  }

  @Override
  public void createProvider(final Provider provider) {
    providerRepository.createProvider(provider);
  }

  @Override
  public void updateProvider(final Provider provider) {
    providerRepository.updateProvider(provider);
  }

  private Provider cacheAndGetProvider(final Provider provider) {
    providerByProviderName.put(provider.getName(), provider);
    return provider;
  }
}
