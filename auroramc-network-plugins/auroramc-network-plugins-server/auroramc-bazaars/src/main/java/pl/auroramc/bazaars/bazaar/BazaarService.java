package pl.auroramc.bazaars.bazaar;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static pl.auroramc.bazaars.bazaar.BazaarUtils.getEmptySlotsCount;
import static pl.auroramc.bazaars.bazaar.BazaarUtils.getQuantityInSlots;
import static pl.auroramc.commons.BukkitUtils.postToMainThread;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import pl.auroramc.bazaars.bazaar.parser.BazaarParsingContext;
import pl.auroramc.bazaars.bazaar.transaction.context.BazaarTransactionContext;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;

class BazaarService implements BazaarFacade {

  private final Plugin plugin;
  private final DecimalFormat priceFormat;
  private final Currency fundsCurrency;
  private final EconomyFacade economyFacade;

  BazaarService(
      final Plugin plugin,
      final DecimalFormat priceFormat,
      final Currency fundsCurrency,
      final EconomyFacade economyFacade
  ) {
    this.plugin = plugin;
    this.priceFormat = priceFormat;
    this.fundsCurrency = fundsCurrency;
    this.economyFacade = economyFacade;
  }

  @Override
  public CompletableFuture<Component> handleItemTransaction(
      final BazaarTransactionContext transactionContext
  ) {
    switch (transactionContext.parsingContext().type()) {
      case BUY -> {
        return economyFacade
            .has(
                transactionContext.customerUniqueId(),
                fundsCurrency,
                transactionContext.parsingContext().price()
            )
            .thenCompose(whetherCustomerHasEnoughFunds ->
                handleItemPurchase(transactionContext, whetherCustomerHasEnoughFunds)
            );
      }
      case SELL -> {
        return economyFacade
            .has(
                transactionContext.merchantUniqueId(),
                fundsCurrency,
                transactionContext.parsingContext().price()
            )
            .thenCompose(
                whetherMerchantHasEnoughFunds -> handleItemSale(transactionContext, whetherMerchantHasEnoughFunds)
            );
      }
      default ->
          throw new IllegalStateException("Could not parse bazaar, because of malformed type.");
    }
  }

  @Override
  public CompletableFuture<Component> handleItemPurchase(
      final BazaarTransactionContext transactionContext,
      final boolean whetherCustomerHasEnoughFunds
  ) {
    if (!whetherCustomerHasEnoughFunds) {
      return completedFuture(miniMessage().deserialize(
          "<red>Nie posiadasz wystarczająco gotówki, aby to zakupić."));
    }

    final BazaarParsingContext parsingContext = transactionContext.parsingContext();

    final int requiredSlots = getQuantityInSlots(
        parsingContext.quantity(),
        parsingContext.material().getMaxStackSize()
    );
    final int obtainedSlots = getEmptySlotsCount(
        transactionContext.customer().getInventory(), parsingContext.material()
    );
    if (requiredSlots > obtainedSlots) {
      return completedFuture(
          miniMessage().deserialize(
              "<red>Nie posiadasz wystarczająco miejsca w ekwipunku, aby to zakupić."
          )
      );
    }

    return economyFacade
        .transfer(
            transactionContext.customerUniqueId(),
            transactionContext.merchantUniqueId(),
            fundsCurrency,
            parsingContext.price()
        )
        .thenAccept(state ->
            postToMainThread(plugin, () ->
                handleItemTransferForPurchase(transactionContext)
            )
        )
        .thenApply(state ->
            miniMessage().deserialize(
                "<gray>Zakupiłeś <white>x<quantity> <material></white> od <white><merchant></white> za <white><price></white>.",
                getPlaceholdersOfParsingContext(parsingContext)
            )
        );
  }

  @Override
  public CompletableFuture<Component> handleItemSale(
      final BazaarTransactionContext transactionContext,
      final boolean whetherMerchantHasEnoughFunds
  ) {
    final BazaarParsingContext parsingContext = transactionContext.parsingContext();

    final boolean whetherCustomerHasEnoughStock = transactionContext.customer()
        .getInventory()
        .containsAtLeast(new ItemStack(parsingContext.material()), parsingContext.quantity());
    if (!whetherCustomerHasEnoughStock) {
      return completedFuture(miniMessage().deserialize(
          "<red>Nie posiadasz wystarczająco przedmiotów, aby to sprzedać."));
    }

    if (!whetherMerchantHasEnoughFunds) {
      return completedFuture(miniMessage().deserialize(
          "<red>Właściciel bazaru, do którego próbujesz sprzedać przedmioty nie posiada wystarczająco gotówki."));
    }

    final int requiredSlots = getQuantityInSlots(
        parsingContext.quantity(),
        parsingContext.material().getMaxStackSize()
    );
    final int obtainedSlots = getEmptySlotsCount(
        transactionContext.magazine().getInventory(), parsingContext.material()
    );
    if (requiredSlots > obtainedSlots) {
      return completedFuture(miniMessage().deserialize(
          "<red>Bazar, do którego próbujesz sprzedać przedmioty nie posiada wystarczająco miejsca."));
    }

    return economyFacade
        .transfer(
            transactionContext.merchantUniqueId(),
            transactionContext.customerUniqueId(),
            fundsCurrency,
            parsingContext.price()
        )
        .thenAccept(state ->
            postToMainThread(plugin,
                () -> handleItemTransferForSale(transactionContext)
            )
        )
        .thenApply(state ->
            miniMessage().deserialize(
                "<gray>Sprzedałeś <white>x<quantity> <material></white> dla <white><merchant></white> za <white><price></white>.",
                getPlaceholdersOfParsingContext(parsingContext)
            )
        );
  }

  private void handleItemTransferForPurchase(final BazaarTransactionContext transactionContext) {
    final BazaarParsingContext parsingContext = transactionContext.parsingContext();
    final ItemStack productItemStack = new ItemStack(parsingContext.material(), parsingContext.quantity());
    transactionContext.magazine().getInventory().removeItemAnySlot(productItemStack);
    transactionContext.customer().getInventory().addItem(productItemStack)
        .forEach((index, remainingItem) ->
            transactionContext.customer()
                .getWorld()
                .dropItemNaturally(transactionContext.customer().getLocation(), remainingItem)
        );
  }

  private void handleItemTransferForSale(final BazaarTransactionContext transactionContext) {
    final BazaarParsingContext parsingContext = transactionContext.parsingContext();
    final ItemStack productItemStack = new ItemStack(parsingContext.material(), parsingContext.quantity());
    transactionContext.customer().getInventory().removeItemAnySlot(productItemStack);
    transactionContext.magazine().getInventory().addItem(productItemStack);
  }

  private TagResolver[] getPlaceholdersOfParsingContext(final BazaarParsingContext parsingContext) {
    return Set.of(
        unparsed("price", "%s%s"
            .formatted(
                fundsCurrency.getSymbol(),
                priceFormat.format(parsingContext.price())
            )
        ),
        unparsed("currency", fundsCurrency.getSymbol()),
        unparsed("merchant", parsingContext.merchant()),
        unparsed("material", capitalize(parsingContext.material().name().toLowerCase(Locale.ROOT))),
        unparsed("quantity", String.valueOf(parsingContext.quantity()))
    ).toArray(TagResolver[]::new);
  }
}
