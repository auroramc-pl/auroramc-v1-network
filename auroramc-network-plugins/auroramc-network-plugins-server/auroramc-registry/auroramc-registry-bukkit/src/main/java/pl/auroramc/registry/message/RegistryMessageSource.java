package pl.auroramc.registry.message;

import pl.auroramc.messages.i18n.MessageSource;

public class RegistryMessageSource extends MessageSource {

  public String localeNotSupported =
      "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Wprowadzony przez ciebie język jest jeszcze niewspierany, dostępne języki: <gradient:#ffad33:#ff8052:#ffdb57:#ff8052:#ffdb57:#ffad33><locales>";

  public String localeChanged =
      "<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>Język został zmieniony na <gradient:#f7cf78:#fbd06a:#f4b352:#fbd06a:#f7cf78><locale><gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>.";

  public String localeIsSame =
      "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Język nie został zmieniony, ponieważ jest on aktualnie ustawiony.";
}
