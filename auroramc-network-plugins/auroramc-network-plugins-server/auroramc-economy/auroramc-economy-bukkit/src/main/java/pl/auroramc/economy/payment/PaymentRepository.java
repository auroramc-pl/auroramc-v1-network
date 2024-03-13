package pl.auroramc.economy.payment;

import java.util.List;

interface PaymentRepository {

  Payment findPaymentById(final Long paymentId);

  List<PaymentSummary> findPaymentSummariesByInitiatorId(final Long initiatorId);

  List<PaymentSummary> findPaymentSummariesByReceiverId(final Long receiverId);

  void createPayment(final Payment payment);

  void deletePayment(final Payment payment);
}
