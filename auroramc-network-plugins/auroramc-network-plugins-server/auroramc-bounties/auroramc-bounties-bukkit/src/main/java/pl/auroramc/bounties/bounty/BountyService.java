package pl.auroramc.bounties.bounty;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static pl.auroramc.commons.eager.Eager.eager;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import pl.auroramc.commons.eager.Eager;
import pl.auroramc.integrations.dsl.BukkitDiscoveryService;

class BountyService extends BukkitDiscoveryService<Bounty> implements BountyFacade {

  private final Eager<List<Bounty>> bounties;
  private final Eager<Map<Long, Bounty>> bountiesByDay;

  BountyService(final ClassLoader parentClassLoader, final Path bountyDefinitionsPath) {
    super(parentClassLoader, Bounty.class);
    this.bounties = eager(() -> getElementDefinitions(bountyDefinitionsPath).stream().toList());
    this.bountiesByDay = eager(() -> getBountiesByDay(bounties.get()));
  }

  @Override
  public Bounty getBountyByDay(final Long day) {
    return bountiesByDay.get().get(day);
  }

  @Override
  public List<Bounty> getBounties() {
    return bounties.get();
  }

  private Map<Long, Bounty> getBountiesByDay(final List<Bounty> bounties) {
    return bounties.stream().collect(toMap(Bounty::getDay, identity()));
  }
}
