package edu.java.bot.configuration;

import edu.java.bot.model.commands.HelpCommand;
import edu.java.bot.model.commands.ListCommand;
import edu.java.bot.model.commands.StartCommand;
import edu.java.bot.model.commands.TrackCommand;
import edu.java.bot.model.commands.UnTrackCommand;
import edu.java.bot.processor.CommandHandler;
import edu.java.bot.repository.UserRepository;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class of bot telegram commands
 */
@Configuration
public class CommandsConfig {
    /**
     * Method of creating a bot command handler bean.
     *
     * @param userRepository repository bean for use in bot commands.
     */
    @Bean
    CommandHandler commandHandler(UserRepository userRepository) {
        var start = new StartCommand(userRepository);
        var list = new ListCommand(userRepository);
        var track = new TrackCommand(userRepository);
        var unTrack = new UnTrackCommand(userRepository);

        var help = new HelpCommand(List.of(start, list, track, unTrack));

        return new CommandHandler(
            Map.of(
                "/start", start,
                "/help", help,
                "/list", list,
                "/track", track,
                "/untrack", unTrack
            )
        );
    }
}
