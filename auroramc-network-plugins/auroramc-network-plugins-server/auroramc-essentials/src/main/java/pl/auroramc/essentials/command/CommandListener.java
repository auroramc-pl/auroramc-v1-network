package pl.auroramc.essentials.command;

import static java.lang.Math.round;
import static java.time.Duration.ofSeconds;
import static java.util.Arrays.stream;
import static java.util.function.Predicate.not;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickEvent.suggestCommand;
import static org.bukkit.event.EventPriority.HIGHEST;
import static pl.auroramc.essentials.command.CommandUtils.resolveCommand;
import static pl.auroramc.commons.lazy.Lazy.lazy;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.command.UnknownCommandEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import pl.auroramc.essentials.EssentialsConfig;
import pl.auroramc.commons.lazy.Lazy;
import pl.auroramc.commons.search.FuzzySearch;
import pl.auroramc.essentials.message.MessageSource;

public class CommandListener implements Listener {

  private static final String PLUGIN_SUMMARY_COMMAND_NAME = "plugins";
  private static final String PLUGIN_SUMMARY_COMMAND_ALIAS = "pl";
  private static final String CUSTOM_MADE_PLUGIN_NAME_PREFIX = "auroramc-";
  private static final String PLUGIN_SEPARATOR = ", ";
  private static final String PLUGIN_SEPARATOR_CLOSING = "";
  private static final String BENTOBOX_EXTENSION_PREFIX = "BentoBox-";
  private static final String SPECIAL_COMMAND_NAME_PREFIX = "/";
  private static final String COMMAND_ARGUMENT_DELIMITER = " ";
  private static final int COMMAND_ARGUMENTS_OFFSET = 1;
  private final Server server;
  private final FuzzySearch fuzzySearch;
  private final MessageSource messageSource;
  private final EssentialsConfig essentialsConfig;
  private final Cache<UUID, Set<String>> availableCommandsByName;
  private final Lazy<Component> overviewOfPluginSummaries;

  public CommandListener(
      final Server server,
      final FuzzySearch fuzzySearch,
      final MessageSource messageSource,
      final EssentialsConfig essentialsConfig
  ) {
    this.server = server;
    this.fuzzySearch = fuzzySearch;
    this.messageSource = messageSource;
    this.essentialsConfig = essentialsConfig;
    this.availableCommandsByName = Caffeine.newBuilder()
        .expireAfterWrite(ofSeconds(30))
        .build();
    this.overviewOfPluginSummaries = lazy(this::getOverviewOfPluginSummaries);
  }

  @EventHandler(priority = HIGHEST, ignoreCancelled = true)
  void onUnknownCommandUse(final UnknownCommandEvent event) {
    final String input = event.getCommandLine();

    final String performedCommand = resolveCommand("/%s".formatted(input));
    final String suggestedCommand = getPotentialSuggestionForCommand(event.getSender(), performedCommand);
    if (suggestedCommand == null) {
      event.message(messageSource.unknownCommand.into());
      return;
    }

    final boolean whetherHasArguments = whetherInputHasArguments(input);
    final String arguments = "%s%s".formatted(
        whetherHasArguments ? COMMAND_ARGUMENT_DELIMITER : "",
        input.substring(performedCommand.length() + (whetherHasArguments ? COMMAND_ARGUMENTS_OFFSET : 0)));

    event.message(messageSource.unknownCommandWithPotentialSuggestion
        .with("suggestion", text(suggestedCommand)
            .hoverEvent(messageSource.potentialSuggestionHover.into())
            .clickEvent(suggestCommand("/%s%s".formatted(suggestedCommand, arguments))))
        .into());
  }

  private boolean whetherInputHasArguments(final String input) {
    return input.split(COMMAND_ARGUMENT_DELIMITER).length > 1;
  }

  private String getPotentialSuggestionForCommand(
      final CommandSender executor, final String usedCommand
  ) {
    if (executor instanceof ConsoleCommandSender) {
      return null;
    }

    final Set<String> availableCommands = getAvailableCommandsWithCaching((Player) executor);
    return fuzzySearch.getMostSimilar(usedCommand, availableCommands, essentialsConfig.minimalScoreForCommandSuggestion);
  }

  private Set<String> getAvailableCommandsWithCaching(final Player executor) {
    return availableCommandsByName.get(executor.getUniqueId(), key -> getAvailableCommands(executor));
  }

  private Set<String> getAvailableCommands(final CommandSender executor) {
    final Set<String> availableCommands = new HashSet<>();
    for (final Entry<String, Command> commandByCommandName : server.getCommandMap()
        .getKnownCommands()
        .entrySet()) {
      if (isSpecialCommand(commandByCommandName.getKey())) {
        continue;
      }

      final Command command = commandByCommandName.getValue();
      if (command.testPermissionSilent(executor)) {
        availableCommands.add(commandByCommandName.getKey().toLowerCase());
      }
    }
    return availableCommands;
  }

  private boolean isSpecialCommand(final String commandName) {
    return commandName.startsWith(SPECIAL_COMMAND_NAME_PREFIX);
  }

  @EventHandler(priority = HIGHEST, ignoreCancelled = true)
  void onPluginSummaryRequest(final PlayerCommandPreprocessEvent event) {
    if (whetherIsPluginSummaryRequest(event.getMessage())) {
      event.setCancelled(true);
      event.getPlayer().sendMessage(overviewOfPluginSummaries.get());
    }
  }

  private Component getOverviewOfPluginSummaries() {
    final List<Plugin> listOfPlugins = stream(server.getPluginManager().getPlugins())
        .filter(not(this::whetherPluginIsBentoBoxExtension))
        .toList();
    final List<Plugin> customPlugins = listOfPlugins.stream()
        .filter(this::whetherPluginIsCustom)
        .toList();
    final int percentageOfCustomPlugins =
        (int) round((double) customPlugins.size() / listOfPlugins.size() * 100);
    return getTitleOfPluginSummary(percentageOfCustomPlugins)
        .append(getEntriesOfPluginSummary(customPlugins));
  }

  private Component getTitleOfPluginSummary(final int percentageOfCustomPlugins) {
    return messageSource.titleOfSummary
        .with("plugins_percentage", percentageOfCustomPlugins)
        .into();
  }

  private Component getEntriesOfPluginSummary(final List<Plugin> plugins) {
    final Component result = Component.empty();
    return plugins.stream()
        .map(plugin ->
            getEntryOfPluginSummary(
                plugin,
                plugins.indexOf(plugin) == plugins.size() - 1
            )
        )
        .reduce(result, Component::append);
  }

  private Component getEntryOfPluginSummary(final Plugin plugin, final boolean whetherIsClosingEntry) {
    return messageSource.entryOfSummary
        .with("plugin_name", plugin.getName())
        .with("separator", whetherIsClosingEntry ? PLUGIN_SEPARATOR_CLOSING : PLUGIN_SEPARATOR)
        .into();
  }

  private boolean whetherIsPluginSummaryRequest(final String query) {
    final String commandName = resolveCommand(query);
    return
        commandName.equals(PLUGIN_SUMMARY_COMMAND_NAME) ||
        commandName.equals(PLUGIN_SUMMARY_COMMAND_ALIAS);
  }

  private boolean whetherPluginIsCustom(final Plugin plugin) {
    return plugin.getName().startsWith(CUSTOM_MADE_PLUGIN_NAME_PREFIX);
  }

  private boolean whetherPluginIsBentoBoxExtension(final Plugin plugin) {
    return plugin.getName().startsWith(BENTOBOX_EXTENSION_PREFIX);
  }
}
