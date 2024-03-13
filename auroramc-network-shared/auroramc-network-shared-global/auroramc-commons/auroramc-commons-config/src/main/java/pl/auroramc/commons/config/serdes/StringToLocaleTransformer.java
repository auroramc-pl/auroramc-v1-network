package pl.auroramc.commons.config.serdes;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;

class StringToLocaleTransformer extends BidirectionalTransformer<String, Locale> {

  StringToLocaleTransformer() {

  }

  @Override
  public GenericsPair<String, Locale> getPair() {
    return genericsPair(String.class, Locale.class);
  }

  @Override
  public Locale leftToRight(
      final @NotNull String data,
      final @NotNull SerdesContext serdesContext
  ) {
    return Locale.forLanguageTag(data);
  }

  @Override
  public String rightToLeft(
      final @NotNull Locale data,
      final @NotNull SerdesContext serdesContext
  ) {
    return data.toLanguageTag();
  }
}