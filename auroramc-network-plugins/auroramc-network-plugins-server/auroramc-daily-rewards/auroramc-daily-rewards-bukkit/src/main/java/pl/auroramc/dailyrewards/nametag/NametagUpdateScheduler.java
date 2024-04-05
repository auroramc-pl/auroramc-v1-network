package pl.auroramc.dailyrewards.nametag;

import static org.bukkit.Bukkit.getOnlinePlayers;
import static pl.auroramc.dailyrewards.message.MessageSourcePaths.DURATION_PATH;

import org.bukkit.entity.Player;
import pl.auroramc.dailyrewards.message.MessageSource;
import pl.auroramc.dailyrewards.visit.VisitController;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.nametag.NametagFacade;

public class NametagUpdateScheduler implements Runnable {

  private final MessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;
  private final NametagFacade nametagFacade;
  private final VisitController visitController;

  public NametagUpdateScheduler(
      final MessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final NametagFacade nametagFacade,
      final VisitController visitController) {
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
    this.nametagFacade = nametagFacade;
    this.visitController = visitController;
  }

  @Override
  public void run() {
    for (final Player player : getOnlinePlayers()) {
      updateNameTag(player);
    }
    nametagFacade.updateServerWide();
  }

  private void updateNameTag(final Player player) {
    nametagFacade.belowName(
        player,
        messageCompiler
            .compile(
                messageSource.belowName.placeholder(
                    DURATION_PATH, visitController.getVisitDuration(player.getUniqueId())))
            .getComponent());
  }
}
