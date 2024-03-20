package pl.auroramc.cheque;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.inventory.ItemStack;
import pl.auroramc.commons.message.MutableMessage;

interface ChequeFacade {

  boolean whetherItemIsCheque(final ItemStack itemStack);

  ItemStack createCheque(final ChequeContext chequeContext);

  CompletableFuture<MutableMessage> finalizeCheque(
      final UUID retrieverUniqueId, ItemStack itemStack);
}
