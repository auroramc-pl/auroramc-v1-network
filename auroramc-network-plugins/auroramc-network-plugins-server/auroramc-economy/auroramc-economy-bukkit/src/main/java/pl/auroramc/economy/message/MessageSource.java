package pl.auroramc.economy.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.economy.balance.BalanceMessageSource;
import pl.auroramc.economy.economy.EconomyMessageSource;
import pl.auroramc.economy.leaderboard.LeaderboardMessageSource;
import pl.auroramc.economy.payment.PaymentMessageSource;
import pl.auroramc.economy.transfer.TransferMessageSource;
import pl.auroramc.integrations.configs.command.CommandMessageSource;
import pl.auroramc.messages.message.MutableMessage;

public class MessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public MutableMessage validationRequiresExistingCurrency =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Wprowadzona przez ciebie waluta <#7c5058>(<gradient:#ffad33:#ff8052:#ffdb57:#ff8052:#ffdb57:#ffad33><currency.id><#7c5058>) <gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>nie została odnaleziona, upewnij się, czy jest ona poprawna.");

  public CommandMessageSource command = new CommandMessageSource();

  public BalanceMessageSource balance = new BalanceMessageSource();

  public EconomyMessageSource economy = new EconomyMessageSource();

  public PaymentMessageSource payment = new PaymentMessageSource();

  public TransferMessageSource transfer = new TransferMessageSource();

  public LeaderboardMessageSource leaderboard = new LeaderboardMessageSource();
}
