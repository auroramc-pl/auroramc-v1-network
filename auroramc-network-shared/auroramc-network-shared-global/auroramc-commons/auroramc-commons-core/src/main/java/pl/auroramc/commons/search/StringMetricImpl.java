package pl.auroramc.commons.search;

import it.unimi.dsi.bits.LongArrayBitVector;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * The implementation of Jaro-Winkler similarity algorithm, for a string metric that measures an
 * edit distance between two strings.
 *
 * @see <a
 *     href="https://iopscience.iop.org/article/10.1088/1742-6596/1361/1/012080/pdf#:~:text=In%20Jaro%2DWinkler%2C%20prefix%20scale,the%20beginning%20of%20each%20word.">Jaro-Winkler's
 *     similarity</a>
 */
class StringMetricImpl implements StringMetric {

  private final double prefixScale;
  private final int prefixLength;

  StringMetricImpl(final double prefixScale, final int prefixLength) {
    this.prefixScale = prefixScale;
    this.prefixLength = prefixLength;
  }

  @Override
  public double getSimilarityScore(final String source, final String target) {
    final int lengthOfSource = source.length();
    final int lengthOfTarget = target.length();
    if (lengthOfSource == 0 && lengthOfTarget == 0) {
      return 0F;
    }

    if (source.equalsIgnoreCase(target)) {
      return 1F;
    }

    final LongArrayBitVector sourceSet = LongArrayBitVector.ofLength(lengthOfSource);
    final LongArrayBitVector targetSet = LongArrayBitVector.ofLength(lengthOfTarget);

    final int m = getNumberOfMatchingCharacters(source, target, sourceSet, targetSet);
    final int t = getNumberOfTranspositions(source, target, sourceSet, targetSet);

    return getSimilarityScore(source, target, getSimilarity(source, target, m, t));
  }

  private double getSimilarityScore(
      final String source, final String target, final double similarity) {
    return similarity + (prefixScale * getLengthOfCommonPrefix(source, target) * (1 - similarity));
  }

  private double getSimilarity(final String source, final String target, final int m, final int t) {
    final double lengthOfSource = source.length();
    final double lengthOfTarget = target.length();
    return (m / lengthOfSource + m / lengthOfTarget + (m - t / 2D) / m) / 3D;
  }

  private int getNumberOfMatchingCharacters(
      final String source,
      final String target,
      final LongArrayBitVector sourceSet,
      final LongArrayBitVector targetSet) {
    final int lengthOfSource = source.length();
    final int lengthOfTarget = target.length();
    final int frame = max(0, max(lengthOfSource, lengthOfTarget) / 2 - 1);

    int matches = 0;
    for (int i = 0; i < lengthOfSource; i++) {
      final int a = max(0, i - frame);
      final int b = min((i + frame + 1), lengthOfTarget);
      for (int j = a; j < b; j++) {
        if (!sourceSet.getBoolean(i) && (source.charAt(i) ^ target.charAt(j)) == 0) {
          sourceSet.set(i);
          targetSet.set(j);
          matches++;
          break;
        }
      }
    }

    return matches;
  }

  private int getNumberOfTranspositions(
      final String source,
      final String target,
      final LongArrayBitVector matchesOfSource,
      final LongArrayBitVector matchesOfTarget) {
    final int lengthOfSource = source.length();
    final int lengthOfTarget = target.length();
    int transpositions = 0;

    int j = 0;
    for (int i = 0; i < lengthOfSource; i++) {
      if (!matchesOfSource.getBoolean(i)) {
        continue;
      }

      while (j < lengthOfTarget && !matchesOfTarget.getBoolean(j)) {
        j++;
      }

      if ((source.charAt(i) ^ target.charAt(j)) != 0) {
        transpositions++;
      }
    }

    return transpositions;
  }

  private int getLengthOfCommonPrefix(final String source, final String target) {
    final int n = min(min(source.length(), target.length()), prefixLength);

    int i = 0;
    while (i < n && (source.charAt(i) ^ target.charAt(i)) == 0) {
      i++;
    }

    return i;
  }
}
