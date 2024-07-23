package pl.auroramc.bounties.bounty;

import java.util.List;
import org.bukkit.inventory.ItemStack;
import pl.auroramc.integrations.reward.Reward;
import pl.auroramc.registry.resource.Resource;
import pl.auroramc.registry.resource.key.ResourceKey;

public class Bounty extends Resource {

  private final ItemStack icon;
  private final long day;
  private final List<Reward> rewards;

  protected Bounty(
      final ResourceKey key, final ItemStack icon, final int day, final List<Reward> rewards) {
    super(key);
    this.icon = icon;
    this.day = day;
    this.rewards = rewards;
  }

  public ItemStack getIcon() {
    return icon;
  }

  public long getDay() {
    return day;
  }

  public List<Reward> getRewards() {
    return rewards;
  }
}
