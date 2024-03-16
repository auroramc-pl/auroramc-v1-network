package pl.auroramc.economy.balance.leaderboard;

import static pl.auroramc.commons.decimal.DecimalFormatter.getFormattedDecimal;
import static pl.auroramc.commons.message.MutableMessage.EMPTY_DELIMITER;
import static pl.auroramc.commons.message.MutableMessage.empty;
import static pl.auroramc.economy.message.MessageVariableKey.BALANCE_VARIABLE_KEY;
import static pl.auroramc.economy.message.MessageVariableKey.CURRENCY_ID_VARIABLE_KEY;
import static pl.auroramc.economy.message.MessageVariableKey.POSITION_VARIABLE_KEY;
import static pl.auroramc.economy.message.MessageVariableKey.CURRENCY_VARIABLE_KEY;
import static pl.auroramc.economy.message.MessageVariableKey.USERNAME_VARIABLE_KEY;

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
          .with(CURRENCY_ID_VARIABLE_KEY, currencyIdOrDefault);
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
            ),
            EMPTY_DELIMITER
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
        .with(CURRENCY_VARIABLE_KEY, currency.getSymbol())
        .with(POSITION_VARIABLE_KEY, entry.position())
        .with(USERNAME_VARIABLE_KEY, entry.username())
        .with(BALANCE_VARIABLE_KEY, getFormattedDecimal(entry.balance()));
  }

  private MutableMessage getLeaderboardFooter(
      final Player player, final Currency currency
  ) {
    final LeaderboardEntry entry = leaderboardFacade.getLeaderboardEntryByCurrencyIdAndUniqueId(
        currency.getId(), player.getUniqueId()
    );
    if (entry == null) {
      return empty();
    }

    return getLeaderboardFooter(currency, entry);
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
        .with(CURRENCY_VARIABLE_KEY, currency.getSymbol())
        .with(POSITION_VARIABLE_KEY, entry.position())
        .with(USERNAME_VARIABLE_KEY, entry.username())
        .with(BALANCE_VARIABLE_KEY, getFormattedDecimal(entry.balance()));
  }
}
