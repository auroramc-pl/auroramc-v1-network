package pl.auroramc.cheque;

import static com.spotify.futures.CompletableFutures.combineFutures;
import static java.util.Objects.requireNonNull;
import static org.bukkit.Bukkit.getPlayer;
import static org.bukkit.Material.PAPER;
import static org.bukkit.persistence.PersistentDataType.STRING;
import static pl.auroramc.cheque.message.MessageSourcePaths.CONTEXT_PATH;
import static pl.auroramc.commons.bukkit.BukkitUtils.decreaseQuantityOfHeldItem;
import static pl.auroramc.messages.message.decoration.MessageDecorations.NO_CURSIVE;

import com.jeff_media.morepersistentdatatypes.DataType;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import pl.auroramc.cheque.message.MessageSource;
import pl.auroramc.cheque.payment.Payment;
import pl.auroramc.cheque.payment.PaymentFacade;
import pl.auroramc.commons.bukkit.item.ItemStackBuilder;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.economy.EconomyFacade;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

class ChequeService implements ChequeFacade {

  private final MessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;
  private final Currency fundsCurrency;
  private final UserFacade userFacade;
  private final EconomyFacade economyFacade;
  private final PaymentFacade paymentFacade;
  private final NamespacedKey amountKey;
  private final NamespacedKey issuerUniqueIdKey;
  private final NamespacedKey issuerUsernameKey;

  ChequeService(
      final MessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final Currency fundsCurrency,
      final UserFacade userFacade,
      final EconomyFacade economyFacade,
      final PaymentFacade paymentFacade,
      final NamespacedKey amountKey,
      final NamespacedKey issuerUniqueIdKey,
      final NamespacedKey issuerUsernameKey) {
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
    this.fundsCurrency = fundsCurrency;
    this.userFacade = userFacade;
    this.economyFacade = economyFacade;
    this.paymentFacade = paymentFacade;
    this.amountKey = amountKey;
    this.issuerUniqueIdKey = issuerUniqueIdKey;
    this.issuerUsernameKey = issuerUsernameKey;
  }

  @Override
  public boolean isCheque(final ItemStack itemStack) {
    final PersistentDataContainer persistentDataContainer =
        itemStack.getItemMeta().getPersistentDataContainer();
    final boolean whetherItemHasAmountKey = persistentDataContainer.has(amountKey, STRING);
    final boolean whetherItemHasIssuerUniqueIdKey =
        persistentDataContainer.has(issuerUniqueIdKey, DataType.UUID);
    final boolean whetherItemHasIssuerUsernameKey =
        persistentDataContainer.has(issuerUsernameKey, STRING);
    return whetherItemHasAmountKey
        && whetherItemHasIssuerUniqueIdKey
        && whetherItemHasIssuerUsernameKey;
  }

  @Override
  public ItemStack createCheque(final ChequeContext chequeContext) {
    final ItemStack renderOfItemStack =
        ItemStackBuilder.newBuilder(PAPER)
            .displayName(
                messageCompiler.compile(
                    messageSource.titleOfCheque.placeholder(CONTEXT_PATH, chequeContext),
                    NO_CURSIVE))
            .lore(
                messageCompiler.compileChildren(
                    messageSource.linesOfCheque.placeholder(CONTEXT_PATH, chequeContext),
                    NO_CURSIVE))
            .build();
    return attachChequeTags(renderOfItemStack, chequeContext);
  }

  private ItemStack attachChequeTags(
      final ItemStack chequeItemStack, final ChequeContext chequeContext) {
    final ItemMeta chequeItemMeta = chequeItemStack.getItemMeta();
    chequeItemMeta
        .getPersistentDataContainer()
        .set(amountKey, STRING, chequeContext.amount().toString());
    chequeItemMeta
        .getPersistentDataContainer()
        .set(issuerUniqueIdKey, DataType.UUID, chequeContext.issuer().uniqueId());
    chequeItemMeta
        .getPersistentDataContainer()
        .set(issuerUsernameKey, STRING, chequeContext.issuer().username());
    chequeItemStack.setItemMeta(chequeItemMeta);
    return chequeItemStack;
  }

  @Override
  public CompletableFuture<ChequeContext> finalizeCheque(
      final UUID retrieverUniqueId, ItemStack itemStack) {
    final Player retriever = getPlayer(retrieverUniqueId);
    if (retriever == null) {
      throw new ChequeFinalizationException(
          "Could not finalize cheque, because retriever seems to be Offline.");
    }

    final ChequeContext chequeContext = getChequeContextByItemStack(itemStack);
    return economyFacade
        .deposit(retrieverUniqueId, fundsCurrency, chequeContext.amount())
        .thenAccept(state -> decreaseQuantityOfHeldItem(retriever))
        .thenCompose(
            state ->
                combineFutures(
                    getUserIdByUniqueId(chequeContext.issuer().uniqueId()),
                    getUserIdByUniqueId(retrieverUniqueId),
                    (issuerId, retrieverId) ->
                        paymentFacade.createPayment(
                            new Payment(issuerId, retrieverId, chequeContext.amount()))))
        .thenApply(state -> chequeContext);
  }

  private CompletableFuture<Long> getUserIdByUniqueId(final UUID uniqueId) {
    return userFacade.getUserByUniqueId(uniqueId).thenApply(User::getId);
  }

  private ChequeContext getChequeContextByItemStack(final ItemStack chequeItemStack) {
    final PersistentDataContainer persistentDataContainer =
        chequeItemStack.getItemMeta().getPersistentDataContainer();
    return new ChequeContext(
        new ChequeIssuer(
            persistentDataContainer.get(issuerUniqueIdKey, DataType.UUID),
            persistentDataContainer.get(issuerUsernameKey, STRING)),
        fundsCurrency,
        new BigDecimal(requireNonNull(persistentDataContainer.get(amountKey, STRING))));
  }
}
