package edu.java.scrapper.repository;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.exceptions.AlreadyTrackLinkException;
import edu.java.exceptions.NotExistLinkException;
import edu.java.exceptions.NotExistTgChatException;
import edu.java.exceptions.NotTrackLinkException;
import edu.java.repository.LinkDao;
import edu.java.repository.LinkRepository;
import edu.java.repository.TgChatRepository;
import edu.java.scrapper.IntegrationTest;
import java.net.URI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@WireMockTest(httpPort = 8080)
@TestPropertySource(locations = "classpath:test")
@Sql(value = "classpath:sql/clearDB.sql",
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class JdbcLinkDaoTest extends IntegrationTest {
    @Autowired TgChatRepository jdbcTgChatRepository;
    @Autowired LinkDao jdbcLinkDao;
    @Autowired LinkRepository jdbcLinkRepository;

    @Test
    @DisplayName(
        "Test that adding a new link to the chat creates a new link in the database and returned the correct number of them")
    @Transactional
    @Rollback
    void testThatAddingANewLinkToTheChatCreatesANewLinkInTheDatabaseAndReturnedTheCorrectNumberOfThem() {
        setUpServer();
        var idChat = 11L;
        var exceptedLinkName =
            URI.create("https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester11");

        jdbcTgChatRepository.add(idChat);
        assertThat(jdbcLinkDao.getAllLinkInRelation(idChat)).isEmpty();

        var actualLinkName = jdbcLinkDao.add(idChat, exceptedLinkName);
        var actualLinkInDB = jdbcLinkRepository.findLinkByName(exceptedLinkName);

        assertThat(actualLinkName.getLinkName()).isEqualTo(exceptedLinkName);
        assertThat(actualLinkInDB).isPresent();
        assertThat(actualLinkInDB.get().getLinkName()).isEqualTo(exceptedLinkName);
    }

    @Test
    @DisplayName("Test that you can't add a link to a non-existent chat to tracking and returned a correct error")
    @Transactional
    @Rollback
    void testThatYouCanTAddALinkToANonExistentChatToTrackingAndReturnedACorrectError() {
        setUpServer();
        var idChat = 12L;
        var exceptedLinkName =
            URI.create("https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester12");

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
        setUpServer();
        var idChat = 13L;
        var exceptedLinkName =
            URI.create("https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester13");

        jdbcTgChatRepository.add(idChat);
        jdbcLinkDao.add(idChat, exceptedLinkName);

        assertThat(jdbcLinkRepository.findLinkByName(exceptedLinkName)).isPresent();

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
        setUpServer();
        var idChat = 14L;
        var exceptedLinkName =
            URI.create("https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester14");

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
        setUpServer();
        var idChat = 15L;
        var exceptedLinkName =
            URI.create("https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester15");

        jdbcTgChatRepository.add(idChat);
        assertThat(jdbcLinkDao.getAllLinkInRelation(idChat)).isEmpty();

            /*
            Adding another user with a link so that the link entity appears in the database,
             otherwise there will be another error - NotExistLinkException
             */
        jdbcTgChatRepository.add(16L);
        jdbcLinkDao.add(16L, exceptedLinkName);

        assertThat(jdbcLinkRepository.findLinkByName(exceptedLinkName)).isPresent();

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
        setUpServer();
        var idChat = 17L;
        var exceptedLinkName =
            URI.create("https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester17");

        jdbcTgChatRepository.add(idChat);
        assertThat(jdbcLinkDao.getAllLinkInRelation(idChat)).isEmpty();

        assertThat(jdbcLinkRepository.findLinkByName(exceptedLinkName)).isEmpty();

        assertThatThrownBy(() -> jdbcLinkDao.remove(
            idChat,
            exceptedLinkName
        )).isInstanceOf(NotExistLinkException.class);
    }

    @Test
    @DisplayName(
        "Test that the dao correctly makes a request to search for all monitored chat links and returned the correct list")
    @Transactional
    @Rollback
    void testThatTheDaoCorrectlyMakesARequestToSearchForAllMonitoredChatLinksAndReturnedTheCorrectList() {
        setUpServer();
        var idChat1 = 18L;
        var idChat2 = 19L;
        var exceptedLinkName1 =
            URI.create("https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester18");
        var exceptedLinkName2 =
            URI.create("https://github.com/TheMLord/java-backend-course-2023-tinkoff19");

        jdbcTgChatRepository.add(idChat1);
        jdbcTgChatRepository.add(idChat2);
        jdbcLinkDao.add(idChat1, exceptedLinkName1);
        jdbcLinkDao.add(idChat1, exceptedLinkName2);
        jdbcLinkDao.add(idChat2, exceptedLinkName1);

        var actualLink1 = jdbcLinkRepository.findLinkByName(exceptedLinkName1);
        var actualLink2 = jdbcLinkRepository.findLinkByName(exceptedLinkName2);

        assertThat(actualLink1).isPresent();
        assertThat(actualLink2).isPresent();

        assertThat(jdbcLinkDao.findAllIdTgChatWhoTrackLink(actualLink1.get().getId())).containsOnly(
            idChat1,
            idChat2
        );
        assertThat(jdbcLinkDao.findAllIdTgChatWhoTrackLink(actualLink2.get().getId())).containsOnly(idChat1);
    }

    private void setUpServer() {
        stubFor(
            get(urlPathMatching("/repos/.*"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                            {
                                "owner": {
                                    "login": "TheMLord",
                                    "id": 113773994
                                },
                                "created_at": "2024-02-05T09:23:06Z",
                                "updated_at": "2024-02-05T09:25:22Z",
                                "pushed_at": "2024-02-13T13:34:39Z"
                            }"""
                    )));
    }
}
