package com.bmaparpaing.sutomslackbot.sutom;

import com.bmaparpaing.sutomslackbot.config.SutomSlackbotProperties;
import com.bmaparpaing.sutomslackbot.model.GolfScore;
import com.bmaparpaing.sutomslackbot.model.Joueur;
import com.bmaparpaing.sutomslackbot.model.SutomPartage;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class PodiumSemaineServiceTest {

    private final SutomSlackbotProperties sutomSlackbotProperties = new SutomSlackbotProperties();
    private final PodiumSemaineService podiumSemaineService = new PodiumSemaineService(sutomSlackbotProperties);

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
        var sutomPartage1 = new SutomPartage(joueur1, now, 0, 0, 0);
        var sutomPartage2 = new SutomPartage(joueur2, now, 0, 0, 0);
        var sutomPartage3 = new SutomPartage(joueur3, now, 0, 0, 0);
        var podium1 = List.of(sutomPartage1, sutomPartage3);
        var podium2 = List.of(sutomPartage2, sutomPartage1, sutomPartage3);
        var podium3 = List.of(sutomPartage1, sutomPartage2);
        var podium4 = List.of(sutomPartage2);
        var result = podiumSemaineService.computeScoreSemaine(
            List.of(podium1, podium2, podium3, podium4, List.of()));

        assertThat(result).containsOnly(
            Map.entry(joueur1, new int[]{1, 2, 1, 2, 1}),
            Map.entry(joueur2, new int[]{3, 1, 2, 1, 1}),
            Map.entry(joueur3, new int[]{2, 3, 3, 2, 1})
        );
    }

    @Test
    void computeScoreSemaineGolf_givenEmptyList_shouldReturnEmptyMap() {
        var result = podiumSemaineService.computeScoreSemaineGolf(Collections.emptyList());

        assertThat(result).isEmpty();
    }

    @Test
    void computeScoreSemaineGolf_givenPodiumJourList_shouldReturnJoueurScoreMap() {
        var joueur1 = new Joueur("A1", "Joueur UN");
        var joueur2 = new Joueur("A2", "Joueur DEUX");
        var joueur3 = new Joueur("A3", "Joueur TROIS");
        var now = Instant.now();
        var sutomPartage1 = new SutomPartage(joueur1, now, 2, 8, 3);
        var sutomPartage2 = new SutomPartage(joueur2, now, 3, 10, 0);
        var sutomPartage3 = new SutomPartage(joueur3, now, 4, 13, 1);
        var sutomPartage3echec = new SutomPartage(joueur3, now, 6, 21, 10, true);
        var podium1 = List.of(sutomPartage1, sutomPartage3);
        var podium2 = List.of(sutomPartage2, sutomPartage1, sutomPartage3);
        var podium3 = List.of(sutomPartage1, sutomPartage2);
        var podium4 = List.of(sutomPartage2, sutomPartage3echec);
        var result = podiumSemaineService.computeScoreSemaineGolf(
            List.of(podium1, podium2, podium3, podium4, List.of()));

        assertThat(result).containsOnly(
            Map.entry(joueur1, new GolfScore(15, 57, 24)),
            Map.entry(joueur2, new GolfScore(15, 60, 30)),
            Map.entry(joueur3, new GolfScore(20, 106, 47))
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
    void sortScoreSemaineGolf_givenEmptyScoreSemaine_shouldReturnEmptyPodiumSemaine() {
        var result = podiumSemaineService.sortScoreSemaineGolf(Collections.emptyMap());

        assertThat(result).isEmpty();
    }

    @Test
    void sortScoreSemaineGolf_givenScoreSemaine_shouldReturnPodiumSemaine() {
        var joueur1 = new Joueur("A1", "Joueur UN");
        var joueur2 = new Joueur("A2", "Joueur DEUX");
        var joueur3 = new Joueur("A3", "Joueur TROIS");
        var scoreSemaine = Map.of(
            joueur1, new GolfScore(15, 57, 24),
            joueur2, new GolfScore(15, 60, 30),
            joueur3, new GolfScore(20, 106, 47));

        var result = podiumSemaineService.sortScoreSemaineGolf(scoreSemaine);

        assertThat(result).containsExactly(Set.of(joueur2), Set.of(joueur1), Set.of(joueur3));
    }

    @Test
    void sortScoreSemaineGolf_givenScoreSemaineWithTiedScore_shouldReturnPodiumSemaine() {
        var joueur1 = new Joueur("A1", "Joueur UN");
        var joueur2 = new Joueur("A2", "Joueur DEUX");
        var joueur3 = new Joueur("A3", "Joueur TROIS");
        var scoreSemaine = Map.of(
            joueur1, new GolfScore(15, 57, 24),
            joueur2, new GolfScore(15, 60, 30),
            joueur3, new GolfScore(15, 60, 30));

        var result = podiumSemaineService.sortScoreSemaineGolf(scoreSemaine);

        assertThat(result).containsExactly(Set.of(joueur2, joueur3), Set.of(joueur1));
    }

    @Test
    void podiumSemainePrettyPrint_givenEmptyList_shouldReturnEmptyString() {
        var result = podiumSemaineService.podiumSemainePrettyPrint(Collections.emptyList(), null, null);

        assertThat(result).isEmpty();
    }

    @Test
    void podiumSemainePrettyPrint_givenListOfJoueursSetWithNullDates_shouldReturnPodiumSemainePrettyPrint() {
        var joueur1 = new Joueur("A1", "Joueur UN");
        var joueur2 = new Joueur("A2", "Joueur DEUX");
        var joueur3 = new Joueur("A3", "Joueur TROIS");
        var joueur4 = new Joueur("A4", "Joueur QUATRE");
        var joueur5 = new Joueur("A5", "Joueur CINQ");

        var result = podiumSemaineService.podiumSemainePrettyPrint(
            List.of(Set.of(joueur1), Set.of(joueur2), Set.of(joueur3), Set.of(joueur4), Set.of(joueur5)), null, null);

        assertThat(result).isEqualTo("""
            *SUTOM classement semaine du ?-?*
                        
            :trophy: *Joueur UN*
            :second_place_medal: Joueur DEUX
            :third_place_medal: Joueur TROIS
                        
            4. Joueur QUATRE
            5. Joueur CINQ""");
    }

    @Test
    void podiumSemainePrettyPrint_givenJoueurWithEqualDates_shouldReturnPodiumSemainePrettyPrint() {
        var joueur1 = new Joueur("A1", "Joueur UN");
        var zonedNow = ZonedDateTime.parse("2007-12-03T10:15:30+01:00[Europe/Paris]");

        var result = podiumSemaineService.podiumSemainePrettyPrint(List.of(Set.of(joueur1)), zonedNow, zonedNow);

        assertThat(result).isEqualTo("""
            *SUTOM classement semaine du 03/12/07*
                        
            :trophy: *Joueur UN*""");
    }

    @Test
    void podiumSemainePrettyPrint_givenJoueurWithTwoDates_shouldReturnPodiumSemainePrettyPrint() {
        var joueur1 = new Joueur("A1", "Joueur UN");
        var firstZonedDate = ZonedDateTime.parse("2022-08-29T10:00:00+02:00[Europe/Paris]");
        var lastZonedDate = ZonedDateTime.parse("2022-09-02T10:00:00+02:00[Europe/Paris]");

        var result = podiumSemaineService.podiumSemainePrettyPrint(List.of(Set.of(joueur1)),
            firstZonedDate, lastZonedDate);

        assertThat(result).isEqualTo("""
            *SUTOM classement semaine du 29/08-02/09/22*
                        
            :trophy: *Joueur UN*""");
    }

    @Test
    void podiumSemainePrettyPrint_givenJoueurWithTwoDatesSpanningTwoYears_shouldReturnPodiumSemainePrettyPrint() {
        var joueur1 = new Joueur("A1", "Joueur UN");
        var firstZonedDate = ZonedDateTime.parse("2021-12-29T10:00:00+01:00[Europe/Paris]");
        var lastZonedDate = ZonedDateTime.parse("2022-01-02T10:00:00+01:00[Europe/Paris]");

        var result = podiumSemaineService.podiumSemainePrettyPrint(List.of(Set.of(joueur1)),
            firstZonedDate, lastZonedDate);

        assertThat(result).isEqualTo("""
            *SUTOM classement semaine du 29/12/21-02/01/22*
                        
            :trophy: *Joueur UN*""");
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
            new LinkedHashSet<>(List.of(joueur2, joueur3)),
            new LinkedHashSet<>(List.of(joueur4, joueur5))), null, null);

        assertThat(result).isEqualTo("""
            *SUTOM classement semaine du ?-?*
                        
            :trophy: *Joueur UN*
            :second_place_medal: Joueur DEUX
            :second_place_medal: Joueur TROIS
                        
            4. Joueur QUATRE
            4. Joueur CINQ""");
    }

    @Test
    void podiumSemainePrettyPrintGolf_givenEmptyList_shouldReturnEmptyString() {
        var result = podiumSemaineService.podiumSemainePrettyPrintGolf(Collections.emptyList(), null, null);

        assertThat(result).isEmpty();
    }

    @Test
    void podiumSemainePrettyPrintGolf_givenJoueurWithTwoDates_shouldReturnPodiumSemainePrettyPrintGolf() {
        var joueur1 = new Joueur("A1", "Joueur UN");
        var firstZonedDate = ZonedDateTime.parse("2022-08-29T10:00:00+02:00[Europe/Paris]");
        var lastZonedDate = ZonedDateTime.parse("2022-09-02T10:00:00+02:00[Europe/Paris]");

        var result = podiumSemaineService.podiumSemainePrettyPrintGolf(List.of(Set.of(joueur1)),
            firstZonedDate, lastZonedDate);

        assertThat(result).isEqualTo("""
            *SUTOM classement semaine du 29/08-02/09/22 mode golf :golf:*
                        
            :trophy: *Joueur UN*""");
    }

    @Test
    void scoreSemainePrettyPrint_givenEmptyScoreSemaine_shouldReturnEmptyString() {
        var result = podiumSemaineService.scoreSemainePrettyPrint(Collections.emptyMap(), Collections.emptyList());

        assertThat(result).isEmpty();
    }

    @Test
    void scoreSemainePrettyPrint_givenScoreSemaine_shouldReturnScoreSemainePrettyPrint() {
        var joueur1 = new Joueur("A1", "Joueur UN");
        var joueur2 = new Joueur("A2", "Joueur DEUX");
        var joueur3 = new Joueur("A3", "Joueur TROIS");
        var joueur4 = new Joueur("A4", "Joueur QUATRE");
        var joueur5 = new Joueur("A5", "Joueur CINQ");
        var scoreSemaine = Map.of(
            joueur1, new int[]{2, 5, 3, 2, 3},
            joueur2, new int[]{1, 4, 1, 2, 3},
            joueur3, new int[]{3, 3, 5, 2, 3},
            joueur4, new int[]{4, 2, 4, 2, 1},
            joueur5, new int[]{4, 1, 2, 1, 2});
        List<Set<Joueur>> podium = List.of(
            Set.of(joueur5),
            Set.of(joueur2),
            Set.of(joueur4),
            Set.of(joueur1),
            Set.of(joueur3));
        var result = podiumSemaineService.scoreSemainePrettyPrint(scoreSemaine, podium);
        assertThat(result).isEqualTo("""
            Joueur CINQ      4 1 2 1 2  10
            Joueur DEUX      1 4 1 2 3  11
            Joueur QUATRE    4 2 4 2 1  13
            Joueur UN        2 5 3 2 3  15
            Joueur TROIS     3 3 5 2 3  16
            """);
    }

    @Test
    void scoreSemainePrettyPrint_givenTiedScoreSemaine_shouldReturnScoreSemainePrettyPrint() {
        var joueur1 = new Joueur("A1", "Joueur UN");
        var joueur2 = new Joueur("A2", "Joueur DEUX");
        var scoreSemaine = Map.of(
            joueur1, new int[]{1, 2, 1, 2},
            joueur2, new int[]{2, 1, 2, 1});
        List<Set<Joueur>> podium = List.of(new LinkedHashSet<>(List.of(joueur1, joueur2)));
        var result = podiumSemaineService.scoreSemainePrettyPrint(scoreSemaine, podium);
        assertThat(result).isEqualTo("""
            Joueur UN      1 2 1 2  6
            Joueur DEUX    2 1 2 1  6
            """);
    }

}
