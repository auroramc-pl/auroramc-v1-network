package pl.auroramc.economy.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.economy.balance.BalanceMessageSource;
import pl.auroramc.economy.economy.EconomyMessageSource;
import pl.auroramc.economy.leaderboard.LeaderboardMessageSource;
import pl.auroramc.economy.payment.PaymentMessageSource;
import pl.auroramc.economy.transfer.TransferMessageSource;
import pl.auroramc.messages.message.MutableMessage;

public class MessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public BalanceMessageSource balance = new BalanceMessageSource();

  public EconomyMessageSource economy = new EconomyMessageSource();

  public LeaderboardMessageSource leaderboard = new LeaderboardMessageSource();

  public PaymentMessageSource payment = new PaymentMessageSource();

  public TransferMessageSource transfer = new TransferMessageSource();

  public MutableMessage availableSchematicsSuggestion =
      MutableMessage.of("<red>Poprawne użycie: <yellow><newline>{schematics}");

  public MutableMessage executionOfCommandIsNotPermitted =
      MutableMessage.of("<red>Nie posiadasz wystarczających uprawnień aby użyć tej komendy.");

  public MutableMessage executionFromConsoleIsUnsupported =
      MutableMessage.of("<red>Nie możesz użyć tej konsoli z poziomu konsoli.");

  public MutableMessage specifiedPlayerIsUnknown =
      MutableMessage.of("<red>Wskazany przez ciebie gracz nie istnieje, lub jest Offline.");

  public MutableMessage validationRequiresExistingCurrency =
      MutableMessage.of(
          "<red>Wprowadzona przez ciebie waluta <dark_gray>(<yellow>{currency.id}<dark_gray>) <red>nie została odnaleziona, upewnij się, czy jest ona poprawna.");
}
