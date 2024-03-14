package pl.auroramc.shops.product;

import static net.kyori.adventure.text.format.TextDecoration.ITALIC;
import static net.kyori.adventure.text.format.TextDecoration.State.FALSE;
import static pl.auroramc.commons.collection.CollectionUtils.merge;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import pl.auroramc.commons.item.ItemStackBuilder;

final class ProductViewUtils {

  private ProductViewUtils() {

  }

  static ItemStack mergeLoreOnItemStack(final ItemStack source, final List<Component> lines) {
    return ItemStackBuilder.newBuilder(source)
        .lore(
            merge(
                Optional.ofNullable(source.lore())
                    .orElse(Collections.emptyList()),
                lines.stream()
                    .map(line -> line.decoration(ITALIC, FALSE))
                    .toList(),
                Component[]::new
            )
        )
        .build();
  }
}
