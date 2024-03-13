package pl.auroramc.commons.message;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.kyori.adventure.text.TextComponent;

record MutableMessageVariableResolver<T>(Function<T, String> resolver) {

  private static final Map<Class<?>, MutableMessageVariableResolver<?>> MESSAGE_VARIABLE_RESOLVERS;
  private static final MutableMessageVariableResolver<Object> MESSAGE_DEFAULT_VARIABLE_RESOLVER =
      new MutableMessageVariableResolver<>(String::valueOf);

  static {
    MESSAGE_VARIABLE_RESOLVERS = new HashMap<>();
    MESSAGE_VARIABLE_RESOLVERS.put(TextComponent.class, new MutableMessageVariableResolver<>(miniMessage()::serialize));
  }

  static MutableMessageVariableResolver<?> getMessageVariableResolver(
      final Class<?> variableType
  ) {
    for (final Map.Entry<Class<?>, MutableMessageVariableResolver<?>> entry : MESSAGE_VARIABLE_RESOLVERS.entrySet()) {
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
