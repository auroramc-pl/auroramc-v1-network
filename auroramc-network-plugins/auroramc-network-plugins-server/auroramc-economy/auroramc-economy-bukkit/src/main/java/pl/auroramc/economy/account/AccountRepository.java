package pl.auroramc.economy.account;

import java.math.BigDecimal;

interface AccountRepository {

  Account findAccountByUserIdAndCurrencyId(final Long userId, final Long currencyId);

  void createAccount(final Account account);

  void updateAccount(final Account account);

  void deleteAccount(final Account account);

  void transferOfBalance(
      final Account initiatorAccount,
      final Account receivingAccount,
      final Long currencyId,
      final BigDecimal amount
  );
}
