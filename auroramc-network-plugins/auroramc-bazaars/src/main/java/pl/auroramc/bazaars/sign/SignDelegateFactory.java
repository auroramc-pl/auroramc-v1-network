package pl.auroramc.bazaars.sign;

import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

public final class SignDelegateFactory {

  private SignDelegateFactory() {

  }

  public static SignDelegate produceSignDelegate(final Sign sign) {
    return new SignDelegateBukkit(sign);
  }

  public static SignDelegate produceSignDelegate(final SignChangeEvent event) {
    return new SignDelegateBukkit(event);
  }
}
