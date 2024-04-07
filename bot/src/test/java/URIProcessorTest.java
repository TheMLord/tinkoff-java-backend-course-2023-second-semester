import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.BotApplication;
import edu.java.bot.processor.UrlProcessor;
import java.net.URI;
import java.util.stream.Stream;
import org.apache.kafka.clients.admin.AdminClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {BotApplication.class})
@DirtiesContext
public class URIProcessorTest {
    @MockBean AdminClient adminClient;
    @MockBean TelegramBot telegramBot;
    @Autowired
    private UrlProcessor urlProcessor;

    private static Stream<Arguments> validSites() {
        return Stream.of(
            Arguments.of(URI.create("https://github.com/sanyarnd/tinkoff-java-course-2023/")),
            Arguments.of(URI.create("https://github.com/TheMLord/java-backend-course-2023-tinkoff")),
            Arguments.of(URI.create("https://stackoverflow.com/questions/1642028/what-is-the-operator-in-c")),
            Arguments.of(URI.create("https://stackoverflow.com/search?q=unsupported%20link"))
        );
    }

    private static Stream<Arguments> invalidSites() {
        return Stream.of(
            Arguments.of(URI.create("https://habr.com/ru/companies/piter/articles/676394/")),
            Arguments.of(URI.create("https://javarush.com/groups/posts/2753-biblioteka-lombok")),
            Arguments.of(URI.create("https://www.ozon.ru/")),
            Arguments.of(URI.create("https://istudent.urfu.ru/"))
        );
    }

    @ParameterizedTest
    @MethodSource("validSites")
    @DisplayName("Test that valid links are being processed correctly returned true")
    void testThatValidLinksAreBeingProcessedCorrectlyReturnedTrue(URI uri) {
        var actualResultProcessor = urlProcessor.isValidUrl(uri);
        assertThat(actualResultProcessor).isTrue();
    }

    @ParameterizedTest
    @MethodSource("invalidSites")
    @DisplayName("Test that invalid links are being processed correctly returned false")
    void testThatInvalidLinksAreBeingProcessedCorrectlyReturnedFalse(URI uri) {
        var actualResultProcessor = urlProcessor.isValidUrl(uri);
        assertThat(actualResultProcessor).isFalse();
    }
}
