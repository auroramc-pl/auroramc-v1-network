package pl.auroramc.scoreboard.quest;

//import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
//import pl.auroramc.quests.quest.observer.QuestObservedEvent;
import pl.auroramc.scoreboard.sidebar.SidebarRenderer;

public class QuestListener implements Listener {

  private final SidebarRenderer sidebarRenderer;

  public QuestListener(final SidebarRenderer sidebarRenderer) {
    this.sidebarRenderer = sidebarRenderer;
  }

//  @EventHandler
//  public void onSidebarUpdateRequest(final QuestObservedEvent event) {
//    sidebarRenderer.render(event.getPlayer());
//  }
}
