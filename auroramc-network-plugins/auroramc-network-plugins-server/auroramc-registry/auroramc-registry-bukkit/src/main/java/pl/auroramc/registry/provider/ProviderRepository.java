package pl.auroramc.registry.provider;

interface ProviderRepository {

  Provider findProviderByName(final String name);

  void createProvider(final Provider provider);

  void updateProvider(final Provider provider);
}
