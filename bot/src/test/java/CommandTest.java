import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.BotApplication;
import edu.java.bot.model.SessionState;
import edu.java.bot.model.commands.Command;
import edu.java.bot.model.commands.StartCommand;
import edu.java.bot.model.commands.TrackCommand;
import edu.java.bot.model.commands.UnTrackCommand;
import edu.java.bot.model.db_entities.User;
import edu.java.bot.processor.CommandHandler;
import edu.java.bot.repository.UserRepository;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {BotApplication.class})
public class CommandTest {
    @MockBean
    Update update;

    @Autowired
    CommandHandler commandHandler;

    @Autowired
    UserRepository userRepository;

    @Nested
    @DisplayName("Test command /start")
    class StartCommandTest {
        private final Command start = new StartCommand(userRepository);

        @Test
        @DisplayName("Test that a new user can register and returned the id from the database")
        void testThatANewUserCanRegisterAndReturnedTheIdFromTheDatabase() {
            var id_user = 4L;
            assertThat(userRepository.findUserById(id_user)).isEmpty();

            mockSetUp(id_user);
            start.execute(update);

            var actualSaveUser = userRepository.findUserById(id_user);
            assertThat(actualSaveUser).isPresent();
            assertThat(actualSaveUser.get().getId()).isEqualTo(id_user);
        }

        @Test
        @DisplayName("Test that the user cannot register twice and will returned the correct state of the database")
        void testThatTheUserCannotRegisterTwiceAndWillReturnedTheCorrectStateOfTheDatabase() {
            var id_user = 5L;
            prepareUser(id_user, List.of());

            mockSetUp(id_user);
            start.execute(update);

            var actualSaveUser = userRepository.findUserById(id_user);
            assertThat(actualSaveUser).isPresent();
            assertThat(actualSaveUser.stream().count()).isEqualTo(1);
            assertThat(actualSaveUser.get().getId()).isEqualTo(id_user);

        }
    }

    @Nested
    @DisplayName("Test command /track and /untrack")
    class TrackAndUntrackCommandTest {
        Command track = commandHandler.getCommand("/track").get();
        Command untrack = commandHandler.getCommand("/untrack").get();

        @Test
        @DisplayName("Test that using the command changes the state returned the correct state")
        void testThatUsingTheCommandChangesTheStateReturnedTheCorrectState() {
            long user_id = 1L;
            prepareUser(user_id, List.of());
            mockSetUp(user_id);

            track.execute(update);

            assertThat(userRepository.findUserById(user_id)).isPresent();
            assertThat(userRepository.findUserById(user_id).get()
                .getState()).isEqualTo(SessionState.WAIT_URI_FOR_TRACKING);

            untrack.execute(update);
            assertThat(userRepository.findUserById(user_id)).isPresent();
            assertThat(userRepository.findUserById(user_id).get()
                .getState()).isEqualTo(SessionState.WAIT_URI_FOR_UNTRACKING);
        }

        @Test
        @DisplayName("Test that the user gets the correct answer returned the waiting input to the registered user")
        void testThatTheUserGetsTheCorrectAnswerReturnedTheWaitingInputToTheRegisteredUser() {
            long user_id = 1L;
            prepareUser(user_id, List.of());
            mockSetUp(user_id);

            var actualTrackResponse = track.execute(update);

            assertThat(actualTrackResponse).isEqualTo(TrackCommand.TRACK_MESSAGE);

            var actualUnTrackResponse = untrack.execute(update);
            assertThat(actualUnTrackResponse).isEqualTo(UnTrackCommand.UNTRACK_MESSAGE);
        }

        @Test
        @DisplayName(
            "Test that the user gets the correct answer returned the registration requirement to the unregistered user")
        void testThatTheUserGetsTheCorrectAnswerReturnedTheRegistrationRequirementToTheUnregisteredUser() {
            long unregisterUserId = 2L;
            mockSetUp(unregisterUserId);

            var actualTrackResponse = track.execute(update);
            assertThat(actualTrackResponse).isEqualTo(TrackCommand.UNKNOWN_USER);

            var actualUnTrackResponse = untrack.execute(update);
            assertThat(actualUnTrackResponse).isEqualTo(UnTrackCommand.UNKNOWN_USER);
        }
    }

    private void prepareUser(long id, List<URI> listSites) {
        userRepository.saveUser(new User(id, listSites, SessionState.BASE_STATE));
    }

    private void mockSetUp(long id_user) {
        Message messageMock = mock(Message.class);
        Chat chatMock = mock(Chat.class);
        when(update.message()).thenReturn(messageMock);
        when(messageMock.chat()).thenReturn(chatMock);
        when(chatMock.id()).thenReturn(id_user);
        when(update.message().chat().id()).thenReturn(id_user);
    }
}
