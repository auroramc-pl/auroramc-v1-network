package pl.auroramc.quests.quest.track;

import static java.util.concurrent.CompletableFuture.runAsync;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static pl.auroramc.commons.BukkitUtils.postToMainThread;
import static pl.auroramc.quests.quest.QuestState.COMPLETED;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pl.auroramc.quests.message.MessageSource;
import pl.auroramc.quests.objective.Objective;
import pl.auroramc.quests.objective.progress.ObjectiveProgressFacade;
import pl.auroramc.quests.quest.Quest;
import pl.auroramc.quests.quest.QuestState;
import pl.auroramc.quests.quest.observer.QuestObserver;
import pl.auroramc.quests.quest.observer.QuestObserverFacade;
import pl.auroramc.quests.quest.reward.QuestReward;
import pl.auroramc.registry.user.User;

public class QuestTrackController {

  private final Plugin plugin;
  private final Server server;
  private final MessageSource messageSource;
  private final QuestTrackFacade questTrackFacade;
  private final QuestObserverFacade questObserverFacade;
  private final ObjectiveProgressFacade objectiveProgressFacade;

  public QuestTrackController(
      final Plugin plugin,
      final Server server,
      final MessageSource messageSource,
      final QuestTrackFacade questTrackFacade,
      final QuestObserverFacade questObserverFacade,
      final ObjectiveProgressFacade objectiveProgressFacade
  ) {
    this.plugin = plugin;
    this.server = server;
    this.messageSource = messageSource;
    this.questTrackFacade = questTrackFacade;
    this.questObserverFacade = questObserverFacade;
    this.objectiveProgressFacade = objectiveProgressFacade;
  }

  public Optional<QuestTrack> getQuestByUserIdAndQuestId(final UUID userUniqueId, final long questId) {
    return questTrackFacade.getQuestTrackByUserUniqueIdAndQuestId(userUniqueId, questId);
  }

  public CompletableFuture<Void> assignQuest(
      final User user, final Quest quest, final QuestState state
  ) {
    return runAsync(() ->
        questTrackFacade.createQuestTrack(
            user.getUniqueId(),
            new QuestTrack(user.getId(), quest.getKey().getId(), state)
        )
    ).thenRun(() -> {
      for (final Objective<?> objective : quest.getObjectives()) {
        objectiveProgressFacade.resolveObjectiveProgress(user.getId(), quest.getKey().getId(),
            objective.getKey().getId(),
            objective.getGoalResolver().resolveGoal());
      }
    });
  }

  public void completeQuest(final User user, final Quest quest) {
    objectiveProgressFacade.deleteObjectiveProgressByUserIdAndQuestId(user.getId(), quest.getKey().getId());

    final QuestTrack questTrack = questTrackFacade.getQuestTrackByUserUniqueIdAndQuestId(user.getUniqueId(), quest.getKey().getId())
        .orElseThrow(() -> new QuestTrackResolvingException(
            "Could not get quest track for user %s and quest %d".formatted(
            user.getUniqueId(), quest.getKey().getId())));
    questTrack.setQuestState(COMPLETED);
    questTrackFacade.updateQuestTrack(questTrack);

    Optional.ofNullable(server.getPlayer(user.getUniqueId()))
        .ifPresent(target -> postToMainThread(plugin, () -> completeQuestSequence(quest, target)));
  }

  private void completeQuestSequence(final Quest quest, final Player player) {
    for (final QuestReward<?> reward : quest.getRewards()) {
      // noinspection unchecked
      ((QuestReward<Player>) reward).apply(player);
    }

    final QuestObserver questObserver = questObserverFacade.findQuestObserverByUserUniqueId(
        player.getUniqueId());
    if (questObserver != null && Objects.equals(questObserver.getQuestId(), quest.getKey().getId())) {
      questObserver.setQuestId(null);
      questObserverFacade.updateQuestObserver(questObserver);
    }

    player.sendMessage(messageSource.questHasBeenCompleted
        .with("quest", miniMessage().serialize(quest.getIcon().displayName()))
        .into());
  }
}
