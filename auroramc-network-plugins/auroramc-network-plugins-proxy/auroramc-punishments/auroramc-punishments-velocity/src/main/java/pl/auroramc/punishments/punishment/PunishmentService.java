package pl.auroramc.punishments.punishment;

import static java.time.Duration.ofSeconds;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

class PunishmentService implements PunishmentFacade {

  private final Logger logger;
  private final PunishmentRepository punishmentRepository;
  private final AsyncLoadingCache<PunishmentCompositeKey, Punishment>
      punishmentByPenalizedIdWithScopeAndState;

  PunishmentService(final Logger logger, final PunishmentRepository punishmentRepository) {
    this.logger = logger;
    this.punishmentRepository = punishmentRepository;
    this.punishmentByPenalizedIdWithScopeAndState =
        Caffeine.newBuilder()
            .expireAfterWrite(ofSeconds(30))
            .buildAsync(
                compositeKey ->
                    punishmentRepository.findPunishmentByPenalizedIdWithScopeAndState(
                        compositeKey.penalizedId(), compositeKey.scope(), compositeKey.state()));
  }

  @Override
  public CompletableFuture<Punishment> getPunishmentById(final Long id) {
    return supplyAsync(() -> punishmentRepository.findPunishmentById(id))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Override
  public CompletableFuture<Punishment> getPunishmentByPenalizedIdWithScopeAndState(
      final Long penalizedId, final PunishmentScope scope, final PunishmentState state) {
    final PunishmentCompositeKey compositeKey =
        new PunishmentCompositeKey(penalizedId, scope, state);
    return punishmentByPenalizedIdWithScopeAndState
        .get(compositeKey)
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Override
  public CompletableFuture<List<Punishment>> getPunishmentsByPenalizedId(final Long penalizedId) {
    return supplyAsync(() -> punishmentRepository.findPunishmentsByPenalizedId(penalizedId))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Override
  public CompletableFuture<Void> createPunishment(final Punishment punishment) {
    return runAsync(() -> punishmentRepository.createPunishment(punishment))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Override
  public CompletableFuture<Void> updatePunishment(final Punishment punishment) {
    return runAsync(() -> punishmentRepository.updatePunishment(punishment))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }
}
