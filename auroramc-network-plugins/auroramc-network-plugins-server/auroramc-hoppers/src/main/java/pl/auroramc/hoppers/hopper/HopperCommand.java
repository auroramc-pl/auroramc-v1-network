package pl.auroramc.hoppers.hopper;

import static org.bukkit.Material.HOPPER;
import static org.bukkit.persistence.PersistentDataType.INTEGER;

import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.auroramc.commons.item.ItemStackBuilder;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.hoppers.message.MessageSource;

@Permission("auroramc.hoppers.hopper")
@Route(name = "hopper", aliases = "hoppers")
public class HopperCommand {

  private final MessageSource messageSource;
  private final NamespacedKey transferQuantityKey;

  public HopperCommand(final MessageSource messageSource, final NamespacedKey transferQuantityKey) {
    this.messageSource = messageSource;
    this.transferQuantityKey = transferQuantityKey;
  }

  @Execute
  MutableMessage createHopper(final Player executor, final @Arg Integer transferQuantity) {
    executor.getInventory().addItem(getHopperItem(transferQuantity));
    return messageSource.hopperHasBeenGiven.with("quantity", transferQuantity);
  }

  private ItemStack getHopperItem(final Integer transferQuantity) {
    return ItemStackBuilder.newBuilder(HOPPER)
        .displayName(
            messageSource.hopperDisplayName
                .with("quantity", transferQuantity)
                .into()
        )
        .data(transferQuantityKey, INTEGER, transferQuantity)
        .build();
  }
}
