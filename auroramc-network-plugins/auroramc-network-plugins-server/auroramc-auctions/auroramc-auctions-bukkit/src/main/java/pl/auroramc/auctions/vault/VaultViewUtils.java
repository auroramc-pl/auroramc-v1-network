package pl.auroramc.auctions.vault;

import static java.util.Collections.emptyList;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;
import static net.kyori.adventure.text.format.TextDecoration.State.FALSE;
import static pl.auroramc.commons.collection.CollectionUtils.merge;

import java.util.List;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import pl.auroramc.commons.item.ItemStackBuilder;

final class VaultViewUtils {

  private VaultViewUtils() {

  }

  static ItemStack mergeLoreOnItemStack(final ItemStack source, final List<Component> lines) {
    return ItemStackBuilder.newBuilder(source)
        .lore(
            merge(
                Optional.ofNullable(source.lore())
                    .orElse(emptyList()),
                lines.stream()
                    .map(line -> line.decoration(ITALIC, FALSE))
                    .toList(),
                Component[]::new
            )
        )
        .build();
  }
}
