package pl.auroramc.commons.item;

import static net.kyori.adventure.text.Component.translatable;
import static org.bukkit.inventory.ItemStack.deserializeBytes;

import java.util.Optional;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.auroramc.commons.message.MutableMessage;

public final class ItemStackFormatter {

  private ItemStackFormatter() {}

  public static Component getFormattedItemStack(final ItemStack itemStack) {
    return getFormattedItemStack(itemStack, itemStack.getAmount());
  }

  public static Component getFormattedItemStack(final ItemStack itemStack, final int amount) {
    return MutableMessage.of("<dark_gray>x{amount} <white>{name}")
        .with("name", getDisplayName(itemStack))
        .with("amount", amount)
        .compile()
        .hoverEvent(itemStack.asHoverEvent());
  }

  public static Component getFormattedItemStack(final byte[] serializedItemStack) {
    return getFormattedItemStack(deserializeBytes(serializedItemStack));
  }

  private static Component getDisplayName(final ItemStack itemStack) {
    return Optional.ofNullable(itemStack.getItemMeta())
        .map(ItemMeta::displayName)
        .orElse(translatable(itemStack));
  }
}
