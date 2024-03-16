package pl.auroramc.scoreboard.quest;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.auroramc.quests.quest.observer.QuestObservedEvent;
import pl.auroramc.scoreboard.ScoreboardConfig;
import pl.auroramc.scoreboard.sidebar.SidebarRenderer;

public class QuestListener implements Listener {

  private final ScoreboardConfig scoreboardConfig;
  private final SidebarRenderer sidebarRenderer;

  public QuestListener(
      final ScoreboardConfig scoreboardConfig,
      final SidebarRenderer sidebarRenderer
  ) {
    this.scoreboardConfig = scoreboardConfig;
    this.sidebarRenderer = sidebarRenderer;
  }

  @EventHandler
  public void onSidebarUpdateRequest(final QuestObservedEvent event) {
    if (scoreboardConfig.renderOnce) {
      return;
    }

    sidebarRenderer.render(event.getPlayer());
  }
}
