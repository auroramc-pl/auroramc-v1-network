package pl.auroramc.bounties.bounty;

import java.util.List;

public interface BountyFacade {

  Bounty getBountyByDay(final Long day);

  List<Bounty> getBounties();
}
