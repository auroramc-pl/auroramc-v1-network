package pl.auroramc.essentials.command;

import static java.lang.Math.round;
import static java.time.Duration.ofSeconds;
import static java.util.Arrays.stream;
import static java.util.function.Predicate.not;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickEvent.suggestCommand;
import static org.bukkit.event.EventPriority.HIGHEST;
import static pl.auroramc.commons.command.CommandUtils.resolveCommand;
import static pl.auroramc.commons.lazy.Lazy.lazy;
import static pl.auroramc.essentials.message.MessageSourcePaths.PERCENTAGE_PATH;
import static pl.auroramc.essentials.message.MessageSourcePaths.PLUGIN_NAME_PATH;
import static pl.auroramc.essentials.message.MessageSourcePaths.SEPARATOR_PATH;
import static pl.auroramc.essentials.message.MessageSourcePaths.SUGGESTION_PATH;

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
import pl.auroramc.commons.lazy.Lazy;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.commons.scheduler.caffeine.CaffeineExecutor;
import pl.auroramc.commons.search.FuzzySearch;
import pl.auroramc.essentials.EssentialsConfig;
import pl.auroramc.essentials.message.MessageSource;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;

public class CommandListener implements Listener {

  private static final String PLUGIN_SUMMARY_COMMAND_NAME = "plugins";
  private static final String PLUGIN_SUMMARY_COMMAND_ALIAS = "pl";
  private static final String PLUGIN_SEPARATOR = ", ";
  private static final String PLUGIN_SEPARATOR_CLOSING = "";
  private static final String BENTOBOX_EXTENSION_PREFIX = "BentoBox-";
  private static final String SPECIAL_COMMAND_NAME_PREFIX = "/";
  private static final String DIRECT_COMMAND_CALL_DELIMITER = ":";
  private static final String CUSTOM_MADE_PLUGIN_NAME_PREFIX = "auroramc-";
  private static final String COMMAND_NAME_PREFIX = "/";
  private static final String COMMAND_ARGUMENT_DELIMITER = " ";

  private final Server server;
  private final FuzzySearch fuzzySearch;
  private final MessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;
  private final EssentialsConfig essentialsConfig;
  private final Cache<SuggestionCompositeKey, String> suggestedCommandsByCompositeKey;
  private final Cache<UUID, Set<String>> availableCommandsByName;
  private final Lazy<Component> overviewOfPluginSummaries;

  public CommandListener(
      final Server server,
      final Scheduler scheduler,
      final FuzzySearch fuzzySearch,
      final MessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final EssentialsConfig essentialsConfig) {
    this.server = server;
    this.fuzzySearch = fuzzySearch;
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
    this.essentialsConfig = essentialsConfig;
    this.suggestedCommandsByCompositeKey =
        Caffeine.newBuilder()
            .executor(new CaffeineExecutor(scheduler))
            .expireAfterWrite(ofSeconds(10))
            .build();
    this.availableCommandsByName =
        Caffeine.newBuilder()
            .executor(new CaffeineExecutor(scheduler))
            .expireAfterWrite(ofSeconds(30))
            .build();
    this.overviewOfPluginSummaries = lazy(this::getOverviewOfPluginSummaries);
  }

  @EventHandler(priority = HIGHEST, ignoreCancelled = true)
  public void onUnknownCommandUse(final UnknownCommandEvent event) {
    final String input = event.getCommandLine();

    final String performedCommand = resolveCommand("/%s".formatted(input)).substring(COMMAND_NAME_PREFIX.length());
    final String suggestedCommand = getPotentialSuggestionForCommand(event.getSender(), performedCommand);
    if (suggestedCommand == null) {
      event.message(messageCompiler.compile(messageSource.unknownCommand).getComponent());
      return;
    }

    event.message(
        getPotentialSuggestion(suggestedCommand, getArgumentsSegment(performedCommand, input)));
  }

  @EventHandler(priority = HIGHEST, ignoreCancelled = true)
  public void onPluginSummaryRequest(final PlayerCommandPreprocessEvent event) {
    if (isPluginSummaryRequest(event.getMessage())) {
      event.setCancelled(true);
      event.getPlayer().sendMessage(overviewOfPluginSummaries.get());
    }
  }

  private String getPotentialSuggestionForCommand(
      final CommandSender invoker, final String invokedCommand) {
    if (isSpecialCommand(invokedCommand)) {
      return null;
    }

    if (invoker instanceof ConsoleCommandSender) {
      return null;
    }

    final Player player = (Player) invoker;
    return suggestedCommandsByCompositeKey.get(
        new SuggestionCompositeKey(player.getUniqueId(), invokedCommand),
        ignored ->
            fuzzySearch.getMostSimilarString(
                invokedCommand,
                availableCommandsByName.get(
                    player.getUniqueId(), name -> getAvailableCommands(player)),
                essentialsConfig.minimalScoreForCommandSuggestion));
  }

  private Component getPotentialSuggestion(final String suggestedCommand, final String arguments) {
    return messageCompiler
        .compile(
            messageSource.unknownCommandWithPotentialSuggestion.placeholder(
                SUGGESTION_PATH,
                text(suggestedCommand)
                    .hoverEvent(
                        messageCompiler
                            .compile(messageSource.potentialSuggestionHover)
                            .getComponent())
                    .clickEvent(suggestCommand("/%s%s".formatted(suggestedCommand, arguments)))))
        .getComponent();
  }

  private String getArgumentsSegment(final String performedCommand, final String input) {
    final boolean hasArguments = input.split(COMMAND_ARGUMENT_DELIMITER).length > 1;
    final String delimiter = hasArguments ? COMMAND_ARGUMENT_DELIMITER : "";
    final int offset = hasArguments ? COMMAND_ARGUMENT_DELIMITER.length() : 0;
    return "%s%s"
        .formatted(
            delimiter,
            input.substring(
                performedCommand.substring(COMMAND_NAME_PREFIX.length()).length() + offset));
  }

  private Set<String> getAvailableCommands(final CommandSender invoker) {
    final Set<String> availableCommands = new HashSet<>();
    for (final Entry<String, Command> commandByCommandName :
        server.getCommandMap().getKnownCommands().entrySet()) {
      final String commandName = commandByCommandName.getKey();
      if (isSpecialCommand(commandName) || isDirectCommand(commandName)) {
        continue;
      }

      final Command command = commandByCommandName.getValue();
      if (command.testPermissionSilent(invoker)) {
        availableCommands.add(commandName.toLowerCase());
      }
    }
    return availableCommands;
  }

  private Component getOverviewOfPluginSummaries() {
    final List<Plugin> listOfPlugins =
        stream(server.getPluginManager().getPlugins())
            .filter(not(this::isBentoBoxExtension))
            .toList();
    final List<Plugin> customPlugins = listOfPlugins.stream().filter(this::isCustomPlugin).toList();
    final int percentageOfCustomPlugins =
        (int) round((double) customPlugins.size() / listOfPlugins.size() * 100);
    return getTitleOfPluginSummary(percentageOfCustomPlugins)
        .append(getEntriesOfPluginSummary(customPlugins));
  }

  private Component getTitleOfPluginSummary(final int percentageOfCustomPlugins) {
    return messageCompiler
        .compile(
            messageSource.titleOfSummary.placeholder(PERCENTAGE_PATH, percentageOfCustomPlugins))
        .getComponent();
  }

  private Component getEntriesOfPluginSummary(final List<Plugin> plugins) {
    final Component result = empty();
    return plugins.stream()
        .map(
            plugin ->
                getEntryOfPluginSummary(plugin, plugins.indexOf(plugin) == plugins.size() - 1))
        .reduce(result, Component::append);
  }

  private Component getEntryOfPluginSummary(
      final Plugin plugin, final boolean whetherIsClosingEntry) {
    return messageCompiler
        .compile(
            messageSource
                .entryOfSummary
                .placeholder(PLUGIN_NAME_PATH, plugin.getName())
                .placeholder(
                    SEPARATOR_PATH,
                    whetherIsClosingEntry ? PLUGIN_SEPARATOR_CLOSING : PLUGIN_SEPARATOR))
        .getComponent();
  }

  private boolean isSpecialCommand(final String commandName) {
    return commandName.startsWith(SPECIAL_COMMAND_NAME_PREFIX);
  }

  private boolean isDirectCommand(final String commandName) {
    return commandName.contains(DIRECT_COMMAND_CALL_DELIMITER);
  }

  private boolean isPluginSummaryRequest(final String query) {
    final String commandName = resolveCommand(query);
    return commandName.equals(PLUGIN_SUMMARY_COMMAND_NAME)
        || commandName.equals(PLUGIN_SUMMARY_COMMAND_ALIAS);
  }

  private boolean isCustomPlugin(final Plugin plugin) {
    return plugin.getName().startsWith(CUSTOM_MADE_PLUGIN_NAME_PREFIX);
  }

  private boolean isBentoBoxExtension(final Plugin plugin) {
    return plugin.getName().startsWith(BENTOBOX_EXTENSION_PREFIX);
  }

  private record SuggestionCompositeKey(UUID uniqueId, String performedCommand) {}
}
