package pl.auroramc.bazaars.bazaar.listener;

import static java.math.BigDecimal.ZERO;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;
import static org.bukkit.block.sign.Side.BACK;
import static org.bukkit.event.EventPriority.HIGHEST;
import static pl.auroramc.bazaars.bazaar.BazaarUtils.resolveSignProp;
import static pl.auroramc.bazaars.bazaar.BazaarUtils.whetherSignHasInvalidProp;
import static pl.auroramc.bazaars.bazaar.parser.BazaarParserToken.MERCHANT;
import static pl.auroramc.bazaars.bazaar.parser.BazaarParserToken.PRICE;
import static pl.auroramc.bazaars.sign.SignDelegateFactory.getSignDelegate;
import static pl.auroramc.commons.format.decimal.DecimalFormatter.getFormattedDecimal;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import pl.auroramc.bazaars.bazaar.parser.BazaarParser;
import pl.auroramc.bazaars.bazaar.parser.BazaarParsingContext;
import pl.auroramc.bazaars.message.MessageSource;
import pl.auroramc.messages.message.MutableMessage;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;

public class BazaarCreateListener implements Listener {

  private static final int PLAYER_INVENTORY_CAPACITY = 2304;
  private final MessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;
  private final BazaarParser bazaarParser;

  public BazaarCreateListener(
      final MessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final BazaarParser bazaarParser) {
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
    this.bazaarParser = bazaarParser;
  }

  @EventHandler(priority = HIGHEST, ignoreCancelled = true)
  public void onBazaarInitialization(final SignChangeEvent event) {
    if (event.getSide() == BACK) {
      return;
    }

    if (event.getBlock().getState() instanceof Sign sign
        && sign.getBlockData() instanceof WallSign wallSign) {
      if (whetherSignHasInvalidProp(resolveSignProp(sign, wallSign))) {
        return;
      }

      final BazaarParsingContext parsingContext =
          bazaarParser.parseContextOrNull(getSignDelegate(event));
      if (parsingContext == null) {
        return;
      }

      if (!event.getPlayer().getName().equalsIgnoreCase(parsingContext.merchant())) {
        destroySign(event.getPlayer(), event.getBlock(), messageSource.invalidMerchant);
        return;
      }

      if (parsingContext.quantity() > PLAYER_INVENTORY_CAPACITY) {
        destroySign(event.getPlayer(), event.getBlock(), messageSource.invalidQuantity);
        return;
      }

      if (parsingContext.price().compareTo(ZERO) <= 0) {
        destroySign(event.getPlayer(), event.getBlock(), messageSource.invalidPrice);
        return;
      }

      event.line(MERCHANT.getLineIndex(), text(parsingContext.merchant()).decorate(BOLD));
      event.line(
          PRICE.getLineIndex(),
          empty()
              .append(text(parsingContext.type().getShortcut()))
              .append(text(" "))
              .append(text(getFormattedDecimal(parsingContext.price()))));
    }
  }

  private void destroySign(final Player player, final Block block, final MutableMessage cause) {
    block.breakNaturally();
    player.sendMessage(messageCompiler.compile(cause).getComponent());
  }
}
