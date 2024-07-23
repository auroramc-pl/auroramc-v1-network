package pl.auroramc.bounties.bounty;

import static com.github.stefvanschie.inventoryframework.gui.type.ChestGui.load;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import org.bukkit.plugin.Plugin;
import pl.auroramc.bounties.BountyConfig;
import pl.auroramc.bounties.progress.BountyProgress;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;

final class BountiesViewFactory {

  private static final String VIEW_DEFINITION_RESOURCE_PATH = "guis/bounties.xml";

  private BountiesViewFactory() {}

  static ChestGui getBountiesGui(
      final Plugin plugin,
      final BountyConfig bountyConfig,
      final BountyFacade bountyFacade,
      final BountyProgress bountyProgress,
      final BountyController bountyController,
      final Duration aggregatedPlaytime,
      final BountyMessageSource messageSource,
      final BukkitMessageCompiler messageCompiler) {
    try (final InputStream inputStream = plugin.getResource(VIEW_DEFINITION_RESOURCE_PATH)) {
      if (inputStream == null) {
        throw new BountiesViewInstantiationException(
            "Could not find bounties gui definition in resources.");
      }

      return load(
          new BountiesView(
              plugin,
              bountyConfig,
              bountyFacade,
              bountyProgress,
              bountyController,
              aggregatedPlaytime,
              messageSource,
              messageCompiler),
          inputStream,
          plugin);
    } catch (final IOException exception) {
      throw new BountiesViewInstantiationException(
          "Could not load bounties gui from resources, because of unexpected exception.",
          exception);
    }
  }
}
