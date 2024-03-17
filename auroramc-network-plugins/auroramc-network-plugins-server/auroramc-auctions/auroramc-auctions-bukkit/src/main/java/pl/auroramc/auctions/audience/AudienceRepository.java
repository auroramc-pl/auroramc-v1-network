package pl.auroramc.auctions.audience;

import java.util.UUID;

interface AudienceRepository {

  Audience findMessageViewerByUniqueId(final UUID uniqueId);

  void createAudience(final Audience audience);

  void updateAudience(final Audience audience);
}
