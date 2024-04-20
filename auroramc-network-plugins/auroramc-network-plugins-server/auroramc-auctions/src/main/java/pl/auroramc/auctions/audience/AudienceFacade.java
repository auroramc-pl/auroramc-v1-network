package pl.auroramc.auctions.audience;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import moe.rafal.juliet.Juliet;
import pl.auroramc.commons.scheduler.Scheduler;

public interface AudienceFacade {

  static AudienceFacade getAudienceFacade(final Scheduler scheduler, final Juliet juliet) {
    final SqlAudienceRepository sqlAudienceRepository = new SqlAudienceRepository(juliet);
    sqlAudienceRepository.createAudienceSchemaIfRequired();
    return new AudienceService(scheduler, sqlAudienceRepository);
  }

  CompletableFuture<Audience> getAudienceByUniqueId(final UUID uniqueId);

  CompletableFuture<Void> createAudience(final Audience audience);

  CompletableFuture<Void> updateAudience(final Audience audience);
}
