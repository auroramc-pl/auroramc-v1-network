package pl.auroramc.quests;

import static java.util.Locale.ROOT;

import eu.okaeri.configs.OkaeriConfig;
import java.util.List;
import pl.auroramc.messages.message.MutableMessage;
import pl.auroramc.quests.message.MessageSource;
import pl.auroramc.quests.objective.Objective;
import pl.auroramc.quests.objective.requirement.ObjectiveRequirement;
import pl.auroramc.quests.quest.Quest;

final class QuestsBukkitPluginUtils {

  QuestsBukkitPluginUtils() {}

  static void initTranslationForObjectivesFromQuests(
      final MessageSource messageSource, final List<Quest> quests) {
    for (final Quest quest : quests) {
      initTranslationForObjectivesAndRequirements(messageSource, quest.getObjectives());
    }
  }

  private static void initTranslationForObjectivesAndRequirements(
      final MessageSource messageSource, final List<Objective<?>> objectives) {
    for (final Objective<?> objective : objectives) {
      objective.setMessage(
          getMessageByFieldName(
              messageSource.objective, getMessageFieldNameByObjective(objective)));
      for (final ObjectiveRequirement requirement : objective.getRequirements()) {
        requirement.setMessage(
            getMessageByFieldName(
                messageSource.requirement, getMessageFieldNameByRequirement(requirement)));
      }
    }
  }

  private static MutableMessage getMessageByFieldName(
      final OkaeriConfig messageSource, final String translationFieldName) {
    try {
      return (MutableMessage)
          messageSource.getClass().getDeclaredField(translationFieldName).get(messageSource);
    } catch (final NoSuchFieldException | IllegalAccessException exception) {
      throw new QuestsInstantiationException(
          "Could not resolve message field by %s field name, because of unexpected exception."
              .formatted(translationFieldName),
          exception);
    }
  }

  private static String getMessageFieldNameByObjective(final Objective<?> objective) {
    return getMessageFieldName(objective.getClass());
  }

  private static String getMessageFieldNameByRequirement(final ObjectiveRequirement requirement) {
    return getMessageFieldName(requirement.getClass());
  }

  private static String getMessageFieldName(final Class<?> clazz) {
    final String nameOfClazzWithoutPackage =
        clazz.getSimpleName().replace(clazz.getPackageName(), "");
    final String initialCharacterOfClazzLowercase =
        nameOfClazzWithoutPackage.substring(0, 1).toLowerCase(ROOT);
    final String clazzWithoutInitialCharacter = nameOfClazzWithoutPackage.substring(1);
    return initialCharacterOfClazzLowercase + clazzWithoutInitialCharacter;
  }
}
