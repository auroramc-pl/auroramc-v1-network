package pl.auroramc.economy.balance.leaderboard;

import java.math.BigDecimal;
import java.util.UUID;

public record LeaderboardEntry(
    UUID uniqueId, String username, Long position, Long currencyId, BigDecimal balance
) {

}
