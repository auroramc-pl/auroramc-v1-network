package pl.auroramc.bazaars.bazaar.listener;

import static com.destroystokyo.paper.MaterialTags.SIGNS;
import static org.bukkit.event.EventPriority.HIGHEST;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static pl.auroramc.bazaars.bazaar.BazaarType.BUY;
import static pl.auroramc.bazaars.bazaar.BazaarUtils.resolveSignProp;
import static pl.auroramc.commons.message.compiler.CompiledMessageUtils.resolveComponent;

import java.util.Optional;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import pl.auroramc.bazaars.bazaar.BazaarFacade;
import pl.auroramc.bazaars.bazaar.parser.BazaarParser;
import pl.auroramc.bazaars.bazaar.parser.BazaarParsingContext;
import pl.auroramc.bazaars.bazaar.transaction.context.BazaarTransactionContext;
import pl.auroramc.bazaars.message.MessageSource;
import pl.auroramc.bazaars.sign.SignDelegateFactory;
import pl.auroramc.commons.CompletableFutureUtils;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.messages.message.compiler.CompiledMessage;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

public class BazaarUsageListener implements Listener {

  private final MessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;
  private final BazaarParser bazaarParser;
  private final BazaarFacade bazaarFacade;
  private final UserFacade userFacade;

  public BazaarUsageListener(
      final MessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final BazaarParser bazaarParser,
      final BazaarFacade bazaarFacade,
      final UserFacade userFacade) {
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
    this.bazaarParser = bazaarParser;
    this.bazaarFacade = bazaarFacade;
    this.userFacade = userFacade;
  }

  @EventHandler(priority = HIGHEST, ignoreCancelled = true)
  public void onBazaarInteraction(final PlayerInteractEvent event) {
    if (event.getAction() != RIGHT_CLICK_BLOCK) {
      return;
    }

    final Block clickedBlock = event.getClickedBlock();
    if (Optional.ofNullable(clickedBlock).filter(SIGNS::isTagged).isEmpty()) {
      return;
    }

    if (clickedBlock.getState() instanceof Sign sign
        && sign.getBlockData() instanceof WallSign wallSign
        && resolveSignProp(sign, wallSign).getState() instanceof Container magazine) {
      final BazaarParsingContext parsingContext =
          bazaarParser.parseContextOrNull(SignDelegateFactory.getSignDelegate(sign));
      if (parsingContext == null) {
        return;
      }

      // In the latest versions of Minecraft, right-click with mouse's button
      // opens a sign editor, so to prevent for this behavior, we have to cancel
      // that event.
      event.setCancelled(true);

      final Player customer = event.getPlayer();
      if (customer.getName().equalsIgnoreCase(parsingContext.merchant())) {
        customer.sendMessage(
            resolveComponent(messageCompiler.compile(messageSource.bazaarSelfInteraction)));
        return;
      }

      // Validation of magazine stock needs to be done at this moment in
      // the main thread, since otherwise the chest's inventory won't be
      // loaded yet and the check will always return false if not warmed up.
      if (parsingContext.type() == BUY && whetherMagazineIsOutOfStock(magazine, parsingContext)) {
        customer.sendMessage(
            resolveComponent(messageCompiler.compile(messageSource.bazaarOutOfStock)));
        return;
      }

      userFacade
          .getUserByUsername(parsingContext.merchant())
          .thenApply(User::getUniqueId)
          .thenApply(
              merchantUniqueId ->
                  new BazaarTransactionContext(
                      customer, magazine, customer.getUniqueId(), merchantUniqueId, parsingContext))
          .thenCompose(bazaarFacade::handleItemTransaction)
          .thenApply(messageCompiler::compile)
          .thenApply(CompiledMessage::getComponent)
          .thenAccept(customer::sendMessage)
          .exceptionally(CompletableFutureUtils::delegateCaughtException);
    }
  }

  private boolean whetherMagazineIsOutOfStock(
      final Container magazine, final BazaarParsingContext parsingContext) {
    return !magazine
        .getInventory()
        .containsAtLeast(new ItemStack(parsingContext.material()), parsingContext.quantity());
  }
}
