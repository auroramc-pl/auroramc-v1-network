package pl.auroramc.bounties.bounty;

import static groovy.lang.Closure.DELEGATE_ONLY;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

class BountyDsl {

  BountyDsl() {}

  public static Bounty bounty(final @DelegatesTo(BountyBuilder.class) Closure<?> closure) {
    final BountyBuilder delegate = new BountyBuilder();
    closure.setDelegate(delegate);
    closure.setResolveStrategy(DELEGATE_ONLY);
    closure.call();
    return delegate.build();
  }
}
