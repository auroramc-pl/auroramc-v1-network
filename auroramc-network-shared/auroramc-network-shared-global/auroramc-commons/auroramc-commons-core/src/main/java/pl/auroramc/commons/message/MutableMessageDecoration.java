package pl.auroramc.commons.message;

import net.kyori.adventure.text.format.TextDecoration;

public record MutableMessageDecoration(
    TextDecoration decoration,
    TextDecoration.State state
) {

  public static MutableMessageDecoration of(
      final TextDecoration decoration,
      final TextDecoration.State state
  ) {
    return new MutableMessageDecoration(decoration, state);
  }
}
