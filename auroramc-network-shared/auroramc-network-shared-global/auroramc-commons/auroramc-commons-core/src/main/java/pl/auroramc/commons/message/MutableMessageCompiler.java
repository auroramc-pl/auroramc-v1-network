package pl.auroramc.commons.message;

import net.kyori.adventure.text.Component;

interface MutableMessageCompiler {

  static MutableMessageCompiler getMutableMessageCompiler() {
    return new CachingMutableMessageCompiler();
  }

  Component getCompiledMessage(
      final MutableMessage mutableMessage,
      final MutableMessageDecoration[] decorations,
      boolean shouldCache);
}
