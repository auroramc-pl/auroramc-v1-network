package pl.auroramc.quests.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.commons.bukkit.page.navigation.NavigationMessageSource;
import pl.auroramc.commons.integration.configs.command.CommandMessageSource;
import pl.auroramc.messages.message.MutableMessage;
import pl.auroramc.quests.quest.QuestMessageSource;

public class MessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public MutableMessage breakBlockObjective =
      MutableMessage.of(
          "<dark_gray>► <gray>Zniszcz <white>{objective.@type} <dark_gray>(<white>{progress.@data}<gray>/<gray>{progress.@goal}<dark_gray>)");

  public MutableMessage placeBlockObjective =
      MutableMessage.of(
          "<dark_gray>► <gray>Postaw <white>{objective.@type} <dark_gray>(<white>{progress.@data}<gray>/<gray>{progress.@goal}<dark_gray>)");

  public MutableMessage distanceObjective =
      MutableMessage.of(
          "<dark_gray>► <gray>Przemierz dystans <dark_gray>(<white>{progress.@data}<gray>/<gray>{progress.@goal}<dark_gray>)");

  public MutableMessage heldItemRequirement =
      MutableMessage.of(
          "<dark_gray>* <gray>Aby wykonać cel, musisz używać przedmiotu <white>{item}");

  public QuestMessageSource quest = new QuestMessageSource();

  public CommandMessageSource command = new CommandMessageSource();

  public NavigationMessageSource navigation = new NavigationMessageSource();
}
