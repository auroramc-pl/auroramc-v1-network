package pl.auroramc.gamble.stake;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.optional.OptionalArg;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.gamble.message.MutableMessageSource;
import pl.auroramc.gamble.stake.view.StakeViewFacade;

@Permission("auroramc.gamble.stake")
@Command(name = "stake")
public class StakeCommand {

  private static final int INITIAL_STAKE_PAGE_INDEX = 0;
  private final MutableMessageSource messageSource;
  private final StakeViewFacade stakeViewFacade;

  public StakeCommand(final MutableMessageSource messageSource, final StakeViewFacade stakeViewFacade) {
    this.messageSource = messageSource;
    this.stakeViewFacade = stakeViewFacade;
  }

  @Execute
  public MutableMessage stake(
      final @Context Player player,
      final @OptionalArg Integer page
  ) {
    if (stakeViewFacade.getPageCount() == 0) {
      return messageSource.missingStakes;
    }

    final Optional<Inventory> destinedPage = stakeViewFacade.getStakeView(
        page == null ? INITIAL_STAKE_PAGE_INDEX : page
    );
    if (destinedPage.isEmpty()) {
      return messageSource.missingStakePage;
    }

    player.openInventory(destinedPage.get());
    return messageSource.displayStakeView;
  }
}
