package pl.auroramc.quests.objective;

import static groovy.lang.Closure.DELEGATE_ONLY;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.ApiStatus.Internal;
import pl.auroramc.quests.objective.goal.DefinedObjectiveGoalResolver;
import pl.auroramc.quests.objective.goal.RangedObjectiveGoalResolver;
import pl.auroramc.quests.objective.requirement.ObjectiveRequirement;
import pl.auroramc.quests.objective.requirement.ObjectiveRequirementDsl;
import pl.auroramc.quests.resource.key.ResourceKey;
import pl.auroramc.quests.resource.key.ResourceKeyBuilder;

public final class ObjectiveBuilder<T> {

  private ResourceKey key;
  private Class<? extends Objective<?>> typeOfAction;
  private T type;
  private int saveInterval = 1;
  private ObjectiveGoalResolver goalResolver;
  private List<ObjectiveRequirement> requirements = new ArrayList<>();

  public ObjectiveBuilder<T> key(final ResourceKey key) {
    this.key = key;
    return this;
  }

  public ObjectiveBuilder<T> key(final @DelegatesTo(ResourceKeyBuilder.class) Closure<?> closure) {
    final ResourceKeyBuilder resourceKeyBuilder = new ResourceKeyBuilder();
    closure.setDelegate(resourceKeyBuilder);
    closure.setResolveStrategy(DELEGATE_ONLY);
    closure.call();
    return key(resourceKeyBuilder.build());
  }

  public ObjectiveBuilder<T> typeOfObjective(final Class<? extends Objective<?>> typeOfObjective) {
    this.typeOfAction = typeOfObjective;
    return this;
  }

  public ObjectiveBuilder<T> type(final T type) {
    this.type = type;
    return this;
  }

  @Internal
  public ObjectiveBuilder<T> saveInterval(final int saveInterval) {
    this.saveInterval = saveInterval;
    return this;
  }

  public ObjectiveBuilder<T> goal(final Integer definedGoal) {
    this.goalResolver = new DefinedObjectiveGoalResolver(definedGoal);
    return this;
  }

  public ObjectiveBuilder<T> goal(final Integer minimumGoal, final Integer maximumGoal) {
    this.goalResolver = new RangedObjectiveGoalResolver(minimumGoal, maximumGoal);
    return this;
  }

  public ObjectiveBuilder<T> requirements(final List<ObjectiveRequirement> requirements) {
    this.requirements = requirements;
    return this;
  }

  public ObjectiveBuilder<T> requirements(
      final @DelegatesTo(ObjectiveRequirementDsl.class) Closure<?> closure) {
    final ObjectiveRequirementDsl requirementDsl = new ObjectiveRequirementDsl();
    closure.setDelegate(requirementDsl);
    closure.setResolveStrategy(DELEGATE_ONLY);
    closure.call();
    return requirements(requirementDsl.requirements());
  }

  public <Y extends Objective<T>> Y build() {
    try {
      // noinspection unchecked
      return (Y) typeOfAction.getDeclaredConstructors()[0].newInstance(key, type, saveInterval, goalResolver, requirements);
    } catch (final InvocationTargetException | InstantiationException | IllegalAccessException exception) {
      throw new ObjectiveInstantiationException(
          "Could not create a new instance of objective, because of unexpected exception.",
          exception);
    }
  }
}
