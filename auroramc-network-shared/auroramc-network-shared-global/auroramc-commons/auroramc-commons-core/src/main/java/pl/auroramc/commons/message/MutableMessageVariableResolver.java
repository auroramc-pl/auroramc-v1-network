package pl.auroramc.commons.message;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;

@Deprecated(forRemoval = true)
record MutableMessageVariableResolver<T>(Function<T, String> resolver) {

  private static final MutableMessageVariableResolver<Object> MESSAGE_DEFAULT_VARIABLE_RESOLVER;
  private static final Map<Class<?>, MutableMessageVariableResolver<?>> MESSAGE_VARIABLE_RESOLVERS;

  static {
    MESSAGE_DEFAULT_VARIABLE_RESOLVER = new MutableMessageVariableResolver<>(String::valueOf);
    MESSAGE_VARIABLE_RESOLVERS = new HashMap<>();
    MESSAGE_VARIABLE_RESOLVERS.put(
        TextComponent.class, new MutableMessageVariableResolver<>(miniMessage()::serialize));
    MESSAGE_VARIABLE_RESOLVERS.put(
        TranslatableComponent.class,
        new MutableMessageVariableResolver<>(miniMessage()::serialize));
    MESSAGE_VARIABLE_RESOLVERS.put(
        MutableMessage.class,
        new MutableMessageVariableResolver<MutableMessage>(
            message -> miniMessage().serialize(message.compile())));
  }

  static MutableMessageVariableResolver<?> getMessageVariableResolver(final Class<?> variableType) {
    for (final Map.Entry<Class<?>, MutableMessageVariableResolver<?>> entry :
        MESSAGE_VARIABLE_RESOLVERS.entrySet()) {
      if (entry.getKey().isAssignableFrom(variableType)) {
        return entry.getValue();
      }
    }

    return MESSAGE_DEFAULT_VARIABLE_RESOLVER;
  }

  static String getResolvedMessageVariable(final Object value) {
    return getMessageVariableResolver(value.getClass()).resolve(value);
  }

  @SuppressWarnings("unchecked")
  private String resolve(final Object value) {
    return resolver.apply((T) value);
  }
}
