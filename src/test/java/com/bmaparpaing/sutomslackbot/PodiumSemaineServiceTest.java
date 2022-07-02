package com.bmaparpaing.sutomslackbot;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class PodiumSemaineServiceTest {

    private final PodiumSemaineService podiumSemaineService = new PodiumSemaineService();

    @Test
    void computeScoreSemaine_givenEmptyList_shouldReturnEmptyMap() {
        var result = podiumSemaineService.computeScoreSemaine(Collections.emptyList());

        assertThat(result).isEmpty();
    }

    @Test
    void computeScoreSemaine_givenPodiumJourList_shouldReturnJoueurScoreMap() {
        var joueur1 = new Joueur("A1", "Joueur UN");
        var joueur2 = new Joueur("A2", "Joueur DEUX");
        var joueur3 = new Joueur("A3", "Joueur TROIS");
        var now = Instant.now();
        var slackPartage1 = new SlackPartage(joueur1, now, 0, 0, 0);
        var slackPartage2 = new SlackPartage(joueur2, now, 0, 0, 0);
        var slackPartage3 = new SlackPartage(joueur3, now, 0, 0, 0);
        var podium1 = List.of(slackPartage1, slackPartage3);
        var podium2 = List.of(slackPartage2, slackPartage1, slackPartage3);
        var podium3 = List.of(slackPartage1, slackPartage2);
        var podium4 = List.of(slackPartage2);
        var result = podiumSemaineService.computeScoreSemaine(List.of(podium1, podium2, podium3, podium4, List.of()));

        assertThat(result).containsOnly(
            Map.entry(joueur1, new int[]{1, 2, 1, 2, 1}),
            Map.entry(joueur2, new int[]{3, 1, 2, 1, 1}),
            Map.entry(joueur3, new int[]{2, 3, 3, 2, 1})
        );
    }

    @Test
    void sortScoreSemaine_givenEmptyScoreSemaine_shouldReturnEmptyPodiumSemaine() {
        var result = podiumSemaineService.sortScoreSemaine(Collections.emptyMap());

        assertThat(result).isEmpty();
    }

    @Test
    void sortScoreSemaine_givenScoreSemaine_shouldReturnPodiumSemaine() {
        var joueur1 = new Joueur("A1", "Joueur UN");
        var joueur2 = new Joueur("A2", "Joueur DEUX");
        var joueur3 = new Joueur("A3", "Joueur TROIS");
        var scoreSemaine = Map.of(
            joueur1, new int[]{2, 3, 3, 2, 1},
            joueur2, new int[]{1, 2, 1, 2, 1},
            joueur3, new int[]{3, 1, 2, 1, 1});

        var result = podiumSemaineService.sortScoreSemaine(scoreSemaine);

        assertThat(result).containsExactly(Set.of(joueur2), Set.of(joueur3), Set.of(joueur1));
    }

    @Test
    void sortScoreSemaine_givenScoreSemaineWithTiedScore_shouldReturnPodiumSemaine() {
        var joueur1 = new Joueur("A1", "Joueur UN");
        var joueur2 = new Joueur("A2", "Joueur DEUX");
        var joueur3 = new Joueur("A3", "Joueur TROIS");
        var scoreSemaine = Map.of(
            joueur1, new int[]{2, 3, 3, 2, 2},
            joueur2, new int[]{1, 2, 1, 2, 2},
            joueur3, new int[]{3, 1, 2, 1, 1});

        var result = podiumSemaineService.sortScoreSemaine(scoreSemaine);

        assertThat(result).containsExactly(Set.of(joueur2, joueur3), Set.of(joueur1));
    }

    @Test
    void podiumSemainePrettyPrint_givenEmptyList_shouldReturnEmptyString() {
        var result = podiumSemaineService.podiumSemainePrettyPrint(Collections.emptyList());

        assertThat(result).isEmpty();
    }

    @Test
    void podiumSemainePrettyPrint_givenListOfJoueursSet_shouldReturnPodiumSemainePrettyPrint() {
        var joueur1 = new Joueur("A1", "Joueur UN");
        var joueur2 = new Joueur("A2", "Joueur DEUX");
        var joueur3 = new Joueur("A3", "Joueur TROIS");
        var joueur4 = new Joueur("A4", "Joueur QUATRE");
        var joueur5 = new Joueur("A5", "Joueur CINQ");

        var result = podiumSemaineService.podiumSemainePrettyPrint(
            List.of(Set.of(joueur1), Set.of(joueur2), Set.of(joueur3), Set.of(joueur4), Set.of(joueur5)));

        assertThat(result).isEqualTo("""
            SUTOM
                        
            :trophy: *Joueur UN*
            :second_place_medal: Joueur DEUX
            :third_place_medal: Joueur TROIS
            4. Joueur QUATRE
            5. Joueur CINQ""");
    }

    @Test
    void podiumSemainePrettyPrint_givenListOfJoueursSetWithTiedScore_shouldReturnPodiumSemainePrettyPrint() {
        var joueur1 = new Joueur("A1", "Joueur UN");
        var joueur2 = new Joueur("A2", "Joueur DEUX");
        var joueur3 = new Joueur("A3", "Joueur TROIS");
        var joueur4 = new Joueur("A4", "Joueur QUATRE");
        var joueur5 = new Joueur("A5", "Joueur CINQ");

        var result = podiumSemaineService.podiumSemainePrettyPrint(List.of(
            Set.of(joueur1),
            new LinkedHashSet<>(Set.of(joueur2, joueur3)),
            new LinkedHashSet<>(Set.of(joueur4, joueur5))));

        assertThat(result).isEqualTo("""
            SUTOM
                        
            :trophy: *Joueur UN*
            :second_place_medal: Joueur DEUX
            :second_place_medal: Joueur TROIS
            4. Joueur QUATRE
            4. Joueur CINQ""");
    }

}
