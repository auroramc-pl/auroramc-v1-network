package pl.auroramc.quests.objective;

import static java.util.Arrays.stream;
import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static pl.auroramc.quests.message.MessageVariableKey.DATA_VARIABLE_KEY;
import static pl.auroramc.quests.message.MessageVariableKey.GOAL_VARIABLE_KEY;
import static pl.auroramc.quests.message.MessageVariableKey.ITEM_VARIABLE_KEY;
import static pl.auroramc.quests.message.MessageVariableKey.TYPE_VARIABLE_KEY;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.StringUtils;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.quests.objective.progress.ObjectiveProgress;
import pl.auroramc.quests.objective.requirement.HeldItemRequirement;
import pl.auroramc.quests.objective.requirement.ObjectiveRequirement;
import pl.auroramc.quests.quest.Quest;

public final class ObjectiveUtils {

  private static final String EMPTY_ARGUMENT = "";

  private ObjectiveUtils() {

  }

  static <T extends Objective<?>> Map<Quest, List<T>> aggregateObjectives(
      final List<Quest> bunchOfQuests, final Class<T> objectiveType
  ) {
    return bunchOfQuests.stream()
        .collect(toMap(quest -> quest, quest -> quest.getObjectives(objectiveType)));
  }

  public static List<Component> getQuestObjective(
      final Objective<?> objective,
      final ObjectiveProgress objectiveProgress
  ) {
    final List<Component> aggregator = new ArrayList<>();

    aggregator.add(getQuestObjective0(objective, objectiveProgress).compile());
    for (final ObjectiveRequirement requirement : objective.getRequirements()) {
      aggregator.add(getObjectiveRequirement(requirement).compile());
    }

    return aggregator;
  }

  private static MutableMessage getQuestObjective0(
      final Objective<?> objective,
      final ObjectiveProgress objectiveProgress
  ) {
    return objective.getMessage()
        .with(TYPE_VARIABLE_KEY, getFormattedNameOfMaterial(objective.getType()))
        .with(DATA_VARIABLE_KEY, objectiveProgress.getData())
        .with(GOAL_VARIABLE_KEY, objectiveProgress.getGoal());
  }

  private static MutableMessage getObjectiveRequirement(
      final ObjectiveRequirement requirement
  ) {
    return requirement.getMessage()
        .with(ITEM_VARIABLE_KEY,
            requirement instanceof HeldItemRequirement heldItemRequirement
                ? getFormattedNameOfMaterial(heldItemRequirement.getRequiredMaterial())
                : EMPTY_ARGUMENT
        );
  }

  private static String getFormattedNameOfMaterial(final Object material) {
    return stream(material.toString().toLowerCase(ROOT).split("_"))
        .map(StringUtils::capitalize)
        .collect(joining(" "));
  }
}
