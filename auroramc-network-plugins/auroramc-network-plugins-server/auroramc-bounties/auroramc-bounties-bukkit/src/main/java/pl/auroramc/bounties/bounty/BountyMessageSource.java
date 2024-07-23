package pl.auroramc.bounties.bounty;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class BountyMessageSource extends OkaeriConfig {

  public MutableMessage bountyTitle = MutableMessage.of("<gray>Dzień <white><day>");

  public MutableMessage bountyAvailable =
      MutableMessage.of("<gray>Naciśnij <white>LPM<gray>, aby odebrać tą nagrodę.");

  public MutableMessage bountyAvailableSinceTomorrow =
      MutableMessage.of("<gray>Nagroda ta będzie dla ciebie dostępna do odebrania już jutro.");

  public MutableMessage remainingTimeUntilBounty =
      MutableMessage.of("<gray>Nagrodę będziesz mógł odebrać za <white><remaining_time><gray>.");

  public MutableMessage remainingDaysUntilBounty =
      MutableMessage.of(
          "<gray>Nagroda będzie dostępna do odebrania za <white><remaining_days><gray>.");

  public MutableMessage pastBounty =
      MutableMessage.of("<red>Ta nagroda została już przez ciebie odebrana.");

  public MutableMessage bountyAcquired =
      MutableMessage.of(
          "<gray>Odebrałeś nagrodę dzienną za <white><day> <gray>dzień, następna nagroda będzie dostępna do odebrania już jutro.");
}
