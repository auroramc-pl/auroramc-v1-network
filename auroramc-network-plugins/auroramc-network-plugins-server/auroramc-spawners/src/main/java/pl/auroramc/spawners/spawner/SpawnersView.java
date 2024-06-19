package pl.auroramc.spawners.spawner;

import static org.bukkit.enchantments.Enchantment.UNBREAKING;
import static org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS;
import static pl.auroramc.commons.bukkit.page.navigation.NavigationDirection.BACKWARD;
import static pl.auroramc.commons.bukkit.page.navigation.NavigationDirection.FORWARD;
import static pl.auroramc.commons.bukkit.page.navigation.NavigationUtils.navigate;
import static pl.auroramc.integrations.item.ItemStackUtils.mergeLore;
import static pl.auroramc.messages.message.decoration.MessageDecorations.NO_CURSIVE;
import static pl.auroramc.spawners.spawner.SpawnerMessageSourcePaths.CURRENCY_PATH;
import static pl.auroramc.spawners.spawner.SpawnerMessageSourcePaths.SPAWNER_PATH;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import pl.auroramc.commons.External;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.integrations.item.ItemStackBuilder;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.messages.message.compiler.CompiledMessage;

class SpawnersView {

  private final Plugin plugin;
  private final Currency fundsCurrency;
  private final SpawnerFacade spawnerFacade;
  private final SpawnerController spawnerController;
  private final SpawnerMessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;
  private final CreatureSpawner subject;
  public @External ChestGui spawnersGui;
  public @External PaginatedPane spawnersPane;

  SpawnersView(
      final Plugin plugin,
      final Currency fundsCurrency,
      final SpawnerFacade spawnerFacade,
      final SpawnerController spawnerController,
      final SpawnerMessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final CreatureSpawner subject) {
    this.plugin = plugin;
    this.fundsCurrency = fundsCurrency;
    this.spawnerFacade = spawnerFacade;
    this.spawnerController = spawnerController;
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
    this.subject = subject;
  }

  public @External void requestClickCancelling(final InventoryClickEvent event) {
    event.setCancelled(true);
  }

  public @External void populateSpawnerItems(final PaginatedPane requestingPane) {
    spawnersPane = requestingPane;
    spawnersPane.clear();
    spawnersPane.populateWithGuiItems(getSpawnerItems(spawnerFacade.getSpawners()));
    spawnersGui.update();
  }

  public @External void navigateToNextPage() {
    navigate(FORWARD, spawnersGui, spawnersPane);
  }

  public @External void navigateToPrevPage() {
    navigate(BACKWARD, spawnersGui, spawnersPane);
  }

  private GuiItem getSpawnerItem(final Spawner spawner) {
    final ItemStack originItemStack = spawner.icon();
    final ItemStack renderItemStack =
        mergeLore(originItemStack, getAdditionalLoreForSpawnerItem(spawner));
    return new GuiItem(
        ItemStackBuilder.newBuilder(renderItemStack)
            .manipulate(
                value -> value.creatureType() == subject.getSpawnedType(),
                spawner,
                builder -> builder.enchantment(UNBREAKING, 1).flags(HIDE_ENCHANTS))
            .build(),
        event ->
            spawnerController.purchaseSpawner((Player) event.getWhoClicked(), spawner, subject),
        plugin);
  }

  private CompiledMessage[] getAdditionalLoreForSpawnerItem(final Spawner spawner) {
    return Stream.of(
            messageSource
                .spawnerPurchaseTag
                .placeholder(CURRENCY_PATH, fundsCurrency)
                .placeholder(SPAWNER_PATH, spawner),
            messageSource.spawnerPurchaseSuggestion)
        .map(message -> messageCompiler.compile(message, NO_CURSIVE))
        .toArray(CompiledMessage[]::new);
  }

  private List<GuiItem> getSpawnerItems(final Set<Spawner> spawners) {
    return spawners.stream().map(this::getSpawnerItem).toList();
  }
}
