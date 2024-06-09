package pl.auroramc.quests.objective.requirement;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

/**
 * Fields in that message source are dynamically attached to the {@link ObjectiveRequirement}
 * instances.
 *
 * @see ObjectiveRequirement#getMessage()
 * @see ObjectiveRequirement#setMessage(MutableMessage)
 */
public class RequirementMessageSource extends OkaeriConfig {

  public MutableMessage heldItemRequirement =
      MutableMessage.of(
          "<dark_gray>* <gray>Aby wykonać cel, musisz używać przedmiotu <white>{item}");
}
