package pl.auroramc.economy.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.commons.config.command.CommandMessageSource;
import pl.auroramc.economy.balance.BalanceMessageSource;
import pl.auroramc.economy.economy.EconomyMessageSource;
import pl.auroramc.economy.leaderboard.LeaderboardMessageSource;
import pl.auroramc.economy.payment.PaymentMessageSource;
import pl.auroramc.economy.transfer.TransferMessageSource;
import pl.auroramc.messages.message.MutableMessage;

public class MessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public MutableMessage validationRequiresExistingCurrency =
      MutableMessage.of(
          "<red>Wprowadzona przez ciebie waluta <dark_gray>(<yellow>{currency.id}<dark_gray>) <red>nie została odnaleziona, upewnij się, czy jest ona poprawna.");

  public CommandMessageSource command = new CommandMessageSource();

  public BalanceMessageSource balance = new BalanceMessageSource();

  public EconomyMessageSource economy = new EconomyMessageSource();

  public PaymentMessageSource payment = new PaymentMessageSource();

  public TransferMessageSource transfer = new TransferMessageSource();

  public LeaderboardMessageSource leaderboard = new LeaderboardMessageSource();
}
