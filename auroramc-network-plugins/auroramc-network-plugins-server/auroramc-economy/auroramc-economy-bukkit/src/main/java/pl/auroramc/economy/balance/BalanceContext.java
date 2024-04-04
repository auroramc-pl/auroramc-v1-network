package pl.auroramc.economy.balance;

import java.math.BigDecimal;
import pl.auroramc.economy.currency.Currency;

record BalanceContext(Currency currency, BigDecimal balance) {}
