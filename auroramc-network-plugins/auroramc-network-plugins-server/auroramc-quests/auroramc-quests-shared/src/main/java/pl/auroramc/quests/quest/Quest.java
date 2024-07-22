package pl.auroramc.quests.quest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus.Internal;
import pl.auroramc.integrations.reward.Reward;
import pl.auroramc.quests.objective.Objective;
import pl.auroramc.registry.resource.Resource;
import pl.auroramc.registry.resource.key.ResourceKey;

public final class Quest extends Resource {

  private final Map<Class<? extends Objective<?>>, List<? extends Objective<?>>> objectivesByType =
      new ConcurrentHashMap<>();
  private final ItemStack icon;
  private final List<Objective<?>> objectives;
  private final List<Reward> rewards;
  private final int weight;

  Quest(
      final ResourceKey key,
      final ItemStack icon,
      final List<Objective<?>> objectives,
      final List<Reward> rewards,
      final int weight) {
    super(key);
    this.icon = icon;
    this.objectives = objectives;
    this.rewards = rewards;
    this.weight = weight;
  }

  @Override
  public List<? extends Resource> children() {
    return List.copyOf(objectives);
  }

  public ItemStack getIcon() {
    return icon;
  }

  public List<Objective<?>> getObjectives() {
    return List.copyOf(objectives);
  }

  public <T extends Objective<?>> List<T> getObjectives(final Class<T> objectiveType) {
    objectivesByType.computeIfAbsent(objectiveType, key -> getObjectivesRaw(objectiveType));
    // noinspection unchecked
    return (List<T>) objectivesByType.get(objectiveType);
  }

  public <T extends Objective<?>> T getObjectiveByObjectiveId(
      final Class<T> objectiveType, final long objectiveId) {
    return objectives.stream()
        .filter(objectiveType::isInstance)
        .filter(objective -> objective.getKey().getId() == objectiveId)
        .map(objectiveType::cast)
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Objective with id %s not found.".formatted(objectiveId)));
  }

  public Objective<?> getObjectiveByObjectiveId(final long objectiveId) {
    return getObjectiveByObjectiveId(Objective.class, objectiveId);
  }

  @Internal
  private <T extends Objective<?>> List<T> getObjectivesRaw(final Class<T> objectiveType) {
    return getObjectives().stream()
        .filter(objectiveType::isInstance)
        .map(objectiveType::cast)
        .toList();
  }

  public List<Reward> getRewards() {
    return rewards;
  }

  public int getWeight() {
    return weight;
  }
}
