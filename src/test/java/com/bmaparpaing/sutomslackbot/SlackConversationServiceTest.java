package com.bmaparpaing.sutomslackbot;

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
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SlackConversationServiceTest {

    private static final String SLACK_PARTAGES_PATH = "slack-partages.txt";

    @Mock
    private SlackService slackService;

    @InjectMocks
    private SlackConversationService slackConversationService;

    @Test
    void readFromFile_withSlackPartagesTestFile_shouldReturnListSlackPartageWithJoueur()
        throws URISyntaxException, IOException {
        var resource = getClass().getClassLoader().getResource(SLACK_PARTAGES_PATH);
        var results = slackConversationService.readFromFilePath(
            Path.of(resource != null ? resource.toURI() : Path.of(SLACK_PARTAGES_PATH).toUri()));

        assertThat(results)
            .extracting(SlackPartage::joueur)
            .containsExactly(
                new Joueur("1", "Michel UN"),
                new Joueur("2", "Martin DEUX"),
                new Joueur("3", "Jean TROIS"),
                new Joueur("4", "Paul QUATRE"));
        assertThat(results)
            .extracting(SlackPartage::coup)
            .doesNotContain(0);
    }

    @Test
    void readFromFile_withNonExistentFile_shouldThrowIOException() {
        var path = Path.of("nonexistentfile");

        assertThrows(IOException.class, () -> slackConversationService.readFromFilePath(path));
    }

    @Test
    void readTodayConversationFromSlackApi_givenEmptyTodayConversation_shouldReturnEmptyList()
        throws SlackApiException, IOException {
        when(slackService.fetchTodayConversation()).thenReturn(Collections.emptyList());

        var result = slackConversationService.readTodayConversationFromSlackApi();

        assertThat(result).isEmpty();
    }

    @Test
    void readTodayConversationFromSlackApi_givenTodayConversation_shouldReturnSlackPartageList()
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
        when(slackService.fetchTodayConversation()).thenReturn(List.of(message1, message2));
        when(slackService.fetchUserInfo(message1.getUser())).thenReturn(userInfo1);
        when(slackService.fetchUserInfo(message2.getUser())).thenReturn(userInfo2);

        var result = slackConversationService.readTodayConversationFromSlackApi();

        assertThat(result).containsExactly(
            new SlackPartage(new Joueur(message1.getUser(), user1.getRealName()),
                new SlackPartageTexte(message1.getText()),
                Instant.parse("2022-07-01T00:00:00.00Z")),
            new SlackPartage(new Joueur(message2.getUser(), user2.getRealName()),
                new SlackPartageTexte(message2.getText()),
                Instant.parse("2022-07-02T00:00:00.00Z")));
    }

}
