package pl.auroramc.economy.leaderboard;

import java.math.BigDecimal;
import pl.auroramc.economy.currency.Currency;

record LeaderboardContext(Long position, String username, Currency currency, BigDecimal balance) {}
