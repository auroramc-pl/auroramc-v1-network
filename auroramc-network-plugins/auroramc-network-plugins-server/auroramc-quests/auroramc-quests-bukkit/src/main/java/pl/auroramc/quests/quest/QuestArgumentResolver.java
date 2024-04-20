package pl.auroramc.quests.quest;

import static dev.rollczi.litecommands.argument.parser.ParseResult.failure;
import static java.util.Locale.ROOT;
import static pl.auroramc.quests.message.MessageSourcePaths.INPUT_PATH;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import java.util.Optional;
import pl.auroramc.quests.message.MessageSource;
import pl.auroramc.quests.resource.key.ResourceKey;

public class QuestArgumentResolver<T> extends ArgumentResolver<T, Quest> {

  private final MessageSource messageSource;
  private final QuestIndex questIndex;

  public QuestArgumentResolver(final MessageSource messageSource, final QuestIndex questIndex) {
    this.messageSource = messageSource;
    this.questIndex = questIndex;
  }

  @Override
  protected ParseResult<Quest> parse(
      final Invocation<T> invocation, final Argument<Quest> context, final String argument) {
    return Optional.ofNullable(questIndex.getQuestByName(argument.toLowerCase(ROOT)))
        .map(ParseResult::success)
        .orElseGet(
            () -> failure(messageSource.questCouldNotBeFound.placeholder(INPUT_PATH, argument)));
  }

  @Override
  public SuggestionResult suggest(
      final Invocation<T> invocation,
      final Argument<Quest> argument,
      final SuggestionContext context) {
    return questIndex.getQuests().stream()
        .map(Quest::getKey)
        .map(ResourceKey::getName)
        .collect(SuggestionResult.collector());
  }
}
