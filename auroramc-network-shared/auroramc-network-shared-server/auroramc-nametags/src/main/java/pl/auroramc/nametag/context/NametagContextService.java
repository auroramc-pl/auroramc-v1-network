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
  public NametagContext findNametagContextByUniqueId(final UUID uniqueId) {
    return nametagContextByUniqueId.get(uniqueId);
  }

  @Override
  public void createNametagContext(final UUID uniqueId, final NametagContext context) {
    nametagContextByUniqueId.put(uniqueId, context);
  }

  @Override
  public void deleteNametagContext(final UUID uniqueId) {
    nametagContextByUniqueId.remove(uniqueId);
  }
}
