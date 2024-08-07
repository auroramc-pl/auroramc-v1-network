package pl.auroramc.registry.settings;

import java.util.concurrent.CompletableFuture;

public interface SettingsFacade {

  CompletableFuture<Settings> getSettingsByUserId(final Long userId);

  CompletableFuture<Void> createSettings(final Settings settings);

  CompletableFuture<Void> updateSettings(final Settings settings);
}
