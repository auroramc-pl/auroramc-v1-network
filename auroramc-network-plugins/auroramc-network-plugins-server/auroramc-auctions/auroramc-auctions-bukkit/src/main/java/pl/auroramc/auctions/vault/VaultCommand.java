package pl.auroramc.auctions.vault;

import static pl.auroramc.auctions.vault.VaultViewFactory.produceVaultView;

import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@Permission("auroramc.auctions.vault")
@Route(name = "vault")
public class VaultCommand {

  private final Plugin plugin;
  private final VaultController vaultController;

  public VaultCommand(final Plugin plugin, final VaultController vaultController) {
    this.plugin = plugin;
    this.vaultController = vaultController;
  }

  @Execute
  public void displayVault(final Player executor) {
    produceVaultView(plugin, vaultController, executor.getUniqueId())
        .show(executor);
  }
}
