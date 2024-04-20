package pl.auroramc.quests.objective;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static pl.auroramc.messages.message.MutableMessage.LINE_DELIMITER;
import static pl.auroramc.messages.message.decoration.MessageDecorations.NO_CURSIVE;
import static pl.auroramc.quests.message.MessageSourcePaths.ITEM_PATH;
import static pl.auroramc.quests.message.MessageSourcePaths.OBJECTIVE_PATH;
import static pl.auroramc.quests.message.MessageSourcePaths.OBJECTIVE_PROGRESS_PATH;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.messages.message.compiler.CompiledMessage;
import pl.auroramc.quests.objective.progress.ObjectiveProgress;
import pl.auroramc.quests.objective.requirement.HeldItemRequirement;
import pl.auroramc.quests.objective.requirement.ObjectiveRequirement;

public class ObjectiveController {

  private final BukkitMessageCompiler messageCompiler;

  public ObjectiveController(final BukkitMessageCompiler messageCompiler) {
    this.messageCompiler = messageCompiler;
  }

  public List<CompiledMessage> getQuestObjective(
      final Objective<?> objective, final ObjectiveProgress objectiveProgress) {
    return concat(
            Stream.of(getQuestObjective0(objective, objectiveProgress)),
            objective.getRequirements().stream().map(this::getObjectiveRequirement))
        .toList();
  }

  private CompiledMessage getQuestObjective0(
      final Objective<?> objective, final ObjectiveProgress objectiveProgress) {
    return messageCompiler.compile(
        objective
            .getMessage()
            .placeholder(OBJECTIVE_PATH, objective)
            .placeholder(OBJECTIVE_PROGRESS_PATH, objectiveProgress),
        NO_CURSIVE);
  }

  public List<CompiledMessage> getQuestObjectives(
      final List<? extends Objective<?>> objectives,
      final Map<? extends Objective<?>, ObjectiveProgress> objectiveToObjectiveProgress) {
    return objectives.stream()
        .map(objective -> getQuestObjective(objective, objectiveToObjectiveProgress.get(objective)))
        .flatMap(List::stream)
        .toList();
  }

  private CompiledMessage getObjectiveRequirement(final ObjectiveRequirement requirement) {
    final Map<String, Object> placeholders = new HashMap<>();
    if (requirement instanceof HeldItemRequirement heldItemRequirement) {
      placeholders.put(ITEM_PATH, heldItemRequirement.getRequiredMaterial());
    }
    return messageCompiler.compile(requirement.getMessage().placeholders(placeholders), NO_CURSIVE);
  }

  public String getQuestObjectiveTemplate(
      final Objective<?> objective, final ObjectiveProgress objectiveProgress) {
    return getTemplateOfCompiledMessages(getQuestObjective(objective, objectiveProgress));
  }

  public String getQuestObjectivesTemplate(
      final List<? extends Objective<?>> objectives,
      final Map<? extends Objective<?>, ObjectiveProgress> objectiveToObjectiveProgress) {
    return getTemplateOfCompiledMessages(
        getQuestObjectives(objectives, objectiveToObjectiveProgress));
  }

  private String getTemplateOfCompiledMessages(final List<CompiledMessage> compiledMessages) {
    return compiledMessages.stream()
        .map(CompiledMessage::getComponent)
        .map(miniMessage()::serialize)
        .collect(joining(LINE_DELIMITER));
  }
}
