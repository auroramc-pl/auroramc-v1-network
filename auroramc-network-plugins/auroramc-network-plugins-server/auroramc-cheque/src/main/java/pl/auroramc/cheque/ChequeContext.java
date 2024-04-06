package pl.auroramc.cheque;

import java.math.BigDecimal;
import pl.auroramc.economy.currency.Currency;

record ChequeContext(ChequeIssuer issuer, Currency currency, BigDecimal amount) {}
