package pl.auroramc.quests.integration.placeholderapi;

import static java.lang.String.join;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
abstract class PlaceholderExpansionDelegate extends PlaceholderExpansion {

  private static final String PLACEHOLDER_API_PLUGIN_NAME = "PlaceholderAPI";
  private final Plugin plugin;

  PlaceholderExpansionDelegate(final Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean canRegister() {
    return plugin.getServer().getPluginManager().isPluginEnabled(PLACEHOLDER_API_PLUGIN_NAME);
  }

  @Override
  public @NotNull String getIdentifier() {
    return plugin.getPluginMeta().getName();
  }

  @Override
  public @NotNull String getAuthor() {
    return plugin.getPluginMeta().getVersion();
  }

  @Override
  public @NotNull String getVersion() {
    return join(", ", plugin.getPluginMeta().getAuthors());
  }
}
