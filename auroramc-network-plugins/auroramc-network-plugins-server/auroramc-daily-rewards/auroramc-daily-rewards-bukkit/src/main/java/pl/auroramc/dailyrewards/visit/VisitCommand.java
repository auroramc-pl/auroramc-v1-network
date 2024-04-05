package pl.auroramc.dailyrewards.visit;

import static java.time.Instant.now;
import static java.util.Comparator.comparing;
import static pl.auroramc.commons.format.temporal.TemporalUtils.getMaximumTimeOfDay;
import static pl.auroramc.commons.format.temporal.TemporalUtils.getMinimumTimeOfDay;
import static pl.auroramc.commons.range.Between.ranged;
import static pl.auroramc.commons.range.Between.single;
import static pl.auroramc.dailyrewards.message.MessageSourcePaths.TIMEFRAME_PATH;
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
    final Between<Instant> timeframe = single(now());
    return userFacade
        .getUserByUniqueId(player.getUniqueId())
        .thenApply(User::getId)
        .thenCompose(
            userId ->
                visitFacade.getVisitsByUserIdInTimeframe(
                    userId,
                    getMinimumTimeOfDay(timeframe.minimum()),
                    getMaximumTimeOfDay(timeframe.minimum())))
        .thenApply(visits -> getFormattedVisits(timeframe, visits));
  }

  @Execute(name = "ranged")
  public CompletableFuture<Component> visitRanged(
      final @Context Player player, final @Arg Instant from, final @Arg Instant to) {
    final Between<Instant> timeframe = ranged(from, to);
    return userFacade
        .getUserByUniqueId(player.getUniqueId())
        .thenApply(User::getId)
        .thenCompose(userId -> visitFacade.getVisitsByUserIdInTimeframe(userId, from, to))
        .thenApply(visits -> getFormattedVisits(timeframe, visits));
  }

  private Component getFormattedVisitHeader(final Between<Instant> timeframe) {
    return messageCompiler
        .compile(
            (timeframe.single() ? messageSource.visitDailySummary : messageSource.visitRangeSummary)
                .placeholder(TIMEFRAME_PATH, timeframe))
        .getComponent();
  }

  private Component getFormattedVisits(final Between<Instant> timeframe, final Set<Visit> visits) {
    if (visits.isEmpty()) {
      return messageCompiler.compile(messageSource.noVisits).getComponent();
    }
    return getFormattedVisitHeader(timeframe).appendNewline().append(getFormattedVisits(visits));
  }

  private Component getFormattedVisit(final Visit visit) {
    return messageCompiler
        .compile(messageSource.visitEntry.placeholder(VISIT_PATH, visit))
        .getComponent();
  }

  private Component getFormattedVisits(final Set<Visit> visits) {
    return visits.stream()
        .sorted(comparing(Visit::getStartTime).reversed())
        .map(this::getFormattedVisit)
        .collect(ComponentCollector.collector());
  }
}
