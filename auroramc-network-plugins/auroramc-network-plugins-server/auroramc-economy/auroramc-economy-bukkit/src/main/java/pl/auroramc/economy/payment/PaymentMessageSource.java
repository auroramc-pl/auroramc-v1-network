package pl.auroramc.economy.payment;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class PaymentMessageSource extends OkaeriConfig {

  public MutableMessage noIncomingPayments =
      MutableMessage.of(
          "<red>Gracz <yellow><player.displayName> <red>nie otrzymał jeszcze żadnych płatności przychodzących.");

  public MutableMessage noOutgoingPayments =
      MutableMessage.of(
          "<red>Gracz <yellow><player.displayName> <red>nie wykonał jeszcze żadnych płatności wychodzących.");

  public MutableMessage incomingPaymentsHeader =
      MutableMessage.of("<gray>Płatności przychodzące dla <white><player.displayName><dark_gray>:");

  public MutableMessage outgoingPaymentsHeader =
      MutableMessage.of("<gray>Płatności wychodzące od <white><player.displayName><dark_gray>:");

  public MutableMessage paymentEntry =
      MutableMessage.of(
          "<gray>• <dark_gray><payment.transactionTime> <dark_gray>(<white><payment.id><dark_gray>) <dark_gray>(<white><payment.initiatorUsername> <dark_gray>→ <white><payment.receiverUsername><dark_gray>) <dark_gray>- <white><payment.currencySymbol><payment.amount>");
}
