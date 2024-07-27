package pl.auroramc.auctions.vault.item;

public class VaultItemDeletionException extends IllegalStateException {

  public VaultItemDeletionException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
