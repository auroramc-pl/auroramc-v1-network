package pl.auroramc.economy.account;

import static java.math.BigDecimal.ZERO;
import static java.time.Duration.ofSeconds;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static pl.auroramc.commons.scheduler.SchedulerPoll.ASYNC;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import pl.auroramc.commons.concurrent.CompletableFutureUtils;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.commons.scheduler.caffeine.CaffeineExecutor;

class AccountService implements AccountFacade {

  private static final BigDecimal INITIAL_ACCOUNT_BALANCE = ZERO;
  private final Scheduler scheduler;
  private final AccountRepository accountRepository;
  private final AsyncLoadingCache<AccountCompositeKey, Account> accountByUserIdAndCurrencyId;

  AccountService(final Scheduler scheduler, final AccountRepository accountRepository) {
    this.scheduler = scheduler;
    this.accountRepository = accountRepository;
    this.accountByUserIdAndCurrencyId =
        Caffeine.newBuilder()
            .executor(new CaffeineExecutor(scheduler))
            .expireAfterWrite(ofSeconds(30))
            .buildAsync(
                key ->
                    accountRepository.findAccountByUserIdAndCurrencyId(
                        key.userId(), key.currencyId()));
  }

  @Override
  public CompletableFuture<Account> getAccount(final Long userId, final Long currencyId) {
    return accountByUserIdAndCurrencyId
        .get(new AccountCompositeKey(userId, currencyId))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  @Override
  public CompletableFuture<Account> createAccount(final Account account) {
    return scheduler
        .run(ASYNC, () -> accountRepository.createAccount(account))
        .thenAccept(
            state ->
                accountByUserIdAndCurrencyId.put(
                    AccountCompositeKey.toCompositeKey(account), completedFuture(account)))
        .thenCompose(state -> getAccount(account.getUserId(), account.getCurrencyId()));
  }

  @Override
  public CompletableFuture<Void> updateAccount(final Account account) {
    return scheduler.run(ASYNC, () -> accountRepository.updateAccount(account));
  }

  @Override
  public CompletableFuture<Void> deleteAccount(final Account account) {
    return scheduler
        .run(ASYNC, () -> accountRepository.deleteAccount(account))
        .thenAccept(
            state ->
                accountByUserIdAndCurrencyId
                    .synchronous()
                    .invalidate(AccountCompositeKey.toCompositeKey(account)));
  }

  @Override
  public CompletableFuture<Account> retrieveAccount(final Long userId, final Long currencyId) {
    return getAccount(userId, currencyId)
        .thenCompose(account -> createAccountIfNotExists(userId, currencyId, account));
  }

  @Override
  public CompletableFuture<Void> transferOfBalance(
      final Account initiatorAccount,
      final Account receivingAccount,
      final Long currencyId,
      final BigDecimal amount) {
    return scheduler.run(
        ASYNC,
        () ->
            accountRepository.transferOfBalance(
                initiatorAccount, receivingAccount, currencyId, amount));
  }

  private CompletableFuture<Account> createAccountIfNotExists(
      final Long userId, final Long currencyId, Account account) {
    if (account == null) {
      account =
          AccountBuilder.newBuilder()
              .withUserId(userId)
              .withCurrencyId(currencyId)
              .withBalance(INITIAL_ACCOUNT_BALANCE)
              .build();
      return createAccount(account);
    }

    return completedFuture(account);
  }

  private record AccountCompositeKey(Long userId, Long currencyId) {

    static AccountCompositeKey toCompositeKey(final Account account) {
      return new AccountCompositeKey(account.getUserId(), account.getCurrencyId());
    }
  }
}
