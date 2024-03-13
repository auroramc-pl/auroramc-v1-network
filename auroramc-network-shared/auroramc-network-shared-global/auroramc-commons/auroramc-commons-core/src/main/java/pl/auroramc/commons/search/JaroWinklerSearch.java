package pl.auroramc.commons.search;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Comparator.comparingDouble;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;

class JaroWinklerSearch implements FuzzySearch {

  private static final Comparator<FuzzySearchEntry> SCORE_COMPARATOR = comparingDouble(
      FuzzySearchEntry::score
  );
  private static final double EXACT_MATCH_SCORE = 1D;
  private final double prefixScale;
  private final double prefixLength;
  private final boolean caseSensitive;

  JaroWinklerSearch(final double prefixScale, final double prefixLength, final boolean caseSensitive) {
    this.prefixScale = prefixScale;
    this.prefixLength = prefixLength;
    this.caseSensitive = caseSensitive;
  }

  @Override
  public String getMostSimilar(
      final String source, final String[] targets, final double minimumScore
  ) {
    final List<FuzzySearchEntry> matches = new ArrayList<>();
    for (final String target : targets) {
      final double score = getSimilarityScore(source, target);
      if (score >= EXACT_MATCH_SCORE) {
        return target;
      }

      if (score < minimumScore) {
        continue;
      }

      matches.add(new FuzzySearchEntry(target, score));
    }

    return matches.stream()
        .max(SCORE_COMPARATOR)
        .map(FuzzySearchEntry::source)
        .orElse(null);
  }

  @Override
  public double getSimilarityScore(final String source, final String target) {
    final int lengthOfSource = source.length();
    final int lengthOfTarget = target.length();
    if (lengthOfSource == 0 && lengthOfTarget == 0) {
      return 0F;
    }

    if (caseSensitive ? source.equals(target) : source.equalsIgnoreCase(target)) {
      return 1F;
    }

    final int frame = max(0, max(lengthOfSource, lengthOfTarget) / 2 - 1);
    final BitSet sourceMatched = new BitSet(lengthOfSource);
    final BitSet targetMatched = new BitSet(lengthOfTarget);

    double matches = 0;
    for (int i = 0; i < lengthOfSource; i++) {
      final int a = max(0, i - frame);
      final int b = min((i + frame + 1), lengthOfTarget);
      for (int j = a; j < b; j++) {
        if (!sourceMatched.get(i) && (source.charAt(i) ^ target.charAt(j)) == 0) {
          sourceMatched.set(i);
          targetMatched.set(j);
          matches++;
          break;
        }
      }
    }

    if (matches == 0) {
      return 0D;
    }

    double transpositions = 0;

    int j = 0;
    for (int i = 0; i < lengthOfSource; i++) {
      if (!sourceMatched.get(i)) {
        continue;
      }

      while (j < lengthOfTarget && !targetMatched.get(j)) {
        j++;
      }

      if ((source.charAt(i) ^ target.charAt(j)) != 0) {
        transpositions++;
      }
    }

    final double similarity = (matches / lengthOfSource + matches / lengthOfTarget + (matches - transpositions / 2) / matches) / 3;
    return similarity + (prefixScale * getLengthOfPrefix(source, target) * (1 - similarity));
  }

  private double getLengthOfPrefix(final String source, final String target) {
    final int n = min(min(source.length(), target.length()), (int) prefixLength);
    int i = 0;
    int j = 0;
    while (i < n && (source.charAt(i) ^ target.charAt(i)) == 0) {
      i++;
      j++;
    }
    return j;
  }
}
