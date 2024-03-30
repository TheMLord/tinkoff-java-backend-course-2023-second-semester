import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.BotApplication;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.ListCommand;
import edu.java.bot.commands.StartCommand;
import edu.java.bot.commands.TrackCommand;
import edu.java.bot.commands.UnTrackCommand;
import edu.java.bot.domain.TgChat;
import edu.java.bot.exceptions.ScrapperApiException;
import edu.java.bot.models.SessionState;
import edu.java.bot.models.dto.api.response.ApiErrorResponse;
import edu.java.bot.models.dto.api.response.LinkResponse;
import edu.java.bot.models.dto.api.response.ListLinksResponse;
import edu.java.bot.proxy.ScrapperProxy;
import edu.java.bot.repository.TgChatRepository;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Mono;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {BotApplication.class})
@DirtiesContext
public class CommandsTest {
    @MockBean TelegramBot telegramBot;
    @MockBean Update update;

    @MockBean ScrapperProxy scrapperProxy;

    @Autowired Map<String, Command> commandMap;

    @Autowired TgChatRepository tgChatRepository;

    private static final String BAD_REQUEST_HTTP_CODE = "400";
    private static final String UNAUTHORIZED_HTTP_CODE = "401";
    private static final String FORBIDDEN_HTTP_CODE = "403";
    private static final String NOT_FOUND_HTTP_CODE = "404";
    private static final String NOT_ACCEPTABLE_HTTP_CODE = "406";
    private static final String PRECONDITION_FAILED_HTTP_CODE = "412";
    private static final String INTERNAL_SERVER_ERROR_HTTP_CODE = "500";
    private static final String BAD_GATEWAY_HTTP_CODE = "502";

    public static final String CHAT_ALREADY_REGISTER_DESCRIPTION = "Чат уже зарегистрирован";
    public static final String CHAT_NOT_REGISTER_DESCRIPTION = "Чат не зарегистрирован";

    public static final String LINK_ALREADY_TRACKED_DESCRIPTION = "Ссылка уже отслеживается";
    public static final String LINK_IS_NOT_TRACK_DESCRIPTION = "Ссылка не отслеживается чатом";
    public static final String LINK_NOT_FOUND_DESCRIPTION = "Несуществующая ссылка";

    @Nested
    @DisplayName("Test command /start")
    class StartCommandTest {
        private final Command start = new StartCommand(tgChatRepository, scrapperProxy);

        @Test
        @DisplayName("Test that a new chat can register and returned the id from the database")
        void testThatANewChatCanRegisterAndReturnedTheIdFromTheDatabase() {
            var tgChatId = 4L;
            var exceptedMessage = StartCommand.REGISTRATION_MESSAGE_SUCCESS;

            mockBeanUpdateSetUp(tgChatId);
            when(scrapperProxy.registerChat(tgChatId)).thenReturn(Mono.empty());

            var actualMessage = start.execute(update);
            assertThat(actualMessage.block()).isEqualTo(exceptedMessage);

            var actualSaveChat = tgChatRepository.findTgChatById(tgChatId);
            assertThat(actualSaveChat).isPresent();
            assertThat(actualSaveChat.get().getId()).isEqualTo(tgChatId);
        }

        @Test
        @DisplayName("Test that the chat cannot register twice and will returned the correct state of the database")
        void testThatTheChatCannotRegisterTwiceAndWillReturnedTheCorrectStateOfTheDatabase() {
            var exceptedMessage = "Чат не зарегистрирован";
            var tgChatId = 5L;

            var exceptionDoubleRegistration = ApiErrorResponse
                .builder()
                .description(CHAT_NOT_REGISTER_DESCRIPTION)
                .code(UNAUTHORIZED_HTTP_CODE)
                .exceptionName("NotExistTgChatException")
                .exceptionMessage(CHAT_NOT_REGISTER_DESCRIPTION)
                .stacktrace(List.of())
                .build();
            prepareTgChat(tgChatId);
            mockBeanUpdateSetUp(tgChatId);
            when(scrapperProxy.registerChat(tgChatId)).thenReturn(Mono.error(new ScrapperApiException(
                exceptionDoubleRegistration)));

            var actualMessage = start.execute(update);

            assertThat(actualMessage.block()).isEqualTo(exceptedMessage);
        }
    }

    @Nested
    @DisplayName("Test command /track and /untrack")
    class TrackAndUntrackCommandTest {
        private final Command track = commandMap.get("/track");
        private final Command untrack = commandMap.get("/untrack");

        @Test
        @DisplayName("Test that using the command changes the state returned the correct state")
        void testThatUsingTheCommandChangesTheStateReturnedTheCorrectState() {
            var chatId = 1L;
            var exceptedMessageTrack = TrackCommand.TRACK_MESSAGE;
            var exceptedMessageUnTrack = UnTrackCommand.UNTRACK_MESSAGE;
            prepareTgChat(chatId);
            mockBeanUpdateSetUp(chatId);

            var actualMessageTrack = track.execute(update).block();
            assertThat(actualMessageTrack).isEqualTo(exceptedMessageTrack);
            assertThat(tgChatRepository.findTgChatById(chatId)).isPresent();
            assertThat(tgChatRepository.findTgChatById(chatId).get()
                .getState()).isEqualTo(SessionState.WAIT_URI_FOR_TRACKING);

            var actualMessageUnTrack = untrack.execute(update).block();
            assertThat(actualMessageUnTrack).isEqualTo(exceptedMessageUnTrack);
            assertThat(tgChatRepository.findTgChatById(chatId)).isPresent();
            assertThat(tgChatRepository.findTgChatById(chatId).get()
                .getState()).isEqualTo(SessionState.WAIT_URI_FOR_UNTRACKING);
        }

        @Test
        @DisplayName("Test that the chat gets the correct answer returned the waiting input to the registered chat")
        void testThatTheChatGetsTheCorrectAnswerReturnedTheWaitingInputToTheRegisteredChat() {
            var chatId = 1L;
            var exceptedMessageTrack = TrackCommand.TRACK_MESSAGE;
            var exceptedMessageUnTrack = UnTrackCommand.UNTRACK_MESSAGE;
            prepareTgChat(chatId);
            mockBeanUpdateSetUp(chatId);

            var actualMessageTrack = track.execute(update).block();
            assertThat(actualMessageTrack).isEqualTo(exceptedMessageTrack);

            var actualMessageUnTrack = untrack.execute(update).block();
            assertThat(actualMessageUnTrack).isEqualTo(exceptedMessageUnTrack);
        }

        @Test
        @DisplayName(
            "Test that the chat gets the correct answer returned the registration requirement to the unregistered chat")
        void testThatTheChatGetsTheCorrectAnswerReturnedTheRegistrationRequirementToTheUnregisteredChat() {
            long unregisterUserId = 2L;
            mockBeanUpdateSetUp(unregisterUserId);

            var actualTrackResponse = track.execute(update).block();
            assertThat(actualTrackResponse).isEqualTo(TrackCommand.UNKNOWN_USER);

            var actualUnTrackResponse = untrack.execute(update).block();
            assertThat(actualUnTrackResponse).isEqualTo(UnTrackCommand.UNKNOWN_USER);
        }
    }

    @Nested
    @DisplayName("Test command /help")
    class HelpCommandTest {
        private final Command help = commandMap.get("/help");
        private final String exceptedMessage = """
            Команды бота:
            /list - команда показать список отслеживаемых ссылок
            /start - зарегистрировать пользователя
            /track - начать отслеживание ссылки
            /untrack - прекратить отслеживание ссылки
            /help - вывести окно с командами
            """;

        @Test
        @DisplayName("Test that command help returned correct list command and descriptions")
        void testThatCommandHelpReturnedCorrectListCommandAndDescriptions() {
            mockBeanUpdateSetUp(1L);
            prepareTgChat(1L);

            var actualMessage = help.execute(update).block();

            assertThat(exceptedMessage).isEqualTo(actualMessage);
        }

        @Test
        @DisplayName("Test that the command returned the message to all chats")
        void testThatTheCommandReturnedTheMessageToAllChats() {
            mockBeanUpdateSetUp(1L);
            prepareTgChat(1L); //register user
            var actualMessageForRegisterUser = help.execute(update).block();

            mockBeanUpdateSetUp(2L); //unregister user
            var actualMessageForUnRegisterUser = help.execute(update).block();

            assertThat(exceptedMessage).isEqualTo(actualMessageForRegisterUser);
            assertThat(exceptedMessage).isEqualTo(actualMessageForUnRegisterUser);
        }
    }

    @Nested
    @DisplayName("Test command /list")
    class ListCommandTest {
        private final Command list = commandMap.get("/list");

        @Test
        @DisplayName(
            "Test that the command returned a excepted list sites message for a chat with an non-empty list of links")
        void testThatTheCommandReturnedAExceptedListSitesMessageForAChatWithAnNonEmptyListOfLinks() {
            var exceptedMessage = """
                Вы отслеживаете 1 сайтов
                https://github.com/sanyarnd/tinkoff-java-course-2023/
                """;
            long tgChatId = 1L;
            mockBeanUpdateSetUp(tgChatId);
            prepareTgChat(tgChatId);

            when(scrapperProxy.getListLinks(tgChatId))
                .thenReturn(
                    Mono.just(
                        new ListLinksResponse(
                            List.of(new LinkResponse(
                                1L,
                                URI.create("https://github.com/sanyarnd/tinkoff-java-course-2023/")
                            )),
                            1
                        )
                    )
                );

            var actualMessage = list.execute(update).block();
            assertThat(actualMessage).isEqualTo(exceptedMessage);
        }

        @Test
        @DisplayName("Test that the command returned correct message to an unregistered chat")
        void testThatTheCommandReturnedCorrectMessageToAnUnregisteredChat() {
            long tgChatId = 116L;
            var exceptedMessage = "Чат не зарегистрирован";
            mockBeanUpdateSetUp(tgChatId);

            when(scrapperProxy.getListLinks(tgChatId))
                .thenReturn(
                    Mono.error(
                        new ScrapperApiException(
                            ApiErrorResponse
                                .builder()
                                .description(CHAT_NOT_REGISTER_DESCRIPTION)
                                .code(UNAUTHORIZED_HTTP_CODE)
                                .exceptionName("NotExistTgChatException")
                                .exceptionMessage(CHAT_NOT_REGISTER_DESCRIPTION)
                                .stacktrace(List.of())
                                .build()
                        )
                    )
                );

            var actualMessage = list.execute(update).block();
            assertThat(actualMessage).isEqualTo(exceptedMessage);
        }

    }

    private void prepareTgChat(long id) {
        tgChatRepository.saveTgChat(new TgChat(id, SessionState.BASE_STATE));
    }

    private void mockBeanUpdateSetUp(long id_user) {
        Message messageMock = mock(Message.class);
        Chat chatMock = mock(Chat.class);
        when(update.message()).thenReturn(messageMock);
        when(messageMock.chat()).thenReturn(chatMock);
        when(chatMock.id()).thenReturn(id_user);
        when(update.message().chat().id()).thenReturn(id_user);
    }
}
