package pl.auroramc.dailyrewards.visit;

import static java.time.Instant.now;
import static java.util.Comparator.comparing;
import static pl.auroramc.commons.format.temporal.TemporalUtils.getMaximumTimeOfDay;
import static pl.auroramc.commons.format.temporal.TemporalUtils.getMinimumTimeOfDay;
import static pl.auroramc.commons.range.Between.ranged;
import static pl.auroramc.commons.range.Between.single;
import static pl.auroramc.dailyrewards.message.MessageSourcePaths.PERIOD_PATH;
import static pl.auroramc.dailyrewards.message.MessageSourcePaths.VISIT_PATH;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import pl.auroramc.commons.range.Between;
import pl.auroramc.dailyrewards.message.MessageSource;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.messages.message.component.ComponentCollector;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

@Command(name = "visit", aliases = "visits")
public class VisitCommand {

  private final MessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;
  private final UserFacade userFacade;
  private final VisitFacade visitFacade;

  public VisitCommand(
      final MessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final UserFacade userFacade,
      final VisitFacade visitFacade) {
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
    this.userFacade = userFacade;
    this.visitFacade = visitFacade;
  }

  @Execute
  public CompletableFuture<Component> visit(final @Context Player player) {
    final Between<Instant> range = single(now());
    return userFacade
        .getUserByUniqueId(player.getUniqueId())
        .thenApply(User::getId)
        .thenCompose(
            userId ->
                visitFacade.getVisitsByUserIdBetween(
                    userId,
                    getMinimumTimeOfDay(range.minimum()),
                    getMaximumTimeOfDay(range.minimum())))
        .thenApply(visits -> getFormattedVisits(range, visits));
  }

  @Execute(name = "ranged")
  public CompletableFuture<Component> visitRanged(
      final @Context Player player, final @Arg Instant from, final @Arg Instant to) {
    final Between<Instant> range = ranged(from, to);
    return userFacade
        .getUserByUniqueId(player.getUniqueId())
        .thenApply(User::getId)
        .thenCompose(userId -> visitFacade.getVisitsByUserIdBetween(userId, from, to))
        .thenApply(visits -> getFormattedVisits(range, visits));
  }

  private Component getFormattedVisitHeader(final Between<Instant> period) {
    return messageCompiler
        .compile(
            (period.single() ? messageSource.visitDailySummary : messageSource.visitRangeSummary)
                .placeholder(PERIOD_PATH, period))
        .getComponent();
  }

  private Component getFormattedVisits(final Between<Instant> period, final Set<Visit> visits) {
    if (visits.isEmpty()) {
      return messageCompiler.compile(messageSource.noVisits).getComponent();
    }
    return getFormattedVisitHeader(period).appendNewline().append(getFormattedVisits(visits));
  }

  private Component getFormattedVisit(final VisitContext context) {
    return messageCompiler
        .compile(messageSource.visitEntry.placeholder(VISIT_PATH, context))
        .getComponent();
  }

  private Component getFormattedVisits(final Set<Visit> visits) {
    return visits.stream()
        .sorted(comparing(Visit::getSessionStartTime).reversed())
        .map(VisitContext::completed)
        .map(this::getFormattedVisit)
        .collect(ComponentCollector.collector());
  }
}
