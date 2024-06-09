package pl.auroramc.gamble.stake.view;

import static net.kyori.adventure.text.Component.empty;
import static org.bukkit.Bukkit.createInventory;
import static org.bukkit.Material.ARROW;
import static org.bukkit.Material.BLACK_STAINED_GLASS_PANE;
import static org.bukkit.Material.PAPER;
import static pl.auroramc.commons.bukkit.page.navigation.NavigationDirection.BACKWARD;
import static pl.auroramc.commons.bukkit.page.navigation.NavigationDirection.FORWARD;
import static pl.auroramc.gamble.message.MessageSourcePaths.CONTEXT_PATH;
import static pl.auroramc.gamble.message.MessageSourcePaths.CURRENCY_PATH;
import static pl.auroramc.gamble.stake.view.StakeViewUtils.getSlotIndexOf;
import static pl.auroramc.gamble.stake.view.StakeViewUtils.setItemAsFrame;
import static pl.auroramc.messages.message.decoration.MessageDecorations.NO_CURSIVE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pl.auroramc.commons.bukkit.page.navigation.Navigator;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.gamble.message.MessageSource;
import pl.auroramc.gamble.stake.context.StakeContext;
import pl.auroramc.integrations.configs.page.navigation.NavigationMessageSource;
import pl.auroramc.integrations.item.ItemStackBuilder;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;

class StakeView implements InventoryHolder {

  private static final int INITIAL_X_OF_FILLING_AREA = 2;
  private static final int INITIAL_Y_OF_FILLING_AREA = 2;
  private static final int CLOSING_X_OF_FILLING_AREA = 8;
  private static final int CLOSING_Y_OF_FILLING_AREA = 5;
  private static final int STAKE_ROW_COUNT = 6;
  private static final int ELEMENTS_PER_ROW = 9;
  private final Map<Integer, StakeContext> stakeContextBySlot;
  private final Map<Integer, Navigator> navigatorBySlot;
  private final int pageIndex;
  private final Currency fundsCurrency;
  private final MessageSource messageSource;
  private final NavigationMessageSource navigationMessageSource;
  private final BukkitMessageCompiler messageCompiler;
  private final List<StakeContext> stakes;
  private final Inventory stakeInventory;

  StakeView(
      final int pageIndex,
      final Currency fundsCurrency,
      final MessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final List<StakeContext> stakes) {
    this.pageIndex = pageIndex;
    this.fundsCurrency = fundsCurrency;
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
    this.navigationMessageSource = messageSource.navigation;
    this.navigatorBySlot =
        Map.of(
            48, new Navigator(BACKWARD, getNavigatorIconPointingBackward()),
            50, new Navigator(FORWARD, getNavigatorIconPointingForward()));
    this.stakeContextBySlot = new HashMap<>();
    this.stakes = stakes;
    this.stakeInventory = createStakeInventory();
  }

  int getPageIndex() {
    return pageIndex;
  }

  @Override
  public @NotNull Inventory getInventory() {
    return stakeInventory;
  }

  private Inventory createStakeInventory() {
    final Inventory inventory =
        createInventory(
            this,
            STAKE_ROW_COUNT * ELEMENTS_PER_ROW,
            messageCompiler.compile(messageSource.stakesTitle).getComponent());

    setItemAsFrame(
        inventory,
        ItemStackBuilder.newBuilder(BLACK_STAINED_GLASS_PANE).displayName(empty()).build());

    int currentIndex = 0;
    int closingIndex = stakes.size() - 1;
    for (int y = INITIAL_Y_OF_FILLING_AREA; y <= CLOSING_Y_OF_FILLING_AREA; y++) {
      for (int x = INITIAL_X_OF_FILLING_AREA; x <= CLOSING_X_OF_FILLING_AREA; x++) {
        if (currentIndex > closingIndex) {
          break;
        }

        final int slot = getSlotIndexOf(x, y);
        final StakeContext stakeContext = stakes.get(currentIndex++);
        stakeContextBySlot.put(slot, stakeContext);

        inventory.setItem(slot, getStakeItemOfStakeContext(stakeContext));
      }
    }

    navigatorBySlot.forEach((slot, navigator) -> inventory.setItem(slot, navigator.icon()));
    return inventory;
  }

  Optional<Navigator> getNavigatorBySlot(final int slot) {
    return Optional.ofNullable(navigatorBySlot.get(slot));
  }

  private ItemStack getNavigatorIconPointingForward() {
    return ItemStackBuilder.newBuilder(ARROW)
        .displayName(
            messageCompiler.compile(navigationMessageSource.nameOfNextPageButton, NO_CURSIVE))
        .lore(
            messageCompiler.compileChildren(
                navigationMessageSource.loreOfNextPageButton, NO_CURSIVE))
        .build();
  }

  private ItemStack getNavigatorIconPointingBackward() {
    return ItemStackBuilder.newBuilder(ARROW)
        .displayName(
            messageCompiler.compile(navigationMessageSource.nameOfPrevPageButton, NO_CURSIVE))
        .lore(
            messageCompiler.compileChildren(
                navigationMessageSource.loreOfPrevPageButton, NO_CURSIVE))
        .build();
  }

  private ItemStack getStakeItemOfStakeContext(final StakeContext stakeContext) {
    return ItemStackBuilder.newBuilder(new ItemStack(PAPER))
        .displayName(
            messageCompiler.compile(
                messageSource.stakeName.placeholder(CONTEXT_PATH, stakeContext), NO_CURSIVE))
        .lore(
            messageCompiler.compileChildren(
                messageSource
                    .stakeBrief
                    .placeholder(CONTEXT_PATH, stakeContext)
                    .placeholder(CURRENCY_PATH, fundsCurrency),
                NO_CURSIVE))
        .build();
  }

  Optional<StakeContext> getStakeContextBySlot(final int slot) {
    return Optional.ofNullable(stakeContextBySlot.get(slot));
  }
}
