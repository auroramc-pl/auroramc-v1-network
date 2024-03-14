package pl.auroramc.auth.account;

import static java.time.Duration.ofSeconds;
import static kong.unirest.core.Unirest.get;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

class HttpAccountService implements AccountFacade {

  private static final String UUID_REQUEST_HTTP_URI_TEMPLATE = "https://api.ashcon.app/mojang/v2/uuid/%s";
  private final AsyncLoadingCache<String, UUID> usernameToPremiumUniqueId;

  HttpAccountService() {
    this.usernameToPremiumUniqueId = Caffeine.newBuilder()
        .expireAfterWrite(ofSeconds(20))
        .buildAsync(this::requestPremiumUniqueIdByUsername);
  }

  @Override
  public CompletableFuture<UUID> getPremiumUniqueIdByUsername(final String username) {
    return usernameToPremiumUniqueId.get(username);
  }

  private UUID requestPremiumUniqueIdByUsername(final String username) {
    return get(UUID_REQUEST_HTTP_URI_TEMPLATE.formatted(username))
        .asString()
        .mapBody(this::parseUuidOrNull);
  }

  private UUID parseUuidOrNull(final String unparsedUuid) {
    try {
      return UUID.fromString(unparsedUuid);
    } catch (final IllegalArgumentException exception) {
      return null;
    }
  }
}
