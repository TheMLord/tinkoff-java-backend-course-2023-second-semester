import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.BotApplication;
import edu.java.bot.model.commands.ListCommand;
import edu.java.bot.model.commands.TrackCommand;
import edu.java.bot.model.commands.UnTrackCommand;
import edu.java.bot.repository.UserRepository;
import edu.java.bot.service.MessageService;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {BotApplication.class})
class MessageServiceTest {
    @MockBean Update update;

    @Autowired MessageService messageService;

    @Autowired UserRepository userRepository;

    private static Stream<Arguments> unregisterUserCommands() {
        return Stream.of(
            Arguments.of(77L, "привет", MessageService.DO_REGISTRATION_MESSAGE),
            Arguments.of(89L, "/list", ListCommand.UNKNOWN_USER),
            Arguments.of(83L, "/track", TrackCommand.UNKNOWN_USER),
            Arguments.of(82L, "/untrack", UnTrackCommand.UNKNOWN_USER),
            Arguments.of(821L, "other message", MessageService.DO_REGISTRATION_MESSAGE)
        );
    }

    @ParameterizedTest
    @MethodSource("unregisterUserCommands")
    @DisplayName("Test that messages from an unregistered user are handled correctly and returned the correct response")
    void testThatMessagesFromAnUnregisteredUserAreHandledCorrectlyAndReturnedTheCorrectResponse(
        long id,
        String text,
        String exceptedResponse
    ) {
        setUpMockUpdate(id, text);

        var actualResponseService = messageService.prepareResponseMessage(update);
        assertThat(actualResponseService).isEqualTo(exceptedResponse);
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

}
