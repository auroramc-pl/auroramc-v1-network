package pl.auroramc.registry.provider;

public interface ProviderFacade {

  Provider getOrCreateProviderByName(final String name);

  Provider getProviderByName(final String name);

  void createProvider(final Provider provider);

  void updateProvider(final Provider provider);
}
