package pl.auroramc.economy.account;

import static java.math.BigDecimal.ZERO;
import static java.time.Duration.ofSeconds;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.runAsync;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

class AccountService implements AccountFacade {

  private static final BigDecimal INITIAL_ACCOUNT_BALANCE = ZERO;
  private final Logger logger;
  private final AccountRepository accountRepository;
  private final AsyncLoadingCache<AccountKey, Account> accountCache;

  AccountService(final Logger logger, final AccountRepository accountRepository) {
    this.logger = logger;
    this.accountRepository = accountRepository;
    this.accountCache = Caffeine.newBuilder()
        .expireAfterWrite(ofSeconds(30))
        .buildAsync(accountKey -> accountRepository.findAccountByUserIdAndCurrencyId(
            accountKey.userId(),
            accountKey.currencyId()));
  }

  @Override
  public CompletableFuture<Account> getAccount(final Long userId, final Long currencyId) {
    return accountCache.get(new AccountKey(userId, currencyId))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Override
  public CompletableFuture<Void> createAccount(final Account account) {
    return runAsync(() -> accountRepository.createAccount(account))
        .thenAccept(state ->
            accountCache.put(
                new AccountKey(
                    account.getUserId(),
                    account.getCurrencyId()),
                completedFuture(account)))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Override
  public CompletableFuture<Void> updateAccount(final Account account) {
    return runAsync(() -> accountRepository.updateAccount(account))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Override
  public CompletableFuture<Void> deleteAccount(final Account account) {
    return runAsync(() -> accountRepository.deleteAccount(account))
        .thenAccept(state -> accountCache.synchronous().invalidate(
            new AccountKey(
                account.getUserId(),
                account.getCurrencyId())))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Override
  public CompletableFuture<Account> retrieveAccount(final Long userId, final Long currencyId) {
    return getAccount(userId, currencyId)
        .thenApply(account -> createAccountIfNotExists(userId, currencyId, account))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private Account createAccountIfNotExists(
      final Long userId, final Long currencyId, Account account
  ) {
    if (account == null) {
      account = AccountBuilder.newBuilder()
          .withUserId(userId)
          .withCurrencyId(currencyId)
          .withBalance(INITIAL_ACCOUNT_BALANCE)
          .build();
      createAccount(account);
    }

    return account;
  }

  @Override
  public CompletableFuture<Void> transferOfBalance(
      final Account initiatorAccount,
      final Account receivingAccount,
      final Long currencyId,
      final BigDecimal amount
  ) {
    return runAsync(() -> accountRepository.transferOfBalance(initiatorAccount, receivingAccount, currencyId, amount))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }
}
