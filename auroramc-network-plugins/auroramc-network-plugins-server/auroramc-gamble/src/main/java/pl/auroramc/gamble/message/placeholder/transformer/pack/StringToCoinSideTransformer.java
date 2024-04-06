package pl.auroramc.gamble.message.placeholder.transformer.pack;

import static java.util.Locale.ROOT;
import static org.apache.commons.lang3.StringUtils.capitalize;

import pl.auroramc.gamble.coinflip.CoinSide;
import pl.auroramc.messages.placeholder.transformer.pack.ObjectTransformer;

class StringToCoinSideTransformer implements ObjectTransformer<CoinSide, String> {

  StringToCoinSideTransformer() {}

  @Override
  public String transform(final CoinSide value) {
    return capitalize(value.name().toLowerCase(ROOT));
  }

  @Override
  public Class<?> getType() {
    return CoinSide.class;
  }
}
