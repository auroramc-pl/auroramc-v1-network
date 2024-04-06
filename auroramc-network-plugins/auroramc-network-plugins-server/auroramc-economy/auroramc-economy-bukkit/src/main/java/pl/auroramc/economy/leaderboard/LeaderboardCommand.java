package pl.auroramc.economy.leaderboard;

import static java.time.temporal.ChronoUnit.SECONDS;
import static pl.auroramc.commons.message.compiler.CompiledMessageUtils.resolveComponent;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.cooldown.Cooldown;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.optional.OptionalArg;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.util.List;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.messages.message.component.ComponentCollector;

@Permission("auroramc.economy.leaderboard")
@Command(name = "leaderboard", aliases = "baltop")
@Cooldown(key = "leaderboard-cooldown", count = 10, unit = SECONDS)
public class LeaderboardCommand {

  private final CurrencyFacade currencyFacade;
  private final LeaderboardMessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;
  private final LeaderboardFacade leaderboardFacade;
  private final LeaderboardConfig leaderboardConfig;

  public LeaderboardCommand(
      final CurrencyFacade currencyFacade,
      final BukkitMessageCompiler messageCompiler,
      final LeaderboardMessageSource messageSource,
      final LeaderboardFacade leaderboardFacade,
      final LeaderboardConfig leaderboardConfig) {
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
    this.currencyFacade = currencyFacade;
    this.leaderboardFacade = leaderboardFacade;
    this.leaderboardConfig = leaderboardConfig;
  }

  @Execute
  public Component leaderboard(final @Context Player player, final @OptionalArg Currency currency) {
    return getLeaderboardView(
        player, Optional.ofNullable(currency).orElseGet(this::getDefaultCurrency));
  }

  private Component getLeaderboardView(final Player player, final Currency currency) {
    final List<LeaderboardEntry> entries =
        leaderboardFacade.getLeaderboardEntriesByCurrencyId(currency.getId());
    if (entries.isEmpty()) {
      return resolveComponent(messageCompiler.compile(messageSource.leaderboardEmpty));
    }

    return getLeaderboardHeader()
        .appendNewline()
        .append(getLeaderboardLines(currency, entries))
        .appendNewline()
        .append(getLeaderboardFooter(player, currency));
  }

  private Component getLeaderboardHeader() {
    return resolveComponent(messageCompiler.compile(messageSource.leaderboardHeader));
  }

  private Component getLeaderboardFooter(final Currency currency, final LeaderboardEntry entry) {
    return resolveComponent(
        messageCompiler.compile(
            messageSource.leaderboardFooter.placeholder(
                LeaderboardMessageSourcePaths.CONTEXT_PATH,
                new LeaderboardContext(
                    entry.position(), entry.username(), currency, entry.balance()))));
  }

  private Component getLeaderboardFooter(final Player player, final Currency currency) {
    final LeaderboardEntry entry =
        leaderboardFacade.getLeaderboardEntryByCurrencyIdAndUniqueId(
            currency.getId(), player.getUniqueId());
    if (entry == null) {
      return Component.empty();
    }

    return getLeaderboardFooter(currency, entry);
  }

  private Component getLeaderboardLines(
      final Currency currency, final List<LeaderboardEntry> entries) {
    return entries.stream()
        .map(entry -> getLeaderboardLine(currency, entry))
        .collect(ComponentCollector.collector());
  }

  private Component getLeaderboardLine(final Currency currency, final LeaderboardEntry entry) {
    return resolveComponent(
        messageCompiler.compile(
            messageSource.leaderboardEntry.placeholder(
                LeaderboardMessageSourcePaths.CONTEXT_PATH,
                new LeaderboardContext(
                    entry.position(), entry.username(), currency, entry.balance()))));
  }

  private Currency getDefaultCurrency() {
    return currencyFacade.getCurrencyById(leaderboardConfig.defaultCurrencyId);
  }
}
