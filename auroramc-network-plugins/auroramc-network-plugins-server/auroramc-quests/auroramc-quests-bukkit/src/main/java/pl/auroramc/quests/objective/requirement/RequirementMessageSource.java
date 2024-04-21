package pl.auroramc.quests.objective.requirement;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class RequirementMessageSource extends OkaeriConfig {

  public MutableMessage heldItemRequirement =
      MutableMessage.of(
          "<dark_gray>* <gray>Aby wykonać cel, musisz używać przedmiotu <white>{item}");
}
