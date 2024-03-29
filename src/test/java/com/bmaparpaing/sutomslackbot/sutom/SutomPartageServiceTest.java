package com.bmaparpaing.sutomslackbot.sutom;

import com.bmaparpaing.sutomslackbot.model.Joueur;
import com.bmaparpaing.sutomslackbot.model.SutomPartage;
import com.bmaparpaing.sutomslackbot.model.SutomPartageTexte;
import com.bmaparpaing.sutomslackbot.slack.SlackService;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.users.UsersInfoResponse;
import com.slack.api.model.Message;
import com.slack.api.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SutomPartageServiceTest {

    private static final String SUTOM_PARTAGES_PATH = "sutom-partages.txt";

    @Mock
    private SlackService slackService;

    @InjectMocks
    private SutomPartageService sutomPartageService;

    @Test
    void readFromFile_withSutomPartagesTestFile_shouldReturnListSutomPartageWithJoueur()
        throws URISyntaxException, IOException {
        var resource = getClass().getClassLoader().getResource(SUTOM_PARTAGES_PATH);
        var results = sutomPartageService.readFromFilePath(
            Path.of(resource != null ? resource.toURI() : Path.of(SUTOM_PARTAGES_PATH).toUri()));

        assertThat(results)
            .extracting(SutomPartage::joueur)
            .containsExactly(
                new Joueur("1", "Michel UN"),
                new Joueur("2", "Martin DEUX"),
                new Joueur("3", "Jean TROIS"),
                new Joueur("4", "Paul QUATRE"));
        assertThat(results)
            .extracting(SutomPartage::coup)
            .doesNotContain(0);
    }

    @Test
    void readFromFile_withNonExistentFile_shouldThrowIOException() {
        var path = Path.of("nonexistentfile");

        assertThrows(IOException.class, () -> sutomPartageService.readFromFilePath(path));
    }

    @Test
    void readConversationFromSlackApiOfDay_givenEmptyConversation_shouldReturnEmptyList()
        throws SlackApiException, IOException {
        var zonedDateTime = ZonedDateTime.now();
        when(slackService.fetchConversationOfDay(zonedDateTime)).thenReturn(Collections.emptyList());

        var result = sutomPartageService.readConversationFromSlackApiOfDay(zonedDateTime);

        assertThat(result).isEmpty();
    }

    @Test
    void readConversationFromSlackApiOfDay_givenConversation_shouldReturnSutomPartageList()
        throws SlackApiException, IOException {
        var message1 = new Message();
        message1.setUser("A1");
        message1.setTs("1656633600.000000");
        message1.setText("SUTOM #40 2/6");
        var message2 = new Message();
        message2.setUser("A2");
        message2.setTs("1656720000.000000");
        message2.setText("SUTOM #40 3/6");
        var userInfo1 = new UsersInfoResponse();
        var user1 = new User();
        user1.setRealName("Joueur UN");
        userInfo1.setUser(user1);
        var userInfo2 = new UsersInfoResponse();
        var user2 = new User();
        user2.setRealName("Joueur DEUX");
        userInfo2.setUser(user2);
        var zonedDateTime = ZonedDateTime.now();
        when(slackService.fetchConversationOfDay(zonedDateTime)).thenReturn(List.of(message1, message2));
        when(slackService.fetchUserInfo(message1.getUser())).thenReturn(userInfo1);
        when(slackService.fetchUserInfo(message2.getUser())).thenReturn(userInfo2);

        var result = sutomPartageService.readConversationFromSlackApiOfDay(zonedDateTime);

        assertThat(result).containsExactly(
            new SutomPartage(new Joueur(message1.getUser(), user1.getRealName()),
                Instant.parse("2022-07-01T00:00:00.00Z"),
                new SutomPartageTexte(message1.getText())),
            new SutomPartage(new Joueur(message2.getUser(), user2.getRealName()),
                Instant.parse("2022-07-02T00:00:00.00Z"),
                new SutomPartageTexte(message2.getText())));
    }
}
