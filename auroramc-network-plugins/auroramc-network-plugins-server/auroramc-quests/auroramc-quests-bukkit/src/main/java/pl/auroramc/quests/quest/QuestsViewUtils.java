package pl.auroramc.quests.quest;

import static java.util.stream.Stream.concat;

import java.util.List;
import java.util.function.IntFunction;

final class QuestsViewUtils {

  private QuestsViewUtils() {

  }

  static <T> T[] mergeLists(final List<T> a, final List<T> b, final IntFunction<T[]> generator) {
    return concat(a.stream(), b.stream()).toArray(generator);
  }
}
