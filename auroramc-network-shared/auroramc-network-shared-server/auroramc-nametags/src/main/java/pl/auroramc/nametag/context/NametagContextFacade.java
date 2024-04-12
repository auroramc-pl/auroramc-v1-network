package pl.auroramc.nametag.context;

import java.util.UUID;

public interface NametagContextFacade {

  static NametagContextFacade getNametagContextFacade() {
    return new NametagContextService();
  }

  NametagContext findNametagContextByUniqueId(final UUID uniqueId);

  void createNametagContext(final UUID uniqueId, final NametagContext context);

  void deleteNametagContext(final UUID uniqueId);
}
