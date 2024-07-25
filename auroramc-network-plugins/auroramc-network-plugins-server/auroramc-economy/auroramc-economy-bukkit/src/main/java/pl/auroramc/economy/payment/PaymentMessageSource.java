package pl.auroramc.economy.payment;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class PaymentMessageSource extends OkaeriConfig {

  public MutableMessage noIncomingPayments =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Gracz <gradient:#ffad33:#ff8052:#ffdb57:#ff8052:#ffdb57:#ffad33><player.displayName> <gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>nie otrzymał jeszcze żadnych płatności przychodzących.");

  public MutableMessage noOutgoingPayments =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Gracz <gradient:#ffad33:#ff8052:#ffdb57:#ff8052:#ffdb57:#ffad33><player.displayName> <gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>nie wykonał jeszcze żadnych płatności wychodzących.");

  public MutableMessage incomingPaymentsHeader =
      MutableMessage.of("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Płatności przychodzące dla <#f4a9ba><player.displayName><#7c5058>:");

  public MutableMessage outgoingPaymentsHeader =
      MutableMessage.of("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Płatności wychodzące od <#f4a9ba><player.displayName><#7c5058>:");

  public MutableMessage paymentEntry =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>• <#7c5058><payment.transactionTime> <#7c5058>(<#f4a9ba><payment.id><#7c5058>) <#7c5058>(<#f4a9ba><payment.initiatorUsername> <#7c5058>→ <#f4a9ba><payment.receiverUsername><#7c5058>) <#7c5058>- <#f4a9ba><payment.currencySymbol><payment.amount>");
}
