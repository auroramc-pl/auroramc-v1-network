package pl.auroramc.gamble.stake;

import dev.rollczi.litecommands.argument.option.Opt;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import panda.std.Option;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.gamble.message.MessageSource;
import pl.auroramc.gamble.stake.view.StakeViewFacade;

@Permission("auroramc.gamble.stake")
@Route(name = "stake")
public class StakeCommand {

  private static final int INITIAL_STAKE_PAGE_INDEX = 0;
  private final MessageSource messageSource;
  private final StakeViewFacade stakeViewFacade;

  public StakeCommand(final MessageSource messageSource, final StakeViewFacade stakeViewFacade) {
    this.messageSource = messageSource;
    this.stakeViewFacade = stakeViewFacade;
  }

  @Execute
  public MutableMessage stake(final Player player, final @Opt Option<Integer> page) {
    if (stakeViewFacade.getPageCount() == 0) {
      return messageSource.missingStakes;
    }

    final Optional<Inventory> destinedPage = stakeViewFacade.getStakeView(
        page.orElseGet(INITIAL_STAKE_PAGE_INDEX)
    );
    if (destinedPage.isEmpty()) {
      return messageSource.missingStakePage;
    }

    player.openInventory(destinedPage.get());
    return messageSource.displayStakeView;
  }
}
