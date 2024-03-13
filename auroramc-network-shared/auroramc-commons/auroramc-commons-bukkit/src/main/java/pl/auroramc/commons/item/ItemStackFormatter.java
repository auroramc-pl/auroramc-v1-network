package pl.auroramc.commons.item;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.component;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed;
import static org.bukkit.inventory.ItemStack.deserializeBytes;

import java.util.Optional;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ItemStackFormatter {

  private ItemStackFormatter() {

  }

  public static Component getFormattedItemStack(final ItemStack itemStack) {
    return miniMessage().deserialize(
        "<dark_gray>x<amount> <white><display_name>",
            component("display_name", getDisplayName(itemStack)),
            unparsed("amount", String.valueOf(itemStack.getAmount()))
        )
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