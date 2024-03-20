package pl.auroramc.commons.message;

import static net.kyori.adventure.text.format.TextDecoration.ITALIC;
import static net.kyori.adventure.text.format.TextDecoration.State.FALSE;

import net.kyori.adventure.text.format.TextDecoration;

public record MutableMessageDecoration(TextDecoration decoration, TextDecoration.State state) {

  public static MutableMessageDecoration NO_CURSIVE = of(ITALIC, FALSE);

  public static MutableMessageDecoration of(
      final TextDecoration decoration, final TextDecoration.State state) {
    return new MutableMessageDecoration(decoration, state);
  }
}
