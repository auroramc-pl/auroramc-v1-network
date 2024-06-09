package pl.auroramc.quests.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.integrations.configs.command.CommandMessageSource;
import pl.auroramc.integrations.configs.page.navigation.NavigationMessageSource;
import pl.auroramc.quests.objective.ObjectiveMessageSource;
import pl.auroramc.quests.objective.requirement.RequirementMessageSource;
import pl.auroramc.quests.quest.QuestMessageSource;

public class MessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public QuestMessageSource quest = new QuestMessageSource();

  public ObjectiveMessageSource objective = new ObjectiveMessageSource();

  public RequirementMessageSource requirement = new RequirementMessageSource();

  public NavigationMessageSource navigation = new NavigationMessageSource();

  public CommandMessageSource command = new CommandMessageSource();
}
