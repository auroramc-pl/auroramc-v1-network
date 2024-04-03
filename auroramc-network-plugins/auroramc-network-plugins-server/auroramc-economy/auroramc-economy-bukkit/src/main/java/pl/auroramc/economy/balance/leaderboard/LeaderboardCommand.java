package pl.auroramc.economy.balance.leaderboard;

import static pl.auroramc.commons.decimal.DecimalFormatter.getFormattedDecimal;
import static pl.auroramc.commons.message.MutableMessage.EMPTY_DELIMITER;
import static pl.auroramc.commons.message.MutableMessage.empty;
import static pl.auroramc.economy.message.MutableMessageVariableKey.BALANCE_PATH;
import static pl.auroramc.economy.message.MutableMessageVariableKey.CURRENCY_ID_PATH;
import static pl.auroramc.economy.message.MutableMessageVariableKey.CURRENCY_PATH;
import static pl.auroramc.economy.message.MutableMessageVariableKey.POSITION_PATH;
import static pl.auroramc.economy.message.MutableMessageVariableKey.USERNAME_PATH;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.optional.OptionalArg;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.util.List;
import org.bukkit.entity.Player;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.economy.message.MutableMessageSource;

@Permission("auroramc.economy.leaderboard")
@Command(name = "leaderboard", aliases = "baltop")
public class LeaderboardCommand {

  private final MutableMessageSource messageSource;
  private final CurrencyFacade currencyFacade;
  private final LeaderboardFacade leaderboardFacade;
  private final LeaderboardConfig leaderboardConfig;

  public LeaderboardCommand(
      final MutableMessageSource messageSource,
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
  public MutableMessage leaderboard(
      final @Context Player player,
      final @OptionalArg Long currencyId
  ) {
    final Long currencyIdOrDefault = currencyId == null
        ? leaderboardConfig.defaultCurrencyId
        : currencyId;

    final Currency currency = currencyFacade.getCurrencyById(currencyIdOrDefault);
    if (currency == null) {
      return messageSource.missingCurrency
          .with(CURRENCY_ID_PATH, currencyIdOrDefault);
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
        .with(CURRENCY_PATH, currency.getSymbol())
        .with(POSITION_PATH, entry.position())
        .with(USERNAME_PATH, entry.username())
        .with(BALANCE_PATH, getFormattedDecimal(entry.balance()));
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
        .with(CURRENCY_PATH, currency.getSymbol())
        .with(POSITION_PATH, entry.position())
        .with(USERNAME_PATH, entry.username())
        .with(BALANCE_PATH, getFormattedDecimal(entry.balance()));
  }
}
