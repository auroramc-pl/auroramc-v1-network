package pl.auroramc.commons.plural.variety;

import static pl.auroramc.commons.plural.PluralizationCase.PLURAL_GENITIVE;
import static pl.auroramc.commons.plural.PluralizationCase.PLURAL_NOMINATIVE;
import static pl.auroramc.commons.plural.PluralizationCase.SINGULAR;

import java.util.EnumMap;
import java.util.Map;
import pl.auroramc.commons.plural.PluralizationCase;

public class VarietiesByCasesBuilder {

  private final Map<PluralizationCase, String> pluralForms;

  VarietiesByCasesBuilder() {
    this.pluralForms = new EnumMap<>(PluralizationCase.class);
  }

  public VarietiesByCasesBuilder withPluralForm(
      final PluralizationCase pluralizationCase, final String pluralForm) {
    pluralForms.put(pluralizationCase, pluralForm);
    return this;
  }

  public VarietiesByCasesBuilder withPluralForm(final String pluralForm) {
    withPluralForm(SINGULAR, pluralForm);
    withPluralForm(PLURAL_GENITIVE, pluralForm);
    withPluralForm(PLURAL_NOMINATIVE, pluralForm);
    return this;
  }

  public VarietiesByCases build() {
    return new VarietiesByCases(pluralForms);
  }
}
