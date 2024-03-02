package edu.java.bot.models.commands;

import com.pengrad.telegrambot.model.Update;
import reactor.core.publisher.Mono;

/**
 * Contract of the bot commands.
 */
public interface Command {

    /**
     * Method returning the name of the command.
     */
    String nameCommand();

    /**
     * Method returning the description of the command.
     */
    String description();

    /**
     * Method that executes the command action.
     *
     * @param update the update that came to the telegram bot.
     * @return response message to the executed command.
     */
    Mono<String> execute(Update update);
}
