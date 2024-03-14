package pl.auroramc.gamble.stake;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

import dev.rollczi.litecommands.argument.option.Opt;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import panda.std.Option;
import pl.auroramc.gamble.stake.view.StakeViewFacade;

@Permission("auroramc.gamble.stake")
@Route(name = "stake", aliases = {"zaklad", "zaklady"})
public class StakeCommand {

  private static final int INITIAL_STAKE_PAGE_INDEX = 0;
  private final StakeViewFacade stakeViewFacade;

  public StakeCommand(final StakeViewFacade stakeViewFacade) {
    this.stakeViewFacade = stakeViewFacade;
  }

  @Execute
  public Component displayStake(final Player player, final @Opt Option<Integer> page) {
    if (stakeViewFacade.getPageCount() == 0) {
      return miniMessage().deserialize(
          "<red>W tym momencie nie ma dostępnych zakładów, spróbuj ponownie później."
      );
    }

    final Optional<Inventory> destinedPage = stakeViewFacade.getStakeView(
        page.orElseGet(INITIAL_STAKE_PAGE_INDEX)
    );
    if (destinedPage.isEmpty()) {
      return miniMessage().deserialize(
          "<red>Wprowadzona przez ciebie strona nie jest dostępna, upewnij się, czy poprawnie ją wprowadziłeś."
      );
    }

    player.openInventory(destinedPage.get());
    return miniMessage().deserialize(
        "<gray>Otworzyłeś podgląd dostępnych zakładów, aby dołączyć do jednego z nich naciśnij na wybrany przez ciebie zakład lewym przyciskiem myszy <dark_gray>(<white>LPM<dark_gray>)<gray>."
    );
  }
}
