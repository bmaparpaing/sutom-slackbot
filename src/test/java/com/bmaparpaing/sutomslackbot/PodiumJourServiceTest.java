package com.bmaparpaing.sutomslackbot;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class PodiumJourServiceTest {

    private final PodiumJourService podiumJourService = new PodiumJourService();

    @Test
    void sortSlackPartages_givenEmptyList_shouldReturnEmptyList() {
        var result = podiumJourService.sortSlackPartages(Collections.emptyList());

        assertThat(result).isEmpty();
    }

    @Test
    void sortSlackPartages_givenJoueurList_shouldReturnSortedList() {
        var partage1 = new SlackPartage(new Joueur(1L, "Joueur 1"),
            Instant.now(), 3, 12, 4);
        var partage2 = new SlackPartage(new Joueur(2L, "Joueur 2"),
            Instant.now(), 1, 6, 0);
        var partage3 = new SlackPartage(new Joueur(3L, "Joueur 3"),
            Instant.now(), 2, 8, 1);
        var partage4 = new SlackPartage(new Joueur(4L, "Joueur 4"),
            Instant.now(), 2, 7, 4);
        var partage5 = new SlackPartage(new Joueur(5L, "Joueur 5"),
            Instant.now(), 2, 8, 1);

        var result = podiumJourService.sortSlackPartages(
            Arrays.asList(partage1, partage2, partage3, partage4, partage5));

        assertThat(result).containsExactly(partage2, partage4, partage3, partage5, partage1);
    }

}