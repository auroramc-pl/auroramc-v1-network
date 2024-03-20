package pl.auroramc.commons.config.serdes;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import java.text.DecimalFormat;
import org.jetbrains.annotations.NotNull;

class StringToDecimalFormatTransformer extends BidirectionalTransformer<String, DecimalFormat> {

  StringToDecimalFormatTransformer() {}

  @Override
  public GenericsPair<String, DecimalFormat> getPair() {
    return genericsPair(String.class, DecimalFormat.class);
  }

  @Override
  public DecimalFormat leftToRight(
      final @NotNull String data, final @NotNull SerdesContext serdesContext) {
    return new DecimalFormat(data);
  }

  @Override
  public String rightToLeft(
      final @NotNull DecimalFormat data, final @NotNull SerdesContext serdesContext) {
    return data.toPattern();
  }
}
