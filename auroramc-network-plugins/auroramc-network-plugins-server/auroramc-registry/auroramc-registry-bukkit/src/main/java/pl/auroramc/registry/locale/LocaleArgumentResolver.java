package pl.auroramc.registry.locale;

import static dev.rollczi.litecommands.argument.parser.ParseResult.failure;
import static dev.rollczi.litecommands.argument.parser.ParseResult.success;
import static java.lang.String.join;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static pl.auroramc.registry.message.RegistryMessageSourcePaths.LOCALES_PATH;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import pl.auroramc.messages.i18n.Message;

public class LocaleArgumentResolver<SENDER> extends ArgumentResolver<SENDER, Locale> {

  private static final String LOCALE_DELIMITER = ", ";
  private final Map<String, Locale> supportedLocales;
  private final Message localeNotSupported;

  public LocaleArgumentResolver(
      final Set<Locale> supportedLocales, final Message localeNotSupported) {
    this.supportedLocales =
        supportedLocales.stream().collect(toMap(Locale::getLanguage, identity()));
    this.localeNotSupported = localeNotSupported;
  }

  @Override
  protected ParseResult<Locale> parse(
      final Invocation<SENDER> invocation, final Argument<Locale> context, final String argument) {
    return supportedLocales.containsKey(argument)
        ? success(supportedLocales.get(argument))
        : failure(
            localeNotSupported.placeholder(
                LOCALES_PATH, join(LOCALE_DELIMITER, supportedLocales.keySet())));
  }

  @Override
  public SuggestionResult suggest(
      final Invocation<SENDER> invocation,
      final Argument<Locale> argument,
      final SuggestionContext context) {
    return SuggestionResult.of(supportedLocales.keySet());
  }
}
