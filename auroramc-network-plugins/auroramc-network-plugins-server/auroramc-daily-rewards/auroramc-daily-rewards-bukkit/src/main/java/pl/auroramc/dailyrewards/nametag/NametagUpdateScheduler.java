package pl.auroramc.dailyrewards.nametag;

import static org.bukkit.Bukkit.getOnlinePlayers;
import static pl.auroramc.dailyrewards.message.MutableMessageVariableKey.PLAYTIME_VARIABLE_KEY;

import java.time.Duration;
import org.bukkit.entity.Player;
import pl.auroramc.commons.duration.DurationFormatter;
import pl.auroramc.dailyrewards.message.MutableMessageSource;
import pl.auroramc.dailyrewards.visit.VisitController;
import pl.auroramc.nametag.NametagFacade;

public class NametagUpdateScheduler implements Runnable {

  private final MutableMessageSource messageSource;
  private final NametagFacade nametagFacade;
  private final VisitController visitController;
  private final DurationFormatter durationFormatter;

  public NametagUpdateScheduler(
      final MutableMessageSource messageSource,
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
        messageSource.belowName
            .with(PLAYTIME_VARIABLE_KEY, durationFormatter.getFormattedDuration(visitPeriod))
            .compile()
    );
  }
}
