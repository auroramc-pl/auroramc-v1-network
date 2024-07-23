package pl.auroramc.spawners.spawner;

import static com.github.stefvanschie.inventoryframework.gui.type.ChestGui.load;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import java.io.IOException;
import java.io.InputStream;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.plugin.Plugin;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;

final class SpawnersViewFactory {

  private static final String VIEW_DEFINITION_RESOURCE_PATH = "guis/spawners.xml";

  private SpawnersViewFactory() {}

  static ChestGui getSpawnersGui(
      final Plugin plugin,
      final Currency fundsCurrency,
      final SpawnerFacade spawnerFacade,
      final SpawnerController spawnerController,
      final SpawnerMessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final CreatureSpawner subject) {
    try (final InputStream inputStream = plugin.getResource(VIEW_DEFINITION_RESOURCE_PATH)) {
      if (inputStream == null) {
        throw new SpawnersViewInstantiationException(
            "Could not find spawners gui definition in resources.");
      }

      return load(
          new SpawnersView(
              plugin,
              fundsCurrency,
              spawnerFacade,
              spawnerController,
              messageSource,
              messageCompiler,
              subject),
          inputStream,
          plugin);
    } catch (final IOException exception) {
      throw new SpawnersViewInstantiationException(
          "Could not load spawners gui from resources, because of unexpected exception.",
          exception);
    }
  }
}
