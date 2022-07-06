package com.bmaparpaing.sutomslackbot;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

import static com.bmaparpaing.sutomslackbot.PodiumJourService.FULL_TEXT_DATE_FORMATTER;
import static org.assertj.core.api.Assertions.assertThat;

class PodiumJourServiceTest {

    private final PodiumJourService podiumJourService = new PodiumJourService();

    @Test
    void sortSutomPartages_givenEmptyList_shouldReturnEmptyList() {
        var result = podiumJourService.sortSutomPartages(Collections.emptyList());

        assertThat(result).isEmpty();
    }

    @Test
    void sortSutomPartages_givenJoueurList_shouldReturnSortedList() {
        var partage1 = new SutomPartage(new Joueur("1", "Joueur 1"),
            Instant.now(), 3, 12, 4);
        var partage2 = new SutomPartage(new Joueur("2", "Joueur 2"),
            Instant.now(), 1, 6, 0);
        var partage3 = new SutomPartage(new Joueur("3", "Joueur 3"),
            Instant.now(), 2, 8, 1);
        var partage4 = new SutomPartage(new Joueur("4", "Joueur 4"),
            Instant.now(), 2, 7, 4);
        var partage5 = new SutomPartage(new Joueur("5", "Joueur 5"),
            Instant.now(), 2, 8, 1);

        var result = podiumJourService.sortSutomPartages(
            Arrays.asList(partage1, partage2, partage3, partage4, partage5));

        assertThat(result).containsExactly(partage2, partage4, partage3, partage5, partage1);
    }

    @Test
    void podiumJourPrettyPrint_givenEmptyList_shouldReturnEmptyString() {
        var result = podiumJourService.podiumJourPrettyPrint(Collections.emptyList());

        assertThat(result).isEmpty();
    }

    @Test
    void podiumJourPrettyPrint_givenListOfSutomPartages_shouldReturnPodiumPrettyPrint() {
        var partage1 = new SutomPartage(new Joueur("1", "Joueur 1"),
            Instant.now(), 3, 12, 4);
        var partage2 = new SutomPartage(new Joueur("2", "Joueur 2"),
            Instant.now(), 1, 6, 0);
        var partage3 = new SutomPartage(new Joueur("3", "Joueur 3"),
            Instant.now(), 2, 8, 1);
        var partage4 = new SutomPartage(new Joueur("4", "Joueur 4"),
            Instant.now(), 2, 7, 4);
        var partage5 = new SutomPartage(new Joueur("5", "Joueur 5"),
            Instant.now(), 2, 8, 1);

        var result = podiumJourService.podiumJourPrettyPrint(
            Arrays.asList(partage1, partage2, partage3, partage4, partage5));

        assertThat(result).isEqualTo("""
            *SUTOM du %s*
                        
            :trophy: *Joueur 1*
            :second_place_medal: Joueur 2
            :third_place_medal: Joueur 3
                        
            4. Joueur 4  5. Joueur 5""".formatted(FULL_TEXT_DATE_FORMATTER.format(Instant.now())));
    }

}
