package pl.auroramc.commons.item;

import static java.util.Arrays.stream;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;
import static net.kyori.adventure.text.format.TextDecoration.State.FALSE;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public final class ItemStackBuilder {

  private final ItemStack itemStack;
  private ItemMeta itemMeta;

  private ItemStackBuilder(final ItemStack itemStack) {
    this.itemStack = itemStack;
    this.itemMeta = itemStack.getItemMeta();
  }

  public static ItemStackBuilder newBuilder(final ItemStack itemStack) {
    return new ItemStackBuilder(itemStack.clone());
  }

  public static ItemStackBuilder newBuilder(final Material material) {
    return newBuilder(new ItemStack(material));
  }

  public ItemStackBuilder displayName(final Component displayName) {
    itemMeta.displayName(displayName);
    return this;
  }

  public ItemStackBuilder displayName(final String unparsedDisplayName) {
    return displayName(miniMessage()
        .deserialize(unparsedDisplayName)
        .decoration(ITALIC, FALSE));
  }

  public ItemStackBuilder type(final Material material) {
    itemStack.setType(material);
    itemMeta = itemStack.getItemMeta();
    return this;
  }

  public ItemStackBuilder lore(final Component... lines) {
    itemMeta.lore(List.of(lines));
    return this;
  }

  public ItemStackBuilder lore(final String... lines) {
    return lore(getFormattedLines(lines));
  }

  private Component[] getFormattedLines(final String... lines) {
    return stream(lines)
        .map(line -> miniMessage().deserialize(line).decoration(ITALIC, FALSE))
        .toArray(Component[]::new);
  }

  public <K, V> ItemStackBuilder data(
      final NamespacedKey key, final PersistentDataType<K, V> type, final V value) {
    itemMeta.getPersistentDataContainer().set(key, type, value);
    return this;
  }

  public ItemStackBuilder flags(final ItemFlag... flags) {
    itemMeta.addItemFlags(flags);
    return this;
  }

  public ItemStackBuilder enchantment(final Enchantment enchantment, final int level) {
    itemMeta.addEnchant(enchantment, level, enchantment.getMaxLevel() < level);
    return this;
  }

  public ItemStackBuilder manipulate(final Consumer<ItemStackBuilder> manipulation) {
    manipulation.accept(this);
    return this;
  }

  public <T> ItemStackBuilder manipulate(
      final Predicate<T> condition, final T value, final Consumer<ItemStackBuilder> manipulator
  ) {
    return condition.test(value) ? manipulate(manipulator) : this;
  }

  public ItemStack build() {
    itemStack.setItemMeta(itemMeta);
    return itemStack;
  }
}