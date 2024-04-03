package pl.auroramc.gamble.stake.view;

import static java.util.Locale.ROOT;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.bukkit.Bukkit.createInventory;
import static org.bukkit.Material.ARROW;
import static org.bukkit.Material.BLACK_STAINED_GLASS_PANE;
import static org.bukkit.Material.PAPER;
import static pl.auroramc.commons.decimal.DecimalFormatter.getFormattedDecimal;
import static pl.auroramc.commons.message.MutableMessageDecoration.NO_CURSIVE;
import static pl.auroramc.commons.page.navigation.PageNavigationDirection.BACKWARD;
import static pl.auroramc.commons.page.navigation.PageNavigationDirection.FORWARD;
import static pl.auroramc.gamble.message.MutableMessageVariableKey.CURRENCY_PATH;
import static pl.auroramc.gamble.message.MutableMessageVariableKey.GAMBLE_PATH;
import static pl.auroramc.gamble.message.MutableMessageVariableKey.INITIATOR_PATH;
import static pl.auroramc.gamble.message.MutableMessageVariableKey.PREDICTION_PATH;
import static pl.auroramc.gamble.message.MutableMessageVariableKey.STAKE_PATH;
import static pl.auroramc.gamble.stake.view.StakeViewUtils.getSlotIndexOf;
import static pl.auroramc.gamble.stake.view.StakeViewUtils.setItemAsFrame;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pl.auroramc.commons.item.ItemStackBuilder;
import pl.auroramc.commons.page.navigation.PageNavigator;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.gamble.message.MutableMessageSource;
import pl.auroramc.gamble.stake.StakeContext;

class StakeView implements InventoryHolder {

  private static final int INITIAL_X_OF_FILLING_AREA = 2;
  private static final int INITIAL_Y_OF_FILLING_AREA = 2;
  private static final int CLOSING_X_OF_FILLING_AREA = 8;
  private static final int CLOSING_Y_OF_FILLING_AREA = 5;

  private static final int STAKE_ROW_COUNT = 6;
  private static final int ELEMENTS_PER_ROW = 9;
  private final Map<Integer, StakeContext> stakeContextBySlot;
  private final Map<Integer, PageNavigator> navigatorBySlot;
  private final int pageIndex;
  private final Currency fundsCurrency;
  private final MutableMessageSource messageSource;
  private final List<StakeContext> stakes;
  private final Inventory stakeInventory;

  StakeView(
      final int pageIndex,
      final Currency fundsCurrency,
      final MutableMessageSource messageSource,
      final List<StakeContext> stakes
  ) {
    this.pageIndex = pageIndex;
    this.fundsCurrency = fundsCurrency;
    this.messageSource = messageSource;
    this.navigatorBySlot = Map.of(
        48, new PageNavigator(BACKWARD, getNavigatorIconPointingBackward()),
        50, new PageNavigator(FORWARD, getNavigatorIconPointingForward())
    );
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
    final Inventory inventory = createInventory(
        this,
        STAKE_ROW_COUNT * ELEMENTS_PER_ROW,
        messageSource.stakesTitle.compile()
    );

    setItemAsFrame(
        inventory,
        ItemStackBuilder.newBuilder(BLACK_STAINED_GLASS_PANE)
            .displayName(Component.empty())
            .build()
    );

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

  Optional<PageNavigator> getNavigatorBySlot(final int slot) {
    return Optional.ofNullable(navigatorBySlot.get(slot));
  }

  private ItemStack getNavigatorIconPointingForward() {
    return ItemStackBuilder.newBuilder(ARROW)
        .displayName(
            messageSource.navigateForward
                .compile(NO_CURSIVE)
        )
        .lore(
            messageSource.navigateForwardSuggestion
                .compile(NO_CURSIVE)
        )
        .build();
  }

  private ItemStack getNavigatorIconPointingBackward() {
    return ItemStackBuilder.newBuilder(ARROW)
        .displayName(
            messageSource.navigateBackward
                .compile(NO_CURSIVE)
        )
        .lore(
            messageSource.navigateBackwardSuggestion
                .compile(NO_CURSIVE)
        )
        .build();
  }

  private ItemStack getStakeItemOfStakeContext(final StakeContext stakeContext) {
    return ItemStackBuilder.newBuilder(new ItemStack(PAPER))
        .displayName(
            messageSource.stakeName
                .with(
                    GAMBLE_PATH,
                    capitalize(
                        stakeContext.gambleKey().id().toLowerCase(ROOT)
                    )
                )
                .compile(NO_CURSIVE)
        )
        .lore(
            messageSource.stakeBrief
                .with(INITIATOR_PATH, stakeContext.initiator().username())
                .with(
                    PREDICTION_PATH,
                    capitalize(
                        stakeContext.initiator().prediction().toString().toLowerCase(ROOT)
                    )
                )
                .with(STAKE_PATH, getFormattedDecimal(stakeContext.stake()))
                .with(CURRENCY_PATH, fundsCurrency.getSymbol())
                .compileChildren(NO_CURSIVE)
        )
        .build();
  }

  Optional<StakeContext> getStakeContextBySlot(final int slot) {
    return Optional.ofNullable(stakeContextBySlot.get(slot));
  }
}
