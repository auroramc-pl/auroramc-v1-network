package pl.auroramc.commons.search;

import java.util.Set;

public interface FuzzySearch {

  static FuzzySearch getJaroWinklerSearch(
      final double prefixScale, final double prefixLength, final boolean caseSensitive
  ) {
    return new JaroWinklerSearch(prefixScale, prefixLength, caseSensitive);
  }

  static FuzzySearch getJaroWinklerSearchWithDefaultSettings() {
    return getJaroWinklerSearch(0.1, 4, false);
  }

  default String getMostSimilar(
      final String source, final Set<String> targets, final double minimumScore
  ) {
    return getMostSimilar(source, targets.toArray(new String[0]), minimumScore);
  }

  String getMostSimilar(final String source, final String[] targets, final double minimumScore);

  double getSimilarityScore(final String source, final String target);
}
