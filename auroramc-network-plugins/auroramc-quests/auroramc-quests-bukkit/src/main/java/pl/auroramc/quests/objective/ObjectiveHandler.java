package pl.auroramc.quests.objective;

import org.bukkit.event.Event;
import pl.auroramc.quests.quest.Quest;

interface ObjectiveHandler<T extends Objective<?>, E extends Event> {

  void validateObjectiveGoal(final Quest quest, final T objective, final E event);
}

