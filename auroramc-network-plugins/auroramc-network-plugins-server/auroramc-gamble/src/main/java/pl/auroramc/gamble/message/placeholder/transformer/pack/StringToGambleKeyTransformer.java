package pl.auroramc.gamble.message.placeholder.transformer.pack;

import static java.util.Locale.ROOT;
import static org.apache.commons.lang3.StringUtils.capitalize;

import pl.auroramc.gamble.gamble.GambleKey;
import pl.auroramc.messages.placeholder.transformer.pack.ObjectTransformer;

public class StringToGambleKeyTransformer implements ObjectTransformer<GambleKey, String> {

  @Override
  public String transform(final GambleKey gambleKey) {
    return capitalize(gambleKey.id().toLowerCase(ROOT));
  }

  @Override
  public Class<?> getType() {
    return GambleKey.class;
  }
}
