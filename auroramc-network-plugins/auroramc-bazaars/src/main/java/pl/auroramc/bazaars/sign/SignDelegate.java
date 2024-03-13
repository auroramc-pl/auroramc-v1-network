package pl.auroramc.bazaars.sign;

import java.util.List;
import net.kyori.adventure.text.Component;

public class SignDelegate {

  private final List<Component> lines;

  SignDelegate(final List<Component> lines) {
    this.lines = lines;
  }

  public Component line(final int index) {
    return lines.get(index);
  }
}
