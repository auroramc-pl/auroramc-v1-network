package pl.auroramc.quests.objective;

import static java.util.Arrays.stream;
import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.concat;
import static pl.auroramc.quests.message.MutableMessageVariableKey.DATA_PATH;
import static pl.auroramc.quests.message.MutableMessageVariableKey.GOAL_PATH;
import static pl.auroramc.quests.message.MutableMessageVariableKey.ITEM_PATH;
import static pl.auroramc.quests.message.MutableMessageVariableKey.TYPE_PATH;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.quests.objective.progress.ObjectiveProgress;
import pl.auroramc.quests.objective.requirement.HeldItemRequirement;
import pl.auroramc.quests.objective.requirement.ObjectiveRequirement;
import pl.auroramc.quests.quest.Quest;

public final class ObjectiveUtils {

  private static final String EMPTY_ARGUMENT = "";

  private ObjectiveUtils() {}

  static <T extends Objective<?>> Map<Quest, List<T>> aggregateObjectives(
      final List<Quest> bunchOfQuests, final Class<T> objectiveType) {
    return bunchOfQuests.stream()
        .collect(toMap(quest -> quest, quest -> quest.getObjectives(objectiveType)));
  }

  public static MutableMessage getQuestObjectives(
      final List<? extends Objective<?>> objectives,
      final Map<? extends Objective<?>, ObjectiveProgress> objectiveToObjectiveProgress) {
    return objectives.stream()
        .map(objective -> getQuestObjective(objective, objectiveToObjectiveProgress.get(objective)))
        .collect(MutableMessage.collector());
  }

  public static String getQuestObjectivesTemplate(
      final List<? extends Objective<?>> objectives,
      final Map<? extends Objective<?>, ObjectiveProgress> objectiveToObjectiveProgress) {
    return getQuestObjectives(objectives, objectiveToObjectiveProgress).getTemplate();
  }

  public static MutableMessage getQuestObjective(
      final Objective<?> objective, final ObjectiveProgress objectiveProgress) {
    return concat(
            Stream.of(getQuestObjective0(objective, objectiveProgress)),
            objective.getRequirements().stream().map(ObjectiveUtils::getObjectiveRequirement))
        .collect(MutableMessage.collector());
  }

  public static String getQuestObjectiveTemplate(
      final Objective<?> objective, final ObjectiveProgress objectiveProgress) {
    return getQuestObjective(objective, objectiveProgress).getTemplate();
  }

  private static MutableMessage getQuestObjective0(
      final Objective<?> objective, final ObjectiveProgress objectiveProgress) {
    return objective
        .getMessage()
        .with(TYPE_PATH, getFormattedNameOfMaterial(objective.getType()))
        .with(DATA_PATH, objectiveProgress.getData())
        .with(GOAL_PATH, objectiveProgress.getGoal());
  }

  private static MutableMessage getObjectiveRequirement(final ObjectiveRequirement requirement) {
    return requirement
        .getMessage()
        .with(
            ITEM_PATH,
            requirement instanceof HeldItemRequirement heldItemRequirement
                ? getFormattedNameOfMaterial(heldItemRequirement.getRequiredMaterial())
                : EMPTY_ARGUMENT);
  }

  private static String getFormattedNameOfMaterial(final Object material) {
    return stream(material.toString().toLowerCase(ROOT).split("_"))
        .map(StringUtils::capitalize)
        .collect(joining(" "));
  }
}
