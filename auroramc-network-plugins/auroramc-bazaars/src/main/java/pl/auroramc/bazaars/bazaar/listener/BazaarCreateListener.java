package pl.auroramc.bazaars.bazaar.listener;

import static java.math.BigDecimal.ZERO;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static org.bukkit.block.sign.Side.BACK;
import static org.bukkit.event.EventPriority.HIGHEST;
import static pl.auroramc.bazaars.bazaar.BazaarUtils.resolveSignProp;
import static pl.auroramc.bazaars.bazaar.BazaarUtils.whetherSignHasInvalidProp;
import static pl.auroramc.bazaars.bazaar.parser.BazaarParserToken.MERCHANT;
import static pl.auroramc.bazaars.bazaar.parser.BazaarParserToken.QUANTITY;
import static pl.auroramc.bazaars.sign.SignDelegateFactory.produceSignDelegate;

import java.text.DecimalFormat;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import pl.auroramc.bazaars.bazaar.parser.BazaarParser;
import pl.auroramc.bazaars.bazaar.parser.BazaarParsingContext;

public class BazaarCreateListener implements Listener {

  private static final int PLAYER_INVENTORY_CAPACITY = 2304;
  private final DecimalFormat priceFormat;
  private final BazaarParser bazaarParser;

  public BazaarCreateListener(final DecimalFormat priceFormat, final BazaarParser bazaarParser) {
    this.priceFormat = priceFormat;
    this.bazaarParser = bazaarParser;
  }

  @EventHandler(priority = HIGHEST, ignoreCancelled = true)
  public void onBazaarInitialization(final SignChangeEvent event) {
    if (event.getSide() == BACK) {
      return;
    }

    if (event.getBlock().getState() instanceof Sign sign &&
        sign.getBlockData() instanceof WallSign wallSign
    ) {
      if (whetherSignHasInvalidProp(resolveSignProp(sign, wallSign))) {
        return;
      }

      final BazaarParsingContext parsingContext = bazaarParser.parseContextOrNull(produceSignDelegate(event));
      if (parsingContext == null) {
        return;
      }

      if (!event.getPlayer().getName().equalsIgnoreCase(parsingContext.merchant())) {
        destroySign(
            event.getPlayer(),
            event.getBlock(),
            "<red>Nie możesz stworzyć tabliczki dla innego gracza, upewnij się, czy wpisałeś swoją nazwę poprawnie."
        );
        return;
      }

      if (parsingContext.quantity() > PLAYER_INVENTORY_CAPACITY) {
        destroySign(
            event.getPlayer(),
            event.getBlock(),
            "<red>Wskazana przez ciebie ilość jest nieprawidłowa."
        );
        return;
      }

      if (parsingContext.price().compareTo(ZERO) <= 0) {
        destroySign(
            event.getPlayer(),
            event.getBlock(),
            "<red>Wskazana przez ciebie cena jest nieprawidłowa."
        );
        return;
      }

      event.line(
          MERCHANT.getLineIndex(),
          text(parsingContext.merchant())
              .decorate(BOLD)
      );
      event.line(
          QUANTITY.getLineIndex(),
          empty()
              .append(text(parsingContext.type().getShortcut()))
              .append(text(" "))
              .append(text(priceFormat.format(parsingContext.price())))
      );
    }
  }

  private void destroySign(final Player player, final Block block, final String rawCause) {
    block.breakNaturally();
    player.sendMessage(miniMessage().deserialize(rawCause));
  }
}
