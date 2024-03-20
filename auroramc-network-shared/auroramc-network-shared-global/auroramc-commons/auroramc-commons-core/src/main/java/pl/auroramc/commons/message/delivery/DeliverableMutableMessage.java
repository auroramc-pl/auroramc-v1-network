package pl.auroramc.commons.message.delivery;

import static pl.auroramc.commons.message.delivery.DeliverableMutableMessageDisplay.CHAT;
import static pl.auroramc.commons.message.delivery.DeliverableMutableMessageDisplay.NONE;

import java.util.Set;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.commons.message.MutableMessageDecoration;

public class DeliverableMutableMessage {

  private static final DeliverableMutableMessage EMPTY_DELIVERABLE_MUTABLE_MESSAGE =
      new DeliverableMutableMessage(
          MutableMessage.empty(),
          Set.of(),
          Set.of(NONE)
      );
  private final MutableMessage mutableMessage;
  private final Set<MutableMessageDecoration> decorations;
  private final Set<DeliverableMutableMessageDisplay> displays;

  DeliverableMutableMessage(
      final MutableMessage mutableMessage,
      final Set<MutableMessageDecoration> decorations,
      final Set<DeliverableMutableMessageDisplay> displays
  ) {
    this.mutableMessage = mutableMessage;
    this.decorations = decorations;
    this.displays = displays;
  }

  public static DeliverableMutableMessage of(
      final MutableMessage mutableMessage,
      final Set<MutableMessageDecoration> decorations,
      final Set<DeliverableMutableMessageDisplay> displays
  ) {
    return new DeliverableMutableMessage(mutableMessage, decorations, displays);
  }

  public static DeliverableMutableMessage of(
      final MutableMessage mutableMessage
  ) {
    return new DeliverableMutableMessage(
        mutableMessage,
        Set.of(),
        Set.of(CHAT)
    );
  }

  public static DeliverableMutableMessage empty() {
    return EMPTY_DELIVERABLE_MUTABLE_MESSAGE;
  }

  public DeliverableMutableMessage with(final String key, final Object value) {
    return new DeliverableMutableMessage(
        mutableMessage.with(key, value),
        decorations,
        displays
    );
  }

  public DeliverableMutableMessage display(final DeliverableMutableMessageDisplay display) {
    displays.add(display);
    return this;
  }

  public Component compile(final MutableMessageDecoration... decorations) {
    return mutableMessage.compile(decorations);
  }

  public Component[] compileChildren(
      final String delimiter, final MutableMessageDecoration... decorations
  ) {
    return mutableMessage.compileChildren(delimiter, decorations);
  }

  public Component[] compileChildren(final MutableMessageDecoration... decorations) {
    return mutableMessage.compileChildren(decorations);
  }

  public void deliver(
      final Audience viewer
  ) {
    final Component compiledMessage = mutableMessage.compile(
        decorations
            .toArray(new MutableMessageDecoration[0])
    );
    for (final DeliverableMutableMessageDisplay display : displays) {
      deliver(viewer, compiledMessage, display);
    }
  }

  private void deliver(
      final Audience viewer,
      final Component compiledMessage,
      final DeliverableMutableMessageDisplay display
  ) {
    switch (display) {
      case NONE -> {}
      case CHAT -> viewer.sendMessage(compiledMessage);
      case ACTION_BAR -> viewer.sendActionBar(compiledMessage);
    }
  }

  public MutableMessage getMutableMessage() {
    return mutableMessage;
  }

  public Set<MutableMessageDecoration> getDecorations() {
    return decorations;
  }

  public Set<DeliverableMutableMessageDisplay> getDisplays() {
    return displays;
  }
}
