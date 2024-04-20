package pl.auroramc.quests.objective;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import pl.auroramc.quests.quest.Quest;

public final class ObjectiveUtils {

  private ObjectiveUtils() {}

  static <T extends Objective<?>> Map<Quest, List<T>> aggregateObjectives(
      final List<Quest> bunchOfQuests, final Class<T> objectiveType) {
    return bunchOfQuests.stream()
        .collect(toMap(quest -> quest, quest -> quest.getObjectives(objectiveType)));
  }
}
