package pl.auroramc.dailyrewards.visit;

import static java.time.Instant.now;
import static java.util.Comparator.comparing;
import static pl.auroramc.commons.period.PeriodFormatter.getFormattedPeriod;
import static pl.auroramc.commons.period.PeriodFormatter.getFormattedPeriodShortly;
import static pl.auroramc.commons.period.PeriodUtils.getMaximumTimeOfDay;
import static pl.auroramc.commons.period.PeriodUtils.getMinimumTimeOfDay;
import static pl.auroramc.dailyrewards.message.MutableMessageVariableKey.FROM_VARIABLE_KEY;
import static pl.auroramc.dailyrewards.message.MutableMessageVariableKey.PERIOD_VARIABLE_KEY;
import static pl.auroramc.dailyrewards.message.MutableMessageVariableKey.PLAYTIME_VARIABLE_KEY;
import static pl.auroramc.dailyrewards.message.MutableMessageVariableKey.SESSION_DITCH_VARIABLE_KEY;
import static pl.auroramc.dailyrewards.message.MutableMessageVariableKey.SESSION_START_VARIABLE_KEY;
import static pl.auroramc.dailyrewards.message.MutableMessageVariableKey.TO_VARIABLE_KEY;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;
import pl.auroramc.commons.duration.DurationFormatter;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.dailyrewards.message.MutableMessageSource;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

@Command(
    name = "visit",
    aliases = {"session", "sessions"})
public class VisitCommand {

  private final MutableMessageSource messageSource;
  private final UserFacade userFacade;
  private final VisitFacade visitFacade;
  private final DurationFormatter durationFormatter;

  public VisitCommand(
      final MutableMessageSource messageSource,
      final UserFacade userFacade,
      final VisitFacade visitFacade,
      final DurationFormatter durationFormatter) {
    this.messageSource = messageSource;
    this.userFacade = userFacade;
    this.visitFacade = visitFacade;
    this.durationFormatter = durationFormatter;
  }

  @Execute
  public CompletableFuture<MutableMessage> visit(final @Context Player player) {
    final Instant now = now();
    return userFacade
        .getUserByUniqueId(player.getUniqueId())
        .thenApply(User::getId)
        .thenApply(
            userId ->
                visitFacade.getVisitsByUserIdBetween(
                    userId, getMinimumTimeOfDay(now), getMaximumTimeOfDay(now)))
        .thenApply(visits -> getFormattedVisits(now, visits));
  }

  @Execute(name = "ranged")
  public CompletableFuture<MutableMessage> visitRanged(
      final @Context Player player, final @Arg Instant from, final @Arg Instant to) {
    return userFacade
        .getUserByUniqueId(player.getUniqueId())
        .thenApply(User::getId)
        .thenApply(userId -> visitFacade.getVisitsByUserIdBetween(userId, from, to))
        .thenApply(visits -> getFormattedVisits(from, to, visits));
  }

  private MutableMessage getFormattedVisitHeader(final Instant period) {
    return messageSource.visitDailySummary.with(
        PERIOD_VARIABLE_KEY, getFormattedPeriodShortly(period));
  }

  private MutableMessage getFormattedVisitHeader(final Instant from, final Instant to) {
    return messageSource
        .visitRangeSummary
        .with(FROM_VARIABLE_KEY, getFormattedPeriodShortly(from))
        .with(TO_VARIABLE_KEY, getFormattedPeriodShortly(to));
  }

  private MutableMessage getFormattedVisits(final Instant period, final Set<Visit> visits) {
    return getFormattedVisits(getFormattedVisitHeader(period), visits);
  }

  private MutableMessage getFormattedVisits(
      final Instant from, final Instant to, final Set<Visit> visits) {
    return getFormattedVisits(getFormattedVisitHeader(from, to), visits);
  }

  private MutableMessage getFormattedVisits(final MutableMessage header, final Set<Visit> visits) {
    return visits.isEmpty() ? messageSource.noVisits : header.append(getFormattedVisits(visits));
  }

  private MutableMessage getFormattedVisit(final Visit visit) {
    return messageSource
        .visitEntry
        .with(SESSION_START_VARIABLE_KEY, getFormattedPeriod(visit.getSessionStartTime()))
        .with(SESSION_DITCH_VARIABLE_KEY, getFormattedPeriod(visit.getSessionDitchTime()))
        .with(
            PLAYTIME_VARIABLE_KEY,
            durationFormatter.getFormattedDuration(visit.getSessionDuration()));
  }

  private MutableMessage getFormattedVisits(final Set<Visit> visits) {
    return visits.stream()
        .sorted(comparing(Visit::getSessionStartTime).reversed())
        .map(this::getFormattedVisit)
        .collect(MutableMessage.collector());
  }
}
