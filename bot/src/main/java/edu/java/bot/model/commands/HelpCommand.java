package edu.java.bot.model.commands;

import com.pengrad.telegrambot.model.Update;
import java.util.List;

/**
 * Class help command;
 */
public final class HelpCommand implements Command {
    private static final String STRING_COMMANDS_BOT = "Команды бота:\n";
    private static final String STRING_COMMANDS_ENUMERATION = "%s - %s\n";
    private final List<Command> listCommand;

    public HelpCommand(List<Command> listCommand) {
        this.listCommand = listCommand;
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
        listCommand.forEach(command ->
            commandListString.append(
                STRING_COMMANDS_ENUMERATION.formatted(command.nameCommand(), command.description())
            )
        );
        return new String(commandListString);
    }
}
