package pl.auroramc.commons.collection;

import static java.util.stream.Stream.concat;

import java.util.Collection;
import java.util.function.IntFunction;

public final class CollectionUtils {

  private CollectionUtils() {

  }

  public static <T> T[] merge(
      final Collection<T> a,
      final Collection<T> b,
      final IntFunction<T[]> arrayResolver
  ) {
    return concat(a.stream(), b.stream()).toArray(arrayResolver);
  }
}
