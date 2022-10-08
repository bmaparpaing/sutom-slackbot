package com.bmaparpaing.sutomslackbot.sutom;

import com.bmaparpaing.sutomslackbot.config.SutomSlackbotProperties;
import com.bmaparpaing.sutomslackbot.model.Joueur;
import com.bmaparpaing.sutomslackbot.model.SutomPartage;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PodiumJourServiceTest {

    private final SutomSlackbotProperties sutomSlackbotProperties = new SutomSlackbotProperties();
    private final PodiumJourService podiumJourService = new PodiumJourService(sutomSlackbotProperties);

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
        var partageA = new SutomPartage(new Joueur("A", "Joueur A"),
            Instant.now(), 6, 15, 0, true);
        var partage3 = new SutomPartage(new Joueur("3", "Joueur 3"),
            Instant.now(), 2, 8, 1);
        var partage4 = new SutomPartage(new Joueur("4", "Joueur 4"),
            Instant.now(), 2, 7, 4);
        var partageB = new SutomPartage(new Joueur("B", "Joueur B"),
            Instant.now(), 6, 8, 5, true);
        var partage5 = new SutomPartage(new Joueur("5", "Joueur 5"),
            Instant.now(), 2, 8, 1);
        var partageC = new SutomPartage(new Joueur("C", "Joueur C"),
            Instant.now(), 6, 15, 0, true);
        var partageD = new SutomPartage(new Joueur("D", "Joueur D"),
            Instant.now(), 6, 8, 3, true);
        var partage6 = new SutomPartage(new Joueur("6", "Joueur 6"),
            Instant.now(), 6, 8, 3);

        var result = podiumJourService.sortSutomPartages(
            List.of(partage1, partage2, partageA, partage3, partage4, partageB, partage5, partageC, partageD,
                partage6));

        assertThat(result).containsExactly(partage2, partage4, partage3, partage5, partage1, partage6, partageA,
            partageC, partageB, partageD);
    }

    @Test
    void sortSutomPartagesGolfMode_givenEmptyList_shouldReturnEmptyList() {
        var result = podiumJourService.sortSutomPartagesGolf(Collections.emptyList());

        assertThat(result).isEmpty();
    }

    @Test
    void sortSutomPartagesGolfMode_givenJoueurList_shouldReturnSortedList() {
        var joueur1 = new Joueur("1", "Joueur 1");
        var partage1 = new SutomPartage(joueur1, Instant.now(), 3, 12, 4);
        var joueur2 = new Joueur("2", "Joueur 2");
        var partage2 = new SutomPartage(joueur2, Instant.now(), 1, 6, 0);
        var joueurA = new Joueur("A", "Joueur A");
        var partageA = new SutomPartage(joueurA, Instant.now(), 6, 15, 0, true);
        var joueur3 = new Joueur("3", "Joueur 3");
        var partage3 = new SutomPartage(joueur3, Instant.now(), 2, 8, 1);
        var joueur4 = new Joueur("4", "Joueur 4");
        var partage4 = new SutomPartage(joueur4, Instant.now(), 2, 7, 4);
        var joueurB = new Joueur("B", "Joueur B");
        var partageB = new SutomPartage(joueurB, Instant.now(), 6, 8, 5, true);
        var joueur5 = new Joueur("5", "Joueur 5");
        var partage5 = new SutomPartage(joueur5, Instant.now(), 2, 7, 3);
        var joueurC = new Joueur("C", "Joueur C");
        var partageC = new SutomPartage(joueurC, Instant.now(), 6, 15, 0, true);
        var joueurD = new Joueur("D", "Joueur D");
        var partageD = new SutomPartage(joueurD, Instant.now(), 6, 8, 3, true);
        var joueur6 = new Joueur("6", "Joueur 6");
        var partage6 = new SutomPartage(joueur6, Instant.now(), 6, 8, 3);

        var result = podiumJourService.sortSutomPartagesGolf(
            List.of(partage1, partage2, partageA, partage3, partage4, partageB, partage5, partageC, partageD,
                partage6));

        assertThat(result).containsExactly(Set.of(joueur2), Set.of(joueur4), Set.of(joueur3),
            Set.of(joueur5), Set.of(joueur1), Set.of(joueur6), Set.of(joueurA,
                joueurC), Set.of(joueurB), Set.of(joueurD));
    }

    @Test
    void podiumJourPrettyPrint_givenEmptyList_shouldReturnEmptyString() {
        var result = podiumJourService.podiumJourPrettyPrint(Collections.emptyList(), null);

        assertThat(result).isEmpty();
    }

    @Test
    void podiumJourPrettyPrint_givenListOfSutomPartagesAndInstant_shouldReturnPodiumPrettyPrintOfDay() {
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
        var zonedDateTime = Instant.parse("2022-07-07T23:00:00.00Z")
            .atZone(ZoneId.of(sutomSlackbotProperties.getTimeZone()));

        var result = podiumJourService.podiumJourPrettyPrint(
            List.of(partage1, partage2, partage3, partage4, partage5),
            zonedDateTime);

        assertThat(result).isEqualTo("""
            *SUTOM du vendredi 8 juillet*
                        
            :trophy: *Joueur 1*
            :second_place_medal: Joueur 2
            :third_place_medal: Joueur 3
                        
            4. Joueur 4  5. Joueur 5""");
    }

    @Test
    void podiumJourPrettyPrintGolf_givenEmptyList_shouldReturnEmptyString() {
        var result = podiumJourService.podiumJourPrettyPrintGolf(Collections.emptyList(), null);

        assertThat(result).isEmpty();
    }

    @Test
    void podiumJourPrettyPrintGolf_givenListOfSutomPartagesAndInstant_shouldReturnPodiumPrettyPrintGolfModeOfDay() {
        var joueur1 = new Joueur("1", "Joueur 1");
        var joueur2 = new Joueur("2", "Joueur 2");
        var joueur3 = new Joueur("3", "Joueur 3");
        var joueur4 = new Joueur("4", "Joueur 4");
        var joueur5 = new Joueur("5", "Joueur 5");
        var zonedDateTime = Instant.parse("2022-07-07T23:00:00.00Z")
            .atZone(ZoneId.of(sutomSlackbotProperties.getTimeZone()));

        var result = podiumJourService.podiumJourPrettyPrintGolf(
            List.of(Set.of(joueur1), Set.of(joueur2), Set.of(joueur3), Set.of(joueur4), Set.of(joueur5)),
            zonedDateTime);

        assertThat(result).isEqualTo("""
            *SUTOM du vendredi 8 juillet mode golf :golf:*
                        
            :trophy: *Joueur 1*
            :second_place_medal: Joueur 2
            :third_place_medal: Joueur 3
                        
            4. Joueur 4  5. Joueur 5""");
    }

}
