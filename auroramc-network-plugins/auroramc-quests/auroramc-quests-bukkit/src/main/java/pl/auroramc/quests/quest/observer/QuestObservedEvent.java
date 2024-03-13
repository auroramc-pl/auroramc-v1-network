package pl.auroramc.quests.quest.observer;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import pl.auroramc.quests.quest.Quest;

public class QuestObservedEvent extends Event {

  private static final HandlerList HANDLER_LIST = new HandlerList();
  private final Player player;
  private final Quest observedQuest;
  private final Quest previousQuest;

  public QuestObservedEvent(final Player player, final Quest observedQuest, final Quest previousQuest) {
    this.player = player;
    this.observedQuest = observedQuest;
    this.previousQuest = previousQuest;
  }

  public Player getPlayer() {
    return player;
  }

  public Quest getObservedQuest() {
    return observedQuest;
  }

  public Quest getPreviousQuest() {
    return previousQuest;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLER_LIST;
  }

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }
}
