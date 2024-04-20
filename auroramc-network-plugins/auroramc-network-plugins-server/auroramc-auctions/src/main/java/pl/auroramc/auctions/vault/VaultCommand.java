package pl.auroramc.auctions.vault;

import static pl.auroramc.auctions.vault.VaultViewFactory.produceVaultView;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;

@Permission("auroramc.auctions.vault")
@Command(name = "vault")
public class VaultCommand {

  private final Plugin plugin;
  private final Scheduler scheduler;
  private final VaultMessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;
  private final VaultController vaultController;

  public VaultCommand(
      final Plugin plugin,
      final Scheduler scheduler,
      final VaultMessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final VaultController vaultController) {
    this.plugin = plugin;
    this.scheduler = scheduler;
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
    this.vaultController = vaultController;
  }

  @Execute
  public void vault(final @Context Player player) {
    produceVaultView(
            plugin,
            scheduler,
            messageSource,
            messageCompiler,
            vaultController,
            player.getUniqueId())
        .show(player);
  }
}
