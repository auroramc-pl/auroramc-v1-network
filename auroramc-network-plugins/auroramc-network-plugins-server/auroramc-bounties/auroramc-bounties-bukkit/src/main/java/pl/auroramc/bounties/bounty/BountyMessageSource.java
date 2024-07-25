package pl.auroramc.bounties.bounty;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class BountyMessageSource extends OkaeriConfig {

  public MutableMessage bountyTitle = MutableMessage.of("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Dzień <#f4a9ba><day>");

  public MutableMessage bountyAvailable =
      MutableMessage.of("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Naciśnij <#f4a9ba>LPM<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>, aby odebrać tą nagrodę.");

  public MutableMessage bountyAvailableSinceTomorrow =
      MutableMessage.of("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Nagroda ta będzie dla ciebie dostępna do odebrania już jutro.");

  public MutableMessage remainingTimeUntilBounty =
      MutableMessage.of("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Nagrodę będziesz mógł odebrać za <#f4a9ba><remaining_time><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>.");

  public MutableMessage remainingDaysUntilBounty =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Nagroda będzie dostępna do odebrania za <#f4a9ba><remaining_days><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>.");

  public MutableMessage pastBounty =
      MutableMessage.of("<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Ta nagroda została już przez ciebie odebrana.");

  public MutableMessage bountyAcquired =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Odebrałeś nagrodę dzienną za <#f4a9ba><day> <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>dzień, następna nagroda będzie dostępna do odebrania już jutro.");
}
