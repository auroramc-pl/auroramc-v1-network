package pl.auroramc.gamble.coinflip;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_DOWN;
import static java.util.Locale.ROOT;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed;
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
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.gamble.gamble.Participant;
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
  private final EconomyFacade economyFacade;

  public CoinflipCommand(
      final Logger logger,
      final StakeFacade stakeFacade,
      final StakeViewFacade stakeViewFacade,
      final Currency fundsCurrency,
      final EconomyFacade economyFacade
  ) {
    this.logger = logger;
    this.stakeFacade = stakeFacade;
    this.stakeViewFacade = stakeViewFacade;
    this.fundsCurrency = fundsCurrency;
    this.economyFacade = economyFacade;
  }

  @Execute
  public CompletableFuture<Component> createCoinflipGamble(
      final Player executor, final @Arg CoinSide prediction, final @Arg BigDecimal stake
  ) {
    final BigDecimal fixedStake = stake.setScale(2, HALF_DOWN);
    if (fixedStake.compareTo(ZERO) <= 0) {
      return completedFuture(miniMessage().deserialize("<red>Stawka musi być większa od zera."));
    }

    return economyFacade.has(executor.getUniqueId(), fundsCurrency, fixedStake)
        .thenCompose(whetherPlayerHasEnoughFunds ->
            completeCoinflipGambleCreation(
                executor, prediction, fixedStake, whetherPlayerHasEnoughFunds
            )
        )
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private CompletableFuture<Component> completeCoinflipGambleCreation(
      final Player player,
      final CoinSide prediction,
      final BigDecimal stake,
      final boolean whetherPlayerHasEnoughFunds
  ) {
    if (!whetherPlayerHasEnoughFunds) {
      return completedFuture(miniMessage().deserialize(
          "<red>Nie posiadasz wystarczających środków aby utworzyć ten zakład."));
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

          return miniMessage().deserialize(
              "<gray>Zakład o stawce <white><stake_symbol><stake> <gray>na <white><prediction> <gray>został utworzony i oczekuje na przeciwnika.",
              unparsed("stake", getFormattedDecimal(stake)),
              unparsed("stake_symbol", fundsCurrency.getSymbol()),
              unparsed("prediction", capitalize(prediction.name().toLowerCase(ROOT)))
          );
        })
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }
}
