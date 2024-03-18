package pl.auroramc.hoppers.hopper;

import static org.bukkit.Material.HOPPER;
import static org.bukkit.persistence.PersistentDataType.INTEGER;
import static pl.auroramc.commons.BukkitUtils.appendItemStackOrDropBelow;
import static pl.auroramc.hoppers.message.MutableMessageVariableKey.QUANTITY_VARIABLE_KEY;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.auroramc.commons.item.ItemStackBuilder;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.hoppers.message.MutableMessageSource;

@Permission("auroramc.hoppers.hopper")
@Command(name = "hopper", aliases = "hoppers")
public class HopperCommand {

  private final MutableMessageSource messageSource;
  private final NamespacedKey transferQuantityKey;

  public HopperCommand(final MutableMessageSource messageSource, final NamespacedKey transferQuantityKey) {
    this.messageSource = messageSource;
    this.transferQuantityKey = transferQuantityKey;
  }

  @Execute
  MutableMessage hopper(
      final @Context Player player,
      final @Arg Integer transferQuantity
  ) {
    appendItemStackOrDropBelow(player, getHopperItem(transferQuantity));
    return messageSource.hopperHasBeenGiven
        .with(QUANTITY_VARIABLE_KEY, transferQuantity);
  }

  private ItemStack getHopperItem(final Integer transferQuantity) {
    return ItemStackBuilder.newBuilder(HOPPER)
        .displayName(
            messageSource.hopperDisplayName
                .with(QUANTITY_VARIABLE_KEY, transferQuantity)
                .compile()
        )
        .data(transferQuantityKey, INTEGER, transferQuantity)
        .build();
  }
}
