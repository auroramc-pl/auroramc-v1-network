package pl.auroramc.commons.search;

import org.openjdk.jmh.annotations.*;

import static pl.auroramc.commons.search.StringMetric.getStringMetric;

@State(Scope.Benchmark)
public class StringMetricBenchmark {

  private final StringMetric stringMetric = getStringMetric(0.1, 4);

  @Benchmark
  public void shortText() {
    stringMetric.getSimilarityScore("test", "test");
  }

  @Benchmark
  public void longText() {
    stringMetric.getSimilarityScore("a very long text", "a very long text");
  }

  @Benchmark
  public void longTextWithTypos() {
    stringMetric.getSimilarityScore("a clueless person", "a cluelezz perzon");
  }
}
