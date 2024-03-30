import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.BotApplication;
import edu.java.bot.commands.StartCommand;
import edu.java.bot.commands.TrackCommand;
import edu.java.bot.commands.UnTrackCommand;
import edu.java.bot.domain.TgChat;
import edu.java.bot.exceptions.ScrapperApiException;
import edu.java.bot.models.SessionState;
import edu.java.bot.models.dto.api.request.AddLinkRequest;
import edu.java.bot.models.dto.api.request.RemoveLinkRequest;
import edu.java.bot.models.dto.api.response.ApiErrorResponse;
import edu.java.bot.models.dto.api.response.LinkResponse;
import edu.java.bot.proxy.ScrapperProxy;
import edu.java.bot.repository.TgChatRepository;
import edu.java.bot.service.MessageService;
import java.net.URI;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
class MessageServiceTest {
    @MockBean Update update;

    @Autowired MessageService messageService;

    @Autowired TgChatRepository tgChatRepository;
    @MockBean ScrapperProxy scrapperProxy;

    private static final String UNAUTHORIZED_HTTP_CODE = "401";
    private static final String FORBIDDEN_HTTP_CODE = "403";
    private static final String NOT_FOUND_HTTP_CODE = "404";
    private static final String NOT_ACCEPTABLE_HTTP_CODE = "406";
    private static final String PRECONDITION_FAILED_HTTP_CODE = "412";

    public static final String CHAT_ALREADY_REGISTER_DESCRIPTION = "Чат уже зарегистрирован";
    public static final String CHAT_NOT_REGISTER_DESCRIPTION = "Чат не зарегистрирован";

    private static final String LINK_ALREADY_TRACKED_DESCRIPTION = "Ссылка уже отслеживается";
    private static final String LINK_IS_NOT_TRACK_DESCRIPTION = "Ссылка не отслеживается чатом";
    private static final String LINK_NOT_FOUND_DESCRIPTION = "Несуществующая ссылка";

    private static final ApiErrorResponse exceptionDoubleRegistration = ApiErrorResponse.builder()
        .description(CHAT_ALREADY_REGISTER_DESCRIPTION)
        .code(NOT_ACCEPTABLE_HTTP_CODE)
        .exceptionName("DoubleRegistrationException")
        .exceptionMessage(CHAT_ALREADY_REGISTER_DESCRIPTION)
        .stacktrace(List.of())
        .build();
    private static final ApiErrorResponse exceptionNotExistChat = ApiErrorResponse.builder()
        .description(CHAT_NOT_REGISTER_DESCRIPTION)
        .code(UNAUTHORIZED_HTTP_CODE)
        .exceptionName("NotExistTgChatException")
        .exceptionMessage(CHAT_NOT_REGISTER_DESCRIPTION)
        .stacktrace(List.of())
        .build();
    private static final ApiErrorResponse exceptionAlreadyTrackLink = ApiErrorResponse.builder()
        .description(LINK_ALREADY_TRACKED_DESCRIPTION)
        .code(PRECONDITION_FAILED_HTTP_CODE)
        .exceptionName("AlreadyTrackLinkException")
        .exceptionMessage(LINK_ALREADY_TRACKED_DESCRIPTION)
        .stacktrace(List.of())
        .build();
    private static final ApiErrorResponse exceptionNotTrackLink = ApiErrorResponse.builder()
        .description(LINK_IS_NOT_TRACK_DESCRIPTION)
        .code(FORBIDDEN_HTTP_CODE)
        .exceptionName("NotExistLinkException")
        .exceptionMessage(LINK_IS_NOT_TRACK_DESCRIPTION)
        .stacktrace(List.of())
        .build();
    private static final ApiErrorResponse exceptionNotExistLink = ApiErrorResponse.builder()
        .description(LINK_NOT_FOUND_DESCRIPTION)
        .code(NOT_FOUND_HTTP_CODE)
        .exceptionName("NotExistLinkException")
        .exceptionMessage(LINK_NOT_FOUND_DESCRIPTION)
        .stacktrace(List.of())
        .build();

    private static Stream<Arguments> unregisterChatCommands() {
        return Stream.of(
            Arguments.of(77L, "привет", MessageService.DO_REGISTRATION_MESSAGE),
            Arguments.of(89L, "/list", "Чат не зарегистрирован"),
            Arguments.of(83L, "/track", TrackCommand.UNKNOWN_USER),
            Arguments.of(82L, "/untrack", UnTrackCommand.UNKNOWN_USER),
            Arguments.of(821L, "other message", MessageService.DO_REGISTRATION_MESSAGE)
        );
    }

    @ParameterizedTest
    @MethodSource("unregisterChatCommands")
    @DisplayName("Test that messages from an unregistered chat are handled correctly and returned the correct response")
    void testThatMessagesFromAnUnregisteredChatAreHandledCorrectlyAndReturnedTheCorrectResponse(
        long id,
        String text,
        String exceptedResponse
    ) {
        setUpMockUpdate(id, text);
        when(scrapperProxy.getListLinks(id))
            .thenReturn(Mono.error(new ScrapperApiException(exceptionNotExistChat)));

        var actualResponseService = messageService.prepareResponseMessage(update).block();
        assertThat(actualResponseService).isEqualTo(exceptedResponse);
    }

    @Test
    @DisplayName("Test that the service returns the expected response when registering a chat twice")
    void testThatTheServiceReturnsTheExpectedResponseWhenRegisteringAChatTwice() {
        var idTgChat = 3L;
        var text = "/start";
        var exceptedMessage = CHAT_ALREADY_REGISTER_DESCRIPTION;
        setUpMockUpdate(idTgChat, text);
        when(scrapperProxy.registerChat(idTgChat))
            .thenReturn(Mono.error(new ScrapperApiException(exceptionDoubleRegistration)));

        var actualResponseService = messageService.prepareResponseMessage(update).block();
        assertThat(actualResponseService).isEqualTo(exceptedMessage);
    }

    @Test
    @DisplayName(
        "Test that the service returns the expected response to an attempt to remove or add the link to an unregistered chat")
    void testThatTheServiceReturnsTheExpectedResponseToAnAttemptToRemoveOrAddTheLinkToAnUnregisteredChat() {
        var idTgChat = 3L;
        var text = "https://github.com/TheMLord/java-backend-course-2023-tinkoff";
        var exceptedMessage = MessageService.DO_REGISTRATION_MESSAGE;
        setUpMockUpdate(idTgChat, text);

        var actualResponseService = messageService.prepareResponseMessage(update).block();
        assertThat(actualResponseService).isEqualTo(exceptedMessage);
    }

    @Test
    @DisplayName(
        "Test that the service returns the expected response to an attempt to get all links to an unregistered chat")
    void testThatTheServiceReturnsTheExpectedResponseToAnAttemptToGetAllLinksToAnUnregisteredChat() {
        var idTgChat = 3L;
        var text = "/list";
        var exceptedMessage = CHAT_NOT_REGISTER_DESCRIPTION;
        setUpMockUpdate(idTgChat, text);
        when(scrapperProxy.getListLinks(idTgChat))
            .thenReturn(Mono.error(new ScrapperApiException(exceptionNotExistChat)));

        var actualResponseService = messageService.prepareResponseMessage(update).block();
        assertThat(actualResponseService).isEqualTo(exceptedMessage);
    }

    @Test
    @DisplayName("Test that the service returns the expected response when the user registers correctly")
    void testThatTheServiceReturnsTheExpectedResponseWhenTheUserRegistersCorrectly() {
        var idTgChat = 4L;
        var text = "/start";
        var exceptedMessage = StartCommand.REGISTRATION_MESSAGE_SUCCESS;
        setUpMockUpdate(idTgChat, text);
        when(scrapperProxy.registerChat(idTgChat))
            .thenReturn(Mono.empty());

        var actualResponseService = messageService.prepareResponseMessage(update).block();
        assertThat(actualResponseService).isEqualTo(exceptedMessage);
    }

    @Test
    @DisplayName("Test that the service returns the expected response if an already tracked link is added")
    void testThatTheServiceReturnsTheExpectedResponseIfAnAlreadyTrackedLinkIsAdded() {
        var idTgChat = 5L;
        var text = "https://github.com/TheMLord/java-backend-course-2023-tinkoff";
        var exceptedMessage = LINK_ALREADY_TRACKED_DESCRIPTION;
        setUpMockUpdate(idTgChat, text);
        prepareChatForTest(idTgChat, SessionState.WAIT_URI_FOR_TRACKING);

        when(scrapperProxy.addLink(new AddLinkRequest(URI.create(text)), idTgChat))
            .thenReturn(Mono.error(new ScrapperApiException(exceptionAlreadyTrackLink)));

        var actualResponseService = messageService.prepareResponseMessage(update).block();
        assertThat(actualResponseService).isEqualTo(exceptedMessage);
    }

    @Test
    @DisplayName("Test that the service returns the expected response if the link is successfully added to tracking")
    void testThatTheServiceReturnsTheExpectedResponseIfTheLinkIsSuccessfullyAddedToTracking() {
        var idTgChat = 6L;
        var text = "https://github.com/TheMLord/java-backend-course-2023-tinkoff";
        var exceptedMessage = MessageService.SUCCESS_TRACK_SITE_MESSAGE;
        setUpMockUpdate(idTgChat, text);
        prepareChatForTest(idTgChat, SessionState.WAIT_URI_FOR_TRACKING);
        when(scrapperProxy.addLink(new AddLinkRequest(URI.create(text)), idTgChat))
            .thenReturn(Mono.just(new LinkResponse(1L, URI.create(text))));

        var actualResponseService = messageService.prepareResponseMessage(update).block();
        assertThat(actualResponseService).isEqualTo(exceptedMessage);
    }

    @Test
    @DisplayName(
        "Test that the service returns the expected response in case of an attempt to delete a link that does not exist or is not tracked")
    void testThatTheServiceReturnsTheExpectedResponseInCaseOfAnAttemptToDeleteALinkThatDoesNotExistOrIsNotTracked() {
        var idTgChat = 6L;
        var text = "https://github.com/TheMLord/java-backend-course-2023-tinkoff";
        var exceptedMessage = LINK_IS_NOT_TRACK_DESCRIPTION;
        setUpMockUpdate(idTgChat, text);
        prepareChatForTest(idTgChat, SessionState.WAIT_URI_FOR_UNTRACKING);

        //situation where a link has not been added by anyone, but they are trying to delete it
        when(scrapperProxy.deleteLink(new RemoveLinkRequest(URI.create(text)), idTgChat))
            .thenReturn(Mono.error(new ScrapperApiException(exceptionNotExistLink)));

        var actualResponseService1 = messageService.prepareResponseMessage(update).block();
        assertThat(actualResponseService1).isEqualTo(exceptedMessage); // the chat should not know
        // whether there is a link in the database or not.
        // Information about the availability of the service link

        //situation where a link exists in the database, but it is not tracked by the chat
        when(scrapperProxy.deleteLink(new RemoveLinkRequest(URI.create(text)), idTgChat))
            .thenReturn(Mono.error(new ScrapperApiException(exceptionNotTrackLink)));
        var actualResponseService2 = messageService.prepareResponseMessage(update).block();
        assertThat(actualResponseService2).isEqualTo(exceptedMessage);
    }

    private void setUpMockUpdate(long id, String text) {
        Message messageMock = mock(Message.class);
        Chat chatMock = mock(Chat.class);

        when(update.message()).thenReturn(messageMock);
        when(messageMock.chat()).thenReturn(chatMock);

        when(chatMock.id()).thenReturn(id);
        when(update.message().chat().id()).thenReturn(id);

        when(messageMock.text()).thenReturn(text);
        when(update.message().text()).thenReturn(text);
    }

    private void prepareChatForTest(long id, SessionState state) {
        tgChatRepository.saveTgChat(new TgChat(id, state));
    }
}
