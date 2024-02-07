package edu.java.bot.processor;

import edu.java.bot.model.commands.Command;
import java.util.Map;
import java.util.Optional;

/**
 * Record containing the bots commands embedded in the configuration
 */
public record CommandHandler(Map<String, Command> commandMap) {
    /**
     * Method that returns a command if the bot has one
     */
    public Optional<Command> getCommand(String c) {
        return (commandMap.containsKey(c)) ? Optional.of(commandMap.get(c)) : Optional.empty();
    }
}
