package edu.java.scrapper.repository.jdbc;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.exceptions.AlreadyTrackLinkException;
import edu.java.exceptions.NotExistLinkException;
import edu.java.exceptions.NotExistTgChatException;
import edu.java.exceptions.NotTrackLinkException;
import edu.java.repository.LinkDao;
import edu.java.repository.LinkRepository;
import edu.java.repository.TgChatRepository;
import edu.java.schedulers.LinkUpdaterScheduler;
import edu.java.scrapper.IntegrationEnvironment;
import java.net.URI;
import org.apache.kafka.clients.admin.AdminClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
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
@DirtiesContext
@WireMockTest(httpPort = 8080)
@TestPropertySource(locations = "classpath:test")
@Sql(value = "classpath:sql/clearDB.sql",
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class JdbcLinkDaoTest extends IntegrationEnvironment {
    @MockBean KafkaAdmin kafkaAdmin;

    @MockBean AdminClient adminClient;

    @MockBean LinkUpdaterScheduler linkUpdaterScheduler;
    @Autowired TgChatRepository tgChatRepository;
    @Autowired LinkDao linkDao;
    @Autowired LinkRepository linkRepository;

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

        tgChatRepository.add(idChat).block();
        assertThat(linkDao.getAllLinkInRelation(idChat).block()).isEmpty();

        var actualLinkName = linkDao.add(idChat, exceptedLinkName).block();
        var actualLinkInDB = linkRepository.findLinkByName(exceptedLinkName).block();

        assertThat(actualLinkName.getLinkUri()).isEqualTo(exceptedLinkName.toString());
        assertThat(actualLinkInDB).isPresent();
        assertThat(actualLinkInDB.get().getLinkUri()).isEqualTo(exceptedLinkName.toString());
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

        assertThatThrownBy(() -> linkDao.add(
            idChat,
            exceptedLinkName
        ).block()).isInstanceOf(NotExistTgChatException.class);
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

        tgChatRepository.add(idChat).block();
        linkDao.add(idChat, exceptedLinkName).block();

        assertThat(linkRepository.findLinkByName(exceptedLinkName).block()).isPresent();

        assertThatThrownBy(() -> linkDao.add(
            idChat,
            exceptedLinkName
        ).block()).isInstanceOf(AlreadyTrackLinkException.class);
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

        assertThatThrownBy(() -> linkDao.remove(
            idChat,
            exceptedLinkName
        ).block()).isInstanceOf(NotExistTgChatException.class);
    }

    @Test
    @DisplayName(
        "Test that it is impossible to remove from tracking a link that the chat does not track and returned the correct error")
    @Transactional
    @Rollback
    void testThatItIsImpossibleToRemoveFromTrackingALinkThatTheChatDoesNotTrackAndReturnedTheCorrectError() {
        setUpServer();
        var idChat1 = 13L;
        var idChat2 = 14L;
        var exceptedLinkName =
            URI.create("https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester13");

        tgChatRepository.add(idChat1).block();
        tgChatRepository.add(idChat2).block();
        linkDao.add(idChat2, exceptedLinkName).block();

        assertThatThrownBy(() -> linkDao.remove(
            idChat1,
            exceptedLinkName
        ).block()).isInstanceOf(NotTrackLinkException.class);
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

        tgChatRepository.add(idChat).block();
        assertThat(linkDao.getAllLinkInRelation(idChat).block()).isEmpty();

        assertThat(linkRepository.findLinkByName(exceptedLinkName).block()).isEmpty();

        assertThatThrownBy(() -> linkDao.remove(
            idChat,
            exceptedLinkName
        ).block()).isInstanceOf(NotExistLinkException.class);
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
            URI.create("https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester19");

        tgChatRepository.add(idChat1).block();
        tgChatRepository.add(idChat2).block();
        linkDao.add(idChat1, exceptedLinkName1).block();
        linkDao.add(idChat1, exceptedLinkName2).block();
        linkDao.add(idChat2, exceptedLinkName1).block();

        var actualLink1 = linkRepository.findLinkByName(exceptedLinkName1).block();
        var actualLink2 = linkRepository.findLinkByName(exceptedLinkName2).block();

        assertThat(actualLink1).isPresent();
        assertThat(actualLink2).isPresent();

        assertThat(linkDao.findAllIdTgChatWhoTrackLink(actualLink1.get().getId()).block()).containsOnly(
            idChat1,
            idChat2
        );
        assertThat(linkDao.findAllIdTgChatWhoTrackLink(actualLink2.get().getId()).block()).containsOnly(idChat1);
    }

    private void setUpServer() {
        stubFor(
            get(urlPathMatching("/repos/TheMLord/tinkoff-java-backend-course-2023-second-semester([1-9][1-9]|[1-9])"))
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

        stubFor(
            get(urlPathMatching(
                "/repos/TheMLord/tinkoff-java-backend-course-2023-second-semester([1-9][1-9]|[1-9])/branches"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                            [
                                 {
                                     "name": "createdAccount",
                                     "commit": {
                                         "sha": "91c6ce32c18cd16baae811a6348ea37ca75a4cdb",
                                         "url": "https://api.github.com/repos/TheMLord/Interface_for_an_ATM_with_a_banking_system/commits/91c6ce32c18cd16baae811a6348ea37ca75a4cdb"
                                     },
                                     "protected": false
                                 },
                                 {
                                     "name": "main",
                                     "commit": {
                                         "sha": "0f9906316ed03aeae035ae851b9742a630b5b070",
                                         "url": "https://api.github.com/repos/TheMLord/Interface_for_an_ATM_with_a_banking_system/commits/0f9906316ed03aeae035ae851b9742a630b5b070"
                                     },
                                     "protected": false
                                 },
                                 {
                                     "name": "master",
                                     "commit": {
                                         "sha": "af16415266c8482501b840c46411cce4a9e1f775",
                                         "url": "https://api.github.com/repos/TheMLord/Interface_for_an_ATM_with_a_banking_system/commits/af16415266c8482501b840c46411cce4a9e1f775"
                                     },
                                     "protected": false
                                 }
                             ]"""
                    )));
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("app.database-access-type", () -> "jdbc");
    }
}
