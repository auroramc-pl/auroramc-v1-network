package pl.auroramc.spawners.spawner;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static pl.auroramc.commons.scheduler.SchedulerPoll.SYNC;
import static pl.auroramc.spawners.spawner.SpawnerMessageSourcePaths.CURRENCY_PATH;
import static pl.auroramc.spawners.spawner.SpawnerMessageSourcePaths.SPAWNER_PATH;

import java.util.concurrent.CompletableFuture;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import pl.auroramc.commons.concurrent.CompletableFutureUtils;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.economy.EconomyFacade;
import pl.auroramc.messages.message.MutableMessage;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.messages.viewer.BukkitViewer;
import pl.auroramc.messages.viewer.Viewer;

public class SpawnerController {

  private final Scheduler scheduler;
  private final Currency fundsCurrency;
  private final EconomyFacade economyFacade;
  private final SpawnerMessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;

  public SpawnerController(
      final Scheduler scheduler,
      final Currency fundsCurrency,
      final EconomyFacade economyFacade,
      final SpawnerMessageSource messageSource,
      final BukkitMessageCompiler messageCompiler) {
    this.scheduler = scheduler;
    this.fundsCurrency = fundsCurrency;
    this.economyFacade = economyFacade;
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
  }

  public void purchaseSpawner(
      final Player player, final Spawner product, final CreatureSpawner subject) {
    final Viewer viewer = BukkitViewer.wrap(player);
    if (product.creatureType() == subject.getSpawnedType()) {
      viewer.deliver(
          messageCompiler.compile(messageSource.spawnerCouldNotBePurchasedBecauseOfSameType));
      return;
    }

    economyFacade
        .has(player.getUniqueId(), fundsCurrency, product.price())
        .thenCompose(
            whetherHasEnoughMoney ->
                finalizeSpawnerPurchase(player, product, subject, whetherHasEnoughMoney))
        .thenApply(messageCompiler::compile)
        .thenAccept(viewer::deliver)
        .thenAccept(state -> scheduler.run(SYNC, player::closeInventory))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  private CompletableFuture<MutableMessage> finalizeSpawnerPurchase(
      final Player player,
      final Spawner product,
      final CreatureSpawner subject,
      final boolean whetherHasEnoughMoney) {
    if (!whetherHasEnoughMoney) {
      return completedFuture(messageSource.spawnerCouldNotBePurchasedBecauseOfMissingMoney);
    }

    return economyFacade
        .withdraw(player.getUniqueId(), fundsCurrency, product.price())
        .thenApply(
            state -> {
              scheduler.run(SYNC, () -> updateSpawnedType(subject, product.creatureType()));
              return messageSource
                  .spawnerPurchased
                  .placeholder(SPAWNER_PATH, product)
                  .placeholder(CURRENCY_PATH, fundsCurrency);
            })
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  private void updateSpawnedType(final CreatureSpawner spawner, final EntityType targetType) {
    spawner.setSpawnedType(targetType);
    spawner.update();
  }
}
