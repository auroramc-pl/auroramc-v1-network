package pl.auroramc.cheque;

class ChequeFinalizationException extends IllegalStateException {

  ChequeFinalizationException(final String message) {
    super(message);
  }
}
