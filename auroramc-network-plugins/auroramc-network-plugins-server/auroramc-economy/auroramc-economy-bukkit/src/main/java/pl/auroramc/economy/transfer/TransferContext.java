package pl.auroramc.economy.transfer;

import java.math.BigDecimal;
import org.bukkit.entity.Player;
import pl.auroramc.economy.currency.Currency;

record TransferContext(Player player, Currency currency, BigDecimal amount) {}
