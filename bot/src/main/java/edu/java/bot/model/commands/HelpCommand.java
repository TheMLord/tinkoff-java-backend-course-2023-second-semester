package edu.java.bot.model.commands;

import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Class help command;
 */

@Component("/help")
@AllArgsConstructor
public final class HelpCommand implements Command {
    private static final String STRING_COMMANDS_BOT = "Команды бота:\n";
    private static final String STRING_COMMANDS_ENUMERATION = "%s - %s\n";
    private static final String NAME_COMMAND = "/help";
    private static final String DESCRIPTION_COMMAND = "вывести окно с командами";

    private final List<Command> commandList;

    @PostConstruct
    private void addCommandHelp() {
        this.commandList.add(this);
    }

    @Override
    public String nameCommand() {
        return NAME_COMMAND;
    }

    @Override
    public String description() {
        return DESCRIPTION_COMMAND;
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
        return commandListString.toString();
    }
}
