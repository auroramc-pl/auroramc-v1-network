package pl.auroramc.quests.quest;

import static groovy.lang.Closure.DELEGATE_ONLY;
import static org.bukkit.Bukkit.getServer;
import static org.bukkit.Material.STONE;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import pl.auroramc.commons.item.ItemStackBuilder;
import pl.auroramc.quests.objective.Objective;
import pl.auroramc.quests.objective.ObjectiveBuilder;
import pl.auroramc.quests.quest.reward.ExecQuestReward;
import pl.auroramc.quests.quest.reward.ItemQuestReward;
import pl.auroramc.quests.quest.reward.QuestReward;

class QuestDsl {

  QuestDsl() {}

  public static Quest quest(final @DelegatesTo(QuestBuilder.class) Closure<?> closure) {
    final QuestBuilder delegate = new QuestBuilder();
    closure.setDelegate(delegate);
    closure.setResolveStrategy(DELEGATE_ONLY);
    closure.call();
    return delegate.build();
  }

  static class ObjectivesDsl {

    private final List<Objective<?>> objectives;

    ObjectivesDsl() {
      this.objectives = new ArrayList<>();
    }

    public void objective(final @DelegatesTo(ObjectiveBuilder.class) Closure<?> closure) {
      final ObjectiveBuilder<?> delegate = new ObjectiveBuilder<>();
      closure.setDelegate(delegate);
      closure.setResolveStrategy(DELEGATE_ONLY);
      closure.call();
      objectives.add(delegate.build());
    }

    public List<Objective<?>> objectives() {
      return objectives;
    }
  }

  static class QuestRewardsDsl {

    private final List<QuestReward<?>> rewards;

    QuestRewardsDsl() {
      this.rewards = new ArrayList<>();
    }

    public void item(final ItemStack item) {
      rewards.add(new ItemQuestReward(item));
    }

    public void item(final @DelegatesTo(ItemStackBuilder.class) Closure<?> closure) {
      final ItemStackBuilder delegate = ItemStackBuilder.newBuilder(STONE);
      closure.setDelegate(delegate);
      closure.setResolveStrategy(DELEGATE_ONLY);
      closure.call();
      rewards.add(new ItemQuestReward(delegate.build()));
    }

    public void exec(final List<String> commands) {
      rewards.add(new ExecQuestReward(getServer(), commands));
    }

    public void exec(final String... commands) {
      exec(List.of(commands));
    }

    public List<QuestReward<?>> rewards() {
      return rewards;
    }
  }
}
