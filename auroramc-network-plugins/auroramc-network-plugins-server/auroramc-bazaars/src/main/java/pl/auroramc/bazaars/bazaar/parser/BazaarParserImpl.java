package pl.auroramc.bazaars.bazaar.parser;

import static java.lang.Integer.parseInt;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_DOWN;
import static net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText;
import static org.bukkit.Material.matchMaterial;
import static pl.auroramc.bazaars.bazaar.BazaarType.getBazaarTypeByShortcut;
import static pl.auroramc.bazaars.bazaar.parser.BazaarParserToken.MATERIAL;
import static pl.auroramc.bazaars.bazaar.parser.BazaarParserToken.MERCHANT;
import static pl.auroramc.bazaars.bazaar.parser.BazaarParserToken.PRICE;
import static pl.auroramc.bazaars.bazaar.parser.BazaarParserToken.QUANTITY;
import static pl.auroramc.bazaars.bazaar.parser.BazaarParsingAssertions.assertNotEmpty;
import static pl.auroramc.bazaars.bazaar.parser.BazaarParsingAssertions.assertNotNull;
import static pl.auroramc.commons.format.decimal.DecimalParser.getParsedDecimal;

import java.math.BigDecimal;
import java.util.Optional;
import org.bukkit.Material;
import pl.auroramc.bazaars.bazaar.BazaarType;
import pl.auroramc.bazaars.sign.SignDelegate;

// Bazaar sign structure:
// * 1st line - merchant
// * 2nd line - quantity
// * 3rd line - price
// * 4th line - material
class BazaarParserImpl implements BazaarParser {

  private static final String EMPTY_LINE = "";

  @Override
  public BazaarParsingContext parseContext(final SignDelegate sign) throws BazaarParsingException {
    final String merchant =
        assertNotEmpty(
            getLineText(sign, MERCHANT.getLineIndex()),
            "Could not parse bazaar, because of malformed merchant.");

    final int quantity;
    try {
      quantity = parseInt(getLineText(sign, QUANTITY.getLineIndex()));
    } catch (final NumberFormatException exception) {
      throw new BazaarParsingException("Could not parse bazaar, because of malformed quantity.");
    }

    if (quantity <= 0) {
      throw new BazaarParsingException(
          "Could not parse bazaar, because of quantity equal or less than zero.");
    }

    final String combinedPriceValue = getLineText(sign, PRICE.getLineIndex());
    final BazaarType bazaarType =
        assertNotNull(
            getBazaarTypeByShortcut(combinedPriceValue.charAt(0)),
            "Could not parse bazaar, because of malformed bazaar type.");
    final BigDecimal price;
    try {
      price = getParsedDecimal(combinedPriceValue.substring(2)).setScale(2, HALF_DOWN);
    } catch (final NumberFormatException exception) {
      throw new BazaarParsingException("Could not parse bazaar, because of malformed price.");
    }

    if (price.compareTo(ZERO) <= 0) {
      throw new BazaarParsingException(
          "Could not parse bazaar, because of price equal or less than zero.");
    }

    final Material material =
        assertNotNull(
            matchMaterial(getLineText(sign, MATERIAL.getLineIndex())),
            "Could not parse bazaar, because of malformed material.");

    return new BazaarParsingContext(bazaarType, merchant, quantity, price, material);
  }

  @Override
  public BazaarParsingContext parseContextOrNull(final SignDelegate sign) {
    try {
      return parseContext(sign);
    } catch (final BazaarParsingException exception) {
      return null;
    }
  }

  private String getLineText(final SignDelegate sign, final int index) {
    return Optional.ofNullable(sign.line(index)).map(plainText()::serialize).orElse(EMPTY_LINE);
  }
}
