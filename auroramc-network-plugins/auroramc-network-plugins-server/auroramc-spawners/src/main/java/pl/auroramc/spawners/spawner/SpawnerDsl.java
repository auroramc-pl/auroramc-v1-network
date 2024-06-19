package pl.auroramc.spawners.spawner;

import static groovy.lang.Closure.DELEGATE_ONLY;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import java.util.ArrayList;
import java.util.List;

class SpawnerDsl {

  private SpawnerDsl() {}

  public static SpawnersDsl spawners(
      final @DelegatesTo(value = SpawnersDsl.class) Closure<?> closure) {
    final SpawnersDsl delegate = new SpawnersDsl();
    closure.setDelegate(delegate);
    closure.setResolveStrategy(DELEGATE_ONLY);
    closure.call();
    return delegate;
  }

  static class SpawnersDsl {

    private final List<Spawner> spawners;

    SpawnersDsl() {
      this.spawners = new ArrayList<>();
    }

    public void spawner(final @DelegatesTo(value = SpawnerBuilder.class) Closure<?> closure) {
      final SpawnerBuilder delegate = Spawner.newBuilder();
      closure.setDelegate(delegate);
      closure.setResolveStrategy(DELEGATE_ONLY);
      closure.call();
      spawners.add(delegate.build());
    }

    public void spawner(final Spawner spawner) {
      spawners.add(spawner);
    }

    List<Spawner> spawners() {
      return spawners;
    }
  }
}
