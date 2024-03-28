package pl.auroramc.commons.message;

import net.kyori.adventure.text.Component;

@Deprecated(forRemoval = true)
interface MutableMessageCompiler {

  static MutableMessageCompiler getMutableMessageCompiler() {
    return new CachingMutableMessageCompiler();
  }

  Component getCompiledMessage(
      final MutableMessage mutableMessage,
      final MutableMessageDecoration[] decorations,
      boolean shouldCache);
}
