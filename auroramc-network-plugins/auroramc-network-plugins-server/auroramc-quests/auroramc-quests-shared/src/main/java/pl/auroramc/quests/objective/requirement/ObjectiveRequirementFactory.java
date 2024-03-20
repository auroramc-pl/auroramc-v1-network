package pl.auroramc.quests.objective.requirement;

import java.util.function.Predicate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class ObjectiveRequirementFactory {

  private static final Predicate<ItemStack> ANY_ITEM = item -> true;

  private ObjectiveRequirementFactory() {}

  public static ObjectiveRequirement heldItem(
      final Material requiredMaterial, final Predicate<ItemStack> requirementOnItem) {
    return new HeldItemRequirement(requiredMaterial, requirementOnItem);
  }

  public static ObjectiveRequirement heldItem(final Material requiredMaterial) {
    return heldItem(requiredMaterial, ANY_ITEM);
  }
}
