package pl.auroramc.cheque;

import java.math.BigDecimal;

public record ChequeContext(ChequeIssuer issuer, BigDecimal amount) {

}
