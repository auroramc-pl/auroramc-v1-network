package pl.auroramc.auctions.vault;

import static pl.auroramc.auctions.vault.VaultViewFactory.produceVaultView;

import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pl.auroramc.auctions.message.MessageSource;

@Permission("auroramc.auctions.vault")
@Route(name = "vault")
public class VaultCommand {

  private final Plugin plugin;
  private final MessageSource messageSource;
  private final VaultController vaultController;

  public VaultCommand(
      final Plugin plugin,
      final MessageSource messageSource,
      final VaultController vaultController
  ) {
    this.plugin = plugin;
    this.messageSource = messageSource;
    this.vaultController = vaultController;
  }

  @Execute
  public void vault(final Player player) {
    produceVaultView(plugin, messageSource, vaultController, player.getUniqueId())
        .show(player);
  }
}
