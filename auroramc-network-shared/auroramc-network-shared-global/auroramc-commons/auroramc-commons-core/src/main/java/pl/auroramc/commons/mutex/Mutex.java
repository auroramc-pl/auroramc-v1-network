package pl.auroramc.commons.mutex;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class Mutex<T> {

  private final ReadWriteLock lock = new ReentrantReadWriteLock();
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
    lock.readLock().lock();
    try {
      return value;
    } finally {
      lock.readLock().unlock();
    }
  }

  public <R> R read(final Function<T, R> reader) {
    lock.readLock().lock();
    try {
      return reader.apply(value);
    } finally {
      lock.readLock().unlock();
    }
  }

  public void mutate(final UnaryOperator<T> modifier) {
    lock.writeLock().lock();
    try {
      value = modifier.apply(value);
    } finally {
      lock.writeLock().unlock();
    }
  }

  public void mutate(final T value) {
    lock.writeLock().lock();
    try {
      this.value = value;
    } finally {
      lock.writeLock().unlock();
    }
  }

  public ReadWriteLock getLock() {
    return lock;
  }
}
