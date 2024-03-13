package pl.auroramc.nametag.context;

import java.util.UUID;

public interface NametagContextFacade {

  static NametagContextFacade getNametagContextFacade() {
    return new NametagContextService();
  }

  NametagContext findNameTagContextByUniqueId(final UUID uniqueId);

  void saveNameTagContext(final UUID uniqueId, final NametagContext nametagContext);
}
