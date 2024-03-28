package pl.auroramc.commons.message;

import static me.clip.placeholderapi.PlaceholderAPI.setPlaceholders;

import org.bukkit.entity.Player;

@Deprecated(forRemoval = true)
public class BukkitMutableMessage extends MutableMessage {

  BukkitMutableMessage(final String template) {
    super(template);
  }

  public static BukkitMutableMessage of(final MutableMessage message) {
    return new BukkitMutableMessage(message.getTemplate());
  }

  public BukkitMutableMessage withTargetedPlaceholders(final Player player) {
    return new BukkitMutableMessage(setPlaceholders(player, getTemplate()));
  }
}
