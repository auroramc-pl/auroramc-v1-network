package pl.auroramc.commons.memoize;

import java.time.Duration;
import java.util.function.Supplier;

public class MemoizedSupplier<T> implements Supplier<T> {

  private final Memoized<T> memoizedValue;

  private MemoizedSupplier(final Duration timeToLive, final Supplier<T> valueInitializer) {
    this.memoizedValue = Memoized.memoize(timeToLive, valueInitializer);
  }

  public static <T> MemoizedSupplier<T> memoize(
      final Duration timeToLive, final Supplier<T> valueInitializer) {
    return new MemoizedSupplier<>(timeToLive, valueInitializer);
  }

  @Override
  public T get() {
    return memoizedValue.get();
  }
}
