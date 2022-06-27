package com.bmaparpaing.sutomslackbot;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class PodiumJourServiceTest {

    private final PodiumJourService podiumJourService = new PodiumJourService();

    @Test
    void sortJoueurs_givenEmptyList_shouldReturnEmptyList() {
        var result = podiumJourService.sortJoueurs(Collections.emptyList());

        assertThat(result).isEmpty();
    }

    @Test
    void sortJoueurs_givenJoueurList_shouldReturnSortedList() {
        var joueur1 = Joueur.builder().id(1L).nom("Joueur 1").coup(3).lettreCorrecte(12).lettreMalPlacee(4)
                .build();
        var joueur2 = Joueur.builder().id(2L).nom("Joueur 2").coup(1).lettreCorrecte(6).lettreMalPlacee(0)
                .build();
        var joueur3 = Joueur.builder().id(3L).nom("Joueur 3").coup(2).lettreCorrecte(8).lettreMalPlacee(1)
                .build();
        var joueur4 = Joueur.builder().id(4L).nom("Joueur 4").coup(2).lettreCorrecte(7).lettreMalPlacee(4)
                .build();

        var result = podiumJourService.sortJoueurs(Arrays.asList(joueur1, joueur2, joueur3, joueur4));

        assertThat(result).containsExactly(joueur2, joueur4, joueur3, joueur1);
    }

}