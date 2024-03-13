package pl.auroramc.cheque;

import static com.spotify.futures.CompletableFutures.combineFutures;
import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;
import static net.kyori.adventure.text.format.TextDecoration.State.FALSE;
import static org.bukkit.Material.PAPER;
import static org.bukkit.persistence.PersistentDataType.STRING;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;
import static pl.auroramc.commons.decimal.DecimalFormatter.getFormattedDecimal;

import com.jeff_media.morepersistentdatatypes.DataType;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import pl.auroramc.cheque.message.MessageSource;
import pl.auroramc.cheque.payment.Payment;
import pl.auroramc.cheque.payment.PaymentFacade;
import pl.auroramc.commons.item.ItemStackBuilder;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

class ChequeService implements ChequeFacade {

  private static final String AMOUNT_KEY_ID = "cheque_amount";
  private static final String ISSUER_UNIQUE_ID_KEY_ID = "cheque_issuer_unique_id";
  private static final String ISSUER_USERNAME_KEY_ID = "cheque_issuer_username";
  private final Logger logger;
  private final MessageSource messageSource;
  private final Currency fundsCurrency;
  private final UserFacade userFacade;
  private final EconomyFacade economyFacade;
  private final PaymentFacade paymentFacade;
  private final NamespacedKey amountKey;
  private final NamespacedKey issuerUniqueIdKey;
  private final NamespacedKey issuerUsernameKey;

  ChequeService(
      final Plugin plugin,
      final Logger logger,
      final MessageSource messageSource,
      final Currency fundsCurrency,
      final UserFacade userFacade,
      final EconomyFacade economyFacade,
      final PaymentFacade paymentFacade
  ) {
    this.logger = logger;
    this.messageSource = messageSource;
    this.fundsCurrency = fundsCurrency;
    this.userFacade = userFacade;
    this.economyFacade = economyFacade;
    this.paymentFacade = paymentFacade;
    this.amountKey = new NamespacedKey(plugin, AMOUNT_KEY_ID);
    this.issuerUniqueIdKey = new NamespacedKey(plugin, ISSUER_UNIQUE_ID_KEY_ID);
    this.issuerUsernameKey = new NamespacedKey(plugin, ISSUER_USERNAME_KEY_ID);
  }

  @Override
  public boolean whetherItemIsCheque(final ItemStack itemStack) {
    final PersistentDataContainer persistentDataContainer = itemStack.getItemMeta()
        .getPersistentDataContainer();
    final boolean whetherItemHasAmountKey = persistentDataContainer.has(amountKey, STRING);
    final boolean whetherItemHasIssuerUniqueIdKey = persistentDataContainer.has(issuerUniqueIdKey, DataType.UUID);
    final boolean whetherItemHasIssuerUsernameKey = persistentDataContainer.has(issuerUsernameKey, STRING);
    return whetherItemHasAmountKey
        && whetherItemHasIssuerUniqueIdKey
        && whetherItemHasIssuerUsernameKey;
  }

  @Override
  public ItemStack createCheque(final ChequeContext chequeContext) {
    final ItemStack renderOfItemStack = ItemStackBuilder.newBuilder(PAPER)
        .displayName(
            messageSource.titleOfCheque
                .with("symbol", fundsCurrency.getSymbol())
                .with("amount", getFormattedDecimal(chequeContext.amount()))
                .compile()
                .decoration(ITALIC, FALSE)
        )
        .lore(
            messageSource.linesOfCheque
                .with("issuer", chequeContext.issuer().username())
                .compile()
                .decoration(ITALIC, FALSE)
        )
        .build();
    return getChequeWithAttachment(renderOfItemStack, chequeContext);
  }

  private ItemStack getChequeWithAttachment(
      final ItemStack chequeItemStack, final ChequeContext chequeContext
  ) {
    final ItemMeta chequeItemMeta = chequeItemStack.getItemMeta();
    chequeItemMeta.getPersistentDataContainer().set(
        amountKey, STRING, chequeContext.amount().toString()
    );
    chequeItemMeta.getPersistentDataContainer().set(
        issuerUniqueIdKey, DataType.UUID, chequeContext.issuer().uniqueId()
    );
    chequeItemMeta.getPersistentDataContainer().set(
        issuerUsernameKey, STRING, chequeContext.issuer().username()
    );
    chequeItemStack.setItemMeta(chequeItemMeta);
    return chequeItemStack;
  }

  @Override
  public CompletableFuture<MutableMessage> finalizeCheque(
      final UUID retrieverUniqueId, ItemStack itemStack
  ) {
    final Player retriever = Bukkit.getPlayer(retrieverUniqueId);
    if (retriever == null) {
      throw new ChequeFinalizationException(
          "Could not finalize cheque, because retriever seems to be Offline."
      );
    }

    final ChequeContext chequeContext = retrieveChequeContext(itemStack);
    return economyFacade.deposit(retrieverUniqueId, fundsCurrency, chequeContext.amount())
        .thenAccept(state -> decreaseQuantityOfItemInHand(retriever))
        .thenCompose(state ->
            combineFutures(
                retrieveUserIdByUniqueId(chequeContext.issuer().uniqueId()),
                retrieveUserIdByUniqueId(retrieverUniqueId),
                (issuerId, retrieverId) ->
                    paymentFacade.createPayment(
                        new Payment(issuerId, retrieverId, chequeContext.amount())
                    )
            )
        )
        .thenApply(state ->
            messageSource.chequeFinalized
                .with("symbol", fundsCurrency.getSymbol())
                .with("amount", getFormattedDecimal(chequeContext.amount()))
                .with("issuer", chequeContext.issuer().username())
        )
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private void decreaseQuantityOfItemInHand(final Player retriever) {
    final ItemStack itemInMainHand = retriever.getInventory().getItemInMainHand();
    if (itemInMainHand.getAmount() == 1) {
      retriever.getInventory().setItemInMainHand(null);
    } else {
      itemInMainHand.setAmount(itemInMainHand.getAmount() - 1);
    }
  }

  private CompletableFuture<Long> retrieveUserIdByUniqueId(final UUID uniqueId) {
    return userFacade.getUserByUniqueId(uniqueId).thenApply(User::getId);
  }

  private ChequeContext retrieveChequeContext(final ItemStack chequeItemStack) {
    final PersistentDataContainer persistentDataContainer = chequeItemStack.getItemMeta()
        .getPersistentDataContainer();
    return new ChequeContext(
        new ChequeIssuer(
            persistentDataContainer.get(issuerUniqueIdKey, DataType.UUID),
            persistentDataContainer.get(issuerUsernameKey, STRING)
        ),
        new BigDecimal(
            requireNonNull(persistentDataContainer.get(amountKey, STRING))
        )
    );
  }
}
