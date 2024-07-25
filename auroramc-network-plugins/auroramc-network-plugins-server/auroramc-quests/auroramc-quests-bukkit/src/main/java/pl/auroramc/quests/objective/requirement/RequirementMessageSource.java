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
          "<#7c5058>* <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Aby wykonać cel, musisz używać przedmiotu <#f4a9ba><item>");
}
