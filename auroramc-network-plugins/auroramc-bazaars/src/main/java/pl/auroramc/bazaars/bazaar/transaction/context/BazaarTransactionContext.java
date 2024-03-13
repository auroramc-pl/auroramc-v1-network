package pl.auroramc.bazaars.bazaar.transaction.context;

import java.util.UUID;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import pl.auroramc.bazaars.bazaar.parser.BazaarParsingContext;

public record BazaarTransactionContext(
    Player customer,
    Container magazine,
    UUID customerUniqueId,
    UUID merchantUniqueId,
    BazaarParsingContext parsingContext
) {

}
