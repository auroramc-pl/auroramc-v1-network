package pl.auroramc.cheque;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.inventory.ItemStack;

interface ChequeFacade {

  boolean isCheque(final ItemStack itemStack);

  ItemStack createCheque(final ChequeContext chequeContext);

  CompletableFuture<ChequeContext> finalizeCheque(
      final UUID retrieverUniqueId, ItemStack itemStack);
}
