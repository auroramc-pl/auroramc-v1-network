package pl.auroramc.auctions.vault;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class VaultMessageSource extends OkaeriConfig {

  public MutableMessage vaultItemReceived =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Do twojego schowka został dodany <#7c5058>x<subject.@amount></dark_gray> <#f4a9ba><subject><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>, możesz odebrać go używając <#f4a9ba><click:run_command:/vault>/vault</click><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>.");

  public MutableMessage vaultItemRedeemed =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Odebrałeś <#7c5058>x<subject.@amount></dark_gray> <#f4a9ba><subject> <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>ze swojego schowka.");

  public MutableMessage vaultItemRedeemSuggestion =
      MutableMessage.of("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Naciśnij aby odebrać ten przedmiot.");
}
