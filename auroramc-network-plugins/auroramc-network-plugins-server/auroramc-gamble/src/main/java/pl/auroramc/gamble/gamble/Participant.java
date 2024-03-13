package pl.auroramc.gamble.gamble;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public record Participant(UUID uniqueId, String username, Object prediction) {

  public static ParticipantBuilder newBuilder() {
    return new ParticipantBuilder();
  }

  public void sendMessage(final Component message) {
    Optional.ofNullable(Bukkit.getPlayer(uniqueId)).ifPresent(player -> player.sendMessage(message));
  }

  @Override
  public boolean equals(final Object object) {
    if (this == object) {
      return true;
    }

    if (object == null || getClass() != object.getClass()) {
      return false;
    }

    final Participant that = (Participant) object;
    return
        Objects.equals(uniqueId, that.uniqueId) &&
        Objects.equals(username, that.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uniqueId, username);
  }
}
