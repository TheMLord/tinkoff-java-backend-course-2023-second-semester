package edu.java.bot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.model.commands.Command;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramBotConfig {

    /**
     * Method of creating a bot telegram bin using the pengrad SDK.
     *
     * @param appConfig  bean configuration application with the necessary parameters for creating a bot.
     * @param commandMap bean with commands for bot.
     */
    @Bean
    TelegramBot telegramBot(ApplicationConfig appConfig, Map<String, Command> commandMap) {
        var bot = new TelegramBot(appConfig.telegramToken());

        bot.execute(createMenuCommand(commandMap));
        return bot;
    }

    /**
     * Method of preparing the menu with commands for the bot.
     */
    private SetMyCommands createMenuCommand(Map<String, Command> commands) {
        return new SetMyCommands(commands.values().stream().map(command -> new BotCommand(
            command.nameCommand(),
            command.description()
        )).toArray(BotCommand[]::new));
    }
}
