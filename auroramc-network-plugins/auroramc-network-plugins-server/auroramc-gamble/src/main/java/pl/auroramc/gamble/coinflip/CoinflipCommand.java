package pl.auroramc.gamble.coinflip;

import static java.math.BigDecimal.ZERO;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static pl.auroramc.gamble.gamble.GambleKey.COINFLIP;
import static pl.auroramc.gamble.message.MessageSourcePaths.CONTEXT_PATH;
import static pl.auroramc.gamble.message.MessageSourcePaths.CURRENCY_PATH;
import static pl.auroramc.gamble.message.MessageSourcePaths.PREDICTION_PATH;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.cooldown.Cooldown;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;
import pl.auroramc.commons.CompletableFutureUtils;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.economy.EconomyFacade;
import pl.auroramc.gamble.participant.Participant;
import pl.auroramc.gamble.message.MessageSource;
import pl.auroramc.gamble.stake.context.StakeContext;
import pl.auroramc.gamble.stake.StakeFacade;
import pl.auroramc.gamble.stake.view.StakeViewFacade;
import pl.auroramc.messages.message.MutableMessage;

@Permission("auroramc.gamble.coinflip")
@Command(name = "coinflip", aliases = "coin")
@Cooldown(key = "coinflip-cooldown", count = 30, unit = SECONDS)
public class CoinflipCommand {

  private final StakeFacade stakeFacade;
  private final StakeViewFacade stakeViewFacade;
  private final Currency fundsCurrency;
  private final MessageSource messageSource;
  private final EconomyFacade economyFacade;

  public CoinflipCommand(
      final StakeFacade stakeFacade,
      final StakeViewFacade stakeViewFacade,
      final Currency fundsCurrency,
      final MessageSource messageSource,
      final EconomyFacade economyFacade) {
    this.stakeFacade = stakeFacade;
    this.stakeViewFacade = stakeViewFacade;
    this.fundsCurrency = fundsCurrency;
    this.messageSource = messageSource;
    this.economyFacade = economyFacade;
  }

  @Execute
  public CompletableFuture<MutableMessage> coinflip(
      final @Context Player player, final @Arg CoinSide prediction, final @Arg BigDecimal stake) {
    if (stake.compareTo(ZERO) <= 0) {
      return completedFuture(messageSource.stakeMustBeGreaterThanZero);
    }

    return economyFacade
        .has(player.getUniqueId(), fundsCurrency, stake)
        .thenCompose(
            whetherPlayerHasEnoughFunds ->
                completeCoinflipGambleCreation(
                    player, prediction, stake, whetherPlayerHasEnoughFunds))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  private CompletableFuture<MutableMessage> completeCoinflipGambleCreation(
      final Player player,
      final CoinSide prediction,
      final BigDecimal stake,
      final boolean whetherPlayerHasEnoughFunds) {
    if (!whetherPlayerHasEnoughFunds) {
      return completedFuture(messageSource.stakeMissingBalance);
    }

    return economyFacade
        .withdraw(player.getUniqueId(), fundsCurrency, stake)
        .thenApply(
            state -> {
              final StakeContext stakeContext = getStakeContext(player, prediction, stake);
              stakeFacade.createStakeContext(stakeContext);
              stakeViewFacade.recalculate();
              return messageSource
                  .stakeCreated
                  .placeholder(CONTEXT_PATH, stakeContext)
                  .placeholder(CURRENCY_PATH, fundsCurrency)
                  .placeholder(PREDICTION_PATH, prediction);
            })
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  private StakeContext getStakeContext(
      final Player player, final CoinSide prediction, final BigDecimal stake) {
    return StakeContext.newBuilder()
        .gambleKey(COINFLIP)
        .stakeUniqueId(UUID.randomUUID())
        .stake(stake)
        .participant(
            Participant.newBuilder()
                .uniqueId(player.getUniqueId())
                .username(player.getName())
                .prediction(prediction)
                .build())
        .build();
  }
}
