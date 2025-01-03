package pl.auroramc.auctions.vault;

import static com.github.stefvanschie.inventoryframework.gui.type.ChestGui.load;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import org.bukkit.plugin.Plugin;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;

class VaultViewFactory {

  private VaultViewFactory() {}

  static ChestGui getVaultView(
      final Plugin plugin,
      final Scheduler scheduler,
      final VaultMessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final VaultController vaultController,
      final UUID vaultOwnerUniqueId) {
    try (final InputStream inputStream = plugin.getResource("guis/vault.xml")) {
      if (inputStream == null) {
        throw new VaultViewInstantiationException(
            "Could not find vault gui definition in resources.");
      }

      return load(
          new VaultView(
              plugin,
              scheduler,
              messageSource,
              messageCompiler,
              vaultController,
              vaultOwnerUniqueId),
          inputStream,
          plugin);
    } catch (final IOException exception) {
      throw new VaultViewInstantiationException(
          "Could not load vault gui from resources, because of unexpected exception.", exception);
    }
  }
}
