package pl.auroramc.essentials.command;

import java.util.UUID;

record CommandSuggestionCompositeKey(UUID uniqueId, String invokedCommand) {}
