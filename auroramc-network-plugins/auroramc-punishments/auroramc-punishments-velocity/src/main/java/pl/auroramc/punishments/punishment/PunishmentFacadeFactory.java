package pl.auroramc.punishments.punishment;

import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;

public final class PunishmentFacadeFactory {

  private PunishmentFacadeFactory() {

  }

  public static PunishmentFacade getPunishmentFacade(
      final Logger logger,
      final Juliet juliet
  ) {
    final SqlPunishmentRepository punishmentRepository = new SqlPunishmentRepository(juliet);
    punishmentRepository.createPunishmentSchemaIfRequired();
    return new PunishmentService(logger, punishmentRepository);
  }
}
