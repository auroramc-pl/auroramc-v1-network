package pl.auroramc.auctions.vault;

import static com.github.stefvanschie.inventoryframework.gui.type.ChestGui.load;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import org.bukkit.plugin.Plugin;
import pl.auroramc.auctions.message.MessageSource;

class VaultViewFactory {

  private VaultViewFactory() {

  }

  static ChestGui produceVaultView(
      final Plugin plugin,
      final MessageSource messageSource,
      final VaultController vaultController,
      final UUID vaultOwnerUniqueId
  ) {
    try (final InputStream inputStream = plugin.getResource("guis/vault.xml")) {
      if (inputStream == null) {
        throw new VaultViewInstantiationException(
            "Could not find vault gui definition in resources.");
      }

      return load(
          new VaultView(
              plugin,
              messageSource,
              vaultController,
              vaultOwnerUniqueId
          ),
          inputStream,
          plugin
      );
    } catch (final IOException exception) {
      throw new VaultViewInstantiationException(
          "Could not load vault gui from resources, because of unexpected exception.",
          exception
      );
    }
  }
}
