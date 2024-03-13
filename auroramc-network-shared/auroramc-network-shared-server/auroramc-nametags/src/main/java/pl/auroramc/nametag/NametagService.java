package pl.auroramc.nametag;

import static net.kyori.adventure.text.serializer.json.JSONComponentSerializer.json;
import static net.minecraft.network.chat.Component.Serializer.fromJson;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.world.scores.DisplaySlot.BELOW_NAME;
import static net.minecraft.world.scores.criteria.ObjectiveCriteria.DUMMY;
import static net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType.INTEGER;
import static org.bukkit.Bukkit.getOnlinePlayers;
import static pl.auroramc.nametag.NametagUtils.getOutboundConnection;

import net.kyori.adventure.text.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.FixedFormat;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import org.bukkit.entity.Player;
import pl.auroramc.nametag.context.NametagContext;
import pl.auroramc.nametag.context.NametagContextFacade;

class NametagService implements NametagFacade {

  private static final String DUMMY_OBJECTIVE_NAME = "dummy_objective";
  private static final int DUMMY_SCORE = 0;
  private static final MutableComponent EMPTY = literal("");
  private static final MutableComponent ALIGNING_COMPONENT = literal(" ");
  private static final Scoreboard DUMMY_SCOREBOARD = new Scoreboard();
  private static final Objective DUMMY_OBJECTIVE = new Objective(
      DUMMY_SCOREBOARD,
      DUMMY_OBJECTIVE_NAME,
      DUMMY,
      EMPTY,
      INTEGER,
      false,
      new FixedFormat(EMPTY)
  );
  private final NametagContextFacade nametagContextFacade;

  NametagService(final NametagContextFacade nametagContextFacade) {
    this.nametagContextFacade = nametagContextFacade;
  }

  @Override
  public void belowName(final Player player, final Component belowName) {
    nametagContextFacade.saveNameTagContext(player.getUniqueId(), new NametagContext(belowName));
  }

  @Override
  public void inject(final Player player) {
    final ServerPlayerConnection outboundConnection = getOutboundConnection(player);

    final ClientboundSetObjectivePacket setObjectivePacket = new ClientboundSetObjectivePacket(
        DUMMY_OBJECTIVE, 0
    );
    final ClientboundSetDisplayObjectivePacket setDisplayObjectivePacket = new ClientboundSetDisplayObjectivePacket(
        BELOW_NAME, DUMMY_OBJECTIVE
    );

    outboundConnection.send(setObjectivePacket);
    outboundConnection.send(setDisplayObjectivePacket);

    update(player);
  }

  @Override
  public void update(final Player player) {
    final ServerPlayerConnection outboundConnection = getOutboundConnection(player);

    for (final Player seenPlayer : getOnlinePlayers()) {
      final NametagContext nametagContext = nametagContextFacade.findNameTagContextByUniqueId(
          seenPlayer.getUniqueId());
      if (nametagContext == null) {
        continue;
      }

      update(outboundConnection, seenPlayer, nametagContext);
    }
  }

  @Override
  public void updateServerWide() {
    for (final Player onlinePlayer : getOnlinePlayers()) {
      update(onlinePlayer);
    }
  }

  private void update(
      final ServerPlayerConnection outboundConnection,
      final Player seenPlayer,
      final NametagContext nametagContext
  ) {
    final MutableComponent belowName = fromJson(json().serialize(nametagContext.belowName()));
    final FixedFormat belowNameFormat = new FixedFormat(getAlignedBelowName(belowName));

    final ClientboundSetScorePacket setScorePacket = new ClientboundSetScorePacket(
        seenPlayer.getName(),
        DUMMY_OBJECTIVE_NAME,
        DUMMY_SCORE,
        EMPTY,
        belowNameFormat
    );

    outboundConnection.send(setScorePacket);
  }

  private MutableComponent getAlignedBelowName(final MutableComponent belowName) {
    return ALIGNING_COMPONENT
        .copy()
        .append(belowName != null ? belowName : EMPTY);
  }
}
