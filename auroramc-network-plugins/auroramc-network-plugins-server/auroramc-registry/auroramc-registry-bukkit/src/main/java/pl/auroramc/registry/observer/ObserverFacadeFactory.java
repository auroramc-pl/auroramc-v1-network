package pl.auroramc.registry.observer;

import moe.rafal.juliet.Juliet;
import pl.auroramc.commons.scheduler.Scheduler;

public final class ObserverFacadeFactory {

  private ObserverFacadeFactory() {}

  public static ObserverFacade getObserverFacade(final Scheduler scheduler, final Juliet juliet) {
    final SqlObserverRepository sqlObserverRepository = new SqlObserverRepository(juliet);
    sqlObserverRepository.createObserverSchemaIfRequired();
    return new ObserverService(scheduler, sqlObserverRepository);
  }
}
