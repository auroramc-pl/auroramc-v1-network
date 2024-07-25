package pl.auroramc.registry.resource.provider;

class ResourceProviderService implements ResourceProviderFacade {

  private final ResourceProviderRepository resourceProviderRepository;

  ResourceProviderService(final ResourceProviderRepository resourceProviderRepository) {
    this.resourceProviderRepository = resourceProviderRepository;
  }

  @Override
  public ResourceProvider resolveResourceProviderByName(final String name) {
    final ResourceProvider oldResourceProvider = getResourceProviderByName(name);
    if (oldResourceProvider != null) {
      return oldResourceProvider;
    }

    final ResourceProvider newResourceProvider =
        ResourceProviderBuilder.newBuilder().withName(name).build();
    createResourceProvider(newResourceProvider);
    return newResourceProvider;
  }

  @Override
  public ResourceProvider getResourceProviderByName(final String name) {
    return resourceProviderRepository.findResourceProviderByName(name);
  }

  @Override
  public void createResourceProvider(final ResourceProvider resourceProvider) {
    resourceProviderRepository.createResourceProvider(resourceProvider);
  }

  @Override
  public void updateResourceProvider(final ResourceProvider resourceProvider) {
    resourceProviderRepository.updateResourceProvider(resourceProvider);
  }
}
