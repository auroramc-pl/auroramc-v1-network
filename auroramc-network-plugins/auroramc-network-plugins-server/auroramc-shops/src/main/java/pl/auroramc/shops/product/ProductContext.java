package pl.auroramc.shops.product;

import java.math.BigDecimal;
import org.bukkit.inventory.ItemStack;
import pl.auroramc.economy.currency.Currency;

record ProductContext(ItemStack product, Currency currency, BigDecimal price) {}
