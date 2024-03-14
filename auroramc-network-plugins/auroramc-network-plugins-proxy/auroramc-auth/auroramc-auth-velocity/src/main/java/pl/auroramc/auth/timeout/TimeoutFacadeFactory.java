package pl.auroramc.auth.timeout;

public final class TimeoutFacadeFactory {

  private TimeoutFacadeFactory() {

  }

  public static TimeoutFacade getTimeoutFacade() {
    return new TimeoutService();
  }
}
