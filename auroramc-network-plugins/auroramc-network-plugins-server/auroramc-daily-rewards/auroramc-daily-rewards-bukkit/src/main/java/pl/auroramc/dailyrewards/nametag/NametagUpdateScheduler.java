package pl.auroramc.dailyrewards.nametag;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed;
import static org.bukkit.Bukkit.getOnlinePlayers;

import java.time.Duration;
import org.bukkit.entity.Player;
import pl.auroramc.commons.duration.DurationFormatter;
import pl.auroramc.dailyrewards.message.MessageSource;
import pl.auroramc.dailyrewards.visit.VisitController;
import pl.auroramc.nametag.NametagFacade;

public class NametagUpdateScheduler implements Runnable {

  private final MessageSource messageSource;
  private final NametagFacade nametagFacade;
  private final VisitController visitController;
  private final DurationFormatter durationFormatter;

  public NametagUpdateScheduler(
      final MessageSource messageSource,
      final NametagFacade nametagFacade,
      final VisitController visitController,
      final DurationFormatter durationFormatter
  ) {
    this.messageSource = messageSource;
    this.nametagFacade = nametagFacade;
    this.visitController = visitController;
    this.durationFormatter = durationFormatter;
  }

  @Override
  public void run() {
    for (final Player player : getOnlinePlayers()) {
      updateNameTag(player);
    }
    nametagFacade.updateServerWide();
  }

  private void updateNameTag(final Player player) {
    final Duration visitPeriod = visitController.getVisitPeriod(player.getUniqueId());
    nametagFacade.belowName(player,
        miniMessage().deserialize(
            messageSource.belowName,
            unparsed("visit_period", durationFormatter.getFormattedDuration(visitPeriod))
        )
    );
  }
}
