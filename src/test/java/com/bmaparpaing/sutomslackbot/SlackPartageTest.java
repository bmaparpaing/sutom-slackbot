package com.bmaparpaing.sutomslackbot;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class SlackPartageTest {

    private static final String SLACK_PARTAGE_1_PATH = "slack-partage-1.txt";
    private static final String SLACK_PARTAGE_2_PATH = "slack-partage-2.txt";

    @Test
    void newSlackPartage_withEmptyText_shouldCreateWithZeros() {
        var result = new SlackPartage(new Joueur("1", "Joueur1"), new SlackPartageTexte(""), Instant.now());

        assertThat(result).extracting(
                SlackPartage::coup,
                SlackPartage::lettreCorrecte,
                SlackPartage::lettreMalPlacee)
            .containsExactly(0, 0, 0);
    }

    @Test
    void newSlackPartage_withSlackPartage1_shouldCreateCorrectly() throws IOException {
        var resourceStream = getClass().getClassLoader().getResourceAsStream(SLACK_PARTAGE_1_PATH);
        String slackPartage1 = resourceStream != null ? new String(resourceStream.readAllBytes()) : "";

        var result = new SlackPartage(new Joueur("1", "Joueur1"), new SlackPartageTexte(slackPartage1), Instant.now());

        assertThat(result).extracting(
                SlackPartage::coup,
                SlackPartage::lettreCorrecte,
                SlackPartage::lettreMalPlacee)
            .containsExactly(3, 8, 4);
    }

    @Test
    void newSlackPartage_withSlackPartage2_shouldCreateCorrectly() throws IOException {
        var resourceStream = getClass().getClassLoader().getResourceAsStream(SLACK_PARTAGE_2_PATH);
        String slackPartage1 = resourceStream != null ? new String(resourceStream.readAllBytes()) : "";

        var result = new SlackPartage(new Joueur("1", "Joueur1"), new SlackPartageTexte(slackPartage1), Instant.now());

        assertThat(result).extracting(
                SlackPartage::coup,
                SlackPartage::lettreCorrecte,
                SlackPartage::lettreMalPlacee)
            .containsExactly(6, 18, 8);
    }
}