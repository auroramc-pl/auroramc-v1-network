package pl.auroramc.bazaars.sign;

import static org.bukkit.block.sign.Side.FRONT;

import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

class SignDelegateBukkit extends SignDelegate {

  SignDelegateBukkit(final Sign sign) {
    super(sign.getSide(FRONT).lines());
  }

  SignDelegateBukkit(final SignChangeEvent event) {
    super(event.lines());
  }
}
