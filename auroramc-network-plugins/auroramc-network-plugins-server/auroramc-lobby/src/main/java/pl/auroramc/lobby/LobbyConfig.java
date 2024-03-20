package pl.auroramc.lobby;

import static org.bukkit.Bukkit.getWorld;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import org.bukkit.Location;

public class LobbyConfig extends OkaeriConfig {

  public static final @Exclude String PLUGIN_CONFIG_FILE_NAME = "config.yml";

  public Location spawn = new Location(getWorld("world"), 0, 70, 0, 0, 0);
}
