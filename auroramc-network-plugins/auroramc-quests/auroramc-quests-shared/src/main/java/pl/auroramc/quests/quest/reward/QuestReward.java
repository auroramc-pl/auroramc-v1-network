package pl.auroramc.quests.quest.reward;

@FunctionalInterface
public interface QuestReward<T> {

  void apply(final T target);
}
