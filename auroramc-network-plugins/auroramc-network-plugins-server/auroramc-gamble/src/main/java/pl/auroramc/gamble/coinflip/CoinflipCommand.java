package pl.auroramc.gamble.coinflip;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_DOWN;
import static java.util.Locale.ROOT;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;
import static pl.auroramc.commons.decimal.DecimalFormatter.getFormattedDecimal;
import static pl.auroramc.gamble.gamble.GambleKey.COINFLIP;
import static pl.auroramc.gamble.message.MutableMessageVariableKey.CURRENCY_VARIABLE_KEY;
import static pl.auroramc.gamble.message.MutableMessageVariableKey.PREDICTION_VARIABLE_KEY;
import static pl.auroramc.gamble.message.MutableMessageVariableKey.STAKE_VARIABLE_KEY;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.gamble.gamble.Participant;
import pl.auroramc.gamble.message.MutableMessageSource;
import pl.auroramc.gamble.stake.StakeContext;
import pl.auroramc.gamble.stake.StakeFacade;
import pl.auroramc.gamble.stake.view.StakeViewFacade;

@Permission("auroramc.gamble.coinflip")
@Command(name = "coinflip", aliases = "coin")
public class CoinflipCommand {

  private final Logger logger;
  private final StakeFacade stakeFacade;
  private final StakeViewFacade stakeViewFacade;
  private final Currency fundsCurrency;
  private final MutableMessageSource messageSource;
  private final EconomyFacade economyFacade;

  public CoinflipCommand(
      final Logger logger,
      final StakeFacade stakeFacade,
      final StakeViewFacade stakeViewFacade,
      final Currency fundsCurrency,
      final MutableMessageSource messageSource,
      final EconomyFacade economyFacade
  ) {
    this.logger = logger;
    this.stakeFacade = stakeFacade;
    this.stakeViewFacade = stakeViewFacade;
    this.fundsCurrency = fundsCurrency;
    this.messageSource = messageSource;
    this.economyFacade = economyFacade;
  }

  @Execute
  public CompletableFuture<MutableMessage> coinflip(
      final @Context Player player,
      final @Arg CoinSide prediction,
      final @Arg BigDecimal stake
  ) {
    final BigDecimal fixedStake = stake.setScale(2, HALF_DOWN);
    if (fixedStake.compareTo(ZERO) <= 0) {
      return completedFuture(
          messageSource.stakeMustBeGreaterThanZero
      );
    }

    return economyFacade.has(player.getUniqueId(), fundsCurrency, fixedStake)
        .thenCompose(whetherPlayerHasEnoughFunds ->
            completeCoinflipGambleCreation(
                player, prediction, fixedStake, whetherPlayerHasEnoughFunds
            )
        )
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private CompletableFuture<MutableMessage> completeCoinflipGambleCreation(
      final Player player,
      final CoinSide prediction,
      final BigDecimal stake,
      final boolean whetherPlayerHasEnoughFunds
  ) {
    if (!whetherPlayerHasEnoughFunds) {
      return completedFuture(
          messageSource.stakeMissingBalance
      );
    }

    return economyFacade.withdraw(player.getUniqueId(), fundsCurrency, stake)
        .thenApply(state -> {
          stakeFacade.createStakeContext(
              StakeContext.newBuilder()
                  .gambleKey(COINFLIP)
                  .stakeUniqueId(UUID.randomUUID())
                  .stake(stake)
                  .participant(
                      Participant.newBuilder()
                          .uniqueId(player.getUniqueId())
                          .username(player.getName())
                          .prediction(prediction)
                          .build()
                  )
              .build()
          );
          stakeViewFacade.recalculate();
          return messageSource.stakeCreated
              .with(CURRENCY_VARIABLE_KEY, fundsCurrency.getSymbol())
              .with(STAKE_VARIABLE_KEY, getFormattedDecimal(stake))
              .with(PREDICTION_VARIABLE_KEY, capitalize(prediction.name().toLowerCase(ROOT)));
        })
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }
}
