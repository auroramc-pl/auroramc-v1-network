package pl.auroramc.auctions.vault;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class VaultMessageSource extends OkaeriConfig {

  public MutableMessage vaultItemReceived =
      MutableMessage.of(
          "<gray>Do twojego schowka został dodany <dark_gray>x<subject.@amount></dark_gray> <white><subject><gray>, możesz odebrać go używając <white><click:run_command:/vault>/vault</click><gray>.");

  public MutableMessage vaultItemRedeemed =
      MutableMessage.of("<gray>Odebrałeś <dark_gray>x<subject.@amount></dark_gray> <white><subject> <gray>ze swojego schowka.");

  public MutableMessage vaultItemRedeemSuggestion =
      MutableMessage.of("<gray>Naciśnij aby odebrać ten przedmiot.");
}
