package pl.auroramc.commons.message;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static pl.auroramc.commons.lazy.Lazy.lazy;

import net.kyori.adventure.text.Component;
import pl.auroramc.commons.lazy.Lazy;

public class MutableMessage {

  public static final MutableMessage EMPTY_TRANSLATION_TEMPLATE = of("");
  private static final char PLACEHOLDER_KEY_INITIATOR = '{';
  private static final char PLACEHOLDER_KEY_TERMINATOR = '}';
  private final String template;
  private final Lazy<Component> compiledTemplate = lazy(this::compile);

  private MutableMessage(final String template) {
    this.template = template;
  }

  public static MutableMessage of(final String template) {
    return new MutableMessage(template);
  }

  public static MutableMessage empty() {
    return EMPTY_TRANSLATION_TEMPLATE;
  }

  public MutableMessage with(final String rawKey, final Object value) {
    return new MutableMessage(template.replace(getKeyWithMarkers(rawKey), String.valueOf(value)));
  }

  public MutableMessage with(final String rawKey, final Component value) {
    return with(rawKey, miniMessage().serialize(value));
  }

  public Component into() {
    return compiledTemplate.get();
  }

  private Component compile() {
    return miniMessage().deserialize(template);
  }

  private String getKeyWithMarkers(final String rawKey) {
    return PLACEHOLDER_KEY_INITIATOR + rawKey + PLACEHOLDER_KEY_TERMINATOR;
  }

  public String getTemplate() {
    return template;
  }
}