package pl.auroramc.economy.balance.leaderboad;

import static pl.auroramc.commons.decimal.DecimalFormatter.getFormattedDecimal;
import static pl.auroramc.commons.message.MutableMessage.empty;

import dev.rollczi.litecommands.argument.option.Opt;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import java.util.List;
import org.bukkit.entity.Player;
import panda.std.Option;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.economy.message.MessageSource;

@Permission("auroramc.economy.leaderboard")
@Route(name = "leaderboard", aliases = "baltop")
public class LeaderboardCommand {

  private final MessageSource messageSource;
  private final CurrencyFacade currencyFacade;
  private final LeaderboardFacade leaderboardFacade;
  private final LeaderboardConfig leaderboardConfig;

  public LeaderboardCommand(
      final MessageSource messageSource,
      final CurrencyFacade currencyFacade,
      final LeaderboardFacade leaderboardFacade,
      final LeaderboardConfig leaderboardConfig
  ) {
    this.messageSource = messageSource;
    this.currencyFacade = currencyFacade;
    this.leaderboardFacade = leaderboardFacade;
    this.leaderboardConfig = leaderboardConfig;
  }

  @Execute
  public MutableMessage leaderboard(final Player player, final @Opt Option<Long> currencyId) {
    final Long currencyIdOrDefault = currencyId.orElseGet(leaderboardConfig.defaultCurrencyId);

    final Currency currency = currencyFacade.getCurrencyById(currencyIdOrDefault);
    if (currency == null) {
      return messageSource.missingCurrency
          .with("currency_id", currencyIdOrDefault);
    }

    return getLeaderboardView(player, currency);
  }

  private MutableMessage getLeaderboardView(
      final Player player, final Currency currency
  ) {
    return empty()
        .append(
            getLeaderboardHeader()
        )
        .append(
            getLeaderboardLines(
                currency, leaderboardFacade.getLeaderboardEntriesByCurrencyId(currency.getId())
            )
        )
        .append(
            getLeaderboardFooter(player, currency)
        );
  }

  private MutableMessage getLeaderboardHeader() {
    return messageSource.leaderboardHeader;
  }

  private MutableMessage getLeaderboardFooter(
      final Currency currency, final LeaderboardEntry entry
  ) {
    return messageSource.leaderboardFooter
        .with("symbol", currency.getSymbol())
        .with("position", entry.position())
        .with("username", entry.username())
        .with("balance", getFormattedDecimal(entry.balance()));
  }

  private MutableMessage getLeaderboardFooter(
      final Player player, final Currency currency
  ) {
    return leaderboardFacade.getLeaderboardEntryByCurrencyIdAndUniqueId(currency.getId(), player.getUniqueId())
        .map(entry -> getLeaderboardFooter(currency, entry))
        .orElse(empty());
  }

  private MutableMessage getLeaderboardLines(
      final Currency currency, final List<LeaderboardEntry> entries
  ) {
    return entries.stream()
        .map(entry -> getLeaderboardLine(currency, entry))
        .collect(MutableMessage.collector());
  }

  private MutableMessage getLeaderboardLine(
      final Currency currency, final LeaderboardEntry entry
  ) {
    return messageSource.leaderboardEntry
        .with("symbol", currency.getSymbol())
        .with("position", entry.position())
        .with("username", entry.username())
        .with("balance", getFormattedDecimal(entry.balance()));
  }
}
