package pl.auroramc.commons.message;

import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collector.Characteristics.CONCURRENT;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class MutableMessageCollector implements
    Collector<MutableMessage, List<MutableMessage>, MutableMessage> {

  @Override
  public Supplier<List<MutableMessage>> supplier() {
    return ArrayList::new;
  }

  @Override
  public BiConsumer<List<MutableMessage>, MutableMessage> accumulator() {
    return List::add;
  }

  @Override
  public BinaryOperator<List<MutableMessage>> combiner() {
    return (reducer, elements) -> {
      reducer.addAll(elements);
      return reducer;
    };
  }

  @Override
  public Function<List<MutableMessage>, MutableMessage> finisher() {
    return messages -> messages.stream()
        .reduce(
            MutableMessage.empty(),
            MutableMessage::appendSeparated
        );
  }

  @Override
  public Set<Characteristics> characteristics() {
    return unmodifiableSet(EnumSet.of(CONCURRENT));
  }
}
