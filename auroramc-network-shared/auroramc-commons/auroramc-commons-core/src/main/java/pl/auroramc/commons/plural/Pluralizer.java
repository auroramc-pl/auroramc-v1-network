package pl.auroramc.commons.plural;

import pl.auroramc.commons.plural.variety.VarietiesByCases;

@FunctionalInterface
public interface Pluralizer {

  String pluralize(final VarietiesByCases context, final long count);
}
