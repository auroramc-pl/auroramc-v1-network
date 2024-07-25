package pl.auroramc.gamble.message;

import static pl.auroramc.messages.message.MutableMessage.empty;
import static pl.auroramc.messages.message.MutableMessage.newline;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.integrations.configs.command.CommandMessageSource;
import pl.auroramc.integrations.configs.page.navigation.NavigationMessageSource;
import pl.auroramc.messages.message.MutableMessage;

public class MessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public MutableMessage missingStakes =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>W tym momencie nie ma dostępnych zakładów, spróbuj ponownie później.");

  public MutableMessage missingStakePage =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Wprowadzona przez ciebie strona nie jest dostępna, upewnij się, czy poprawnie ją wprowadziłeś.");

  public MutableMessage displayStakeView =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Otworzyłeś podgląd dostępnych zakładów, aby dołączyć do jednego z nich naciśnij na wybrany przez ciebie zakład lewym przyciskiem myszy <#7c5058>(<#f4a9ba>LPM<#7c5058>)<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>.");

  public MutableMessage stakeMissingBalance =
      MutableMessage.of("<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie posiadasz wystarczających środków aby utworzyć ten zakład.");

  public MutableMessage stakeMustBeGreaterThanZero =
      MutableMessage.of("<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Stawka musi być większa od zera.");

  public MutableMessage stakeCreated =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Zakład o stawce <#f4a9ba><currency.@symbol><context.stake> <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>na <#f4a9ba><prediction> <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>został utworzony i oczekuje na przeciwnika.");

  public MutableMessage stakeWon =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Wygrałeś <hover:show_text:'<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Unikalny identyfikator: <#f4a9ba><context.gambleUniqueId>'>zakład</hover> <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>o stawce <#f4a9ba><currency.@symbol><context.stake> <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>mierząc się z <#f4a9ba><competitor.username><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>.");

  public MutableMessage stakeLost =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Przegrałeś <hover:show_text:'<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Unikalny identyfikator: <#f4a9ba><context.gambleUniqueId>'>zakład</hover> <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>o stawce <#f4a9ba><currency.@symbol><context.stake> <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>mierząc się z <#f4a9ba><competitor.username><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>.");

  public MutableMessage stakeFinalizationSelf =
      MutableMessage.of("<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie możesz dołączyć do własnego zakładu.");

  public MutableMessage stakeFinalizationMissingBalance =
      MutableMessage.of("<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie posiadasz wystarczających środków aby dołączyć do tego zakładu.");

  public MutableMessage stakesTitle = MutableMessage.of("Oczekujące zakłady");

  public MutableMessage stakeName =
      MutableMessage.of("<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894><bold>Zakład <#d3a37e>(<gradient:#f7cf78:#fbd06a:#f4b352:#fbd06a:#f7cf78><context.gambleKey><#d3a37e>)");

  public MutableMessage stakeBrief =
      empty()
          .append(newline())
          .append(MutableMessage.of("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Gracz: <#f4a9ba><context.initiator.username>"))
          .append(MutableMessage.of("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Wybór: <#f4a9ba><context.initiator.prediction>"))
          .append(MutableMessage.of("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Stawka: <#f4a9ba><currency.@symbol><context.stake>"))
          .append(empty())
          .append(MutableMessage.of("<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>Naciśnij <gradient:#f7cf78:#fbd06a:#f4b352:#fbd06a:#f7cf78><bold>LPM</bold><gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>, aby dołączyć do zakładu."));

  public NavigationMessageSource navigation = new NavigationMessageSource();

  public CommandMessageSource command = new CommandMessageSource();
}
