package pl.auroramc.gamble.gamble;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;
import static pl.auroramc.commons.decimal.DecimalFormatter.getFormattedDecimal;

import java.util.List;
import java.util.logging.Logger;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;

class GambleService implements GambleFacade {

  private final Logger logger;
  private final Currency fundsCurrency;
  private final EconomyFacade economyFacade;

  GambleService(
      final Logger logger, final Currency fundsCurrency, final EconomyFacade economyFacade
  ) {
    this.logger = logger;
    this.fundsCurrency = fundsCurrency;
    this.economyFacade = economyFacade;
  }

  @Override
  public void settleGamble(final Gamble gamble) {
    final Participant winnerParticipant = gamble.pickWinner();
    final Participant losingParticipant = getOpponent(gamble, winnerParticipant);

    economyFacade.deposit(gamble.getGambleContext().initiator().uniqueId(), fundsCurrency, gamble.getGambleContext().stake())
        .thenCompose(state ->
            economyFacade.transfer(
                losingParticipant.uniqueId(),
                winnerParticipant.uniqueId(),
                fundsCurrency,
                gamble.getGambleContext().stake()
            )
        )
        .thenAccept(state -> {
          winnerParticipant.sendMessage(
              miniMessage().deserialize(
                  "<gray>Wygrałeś <hover:show_text:'<gray>Unikalny identyfikator: <white><gamble_unique_id>'>zakład</hover> <gray>o stawce <white><stake_symbol><stake> <gray>mierząc się z <white><competitor><gray>.",
                  getGambleTagResolvers(gamble, losingParticipant)
              )
          );
          losingParticipant.sendMessage(
              miniMessage().deserialize(
                  "<gray>Przegrałeś <hover:show_text:'<gray>Unikalny identyfikator: <white><gamble_unique_id>'>zakład</hover> <gray>o stawce <white><stake_symbol><stake> <gray>mierząc się z <white><competitor><gray>.",
                  getGambleTagResolvers(gamble, winnerParticipant)
              )
          );
        })
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private Participant getOpponent(final Gamble gamble, final Participant participant) {
    return gamble.getGambleContext().initiator().equals(participant)
        ? gamble.getGambleContext().competitor()
        : gamble.getGambleContext().initiator();
  }

  private TagResolver[] getGambleTagResolvers(final Gamble gamble, final Participant competitor) {
    return List.of(
        unparsed("gamble_unique_id", gamble.getGambleContext().gambleUniqueId().toString()),
        unparsed("stake", getFormattedDecimal(gamble.getGambleContext().stake())),
        unparsed("stake_symbol", fundsCurrency.getSymbol()),
        unparsed("competitor", competitor.username())
    ).toArray(TagResolver[]::new);
  }
}
