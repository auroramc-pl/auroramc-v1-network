package pl.auroramc.commons.message;

import static pl.auroramc.commons.message.MutableMessageCompiler.getMutableMessageCompiler;

import net.kyori.adventure.text.Component;

@Deprecated(forRemoval = true)
public final class MutableMessageProxy {

  private static final MutableMessageCompiler MESSAGE_COMPILER = getMutableMessageCompiler();

  private MutableMessageProxy() {}

  public static Component getCompiledMessage(
      final MutableMessage mutableMessage,
      final MutableMessageDecoration[] decorations,
      final boolean shouldCache) {
    return MESSAGE_COMPILER.getCompiledMessage(mutableMessage, decorations, shouldCache);
  }

  public static Component getCompiledMessage(
      final MutableMessage mutableMessage, final MutableMessageDecoration[] decorations) {
    return getCompiledMessage(mutableMessage, decorations, true);
  }
}
