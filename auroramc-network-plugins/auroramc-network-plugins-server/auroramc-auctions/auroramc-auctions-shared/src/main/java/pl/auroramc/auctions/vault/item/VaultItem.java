package pl.auroramc.auctions.vault.item;

public class VaultItem {

  private Long id;
  private Long vaultId;
  private byte[] subject;

  public VaultItem(final Long id, final Long vaultId, final byte[] subject) {
    this.id = id;
    this.vaultId = vaultId;
    this.subject = subject;
  }

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Long getVaultId() {
    return vaultId;
  }

  public void setVaultId(final Long vaultId) {
    this.vaultId = vaultId;
  }

  public byte[] getSubject() {
    return subject;
  }

  public void setSubject(final byte[] subject) {
    this.subject = subject;
  }
}
