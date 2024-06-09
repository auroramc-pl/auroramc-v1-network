package pl.auroramc.hoppers.hopper;

import static org.bukkit.Material.HOPPER;
import static org.bukkit.persistence.PersistentDataType.INTEGER;
import static pl.auroramc.hoppers.message.MessageSourcePaths.QUANTITY_PATH;
import static pl.auroramc.integrations.item.ItemStackUtils.giveOrDropItemStack;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.auroramc.hoppers.message.MessageSource;
import pl.auroramc.integrations.item.ItemStackBuilder;
import pl.auroramc.messages.message.MutableMessage;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;

@Permission("auroramc.hoppers.hopper")
@Command(name = "hopper", aliases = "hoppers")
public class HopperCommand {

  private final MessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;
  private final NamespacedKey transferQuantityKey;

  public HopperCommand(
      final MessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final NamespacedKey transferQuantityKey) {
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
    this.transferQuantityKey = transferQuantityKey;
  }

  @Execute
  public MutableMessage hopper(final @Context Player player, final @Arg int transferQuantity) {
    giveOrDropItemStack(player, getHopperItem(transferQuantity));
    return messageSource.hopperHasBeenGiven.placeholder(QUANTITY_PATH, transferQuantity);
  }

  private ItemStack getHopperItem(final int transferQuantity) {
    return ItemStackBuilder.newBuilder(HOPPER)
        .displayName(
            messageCompiler.compile(
                messageSource.hopperDisplayName.placeholder(QUANTITY_PATH, transferQuantity)))
        .data(transferQuantityKey, INTEGER, transferQuantity)
        .build();
  }
}
