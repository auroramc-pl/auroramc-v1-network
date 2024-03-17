package pl.auroramc.auctions.audience;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;

public interface AudienceFacade {

  static AudienceFacade getAudienceFacade(final Logger logger, final Juliet juliet) {
    final SqlAudienceRepository sqlAudienceRepository = new SqlAudienceRepository(juliet);
    sqlAudienceRepository.createAudienceSchemaIfRequired();
    return new AudienceService(logger, sqlAudienceRepository);
  }

  CompletableFuture<Audience> getAudienceByUniqueId(final UUID uniqueId);

  CompletableFuture<Void> createAudience(final Audience audience);

  CompletableFuture<Void> updateAudience(final Audience audience);
}
