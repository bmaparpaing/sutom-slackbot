package com.bmaparpaing.sutomslackbot.sutom;

import com.bmaparpaing.sutomslackbot.config.SutomSlackbotProperties;
import com.bmaparpaing.sutomslackbot.model.GolfScore;
import com.bmaparpaing.sutomslackbot.model.Joueur;
import com.bmaparpaing.sutomslackbot.model.JoueurGolfScore;
import com.bmaparpaing.sutomslackbot.model.SutomPartage;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PodiumJourService {

    public static final String FULL_TEXT_LOCAL_DATE_PATTERN = "EEEE d LLLL";

    private final DateTimeFormatter fullTextLocalDateFormatter;

    public PodiumJourService(SutomSlackbotProperties sutomSlackbotProperties) {
        fullTextLocalDateFormatter = DateTimeFormatter.ofPattern(FULL_TEXT_LOCAL_DATE_PATTERN)
            .withLocale(Locale.forLanguageTag(sutomSlackbotProperties.getLocale()));
    }

    public List<SutomPartage> sortSutomPartages(List<SutomPartage> sutomPartages) {
        return Stream.concat(
            sutomPartages
                .stream()
                .filter(sutomPartage -> !sutomPartage.echec())
                .sorted(Comparator.comparingInt(SutomPartage::coup)
                    .thenComparingInt(SutomPartage::lettreCorrecte)
                    .thenComparingInt(SutomPartage::lettreMalPlacee)
                    .thenComparing(SutomPartage::timestamp)),
            sutomPartages
                .stream()
                .filter(SutomPartage::echec)
                .sorted(Comparator.comparingInt(SutomPartage::lettreCorrecte).reversed()
                    .thenComparing(Comparator.comparingInt(SutomPartage::lettreMalPlacee).reversed())
                    .thenComparing(SutomPartage::timestamp))
        ).toList();
    }

    public List<Set<Joueur>> sortSutomPartagesGolf(List<SutomPartage> sutomPartages) {
        Map<GolfScore, Set<Joueur>> joueursByScore = sutomPartages.stream()
            .map(JoueurGolfScore::new)
            .collect(Collectors.groupingBy(JoueurGolfScore::golfScore,
                Collectors.mapping(JoueurGolfScore::joueur, Collectors.toSet())));
        return joueursByScore.keySet().stream()
            .sorted(Comparator.comparingInt(GolfScore::coup)
                .thenComparing(Comparator.comparingInt(GolfScore::scoreLettre).reversed())
                .thenComparing(Comparator.comparingInt(GolfScore::subScoreLettre).reversed()))
            .map(joueursByScore::get)
            .toList();
    }

    public String podiumJourPrettyPrintGolf(List<Set<Joueur>> podiumJour, ZonedDateTime zonedDateTime) {
        return podiumJourPrettyPrint(podiumJour, zonedDateTime, true);
    }

    public String podiumJourPrettyPrint(List<SutomPartage> sutomPartages, ZonedDateTime zonedDateTime) {
        return podiumJourPrettyPrint(sutomPartages.stream().map(SutomPartage::joueur).map(Set::of).toList(),
            zonedDateTime, false);
    }

    private String podiumJourPrettyPrint(List<Set<Joueur>> podiumJour, ZonedDateTime zonedDateTime, boolean golf) {
        var sb = new StringBuilder();
        if (!podiumJour.isEmpty()) {
            sb.append("*SUTOM du ").append(fullTextLocalDateFormatter.format(zonedDateTime))
                .append(golf ? " mode golf :golf:" : "").append("*\n");
        }
        int i = 0;
        for (Set<Joueur> joueurs : podiumJour) {
            boolean linebreak = i == 3;
            for (Joueur joueur : joueurs) {
                switch (i) {
                    case 0 -> sb.append("\n:trophy: *").append(joueur.nom()).append("*");
                    case 1 -> sb.append("\n:second_place_medal: ").append(joueur.nom());
                    case 2 -> sb.append("\n:third_place_medal: ").append(joueur.nom());
                    default -> sb.append(linebreak ? "\n\n" : "  ").append(i + 1).append(". ").append(joueur.nom());
                }
                linebreak = false;
            }
            i += joueurs.size();
        }
        return sb.toString();
    }
}
