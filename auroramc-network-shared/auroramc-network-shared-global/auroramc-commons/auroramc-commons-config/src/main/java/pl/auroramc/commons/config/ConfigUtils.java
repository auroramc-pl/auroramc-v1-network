package pl.auroramc.commons.config;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public final class ConfigUtils {

  private ConfigUtils() {}

  public static <T> Set<T> getAsHashSet(
      final @NotNull DeserializationData data,
      final @NotNull String key,
      final @NotNull Class<T> setValueType) {
    if (data.containsKey(key)) {
      final GenericsDeclaration genericType =
          GenericsDeclaration.of(HashSet.class, List.of(setValueType));
      return (Set<T>) data.getAsCollection(key, genericType);
    }

    return new HashSet<>();
  }
}
