package pl.auroramc.quests.quest;

import static groovy.lang.Closure.DELEGATE_ONLY;
import static org.bukkit.Material.STONE;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import pl.auroramc.integrations.item.ItemStackBuilder;
import pl.auroramc.quests.objective.Objective;
import pl.auroramc.quests.quest.QuestDsl.ObjectivesDsl;
import pl.auroramc.quests.quest.QuestDsl.QuestRewardsDsl;
import pl.auroramc.quests.quest.reward.QuestReward;
import pl.auroramc.quests.resource.key.ResourceKey;
import pl.auroramc.quests.resource.key.ResourceKeyBuilder;

public final class QuestBuilder {

  private ResourceKey key;
  private ItemStack icon;
  private List<Objective<?>> objectives = List.of();
  private List<QuestReward<?>> rewards = List.of();
  private int weight;

  QuestBuilder() {}

  public QuestBuilder key(final ResourceKey key) {
    this.key = key;
    return this;
  }

  public QuestBuilder key(final @DelegatesTo(ResourceKeyBuilder.class) Closure<?> closure) {
    final ResourceKeyBuilder resourceKeyBuilder = new ResourceKeyBuilder();
    closure.setDelegate(resourceKeyBuilder);
    closure.setResolveStrategy(DELEGATE_ONLY);
    closure.call();
    return key(resourceKeyBuilder.build());
  }

  public QuestBuilder icon(final @DelegatesTo(ItemStackBuilder.class) Closure<?> closure) {
    final ItemStackBuilder itemStackBuilder = ItemStackBuilder.newBuilder(STONE);
    closure.setDelegate(itemStackBuilder);
    closure.setResolveStrategy(DELEGATE_ONLY);
    closure.call();
    this.icon = itemStackBuilder.build();
    return this;
  }

  public QuestBuilder objectives(final List<Objective<?>> objectives) {
    this.objectives = objectives;
    return this;
  }

  public QuestBuilder objectives(
      final @DelegatesTo(value = ObjectivesDsl.class) Closure<?> closure) {
    final ObjectivesDsl objectivesDsl = new ObjectivesDsl();
    closure.setDelegate(objectivesDsl);
    closure.setResolveStrategy(DELEGATE_ONLY);
    closure.call();
    return objectives(objectivesDsl.objectives());
  }

  public QuestBuilder rewards(final List<QuestReward<?>> rewards) {
    this.rewards = rewards;
    return this;
  }

  public QuestBuilder rewards(
      final @DelegatesTo(value = QuestRewardsDsl.class) Closure<?> closure) {
    final QuestRewardsDsl rewardsDsl = new QuestRewardsDsl();
    closure.setDelegate(rewardsDsl);
    closure.setResolveStrategy(DELEGATE_ONLY);
    closure.call();
    return rewards(rewardsDsl.rewards());
  }

  public QuestBuilder weight(final int weight) {
    this.weight = weight;
    return this;
  }

  public Quest build() {
    return new Quest(key, icon, objectives, rewards, weight);
  }
}
