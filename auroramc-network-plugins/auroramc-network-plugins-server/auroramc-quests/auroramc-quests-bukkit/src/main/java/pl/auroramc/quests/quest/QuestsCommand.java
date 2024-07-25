package pl.auroramc.quests.quest;

import static java.time.temporal.ChronoUnit.SECONDS;
import static pl.auroramc.quests.quest.QuestMessageSourcePaths.QUEST_PATH;
import static pl.auroramc.quests.quest.QuestMessageSourcePaths.USER_PATH;
import static pl.auroramc.quests.quest.QuestState.COMPLETED;
import static pl.auroramc.quests.quest.QuestState.IN_PROGRESS;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.cooldown.Cooldown;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.optional.OptionalArg;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;
import pl.auroramc.commons.concurrent.CompletableFutureUtils;
import pl.auroramc.messages.message.MutableMessage;
import pl.auroramc.quests.quest.track.QuestTrack;
import pl.auroramc.quests.quest.track.QuestTrackController;
import pl.auroramc.quests.quest.track.QuestTrackFacade;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

@Permission("auroramc.quests.quests")
@Command(
    name = "quests",
    aliases = {"quest", "zadania", "zadanie"})
@Cooldown(key = "quests-cooldown", count = 30, unit = SECONDS)
public class QuestsCommand {

  private final QuestMessageSource messageSource;
  private final UserFacade userFacade;
  private final QuestsView questsView;
  private final QuestTrackFacade questTrackFacade;
  private final QuestTrackController questTrackController;

  public QuestsCommand(
      final QuestMessageSource messageSource,
      final UserFacade userFacade,
      final QuestsView questsView,
      final QuestTrackFacade questTrackFacade,
      final QuestTrackController questTrackController) {
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
      final @Arg Player target, final @Arg Quest quest, final @OptionalArg QuestState state) {
    return userFacade
        .getUserByUniqueId(target.getUniqueId())
        .thenApply(user -> assignQuest(user, quest, state))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  private MutableMessage assignQuest(final User user, final Quest quest, final QuestState state) {
    final Optional<QuestState> currentState =
        questTrackFacade
            .getQuestTrackByUniqueIdAndQuestId(user.getUniqueId(), quest.getKey().getId())
            .map(QuestTrack::getQuestState);
    if (currentState.isPresent()) {
      return (currentState.get() == COMPLETED
              ? messageSource.questIsAlreadyCompleted
              : messageSource.questIsAlreadyAssigned)
          .placeholder(USER_PATH, user)
          .placeholder(QUEST_PATH, quest);
    }

    questTrackController.assignQuest(user, quest, state == null ? IN_PROGRESS : state);
    return messageSource
        .questHasBeenAssigned
        .placeholder(USER_PATH, user)
        .placeholder(QUEST_PATH, quest);
  }
}
