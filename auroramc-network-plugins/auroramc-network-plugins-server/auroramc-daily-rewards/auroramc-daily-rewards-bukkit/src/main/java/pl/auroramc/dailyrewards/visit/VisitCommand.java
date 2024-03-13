package pl.auroramc.dailyrewards.visit;

import static java.time.Instant.now;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
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
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import pl.auroramc.commons.duration.DurationFormatter;
import pl.auroramc.dailyrewards.message.MessageSource;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

@Route(name = "visit", aliases = {"session", "sessions"})
public class VisitCommand {

  private static final String LINE_SEPARATOR = "<newline>";
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
  public CompletableFuture<Component> getSessionsFromToday(
      final Player executor
  ) {
    final Instant now = now();
    return userFacade.getUserByUniqueId(executor.getUniqueId())
        .thenApply(User::getId)
        .thenApply(userId ->
            visitFacade.getVisitsByUserIdBetween(userId,
                getMinimumTimeOfDay(now),
                getMaximumTimeOfDay(now)
            )
        )
        .thenApply(visits -> getFormattedVisits(now, visits))
        .thenApply(miniMessage()::deserialize);
  }

  @Execute(route = "ranged")
  public CompletableFuture<Component> getSessions(
      final Player executor, final @Arg Instant from, final @Arg Instant to
  ) {
    return userFacade.getUserByUniqueId(executor.getUniqueId())
        .thenApply(User::getId)
        .thenApply(userId -> visitFacade.getVisitsByUserIdBetween(userId, from, to))
        .thenApply(visits -> getFormattedVisits(from, to, visits))
        .thenApply(miniMessage()::deserialize);
  }

  private String getFormattedVisitHeader(final Instant period) {
    return messageSource.visitDailySummary.formatted(
        getFormattedPeriodShortly(period)
    );
  }

  private String getFormattedVisitHeader(final Instant from, final Instant to) {
    return messageSource.visitRangeSummary
        .formatted(
            getFormattedPeriodShortly(from),
            getFormattedPeriodShortly(to)
        );
  }

  private String getFormattedVisits(final Instant period, final Set<Visit> visits) {
    if (visits.isEmpty()) {
      return messageSource.noVisits;
    }

    return "%s%s%s"
        .formatted(
            getFormattedVisitHeader(period),
            LINE_SEPARATOR,
            getFormattedVisits(visits)
        );
  }

  private String getFormattedVisits(final Instant from, final Instant to, final Set<Visit> visits) {
    if (visits.isEmpty()) {
      return messageSource.noVisits;
    }

    return "%s<newline>%s"
        .formatted(
            getFormattedVisitHeader(from, to),
            getFormattedVisits(visits)
        );
  }

  private String getFormattedVisits(final Set<Visit> visits) {
    return visits.stream()
        .sorted(comparing(Visit::getSessionStartTime).reversed())
        .map(this::getFormattedVisit)
        .collect(joining(LINE_SEPARATOR));
  }

  private String getFormattedVisit(final Visit visit) {
    return messageSource.visitEntry
        .formatted(
            getFormattedPeriod(visit.getSessionStartTime()),
            getFormattedPeriod(visit.getSessionDitchTime()),
            durationFormatter.getFormattedDuration(visit.getSessionDuration())
        );
  }
}
