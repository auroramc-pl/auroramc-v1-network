package pl.auroramc.quests.quest;

import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;
import static pl.auroramc.quests.message.MessageVariableKey.QUEST_VARIABLE_KEY;
import static pl.auroramc.quests.message.MessageVariableKey.USERNAME_VARIABLE_KEY;
import static pl.auroramc.quests.quest.QuestState.COMPLETED;
import static pl.auroramc.quests.quest.QuestState.IN_PROGRESS;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.quests.message.MessageSource;
import pl.auroramc.quests.quest.track.QuestTrack;
import pl.auroramc.quests.quest.track.QuestTrackController;
import pl.auroramc.quests.quest.track.QuestTrackFacade;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

@Permission("auroramc.quests.quests")
@Command(name = "quests")
public class QuestsCommand {

  private final Logger logger;
  private final MessageSource messageSource;
  private final UserFacade userFacade;
  private final QuestsView questsView;
  private final QuestTrackFacade questTrackFacade;
  private final QuestTrackController questTrackController;

  public QuestsCommand(
      final Logger logger,
      final MessageSource messageSource,
      final UserFacade userFacade,
      final QuestsView questsView,
      final QuestTrackFacade questTrackFacade,
      final QuestTrackController questTrackController
  ) {
    this.logger = logger;
    this.messageSource = messageSource;
    this.userFacade = userFacade;
    this.questsView = questsView;
    this.questTrackFacade = questTrackFacade;
    this.questTrackController = questTrackController;
  }

  @Execute
  public void quests(final @Context Player player) {
    questsView.render(player);
  }

  @Execute(name = "assign")
  public CompletableFuture<MutableMessage> assign(
      final @Arg Player target,
      final @Arg Quest quest,
      final @Arg Optional<QuestState> state
  ) {
    return userFacade.getUserByUniqueId(target.getUniqueId())
        .thenApply(user -> assignQuest(user, quest, state.orElse(IN_PROGRESS)))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private MutableMessage assignQuest(
      final User user, final Quest quest, final QuestState state
  ) {
    final Optional<QuestState> currentState =
        questTrackFacade.getQuestTrackByUniqueIdAndQuestId(user.getUniqueId(), quest.getKey().getId())
            .map(QuestTrack::getQuestState);
    if (currentState.isPresent()) {
      return (
          currentState.get() == COMPLETED
              ? messageSource.questIsAlreadyCompleted
              : messageSource.questIsAlreadyAssigned
      )
          .with(USERNAME_VARIABLE_KEY, user.getUsername())
          .with(QUEST_VARIABLE_KEY, quest.getKey().getName());
    }

    questTrackController.assignQuest(user, quest, state);
    return messageSource.questHasBeenAssigned
        .with(USERNAME_VARIABLE_KEY, user.getUsername())
        .with(QUEST_VARIABLE_KEY, quest.getKey().getName());
  }
}
