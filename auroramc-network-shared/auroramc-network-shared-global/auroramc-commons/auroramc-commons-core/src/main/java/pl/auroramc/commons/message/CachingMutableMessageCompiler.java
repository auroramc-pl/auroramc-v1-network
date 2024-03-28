package pl.auroramc.commons.message;

import static java.time.Duration.ofSeconds;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Optional;
import net.kyori.adventure.text.Component;

@Deprecated(forRemoval = true)
class CachingMutableMessageCompiler implements MutableMessageCompiler {

  private final Cache<MutableMessage, Component> compiledMessageByMutableMessage;

  CachingMutableMessageCompiler() {
    this.compiledMessageByMutableMessage =
        Caffeine.newBuilder().expireAfterAccess(ofSeconds(20)).build();
  }

  @Override
  public Component getCompiledMessage(
      final MutableMessage mutableMessage,
      final MutableMessageDecoration[] decorations,
      final boolean shouldCache) {
    return Optional.ofNullable(compiledMessageByMutableMessage.getIfPresent(mutableMessage))
        .orElseGet(() -> createMessage(mutableMessage, decorations, shouldCache));
  }

  private Component createMessage(
      final MutableMessage mutableMessage,
      final MutableMessageDecoration[] decorations,
      final boolean shouldCache) {
    final Component compiledMessage =
        miniMessage()
            .deserialize(mutableMessage.getTemplate())
            .decorations(
                stream(decorations)
                    .collect(
                        toMap(
                            MutableMessageDecoration::decoration,
                            MutableMessageDecoration::state)));
    if (shouldCache) {
      compiledMessageByMutableMessage.put(mutableMessage, compiledMessage);
    }
    return compiledMessage;
  }
}
