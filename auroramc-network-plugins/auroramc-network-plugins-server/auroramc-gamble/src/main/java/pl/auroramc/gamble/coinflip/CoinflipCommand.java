package pl.auroramc.gamble.coinflip;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_DOWN;
import static java.util.Locale.ROOT;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;
import static pl.auroramc.commons.decimal.DecimalFormatter.getFormattedDecimal;
import static pl.auroramc.gamble.gamble.GambleKey.COINFLIP;

import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.gamble.gamble.Participant;
import pl.auroramc.gamble.message.MessageSource;
import pl.auroramc.gamble.stake.StakeContext;
import pl.auroramc.gamble.stake.StakeFacade;
import pl.auroramc.gamble.stake.view.StakeViewFacade;

@Permission("auroramc.gamble.coinflip")
@Route(name = "coinflip", aliases = "coin")
public class CoinflipCommand {

  private final Logger logger;
  private final StakeFacade stakeFacade;
  private final StakeViewFacade stakeViewFacade;
  private final Currency fundsCurrency;
  private final MessageSource messageSource;
  private final EconomyFacade economyFacade;

  public CoinflipCommand(
      final Logger logger,
      final StakeFacade stakeFacade,
      final StakeViewFacade stakeViewFacade,
      final Currency fundsCurrency,
      final MessageSource messageSource,
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
      final Player player, final @Arg CoinSide prediction, final @Arg BigDecimal stake
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
              .with("symbol", fundsCurrency.getSymbol())
              .with("stake", getFormattedDecimal(stake))
              .with("prediction", capitalize(prediction.name().toLowerCase(ROOT)));
        })
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }
}
