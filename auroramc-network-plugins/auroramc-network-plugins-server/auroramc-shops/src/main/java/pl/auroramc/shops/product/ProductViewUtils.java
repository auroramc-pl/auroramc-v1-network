package pl.auroramc.shops.product;

import static pl.auroramc.commons.collection.CollectionUtils.merge;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import pl.auroramc.commons.bukkit.item.ItemStackBuilder;
import pl.auroramc.messages.message.compiler.CompiledMessage;

final class ProductViewUtils {

  private ProductViewUtils() {}

  static ItemStack mergeLoreOnItemStack(final ItemStack source, final List<CompiledMessage> lines) {
    return ItemStackBuilder.newBuilder(source)
        .lore(
            merge(
                Optional.ofNullable(source.lore()).orElse(Collections.emptyList()),
                lines.stream().map(CompiledMessage::getComponent).toList(),
                Component[]::new))
        .build();
  }
}
