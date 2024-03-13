package pl.auroramc.nametag.context;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class NametagContextService implements NametagContextFacade {

  private final Map<UUID, NametagContext> nametagContextByUniqueId;

  NametagContextService() {
    this.nametagContextByUniqueId = new HashMap<>();
  }

  @Override
  public NametagContext findNameTagContextByUniqueId(final UUID uniqueId) {
    return nametagContextByUniqueId.get(uniqueId);
  }

  @Override
  public void saveNameTagContext(final UUID uniqueId, final NametagContext nametagContext) {
    nametagContextByUniqueId.put(uniqueId, nametagContext);
  }
}
