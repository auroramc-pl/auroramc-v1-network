package pl.auroramc.commons.config.serdes.message;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import pl.auroramc.commons.message.MutableMessageDecoration;

class MutableMessageDecorationSerializer implements ObjectSerializer<MutableMessageDecoration> {

  MutableMessageDecorationSerializer() {

  }

  @Override
  public boolean supports(final @NotNull Class<? super MutableMessageDecoration> type) {
    return MutableMessageDecoration.class.isAssignableFrom(type);
  }

  @Override
  public void serialize(
      final @NotNull MutableMessageDecoration object,
      final @NotNull SerializationData data,
      final @NotNull GenericsDeclaration generics
  ) {
    data.add("decoration", object.decoration());
    data.add("state", object.state());
  }

  @Override
  public MutableMessageDecoration deserialize(
      final @NotNull DeserializationData data,
      final @NotNull GenericsDeclaration generics
  ) {
    return MutableMessageDecoration.of(
        data.get("decoration", TextDecoration.class),
        data.get("state", TextDecoration.State.class)
    );
  }
}
