package pl.auroramc.registry.resource.provider;

public interface ResourceProviderFacade {

  ResourceProvider resolveResourceProviderByName(final String name);

  ResourceProvider getResourceProviderByName(final String name);

  void createResourceProvider(final ResourceProvider resourceProvider);

  void updateResourceProvider(final ResourceProvider resourceProvider);
}
