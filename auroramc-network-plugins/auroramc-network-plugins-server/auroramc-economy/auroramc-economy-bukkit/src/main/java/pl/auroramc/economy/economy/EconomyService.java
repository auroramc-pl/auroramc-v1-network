package pl.auroramc.economy.economy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.spotify.futures.CompletableFutures.combineFutures;
import static java.math.BigDecimal.ZERO;
import static java.util.concurrent.CompletableFuture.completedFuture;

import com.google.common.base.Preconditions;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BinaryOperator;
import java.util.logging.Logger;
import pl.auroramc.commons.concurrent.CompletableFutureUtils;
import pl.auroramc.economy.account.Account;
import pl.auroramc.economy.account.AccountFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.payment.PaymentBuilder;
import pl.auroramc.economy.payment.PaymentFacade;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

class EconomyService implements EconomyFacade {

  private static final CompletableFuture<Account> EMPTY_FUTURE = completedFuture(null);
  private final Logger logger;
  private final UserFacade userFacade;
  private final AccountFacade accountFacade;
  private final PaymentFacade paymentFacade;

  EconomyService(
      final Logger logger,
      final UserFacade userFacade,
      final AccountFacade accountFacade,
      final PaymentFacade paymentFacade) {
    this.logger = logger;
    this.userFacade = userFacade;
    this.accountFacade = accountFacade;
    this.paymentFacade = paymentFacade;
  }

  @Override
  public CompletableFuture<BigDecimal> balance(final UUID uniqueId, final Currency currency) {
    return retrieveAccountOf(uniqueId, currency)
        .thenApply(this::balanceOrZero)
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  @Override
  public CompletableFuture<Void> balance(
      final UUID uniqueId, final Currency currency, final BigDecimal amount) {
    return retrieveAccountOf(uniqueId, currency)
        .thenCompose(account -> mutateBalanceOfAccount(account, amount, (a, b) -> amount))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  @Override
  public CompletableFuture<Void> deposit(
      final UUID uniqueId, final Currency currency, final BigDecimal amount) {
    return retrieveAccountOf(uniqueId, currency)
        .thenCompose(account -> mutateBalanceOfAccount(account, amount, BigDecimal::add))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  @Override
  public CompletableFuture<Void> withdraw(
      final UUID uniqueId, final Currency currency, final BigDecimal amount) {
    return retrieveAccountOf(uniqueId, currency)
        .thenCompose(account -> mutateBalanceOfAccount(account, amount, BigDecimal::subtract))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  @Override
  public CompletableFuture<Void> transfer(
      final UUID initiatorUniqueId,
      final UUID receiverUniqueId,
      final Currency currency,
      final BigDecimal amount) {
    if (initiatorUniqueId.equals(receiverUniqueId)) {
      throw new EconomyException(
          "Could not transfer funds to the same account, unique id: %s."
              .formatted(initiatorUniqueId));
    }

    checkNotNull(currency.getId());
    return combineFutures(
            retrieveAccountOf(initiatorUniqueId, currency).thenApply(Preconditions::checkNotNull),
            retrieveAccountOf(receiverUniqueId, currency).thenApply(Preconditions::checkNotNull),
            (initiatorAccount, receivingAccount) ->
                accountFacade
                    .transferOfBalance(initiatorAccount, receivingAccount, currency.getId(), amount)
                    .thenCompose(
                        state ->
                            paymentFacade.createPayment(
                                PaymentBuilder.newBuilder()
                                    .withInitiatorId(initiatorAccount.getUserId())
                                    .withReceiverId(receivingAccount.getUserId())
                                    .withCurrencyId(currency.getId())
                                    .withAmount(amount)
                                    .withTransactionTime(Instant.now())
                                    .build())))
        .exceptionally(CompletableFutureUtils::delegateCaughtException)
        .toCompletableFuture();
  }

  @Override
  public CompletableFuture<Boolean> has(
      final UUID uniqueId, final Currency currency, final BigDecimal amount) {
    return retrieveAccountOf(uniqueId, currency)
        .thenApply(Preconditions::checkNotNull)
        .thenApply(account -> account.getBalance().compareTo(amount) >= 0)
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  private CompletableFuture<Void> mutateBalanceOfAccount(
      final Account account,
      final BigDecimal amount,
      final BinaryOperator<BigDecimal> balanceMutator) {
    checkNotNull(account);

    final long stamp = account.getLock().writeLock();
    try {
      account.setBalance(balanceMutator.apply(account.getBalance(), amount));
      return accountFacade.updateAccount(account);
    } finally {
      account.getLock().unlockWrite(stamp);
    }
  }

  private CompletableFuture<Account> retrieveAccountOf(
      final UUID uniqueId, final Currency currency) {
    return userFacade
        .getUserByUniqueId(uniqueId)
        .thenCompose(user -> retrieveAccountOf(uniqueId, user, currency));
  }

  private CompletableFuture<Account> retrieveAccountOf(
      final UUID uniqueId, final User user, final Currency currency) {
    if (user == null) {
      logger.warning("Could not retrieve user with unique id %s.".formatted(uniqueId));
      return EMPTY_FUTURE;
    }

    return accountFacade.retrieveAccount(user.getId(), currency.getId());
  }

  private BigDecimal balanceOrZero(final Account account) {
    return Optional.ofNullable(account).map(Account::getBalance).orElse(ZERO);
  }
}
