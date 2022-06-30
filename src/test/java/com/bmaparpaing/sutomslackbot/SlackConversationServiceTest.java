package com.bmaparpaing.sutomslackbot;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SlackConversationServiceTest {

    private static final String SLACK_PARTAGES_PATH = "slack-partages.txt";

    private final SlackConversationService slackConversationService = new SlackConversationService();

    @Test
    void readFromFile_withSlackPartagesTestFile_shouldReturnListSlackPartageWithJoueur()
        throws URISyntaxException, IOException {
        var resource = getClass().getClassLoader().getResource(SLACK_PARTAGES_PATH);
        var results = slackConversationService.readFromFilePath(
            Path.of(resource != null ? resource.toURI() : Path.of(SLACK_PARTAGES_PATH).toUri()));

        assertThat(results)
            .extracting(SlackPartage::getJoueur)
            .containsExactly(
                new Joueur(1L, "Michel UN"),
                new Joueur(2L, "Martin DEUX"),
                new Joueur(3L, "Jean TROIS"),
                new Joueur(4L, "Paul QUATRE"));
        assertThat(results)
            .extracting(SlackPartage::getCoup)
            .doesNotContain(0);
    }

    @Test
    void readFromFile_withNonExistentFile_shouldThrowIOException() {
        var path = Path.of("nonexistentfile");

        assertThrows(IOException.class, () -> slackConversationService.readFromFilePath(path));
    }


}