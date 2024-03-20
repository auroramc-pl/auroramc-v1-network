package pl.auroramc.commons.search;

public interface FuzzySearch {

  static FuzzySearch getFuzzySearch(final StringMetric stringMetric) {
    return new FuzzySearchImpl(stringMetric);
  }

  String getMostSimilarString(
      final String source, final Iterable<String> possibilities, final double threshold);
}
