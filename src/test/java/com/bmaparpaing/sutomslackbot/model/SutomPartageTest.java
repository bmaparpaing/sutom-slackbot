package com.bmaparpaing.sutomslackbot.model;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class SutomPartageTest {

    private static final String SUTOM_PARTAGE_1_PATH = "sutom-partage-1.txt";
    private static final String SUTOM_PARTAGE_2_PATH = "sutom-partage-2.txt";
    private static final String SUTOM_PARTAGE_3_PATH = "sutom-partage-3.txt";

    @Test
    void newSutomPartage_withEmptyText_shouldCreateWithZeros() {
        var result = new SutomPartage(new Joueur("1", "Joueur1"), Instant.now(), new SutomPartageTexte(""));

        assertThat(result).extracting(
                SutomPartage::coup,
                SutomPartage::lettreCorrecte,
                SutomPartage::lettreMalPlacee,
                SutomPartage::echec)
            .containsExactly(0, 0, 0, false);
    }

    @Test
    void newSutomPartage_withSutomPartage1Text_shouldCreateCorrectly() throws IOException {
        String sutomPartage1;
        try (var resourceStream = getClass().getClassLoader().getResourceAsStream(SUTOM_PARTAGE_1_PATH)) {
            sutomPartage1 = resourceStream != null ? new String(resourceStream.readAllBytes()) : "";
        }

        var result = new SutomPartage(new Joueur("1", "Joueur1"), Instant.now(), new SutomPartageTexte(sutomPartage1));

        assertThat(result).extracting(
                SutomPartage::coup,
                SutomPartage::lettreCorrecte,
                SutomPartage::lettreMalPlacee,
                SutomPartage::echec)
            .containsExactly(3, 8, 4, false);
    }

    @Test
    void newSutomPartage_withSutomPartage2Text_shouldCreateCorrectly() throws IOException {
        String sutomPartage1;
        try (var resourceStream = getClass().getClassLoader().getResourceAsStream(SUTOM_PARTAGE_2_PATH)) {
            sutomPartage1 = resourceStream != null ? new String(resourceStream.readAllBytes()) : "";
        }

        var result = new SutomPartage(new Joueur("1", "Joueur1"), Instant.now(), new SutomPartageTexte(sutomPartage1));

        assertThat(result).extracting(
                SutomPartage::coup,
                SutomPartage::lettreCorrecte,
                SutomPartage::lettreMalPlacee,
                SutomPartage::echec)
            .containsExactly(6, 18, 8, false);
    }

    @Test
    void newSutomPartage_withSutomPartage3Text_shouldCreateCorrectly() throws IOException {
        String sutomPartage1;
        try (var resourceStream = getClass().getClassLoader().getResourceAsStream(SUTOM_PARTAGE_3_PATH)) {
            sutomPartage1 = resourceStream != null ? new String(resourceStream.readAllBytes()) : "";
        }

        var result = new SutomPartage(new Joueur("1", "Joueur1"), Instant.now(), new SutomPartageTexte(sutomPartage1));

        assertThat(result).extracting(
                SutomPartage::coup,
                SutomPartage::lettreCorrecte,
                SutomPartage::lettreMalPlacee,
                SutomPartage::echec)
            .containsExactly(6, 6, 5, true);
    }
}
