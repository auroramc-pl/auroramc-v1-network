package pl.auroramc.quests.objective.requirement;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus.Internal;
import pl.auroramc.messages.message.MutableMessage;

public interface ObjectiveRequirement {

  boolean isValid(final Player viewer);

  MutableMessage getMessage();

  @Internal
  void setMessage(final MutableMessage message);
}
