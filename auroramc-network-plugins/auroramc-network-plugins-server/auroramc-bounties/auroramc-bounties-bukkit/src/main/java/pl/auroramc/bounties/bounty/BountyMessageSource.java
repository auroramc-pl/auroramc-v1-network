package pl.auroramc.bounties.bounty;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class BountyMessageSource extends OkaeriConfig {

  public MutableMessage bountyTitle =
      MutableMessage.of("<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894><bold>Dzień <day>");

  public MutableMessage bountyAvailable =
      MutableMessage.of(
          "<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>Naciśnij <gradient:#f7cf78:#fbd06a:#f4b352:#fbd06a:#f7cf78><bold><gradient:#f7cf78:#fbd06a:#f4b352:#fbd06a:#f7cf78>LPM</bold><gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>, aby odebrać tą nagrodę.");

  public MutableMessage bountyAvailableSinceTomorrow =
      MutableMessage.of(
          "<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>Nagroda ta będzie dla ciebie dostępna do odebrania już jutro.");

  public MutableMessage remainingTimeUntilBounty =
      MutableMessage.of(
          "<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>Nagrodę będziesz mógł odebrać za <gradient:#f7cf78:#fbd06a:#f4b352:#fbd06a:#f7cf78><remaining_time><gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>.");

  public MutableMessage remainingDaysUntilBounty =
      MutableMessage.of(
          "<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>Nagroda będzie dostępna do odebrania za <gradient:#f7cf78:#fbd06a:#f4b352:#fbd06a:#f7cf78><remaining_days><gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>.");

  public MutableMessage pastBounty =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Ta nagroda została już przez ciebie odebrana.");

  public MutableMessage bountyAcquired =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Odebrałeś nagrodę dzienną za <#f4a9ba><day> <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>dzień, następna nagroda będzie dostępna do odebrania już jutro.");
}
