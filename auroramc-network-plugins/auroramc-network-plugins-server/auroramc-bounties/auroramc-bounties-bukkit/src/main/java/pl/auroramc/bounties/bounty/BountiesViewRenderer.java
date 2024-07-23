package pl.auroramc.bounties.bounty;

import static pl.auroramc.bounties.bounty.BountiesViewFactory.getBountiesGui;

import java.time.Duration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pl.auroramc.bounties.BountyConfig;
import pl.auroramc.bounties.progress.BountyProgress;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;

public class BountiesViewRenderer {

  private final Plugin plugin;
  private final BountyConfig bountyConfig;
  private final BountyFacade bountyFacade;
  private final BountyController bountyController;
  private final BountyMessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;

  public BountiesViewRenderer(
      final Plugin plugin,
      final BountyConfig bountyConfig,
      final BountyFacade bountyFacade,
      final BountyController bountyController,
      final BountyMessageSource messageSource,
      final BukkitMessageCompiler messageCompiler) {
    this.plugin = plugin;
    this.bountyConfig = bountyConfig;
    this.bountyFacade = bountyFacade;
    this.bountyController = bountyController;
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
  }

  public void render(
      final Player viewer, final BountyProgress bountyProgress, final Duration aggregatedPlaytime) {
    getBountiesGui(
            plugin,
            bountyConfig,
            bountyFacade,
            bountyProgress,
            bountyController,
            aggregatedPlaytime,
            messageSource,
            messageCompiler)
        .show(viewer);
  }
}
