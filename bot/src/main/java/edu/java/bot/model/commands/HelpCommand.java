package edu.java.bot.model.commands;

import com.pengrad.telegrambot.model.Update;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Class help command;
 */

@Component("/help")
public final class HelpCommand implements Command {
    private static final String STRING_COMMANDS_BOT = "Команды бота:\n";
    private static final String STRING_COMMANDS_ENUMERATION = "%s - %s\n";
    private final List<Command> commandList;

    public HelpCommand(@Qualifier("action_command") List<Command> commandList) {
        this.commandList = commandList;
    }

    @Override
    public String nameCommand() {
        return "/help";
    }

    @Override
    public String description() {
        return "вывести окно с командами";
    }

    @Override
    public String execute(Update update) {
        return prepareHelpCommandDescription();

    }

    /**
     * Method that prepares a string with a description of commands
     */
    private String prepareHelpCommandDescription() {
        var commandListString = new StringBuilder();

        commandListString.append(STRING_COMMANDS_BOT);
        commandList.forEach(command ->
            commandListString.append(
                STRING_COMMANDS_ENUMERATION.formatted(command.nameCommand(), command.description())
            )
        );
        commandListString.append(STRING_COMMANDS_ENUMERATION.formatted(this.nameCommand(), this.description()));
        return new String(commandListString);
    }
}
