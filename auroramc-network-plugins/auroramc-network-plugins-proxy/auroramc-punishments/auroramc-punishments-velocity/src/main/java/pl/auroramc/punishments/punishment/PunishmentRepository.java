package pl.auroramc.punishments.punishment;

import java.util.List;

interface PunishmentRepository {

  Punishment findPunishmentById(final Long id);

  Punishment findPunishmentByPenalizedIdWithScopeAndState(
      final Long penalizedId, final PunishmentScope scope, final PunishmentState state);

  List<Punishment> findPunishmentsByPenalizedId(final Long penalizedId);

  void createPunishment(final Punishment punishment);

  void updatePunishment(final Punishment punishment);
}
