package pl.auroramc.cheque.payment;

import java.math.BigDecimal;

public record Payment(Long issuerId, Long retrieverId, BigDecimal amount) {}
