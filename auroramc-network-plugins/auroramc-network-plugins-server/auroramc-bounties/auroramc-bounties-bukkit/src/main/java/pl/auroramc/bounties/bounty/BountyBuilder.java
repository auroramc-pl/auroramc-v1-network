package pl.auroramc.bounties.bounty;

import static groovy.lang.Closure.DELEGATE_ONLY;
import static org.bukkit.Material.STONE;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import pl.auroramc.integrations.item.ItemStackBuilder;
import pl.auroramc.integrations.reward.Reward;
import pl.auroramc.integrations.reward.RewardsDsl;
import pl.auroramc.registry.resource.key.ResourceKey;
import pl.auroramc.registry.resource.key.ResourceKeyBuilder;

public final class BountyBuilder {

  private ResourceKey key;
  private ItemStack icon;
  private int day;
  private List<Reward> rewards = List.of();

  public BountyBuilder key(final ResourceKey key) {
    this.key = key;
    return this;
  }

  public BountyBuilder key(final @DelegatesTo(ResourceKeyBuilder.class) Closure<?> closure) {
    final ResourceKeyBuilder resourceKeyBuilder = new ResourceKeyBuilder();
    closure.setDelegate(resourceKeyBuilder);
    closure.setResolveStrategy(DELEGATE_ONLY);
    closure.call();
    return key(resourceKeyBuilder.build());
  }

  public BountyBuilder icon(final ItemStack icon) {
    this.icon = icon;
    return this;
  }

  public BountyBuilder icon(final @DelegatesTo(ItemStackBuilder.class) Closure<?> closure) {
    final ItemStackBuilder itemStackBuilder = ItemStackBuilder.newBuilder(STONE);
    closure.setDelegate(itemStackBuilder);
    closure.setResolveStrategy(DELEGATE_ONLY);
    closure.call();
    return icon(itemStackBuilder.build());
  }

  public BountyBuilder day(final int day) {
    this.day = day;
    return this;
  }

  public BountyBuilder rewards(final List<Reward> rewards) {
    this.rewards = rewards;
    return this;
  }

  public BountyBuilder rewards(final @DelegatesTo(value = RewardsDsl.class) Closure<?> closure) {
    final RewardsDsl rewardsDsl = new RewardsDsl();
    closure.setDelegate(rewardsDsl);
    closure.setResolveStrategy(DELEGATE_ONLY);
    closure.call();
    return rewards(rewardsDsl.rewards());
  }

  public Bounty build() {
    return new Bounty(key, icon, day, rewards);
  }
}
