package pl.auroramc.registry.resource.provider;

interface ResourceProviderRepository {

  ResourceProvider findResourceProviderByName(final String name);

  void createResourceProvider(final ResourceProvider resourceProvider);

  void updateResourceProvider(final ResourceProvider resourceProvider);
}
