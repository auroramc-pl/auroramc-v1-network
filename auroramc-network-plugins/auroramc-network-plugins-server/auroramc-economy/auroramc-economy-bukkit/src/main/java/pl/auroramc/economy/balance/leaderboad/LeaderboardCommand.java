package pl.auroramc.economy.balance.leaderboad;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed;
import static pl.auroramc.commons.decimal.DecimalFormatter.getFormattedDecimal;

import dev.rollczi.litecommands.argument.option.Opt;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import panda.std.Option;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;

@Permission("auroramc.economy.leaderboard")
@Route(name = "leaderboard", aliases = "baltop")
public class LeaderboardCommand {

  private final CurrencyFacade currencyFacade;
  private final LeaderboardFacade leaderboardFacade;
  private final LeaderboardConfig leaderboardConfig;

  public LeaderboardCommand(
      final CurrencyFacade currencyFacade,
      final LeaderboardFacade leaderboardFacade,
      final LeaderboardConfig leaderboardConfig
  ) {
    this.currencyFacade = currencyFacade;
    this.leaderboardFacade = leaderboardFacade;
    this.leaderboardConfig = leaderboardConfig;
  }

  @Execute
  public Component leaderboard(final Player player, final @Opt Option<Long> currencyId) {
    final Long currencyIdOrDefault = currencyId.orElseGet(leaderboardConfig.defaultCurrencyId);

    final Currency currency = currencyFacade.getCurrencyById(currencyIdOrDefault);
    if (currency == null) {
      return miniMessage().deserialize(
          "<red>Wprowadzona przez ciebie waluta <dark_gray>(<yellow><currency_id><dark_gray>) <red>nie została odnaleziona, upewnij się, czy jest ona poprawna.",
          unparsed("currency_id", String.valueOf(currencyIdOrDefault))
      );
    }

    return getLeaderboardView(player, currency.getId());
  }

  private Component getLeaderboardView(final Player player, final Long currencyId) {
    return empty()
        .append(getLeaderboardHeader())
        .append(getLeaderboardLines(leaderboardFacade.getLeaderboardEntriesByCurrencyId(currencyId)))
        .append(getLeaderboardFooter(player, currencyId));
  }

  private Component getLeaderboardHeader() {
    return miniMessage().deserialize(
        "<gray>Ranking najbogatszych graczy:<newline>"
    );
  }

  private Component getLeaderboardLines(final List<LeaderboardEntry> entries) {
    final Component result = empty();
    return entries.stream()
        .map(this::getLeaderboardLine)
        .reduce(result, Component::append);
  }

  private Component getLeaderboardLine(final LeaderboardEntry entry) {
    return miniMessage().deserialize(
        "<dark_gray><position>) <gray><username> <dark_gray>- <white>$<balance><newline>",
        getLeaderboardEntryTagResolvers(entry)
    );
  }

  private Component getLeaderboardFooter(final Player viewer, final Long currencyId) {
    return leaderboardFacade.getLeaderboardEntryByCurrencyIdAndUniqueId(currencyId, viewer.getUniqueId())
        .map(this::getLeaderboardFooter)
        .orElse(empty());
  }

  private Component getLeaderboardFooter(final LeaderboardEntry entry) {
    return miniMessage().deserialize(
        "<gray>Twoja pozycja:<newline><yellow><position>) <gray><username> <dark_gray>- <white>$<balance>",
        getLeaderboardEntryTagResolvers(entry)
    );
  }

  private TagResolver[] getLeaderboardEntryTagResolvers(final LeaderboardEntry entry) {
    return List.of(
        unparsed("position", String.valueOf(entry.position())),
        unparsed("username", entry.username()),
        unparsed("balance", getFormattedDecimal(entry.balance()))
    ).toArray(TagResolver[]::new);
  }
}
