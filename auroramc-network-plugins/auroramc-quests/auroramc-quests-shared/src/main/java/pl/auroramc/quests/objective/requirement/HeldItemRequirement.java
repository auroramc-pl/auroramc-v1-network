package pl.auroramc.quests.objective.requirement;

import java.util.function.Predicate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.auroramc.commons.message.MutableMessage;

public class HeldItemRequirement implements ObjectiveRequirement {

  private final Material requiredMaterial;
  private final Predicate<ItemStack> requirementOnItem;
  private MutableMessage message;

  public HeldItemRequirement(
      final Material requiredMaterial,
      final Predicate<ItemStack> requirementOnItem
  ) {
    this.requiredMaterial = requiredMaterial;
    this.requirementOnItem = requirementOnItem;
  }

  public Material getRequiredMaterial() {
    return requiredMaterial;
  }

  public Predicate<ItemStack> getRequirementOnItem() {
    return requirementOnItem;
  }

  @Override
  public boolean isValid(final Player viewer) {
    final ItemStack heldItem = viewer.getInventory().getItemInMainHand();
    return heldItem.getType() == requiredMaterial && requirementOnItem.test(heldItem);
  }

  @Override
  public MutableMessage getMessage() {
    return message;
  }

  @Override
  public void setMessage(final MutableMessage message) {
    this.message = message;
  }
}
