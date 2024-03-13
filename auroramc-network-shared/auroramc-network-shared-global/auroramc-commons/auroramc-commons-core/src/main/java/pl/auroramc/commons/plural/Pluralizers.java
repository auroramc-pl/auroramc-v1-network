package pl.auroramc.commons.plural;

import static java.lang.Math.abs;
import static java.util.Locale.ENGLISH;
import static pl.auroramc.commons.plural.PluralizationCase.PLURAL_GENITIVE;
import static pl.auroramc.commons.plural.PluralizationCase.PLURAL_NOMINATIVE;
import static pl.auroramc.commons.plural.PluralizationCase.SINGULAR;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Pluralizers {

  private static final String POLISH_LOCALE_TAG = "pl";
  private static final Locale POLISH = new Locale(POLISH_LOCALE_TAG);
  private static final Map<Locale, Pluralizer> PLURALIZES_BY_LOCALE = new HashMap<>();

  static {
    PLURALIZES_BY_LOCALE.put(POLISH, (context, count) -> {
      if (count % 10 >= 2 && count % 10 <= 4 && (count % 100 < 10 || count % 100 >= 20)) {
        return context.getPluralForm(PLURAL_NOMINATIVE);
      }

      return context.getPluralForm(count == 1 ? SINGULAR : PLURAL_GENITIVE);
    });
    PLURALIZES_BY_LOCALE.put(ENGLISH, (context, count) ->
        context.getPluralForm(abs(count) == 1 ? SINGULAR : PLURAL_GENITIVE));
  }

  private Pluralizers() {

  }

  public static Pluralizer getPluralizer(final Locale locale) {
    return PLURALIZES_BY_LOCALE.getOrDefault(locale, PLURALIZES_BY_LOCALE.get(POLISH));
  }
}