package pl.auroramc.commons.message;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static pl.auroramc.commons.lazy.Lazy.lazy;

import net.kyori.adventure.text.Component;
import pl.auroramc.commons.lazy.Lazy;

public class MutableMessage {

  private static final MutableMessage EMPTY_TRANSLATION_TEMPLATE = of("");
  private static final char PLACEHOLDER_KEY_INITIATOR = '{';
  private static final char PLACEHOLDER_KEY_TERMINATOR = '}';
  private static final String LINE_SEPARATOR = "<newline>";
  private final String template;
  private final Lazy<Component> templateCompiled;

  private MutableMessage(final String template) {
    this.template = template;
    this.templateCompiled = lazy(() -> miniMessage().deserialize(template));
  }

  public static MutableMessage of(final String template) {
    return new MutableMessage(template);
  }

  public static MutableMessage empty() {
    return EMPTY_TRANSLATION_TEMPLATE;
  }

  public static MutableMessageCollector collector() {
    return new MutableMessageCollector();
  }

  public MutableMessage with(final String key, final Object value) {
    return new MutableMessage(template.replace(getKeyWithMarkers(key), String.valueOf(value)));
  }

  public MutableMessage with(final String key, final Component value) {
    return with(key, miniMessage().serialize(value));
  }

  public MutableMessage append(final MutableMessage message) {
    return of(template + message.getTemplate());
  }

  public MutableMessage appendSeparated(final MutableMessage message) {
    return of(
        template + LINE_SEPARATOR + message.getTemplate()
    );
  }

  public Component compile() {
    return templateCompiled.get();
  }

  public String getTemplate() {
    return template;
  }

  private String getKeyWithMarkers(final String rawKey) {
    return PLACEHOLDER_KEY_INITIATOR + rawKey + PLACEHOLDER_KEY_TERMINATOR;
  }
}