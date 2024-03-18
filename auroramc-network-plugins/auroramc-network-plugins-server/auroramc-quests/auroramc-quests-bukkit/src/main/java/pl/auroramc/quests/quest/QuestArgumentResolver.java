package pl.auroramc.quests.quest;

import static dev.rollczi.litecommands.argument.parser.ParseResult.failure;
import static java.util.Locale.ROOT;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import pl.auroramc.quests.message.MutableMessageSource;
import pl.auroramc.quests.resource.key.ResourceKey;

public class QuestArgumentResolver<T> extends ArgumentResolver<T, Quest> {

  private final MutableMessageSource messageSource;
  private final QuestIndex questIndex;

  public QuestArgumentResolver(final MutableMessageSource messageSource, final QuestIndex questIndex) {
    this.messageSource = messageSource;
    this.questIndex = questIndex;
  }

  @Override
  protected ParseResult<Quest> parse(
      final Invocation<T> invocation, final Argument<Quest> context, final String argument) {
    return questIndex.resolveQuests().stream()
        .filter(quest -> quest.getKey().getName().equals(argument.toLowerCase(ROOT)))
        .findAny()
        .map(ParseResult::success)
        .orElseGet(() ->
            failure(
                messageSource.questCouldNotBeFound.with("quest", argument)
            )
        );
  }

  @Override
  public SuggestionResult suggest(
      final Invocation<T> invocation, final Argument<Quest> argument, final SuggestionContext context) {
    return questIndex.resolveQuests().stream()
        .map(Quest::getKey)
        .map(ResourceKey::getName)
        .collect(SuggestionResult.collector());
  }
}
