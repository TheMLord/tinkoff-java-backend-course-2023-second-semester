package edu.java.scrapper;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.exceptions.AlreadyTrackLinkException;
import edu.java.exceptions.DoubleRegistrationException;
import edu.java.exceptions.NotExistLinkException;
import edu.java.exceptions.NotExistTgChatException;
import edu.java.exceptions.NotTrackLinkException;
import edu.java.repository.LinkDao;
import edu.java.repository.TgChatRepository;
import java.net.URI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestPropertySource(locations = "classpath:test")
@WireMockTest(httpPort = 8080)
public class JdbcRepositoryTest extends IntegrationTest {
    @Autowired TgChatRepository jdbcTgChatRepository;
    @Autowired LinkDao jdbcLinkDao;

    @Nested
    @DisplayName("Tests of the correct operation of telegram chat repository methods")
    class TgChatRepositoryTest {
        @Test
        @DisplayName("Test that the chat is being added successfully returned the chat with the correct id")
        @Transactional
        @Rollback
        void testThatTheChatIsBeingAddedSuccessfullyReturnedTheChatWithTheCorrectId() {
            var exceptedId = 1L;

            jdbcTgChatRepository.add(exceptedId);
            var actualChatEntity = jdbcTgChatRepository.findById(exceptedId);

            assertThat(actualChatEntity).isPresent();
            assertThat(actualChatEntity.get().getChatId()).isEqualTo(exceptedId);
        }

        @Test
        @DisplayName("Test that the chat cannot be registered twice and returned a correct error")
        @Transactional
        @Rollback
        void testThatTheChatCannotBeRegisteredTwiceAndReturnedACorrectError() {
            var existChat1 = 1L;

            jdbcTgChatRepository.add(existChat1);

            assertThatThrownBy(() -> jdbcTgChatRepository.add(existChat1))
                .isInstanceOf(DoubleRegistrationException.class);
        }

        @Test
        @DisplayName("Test that the user is being deleted successfully")
        @Transactional
        @Rollback
        void testThatTheUserIsBeingDeletedSuccessfully() {
            var idChatToDelete = 1L;

            jdbcTgChatRepository.add(idChatToDelete);
            assertThat(jdbcTgChatRepository.findById(idChatToDelete)).isPresent();

            jdbcTgChatRepository.remove(idChatToDelete);
            assertThat(jdbcTgChatRepository.findById(idChatToDelete)).isEmpty();
        }

        @Test
        @DisplayName("Test that it is impossible to delete a non-existent chat and returned the correct error")
        @Transactional
        @Rollback
        void testThatItIsImpossibleToDeleteANonExistentChatAndReturnedTheCorrectError() {
            var idChatToDelete = 1L;

            assertThat(jdbcTgChatRepository.findById(idChatToDelete)).isEmpty();

            assertThatThrownBy(() -> jdbcTgChatRepository.remove(idChatToDelete))
                .isInstanceOf(NotExistTgChatException.class);
        }
    }

    @Nested
    @DisplayName("Tests of the correct operation of link dao methods")
    class JdbcLinkDaoTests {
        private static final String GITHUB_ANSWER_BODY =
            """
                {
                    "owner": {
                        "login": "TheMLord",
                        "id": 113773994
                    },
                    "created_at": "2024-02-05T09:23:06Z",
                    "updated_at": "2024-02-05T09:25:22Z",
                    "pushed_at": "2024-02-13T13:34:39Z"
                }""";

        @Test
        @DisplayName(
            "Test that adding a new link to the chat creates a new link in the database and returned the correct number of them")
        @Transactional
        @Rollback
        void testThatAddingANewLinkToTheChatCreatesANewLinkInTheDatabaseAndReturnedTheCorrectNumberOfThem() {
            setUpServer("/repos/TheMLord/tinkoff-java-backend-course-2023-second-semester", GITHUB_ANSWER_BODY);
            var idChat = 1L;
            var exceptedLinkName =
                URI.create("https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester");

            jdbcTgChatRepository.add(idChat);
            assertThat(jdbcLinkDao.getAllLinkInRelation(idChat)).isEmpty();

            var actualLinkName = jdbcLinkDao.add(idChat, exceptedLinkName);
            assertThat(actualLinkName.getLinkName()).isEqualTo(exceptedLinkName);
            assertThat(jdbcLinkDao.getAllLinkInRelation(idChat)).isNotEmpty();
            assertThat(jdbcLinkDao.getAllLinkInRelation(idChat).size()).isEqualTo(1);
            assertThat(jdbcLinkDao.getAllLinkInRelation(idChat).getFirst().getLinkName()).isEqualTo(exceptedLinkName);
        }

        @Test
        @DisplayName("Test that you can't add a link to a non-existent chat to tracking and returned a correct error")
        @Transactional
        @Rollback
        void testThatYouCanTAddALinkToANonExistentChatToTrackingAndReturnedACorrectError() {
            setUpServer("/repos/TheMLord/tinkoff-java-backend-course-2023-second-semester", GITHUB_ANSWER_BODY);
            var idChat = 1L;
            var exceptedLinkName =
                URI.create("https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester");

            assertThatThrownBy(() -> jdbcLinkDao.add(
                idChat,
                exceptedLinkName
            )).isInstanceOf(NotExistTgChatException.class);
        }

        @Test
        @DisplayName("Test that you can't add a link to the tracking chat twice and returned the correct error")
        @Transactional
        @Rollback
        void testThatYouCanTAddALinkToTheTrackingChatTwiceAndReturnedTheCorrectError() {
            setUpServer("/repos/TheMLord/tinkoff-java-backend-course-2023-second-semester", GITHUB_ANSWER_BODY);
            var idChat = 1L;
            var exceptedLinkName =
                URI.create("https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester");

            jdbcTgChatRepository.add(idChat);
            jdbcLinkDao.add(idChat, exceptedLinkName);

            assertThatThrownBy(() -> jdbcLinkDao.add(
                idChat,
                exceptedLinkName
            )).isInstanceOf(AlreadyTrackLinkException.class);
        }

        @Test
        @DisplayName(
            "Test that can't be removed from the tracking of a non-existent chat link and returned the correct error")
        @Transactional
        @Rollback
        void testThatCanTBeRemovedFromTheTrackingOfANonExistentChatLinkAndReturnedTheCorrectError() {
            setUpServer("/repos/TheMLord/tinkoff-java-backend-course-2023-second-semester", GITHUB_ANSWER_BODY);
            var idChat = 1L;
            var exceptedLinkName =
                URI.create("https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester");

            assertThatThrownBy(() -> jdbcLinkDao.remove(
                idChat,
                exceptedLinkName
            )).isInstanceOf(NotExistTgChatException.class);
        }

        @Test
        @DisplayName(
            "Test that it is impossible to remove from tracking a link that the chat does not track and returned the correct error")
        @Transactional
        @Rollback
        void testThatItIsImpossibleToRemoveFromTrackingALinkThatTheChatDoesNotTrackAndReturnedTheCorrectError() {
            setUpServer("/repos/TheMLord/tinkoff-java-backend-course-2023-second-semester", GITHUB_ANSWER_BODY);
            var idChat = 1L;
            var exceptedLinkName =
                URI.create("https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester");

            jdbcTgChatRepository.add(idChat);
            assertThat(jdbcLinkDao.getAllLinkInRelation(idChat)).isEmpty();

            /*
            Adding another user with a link so that the link entity appears in the database,
             otherwise there will be another error - NotExistLinkException
             */
            jdbcTgChatRepository.add(2L);
            jdbcLinkDao.add(2L, exceptedLinkName);

            assertThatThrownBy(() -> jdbcLinkDao.remove(
                idChat,
                exceptedLinkName
            )).isInstanceOf(NotTrackLinkException.class);
        }

        @Test
        @DisplayName(
            "Test that deleting a non-existent link in the user's database is impossible and returned the correct error")
        @Transactional
        @Rollback
        void testThatDeletingANonExistentLinkInTheUserSDatabaseIsImpossibleAndReturnedTheCorrectError() {
            setUpServer("/repos/TheMLord/tinkoff-java-backend-course-2023-second-semester", GITHUB_ANSWER_BODY);
            var idChat = 1L;
            var exceptedLinkName =
                URI.create("https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester");

            jdbcTgChatRepository.add(idChat);
            assertThat(jdbcLinkDao.getAllLinkInRelation(idChat)).isEmpty();

            assertThatThrownBy(() -> jdbcLinkDao.remove(
                idChat,
                exceptedLinkName
            )).isInstanceOf(NotExistLinkException.class);
        }

    }

    private void setUpServer(String url, String body) {
        stubFor(
            get(urlPathMatching(url))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        body
                    )));
    }
}
