package pl.auroramc.economy.rest.server;

import static io.javalin.http.HttpStatus.FORBIDDEN;
import static pl.auroramc.economy.rest.server.RestResponse.response;

import io.javalin.http.Context;
import io.javalin.http.ExceptionHandler;
import io.javalin.http.Handler;

public class RestSecurityHandler implements Handler, ExceptionHandler<RestSecurityException> {

  private final RestSecurityConfig restSecurityConfig;

  public RestSecurityHandler(final RestSecurityConfig restSecurityConfig) {
    this.restSecurityConfig = restSecurityConfig;
  }

  @Override
  public void handle(final Context context) {
    final String retrievedApiKey = context.header(restSecurityConfig.apiKeyHeaderName);
    if (retrievedApiKey == null) {
      throw new RestSecurityException("Could not retrieve api key from request.");
    }

    if (!retrievedApiKey.equals(restSecurityConfig.apiKey)) {
      throw new RestSecurityException("Invalid api key.");
    }
  }

  @Override
  public void handle(final RestSecurityException exception, final Context context) {
    context
        .status(FORBIDDEN)
        .json(response(FORBIDDEN, exception.getMessage()));
  }
}
