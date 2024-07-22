package pl.auroramc.spawners.message.placeholder.transformer.pack;

import static java.util.Arrays.stream;
import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.joining;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.EntityType;
import pl.auroramc.messages.placeholder.transformer.pack.ObjectTransformer;

class StringToEntityTypeTransformer extends ObjectTransformer<EntityType, String> {

  private static final String NAME_DELIMITER = " ";
  private static final String NAME_SEGMENTS_DELIMITER = "_";

  StringToEntityTypeTransformer() {
    super(EntityType.class);
  }

  @Override
  public String transform(final EntityType value) {
    return stream(value.name().toLowerCase(ROOT).split(NAME_SEGMENTS_DELIMITER))
            .map(StringUtils::capitalize)
            .collect(joining(NAME_DELIMITER))
        + NAME_DELIMITER;
  }
}
