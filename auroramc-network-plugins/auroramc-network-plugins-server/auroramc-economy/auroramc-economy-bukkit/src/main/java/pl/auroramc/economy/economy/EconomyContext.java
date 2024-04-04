package pl.auroramc.economy.economy;

import java.math.BigDecimal;
import org.bukkit.entity.Player;
import pl.auroramc.economy.currency.Currency;

record EconomyContext(Player player, Currency currency, BigDecimal amount) {}
