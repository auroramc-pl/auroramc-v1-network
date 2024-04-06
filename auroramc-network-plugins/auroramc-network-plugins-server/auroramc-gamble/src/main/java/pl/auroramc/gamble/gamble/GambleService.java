package pl.auroramc.gamble.gamble;

import static pl.auroramc.commons.message.compiler.CompiledMessageUtils.resolveComponent;
import static pl.auroramc.gamble.message.MessageSourcePaths.COMPETITOR_PATH;
import static pl.auroramc.gamble.message.MessageSourcePaths.CONTEXT_PATH;
import static pl.auroramc.gamble.message.MessageSourcePaths.CURRENCY_PATH;

import pl.auroramc.commons.CompletableFutureUtils;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.economy.EconomyFacade;
import pl.auroramc.gamble.message.MessageSource;
import pl.auroramc.gamble.participant.Participant;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;

class GambleService implements GambleFacade {

  private final Currency fundsCurrency;
  private final MessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;
  private final EconomyFacade economyFacade;

  GambleService(
      final Currency fundsCurrency,
      final MessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final EconomyFacade economyFacade) {
    this.fundsCurrency = fundsCurrency;
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
    this.economyFacade = economyFacade;
  }

  @Override
  public void settleGamble(final Gamble gamble) {
    final Participant winnerParticipant = gamble.pickWinner();
    final Participant losingParticipant = getOpponent(gamble, winnerParticipant);

    economyFacade
        .deposit(
            gamble.getGambleContext().initiator().uniqueId(),
            fundsCurrency,
            gamble.getGambleContext().stake())
        .thenCompose(
            state ->
                economyFacade.transfer(
                    losingParticipant.uniqueId(),
                    winnerParticipant.uniqueId(),
                    fundsCurrency,
                    gamble.getGambleContext().stake()))
        .thenAccept(
            state -> {
              winnerParticipant.sendMessage(
                  resolveComponent(
                      messageCompiler.compile(
                          messageSource
                              .stakeWon
                              .placeholder(CONTEXT_PATH, gamble.getGambleContext())
                              .placeholder(CURRENCY_PATH, fundsCurrency)
                              .placeholder(COMPETITOR_PATH, losingParticipant))));
              losingParticipant.sendMessage(
                  resolveComponent(
                      messageCompiler.compile(
                          messageSource
                              .stakeLost
                              .placeholder(CONTEXT_PATH, gamble.getGambleContext())
                              .placeholder(CURRENCY_PATH, fundsCurrency)
                              .placeholder(COMPETITOR_PATH, winnerParticipant))));
            })
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  private Participant getOpponent(final Gamble gamble, final Participant participant) {
    return gamble.getGambleContext().initiator().equals(participant)
        ? gamble.getGambleContext().competitor()
        : gamble.getGambleContext().initiator();
  }
}
