package pl.auroramc.commons.config.serdes.message;

import static pl.auroramc.commons.config.ConfigUtils.getAsHashSet;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.jetbrains.annotations.NotNull;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.commons.message.MutableMessageDecoration;
import pl.auroramc.commons.message.delivery.DeliverableMutableMessage;
import pl.auroramc.commons.message.delivery.DeliverableMutableMessageDisplay;

class DeliverableMutableMessageSerializer implements ObjectSerializer<DeliverableMutableMessage> {

  DeliverableMutableMessageSerializer() {}

  @Override
  public boolean supports(final @NotNull Class<? super DeliverableMutableMessage> type) {
    return DeliverableMutableMessage.class.isAssignableFrom(type);
  }

  @Override
  public void serialize(
      final @NotNull DeliverableMutableMessage object,
      final @NotNull SerializationData data,
      final @NotNull GenericsDeclaration generics) {
    data.add("message", object.getMutableMessage());
    if (!object.getDecorations().isEmpty()) {
      data.addCollection("decorations", object.getDecorations(), MutableMessageDecoration.class);
    }
    data.addCollection("displays", object.getDisplays(), DeliverableMutableMessageDisplay.class);
  }

  @Override
  public DeliverableMutableMessage deserialize(
      final @NotNull DeserializationData data, final @NotNull GenericsDeclaration generics) {
    return DeliverableMutableMessage.of(
        data.get("message", MutableMessage.class),
        getAsHashSet(data, "decorations", MutableMessageDecoration.class),
        getAsHashSet(data, "displays", DeliverableMutableMessageDisplay.class));
  }
}
