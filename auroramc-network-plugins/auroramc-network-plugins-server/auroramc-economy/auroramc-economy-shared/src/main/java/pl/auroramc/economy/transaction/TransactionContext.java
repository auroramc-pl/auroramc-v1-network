package pl.auroramc.economy.transaction;

import java.math.BigDecimal;
import pl.auroramc.economy.currency.Currency;

public record TransactionContext(Currency currency, BigDecimal amount) {}
