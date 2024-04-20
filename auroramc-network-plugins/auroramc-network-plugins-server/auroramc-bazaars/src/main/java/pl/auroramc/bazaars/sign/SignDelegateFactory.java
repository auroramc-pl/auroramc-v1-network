package pl.auroramc.bazaars.sign;

import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

public final class SignDelegateFactory {

  private SignDelegateFactory() {}

  public static SignDelegate getSignDelegate(final Sign sign) {
    return new SignDelegateBukkit(sign);
  }

  public static SignDelegate getSignDelegate(final SignChangeEvent event) {
    return new SignDelegateBukkit(event);
  }
}
