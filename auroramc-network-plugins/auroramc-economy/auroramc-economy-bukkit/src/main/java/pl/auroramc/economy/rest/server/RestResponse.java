package pl.auroramc.economy.rest.server;

import io.javalin.http.HttpStatus;

public record RestResponse(int statusCode, String message) {

  public static RestResponse response(final HttpStatus status, final String message) {
    return new RestResponse(status.getCode(), message);
  }
}
