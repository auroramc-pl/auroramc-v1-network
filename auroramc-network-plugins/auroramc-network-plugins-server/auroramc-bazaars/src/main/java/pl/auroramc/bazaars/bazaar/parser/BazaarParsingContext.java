package pl.auroramc.bazaars.bazaar.parser;

import java.math.BigDecimal;
import org.bukkit.Material;
import pl.auroramc.bazaars.bazaar.BazaarType;

public record BazaarParsingContext(
    BazaarType type, String merchant, Integer quantity, BigDecimal price, Material material) {}
