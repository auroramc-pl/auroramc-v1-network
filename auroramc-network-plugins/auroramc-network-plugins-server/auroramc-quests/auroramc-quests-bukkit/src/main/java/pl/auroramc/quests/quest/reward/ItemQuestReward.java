package pl.auroramc.quests.quest.reward;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemQuestReward implements QuestReward<Player> {

  private final ItemStack item;

  public ItemQuestReward(final ItemStack item) {
    this.item = item;
  }

  @Override
  public void apply(final Player target) {
    target
        .getInventory()
        .addItem(item)
        .forEach(
            (index, remainingItem) ->
                target.getWorld().dropItemNaturally(target.getLocation(), remainingItem));
  }
}
