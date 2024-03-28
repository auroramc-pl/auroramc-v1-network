package pl.auroramc.commons.message;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static pl.auroramc.commons.message.MutableMessageProxy.getCompiledMessage;
import static pl.auroramc.commons.message.MutableMessageVariableResolver.getResolvedMessageVariable;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;

@Deprecated(forRemoval = true)
public class MutableMessage {

  public static final String LINE_SEPARATOR = "<newline>";
  public static final String EMPTY_DELIMITER = "";
  private static final char PLACEHOLDER_KEY_INITIATOR = '{';
  private static final char PLACEHOLDER_KEY_TERMINATOR = '}';
  private static final MutableMessage EMPTY_MUTABLE_MESSAGE = of(EMPTY_DELIMITER);
  private static final MutableMessage NEWLINE_MUTABLE_MESSAGE = of(LINE_SEPARATOR);
  private final String template;

  MutableMessage(final String template) {
    this.template = template;
  }

  public static MutableMessage of(final String template) {
    return new MutableMessage(template);
  }

  public static MutableMessage empty() {
    return EMPTY_MUTABLE_MESSAGE;
  }

  public static MutableMessage newline() {
    return NEWLINE_MUTABLE_MESSAGE;
  }

  public static MutableMessageCollector collector() {
    return new MutableMessageCollector();
  }

  public boolean isEmpty() {
    return template.isEmpty();
  }

  public MutableMessage with(final String key, final Object value) {
    return new MutableMessage(
        template.replace(
            PLACEHOLDER_KEY_INITIATOR + key + PLACEHOLDER_KEY_TERMINATOR,
            getResolvedMessageVariable(value)));
  }

  public MutableMessage append(final MutableMessage message, final String delimiter) {
    if (isEmpty()) {
      return message;
    }

    return of(template + delimiter + message.getTemplate());
  }

  public MutableMessage append(final MutableMessage message) {
    return append(message, LINE_SEPARATOR);
  }

  public MutableMessage[] children(final String delimiter) {
    return Stream.of(template.split(delimiter))
        .map(MutableMessage::of)
        .toArray(MutableMessage[]::new);
  }

  public MutableMessage[] children() {
    return children(LINE_SEPARATOR);
  }

  public Component compile(final MutableMessageDecoration... decorations) {
    return getCompiledMessage(this, decorations);
  }

  public Component[] compileChildren(
      final String delimiter, final MutableMessageDecoration... decorations) {
    return Stream.of(children(delimiter))
        .map(message -> message.compile(decorations))
        .toArray(Component[]::new);
  }

  public Component[] compileChildren(final MutableMessageDecoration... decorations) {
    return compileChildren(LINE_SEPARATOR, decorations);
  }

  public String getTemplate() {
    return template;
  }

  public CompletableFuture<MutableMessage> asCompletedFuture() {
    return completedFuture(this);
  }
}
