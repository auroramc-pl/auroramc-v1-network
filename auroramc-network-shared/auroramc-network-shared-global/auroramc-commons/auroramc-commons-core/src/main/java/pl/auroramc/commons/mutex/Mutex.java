package pl.auroramc.commons.mutex;

import java.util.concurrent.locks.StampedLock;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class Mutex<T> {

  private final StampedLock lock = new StampedLock();
  private T value;

  private Mutex(final T value) {
    this.value = value;
  }

  public static <T> Mutex<T> mutex(final T value) {
    return new Mutex<>(value);
  }

  public static <T> Mutex<T> mutex() {
    return new Mutex<>(null);
  }

  public T read() {
    final long stamp = lock.readLock();
    try {
      return value;
    } finally {
      lock.unlockRead(stamp);
    }
  }

  public <R> R read(final Function<T, R> reader) {
    final long stamp = lock.readLock();
    try {
      return reader.apply(value);
    } finally {
      lock.unlockRead(stamp);
    }
  }

  public void mutate(final UnaryOperator<T> modifier) {
    final long stamp = lock.writeLock();
    try {
      value = modifier.apply(value);
    } finally {
      lock.unlockWrite(stamp);
    }
  }

  public void mutate(final T value) {
    final long stamp = lock.writeLock();
    try {
      this.value = value;
    } finally {
      lock.unlockWrite(stamp);
    }
  }

  public StampedLock getLock() {
    return lock;
  }
}
