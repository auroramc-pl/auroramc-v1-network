package pl.auroramc.auctions.vault.item;

import static pl.auroramc.commons.lazy.Lazy.lazy;

import org.bukkit.inventory.ItemStack;
import pl.auroramc.commons.lazy.Lazy;

public class VaultItem {

  private final Lazy<ItemStack> resolvedSubject;
  private Long id;
  private Long userId;
  private Long vaultId;
  private byte[] subject;

  public VaultItem(final Long id, final Long userId, final Long vaultId, final byte[] subject) {
    this.resolvedSubject = lazy(() -> ItemStack.deserializeBytes(subject));
    this.id = id;
    this.userId = userId;
    this.vaultId = vaultId;
    this.subject = subject;
  }

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(final Long userId) {
    this.userId = userId;
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

  public ItemStack getResolvedSubject() {
    return resolvedSubject.get();
  }
}
