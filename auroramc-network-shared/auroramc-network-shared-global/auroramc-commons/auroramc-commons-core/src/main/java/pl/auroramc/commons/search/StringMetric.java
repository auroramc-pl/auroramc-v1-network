package pl.auroramc.commons.search;

public interface StringMetric {

  static StringMetric getStringMetric(final double prefixScale, final int prefixLength) {
    return new StringMetricImpl(prefixScale, prefixLength);
  }

  double getSimilarityScore(final String source, final String target);
}
