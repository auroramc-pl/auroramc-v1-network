package pl.auroramc.commons.search;

class FuzzySearchImpl implements FuzzySearch {

  private static final double EXACT_MATCH = 1D;

  private final StringMetric stringMetric;

  FuzzySearchImpl(final StringMetric stringMetric) {
    this.stringMetric = stringMetric;
  }

  @Override
  public String getMostSimilarString(
      final String source, final Iterable<String> possibilities, final double threshold) {
    StringMetricResult closestResult = null;
    for (final String possibility : possibilities) {
      final double similarity = stringMetric.getSimilarityScore(source, possibility);
      if (similarity >= EXACT_MATCH) {
        return possibility;
      }

      if (similarity >= threshold
          && (closestResult == null || closestResult.similarity() < similarity)) {
        closestResult =
            new StringMetricResult(
                possibility, stringMetric.getSimilarityScore(source, possibility));
      }
    }
    return closestResult != null ? closestResult.result() : null;
  }
}
