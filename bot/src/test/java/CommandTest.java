//import com.pengrad.telegrambot.model.Chat;
//import com.pengrad.telegrambot.model.Message;
//import com.pengrad.telegrambot.model.Update;
//import edu.java.bot.BotApplication;
//import edu.java.bot.model.SessionState;
//import edu.java.bot.model.commands.Command;
//import edu.java.bot.model.commands.ListCommand;
//import edu.java.bot.model.commands.StartCommand;
//import edu.java.bot.model.commands.TrackCommand;
//import edu.java.bot.model.commands.UnTrackCommand;
//import edu.java.bot.model.db_entities.User;
//import edu.java.bot.repository.UserRepository;
//import java.net.URI;
//import java.util.List;
//import java.util.Map;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest(classes = {BotApplication.class})
//public class CommandTest {
//    @MockBean
//    Update update;
//
//    @Autowired
//    Map<String, Command> commandMap;
//
//    @Autowired
//    UserRepository userRepository;
//
//    @Nested
//    @DisplayName("Test command /start")
//    class StartCommandTest {
//        private final Command start = new StartCommand(userRepository);
//
//        @Test
//        @DisplayName("Test that a new user can register and returned the id from the database")
//        void testThatANewUserCanRegisterAndReturnedTheIdFromTheDatabase() {
//            var id_user = 4L;
//            assertThat(userRepository.findUserById(id_user)).isEmpty();
//
//            mockSetUp(id_user);
//            var actualMessage = start.execute(update);
//            assertThat(actualMessage).isEqualTo(StartCommand.REGISTRATION_MESSAGE_SUCCESS);
//
//            var actualSaveUser = userRepository.findUserById(id_user);
//            assertThat(actualSaveUser).isPresent();
//            assertThat(actualSaveUser.get().getId()).isEqualTo(id_user);
//        }
//
//        @Test
//        @DisplayName("Test that the user cannot register twice and will returned the correct state of the database")
//        void testThatTheUserCannotRegisterTwiceAndWillReturnedTheCorrectStateOfTheDatabase() {
//            var id_user = 5L;
//            prepareUser(id_user, List.of());
//
//            mockSetUp(id_user);
//            var actualMessage = start.execute(update);
//            assertThat(actualMessage).isEqualTo(StartCommand.ALREADY_EXIST_MESSAGE);
//
//            var actualSaveUser = userRepository.findUserById(id_user);
//            assertThat(actualSaveUser).isPresent();
//            assertThat(actualSaveUser.stream().count()).isEqualTo(1);
//            assertThat(actualSaveUser.get().getId()).isEqualTo(id_user);
//
//        }
//    }
//
//    @Nested
//    @DisplayName("Test command /track and /untrack")
//    class TrackAndUntrackCommandTest {
//        private final Command track = commandMap.get("/track");
//        private final Command untrack = commandMap.get("/untrack");
//
//        @Test
//        @DisplayName("Test that using the command changes the state returned the correct state")
//        void testThatUsingTheCommandChangesTheStateReturnedTheCorrectState() {
//            long user_id = 1L;
//            prepareUser(user_id, List.of());
//            mockSetUp(user_id);
//
//            track.execute(update);
//
//            assertThat(userRepository.findUserById(user_id)).isPresent();
//            assertThat(userRepository.findUserById(user_id).get()
//                .getState()).isEqualTo(SessionState.WAIT_URI_FOR_TRACKING);
//
//            untrack.execute(update);
//            assertThat(userRepository.findUserById(user_id)).isPresent();
//            assertThat(userRepository.findUserById(user_id).get()
//                .getState()).isEqualTo(SessionState.WAIT_URI_FOR_UNTRACKING);
//        }
//
//        @Test
//        @DisplayName("Test that the user gets the correct answer returned the waiting input to the registered user")
//        void testThatTheUserGetsTheCorrectAnswerReturnedTheWaitingInputToTheRegisteredUser() {
//            long user_id = 1L;
//            prepareUser(user_id, List.of());
//            mockSetUp(user_id);
//
//            var actualTrackResponse = track.execute(update);
//
//            assertThat(actualTrackResponse).isEqualTo(TrackCommand.TRACK_MESSAGE);
//
//            var actualUnTrackResponse = untrack.execute(update);
//            assertThat(actualUnTrackResponse).isEqualTo(UnTrackCommand.UNTRACK_MESSAGE);
//        }
//
//        @Test
//        @DisplayName(
//            "Test that the user gets the correct answer returned the registration requirement to the unregistered user")
//        void testThatTheUserGetsTheCorrectAnswerReturnedTheRegistrationRequirementToTheUnregisteredUser() {
//            long unregisterUserId = 2L;
//            mockSetUp(unregisterUserId);
//
//            var actualTrackResponse = track.execute(update);
//            assertThat(actualTrackResponse).isEqualTo(TrackCommand.UNKNOWN_USER);
//
//            var actualUnTrackResponse = untrack.execute(update);
//            assertThat(actualUnTrackResponse).isEqualTo(UnTrackCommand.UNKNOWN_USER);
//        }
//    }
//
//    @Nested
//    @DisplayName("Test command /help")
//    class HelpCommandTest {
//        private final Command help = commandMap.get("/help");
//        private final String exceptedMessage = "Команды бота:\n" +
//            "/list - команда показать список отслеживаемых ссылок\n" +
//            "/start - зарегистрировать пользователя\n" +
//            "/track - начать отслеживание ссылки\n" +
//            "/untrack - прекратить отслеживание ссылки\n" +
//            "/help - вывести окно с командами\n";
//
//        @Test
//        @DisplayName("Test that command help returned correct list command and descriptions")
//        void testThatCommandHelpReturnedCorrectListCommandAndDescriptions() {
//            mockSetUp(1L);
//            prepareUser(1L, List.of());
//
//            var actualMessage = help.execute(update);
//
//            assertThat(exceptedMessage).isEqualTo(actualMessage);
//        }
//
//        @Test
//        @DisplayName("Test that the team returned the message to all users")
//        void testThatTheTeamReturnedTheMessageToAllUsers() {
//            mockSetUp(1L);
//            prepareUser(1L, List.of()); //register user
//            var actualMessageForRegisterUser = help.execute(update);
//
//            mockSetUp(2L); //unregister user
//            var actualMessageForUnRegisterUser = help.execute(update);
//
//            assertThat(exceptedMessage).isEqualTo(actualMessageForRegisterUser);
//            assertThat(exceptedMessage).isEqualTo(actualMessageForUnRegisterUser);
//        }
//    }
//
//    @Nested
//    @DisplayName("Test command /list")
//    class ListCommandTest {
//        private final Command list = commandMap.get("/list");
//
//        @Test
//        @DisplayName("Test that the command returned a special message for a user with an empty list of links")
//        void testThatTheCommandReturnedASpecialMessageForAUserWithAnEmptyListOfLinks() {
//            var exceptedSpecialMessage = ListCommand.EMPTY_LIST_SITES;
//            long user_id = 1L;
//            mockSetUp(user_id);
//            prepareUser(user_id, List.of());
//
//            var actualMessage = list.execute(update);
//
//            assertThat(actualMessage).isEqualTo(exceptedSpecialMessage);
//        }
//
//        @Test
//        @DisplayName(
//            "Test that the command returned a excepted list sites message for a user with an non-empty list of links")
//        void testThatTheCommandReturnedAExceptedListSitesMessageForAUserWithAnNonEmptyListOfLinks() {
//            var exceptedMessage = "Вы отслеживаете 1 сайтов\n" +
//                "https://github.com/sanyarnd/tinkoff-java-course-2023/\n";
//            long user_id = 1L;
//            mockSetUp(user_id);
//            prepareUser(user_id, List.of(URI.create("https://github.com/sanyarnd/tinkoff-java-course-2023/")));
//
//            var actualMessage = list.execute(update);
//
//            assertThat(actualMessage).isEqualTo(exceptedMessage);
//        }
//
//        @Test
//        @DisplayName("Test that the command returned correct message to an unregistered user")
//        void testThatTheCommandReturnedCorrectMessageToAnUnregisteredUser() {
//            long user_id = 116L;
//            mockSetUp(user_id);
//
//            var actualMessage = list.execute(update);
//
//            assertThat(actualMessage).isEqualTo(ListCommand.UNKNOWN_USER);
//        }
//
//    }
//
//    private void prepareUser(long id, List<URI> listSites) {
//        userRepository.saveUser(new User(id, listSites, SessionState.BASE_STATE));
//    }
//
//    private void mockSetUp(long id_user) {
//        Message messageMock = mock(Message.class);
//        Chat chatMock = mock(Chat.class);
//        when(update.message()).thenReturn(messageMock);
//        when(messageMock.chat()).thenReturn(chatMock);
//        when(chatMock.id()).thenReturn(id_user);
//        when(update.message().chat().id()).thenReturn(id_user);
//    }
//}
