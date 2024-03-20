package pl.auroramc.punishments.punishment;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PunishmentFacade {

  CompletableFuture<Punishment> getPunishmentById(final Long id);

  CompletableFuture<Punishment> getPunishmentByPenalizedIdWithScopeAndState(
      final Long penalizedId, final PunishmentScope scope, final PunishmentState state);

  CompletableFuture<List<Punishment>> getPunishmentsByPenalizedId(final Long penalizedId);

  CompletableFuture<Void> createPunishment(final Punishment punishment);

  CompletableFuture<Void> updatePunishment(final Punishment punishment);
}
