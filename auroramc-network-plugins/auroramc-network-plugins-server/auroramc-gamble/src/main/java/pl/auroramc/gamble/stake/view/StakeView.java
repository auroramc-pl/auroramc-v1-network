package pl.auroramc.gamble.stake.view;

import static java.util.Locale.ROOT;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;
import static net.kyori.adventure.text.format.TextDecoration.State.FALSE;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.bukkit.Bukkit.createInventory;
import static org.bukkit.Material.ARROW;
import static org.bukkit.Material.BLACK_STAINED_GLASS_PANE;
import static org.bukkit.Material.PAPER;
import static pl.auroramc.commons.decimal.DecimalFormatter.getFormattedDecimal;
import static pl.auroramc.commons.page.navigation.PageNavigationDirection.BACKWARD;
import static pl.auroramc.commons.page.navigation.PageNavigationDirection.FORWARD;
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
import pl.auroramc.gamble.stake.StakeContext;

class StakeView implements InventoryHolder {

  private static final int INITIAL_X_OF_FILLING_AREA = 2;
  private static final int INITIAL_Y_OF_FILLING_AREA = 2;
  private static final int CLOSING_X_OF_FILLING_AREA = 8;
  private static final int CLOSING_Y_OF_FILLING_AREA = 5;

  private static final int STAKE_ROW_COUNT = 6;
  private static final int ELEMENTS_PER_ROW = 9;
  private final Map<Integer, PageNavigator> navigatorBySlot =
      Map.of(
          48, new PageNavigator(BACKWARD, getNavigatorIconPointingBackward()),
          50, new PageNavigator(FORWARD, getNavigatorIconPointingForward())
      );
  private final Map<Integer, StakeContext> stakeContextBySlot = new HashMap<>();
  private final int pageIndex;
  private final Currency fundsCurrency;
  private final List<StakeContext> stakes;
  private final Inventory stakeInventory;

  StakeView(
      final int pageIndex,
      final Currency fundsCurrency,
      final List<StakeContext> stakes
  ) {
    this.pageIndex = pageIndex;
    this.fundsCurrency = fundsCurrency;
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
        Component.text("Oczekujące zakłady")
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

    navigatorBySlot.forEach((slot, pageNavigator) -> inventory.setItem(slot, pageNavigator.icon()));
    return inventory;
  }

  Optional<PageNavigator> getNavigatorBySlot(final int slot) {
    return Optional.ofNullable(navigatorBySlot.get(slot));
  }

  private ItemStack getNavigatorIconPointingForward() {
    return ItemStackBuilder.newBuilder(ARROW)
        .displayName(
            miniMessage()
                .deserialize(
                    "<gray>Następna strona"
                )
                .decoration(ITALIC, FALSE)
        )
        .lore(
            miniMessage()
                .deserialize(
                    "<gray>Naciśnij aby przejść do następnej strony."
                )
                .decoration(ITALIC, FALSE)
        )
        .build();
  }

  private ItemStack getNavigatorIconPointingBackward() {
    return ItemStackBuilder.newBuilder(ARROW)
        .displayName(
            miniMessage()
                .deserialize(
                    "<gray>Poprzednia strona"
                )
                .decoration(ITALIC, FALSE)
        )
        .lore(
            miniMessage()
                .deserialize(
                    "<gray>Naciśnij aby przejść do poprzedniej strony."
                )
                .decoration(ITALIC, FALSE)
        )
        .build();
  }

  private ItemStack getStakeItemOfStakeContext(final StakeContext stakeContext) {
    return ItemStackBuilder.newBuilder(new ItemStack(PAPER))
        .displayName(
            getDisplayNameOfStakeItem(stakeContext)
        )
        .lore(
            getLoreOfStakeItem(stakeContext).stream()
                .map(line -> line.decoration(ITALIC, FALSE))
                .toArray(Component[]::new)
        )
        .build();
  }

  private Component getDisplayNameOfStakeItem(final StakeContext stakeContext) {
    return miniMessage().deserialize(
        "<gray>Zakład <dark_gray>(<white><gamble><dark_gray>)",
            unparsed("gamble", capitalize(stakeContext.gambleKey().id().toLowerCase(ROOT)))
        )
        .decoration(ITALIC, FALSE);
  }

  private List<Component> getLoreOfStakeItem(final StakeContext stakeContext) {
    return List.of(
        miniMessage().deserialize(
            "<gray>Gracz: <white><initiator>",
            unparsed("initiator", stakeContext.initiator().username())
        ),
        miniMessage().deserialize(
            "<gray>Wybór: <white><prediction>",
            unparsed(
                "prediction",
                capitalize(
                    stakeContext.initiator().prediction().toString().toLowerCase(ROOT)
                )
            )
        ),
        miniMessage().deserialize(
            "<gray>Stawka: <white><stake_symbol><stake>",
            unparsed("stake", getFormattedDecimal(stakeContext.stake())),
            unparsed("stake_symbol", fundsCurrency.getSymbol())
        ),
        miniMessage().deserialize(
            "<gray>Naciśnij aby dołączyć do zakładu."
        ));
  }

  Optional<StakeContext> getStakeContextBySlot(final int slot) {
    return Optional.ofNullable(stakeContextBySlot.get(slot));
  }
}
