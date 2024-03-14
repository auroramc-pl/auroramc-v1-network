package pl.auroramc.dailyrewards.visit;

import static java.time.Instant.now;
import static java.util.Comparator.comparing;
import static pl.auroramc.commons.period.PeriodFormatter.getFormattedPeriod;
import static pl.auroramc.commons.period.PeriodFormatter.getFormattedPeriodShortly;
import static pl.auroramc.commons.period.PeriodUtils.getMaximumTimeOfDay;
import static pl.auroramc.commons.period.PeriodUtils.getMinimumTimeOfDay;

import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.route.Route;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;
import pl.auroramc.commons.duration.DurationFormatter;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.dailyrewards.message.MessageSource;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

@Route(name = "visit", aliases = {"session", "sessions"})
public class VisitCommand {

  private final MessageSource messageSource;
  private final UserFacade userFacade;
  private final VisitFacade visitFacade;
  private final DurationFormatter durationFormatter;

  public VisitCommand(
      final MessageSource messageSource,
      final UserFacade userFacade,
      final VisitFacade visitFacade,
      final DurationFormatter durationFormatter
  ) {
    this.messageSource = messageSource;
    this.userFacade = userFacade;
    this.visitFacade = visitFacade;
    this.durationFormatter = durationFormatter;
  }

  @Execute
  public CompletableFuture<MutableMessage> getSessionsFromToday(final Player player) {
    final Instant now = now();
    return userFacade.getUserByUniqueId(player.getUniqueId())
        .thenApply(User::getId)
        .thenApply(userId ->
            visitFacade.getVisitsByUserIdBetween(userId,
                getMinimumTimeOfDay(now),
                getMaximumTimeOfDay(now)
            )
        )
        .thenApply(visits -> getFormattedVisits(now, visits));
  }

  @Execute(route = "ranged")
  public CompletableFuture<MutableMessage> getSessions(
      final Player player, final @Arg Instant from, final @Arg Instant to
  ) {
    return userFacade.getUserByUniqueId(player.getUniqueId())
        .thenApply(User::getId)
        .thenApply(userId -> visitFacade.getVisitsByUserIdBetween(userId, from, to))
        .thenApply(visits -> getFormattedVisits(from, to, visits));
  }

  private MutableMessage getFormattedVisitHeader(final Instant period) {
    return messageSource.visitDailySummary
        .with("period", getFormattedPeriodShortly(period));
  }

  private MutableMessage getFormattedVisitHeader(final Instant from, final Instant to) {
    return messageSource.visitRangeSummary
        .with("from", getFormattedPeriodShortly(from))
        .with("to", getFormattedPeriodShortly(to));
  }

  private MutableMessage getFormattedVisits(final Instant period, final Set<Visit> visits) {
    return getFormattedVisits(getFormattedVisitHeader(period), visits);
  }

  private MutableMessage getFormattedVisits(final Instant from, final Instant to, final Set<Visit> visits) {
    return getFormattedVisits(getFormattedVisitHeader(from, to), visits);
  }

  private MutableMessage getFormattedVisits(final MutableMessage header, final Set<Visit> visits) {
    return visits.isEmpty()
        ? messageSource.noVisits
        : header.append(getFormattedVisits(visits));
  }

  private MutableMessage getFormattedVisit(final Visit visit) {
    return messageSource.visitEntry
        .with("session_start", getFormattedPeriod(visit.getSessionStartTime()))
        .with("session_ditch", getFormattedPeriod(visit.getSessionDitchTime()))
        .with("playtime", durationFormatter.getFormattedDuration(visit.getSessionDuration()));
  }

  private MutableMessage getFormattedVisits(final Set<Visit> visits) {
    return visits.stream()
        .sorted(comparing(Visit::getSessionStartTime).reversed())
        .map(this::getFormattedVisit)
        .collect(MutableMessage.collector());
  }
}
