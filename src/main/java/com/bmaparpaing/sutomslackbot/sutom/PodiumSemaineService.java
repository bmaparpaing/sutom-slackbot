package com.bmaparpaing.sutomslackbot.sutom;

import com.bmaparpaing.sutomslackbot.config.SutomSlackbotProperties;
import com.bmaparpaing.sutomslackbot.model.Joueur;
import com.bmaparpaing.sutomslackbot.model.SutomPartage;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PodiumSemaineService {

    public static final String DAY_MONTH_DATE_PATTERN = "dd/MM";
    public static final String DAY_MONTH_YEAR_DATE_PATTERN = "dd/MM/yy";

    private final DateTimeFormatter dayMonthDateFormatter;

    private final DateTimeFormatter dayMonthYearDateFormatter;

    public PodiumSemaineService(SutomSlackbotProperties sutomSlackbotProperties) {
        dayMonthDateFormatter = DateTimeFormatter.ofPattern(DAY_MONTH_DATE_PATTERN)
            .withLocale(Locale.forLanguageTag(sutomSlackbotProperties.getLocale()));
        dayMonthYearDateFormatter = DateTimeFormatter.ofPattern(DAY_MONTH_YEAR_DATE_PATTERN)
            .withLocale(Locale.forLanguageTag(sutomSlackbotProperties.getLocale()));
    }

    public Map<Joueur, int[]> computeScoreSemaine(List<List<SutomPartage>> podiumJours) {
        Integer[] nombreJoueurs = podiumJours.stream().map(List::size).toArray(Integer[]::new);
        var scoreSemaine = new HashMap<Joueur, int[]>();
        for (int i = 0; i < podiumJours.size(); i++) {
            var podiumJour = podiumJours.get(i);
            for (int j = 0; j < podiumJour.size(); j++) {
                scoreSemaine.putIfAbsent(podiumJour.get(j).joueur(), new int[podiumJours.size()]);
                scoreSemaine.get(podiumJour.get(j).joueur())[i] = j + 1;
            }
        }
        for (int[] scores : scoreSemaine.values()) {
            for (int i = 0; i < podiumJours.size(); i++) {
                if (scores[i] == 0) {
                    scores[i] = nombreJoueurs[i] + 1;
                }
            }
        }
        return scoreSemaine;
    }

    public List<Set<Joueur>> sortScoreSemaine(Map<Joueur, int[]> scoreSemaine) {
        Map<Integer, Set<Joueur>> joueursByScore = scoreSemaine.entrySet().stream()
            .collect(Collectors.groupingBy(
                joueurEntry -> Arrays.stream(joueurEntry.getValue()).sum(),
                Collectors.mapping(Map.Entry::getKey, Collectors.toSet())));
        return joueursByScore.keySet().stream()
            .sorted()
            .map(joueursByScore::get)
            .toList();
    }

    public String podiumSemainePrettyPrint(
        List<Set<Joueur>> podiumSemaine,
        ZonedDateTime firstDay,
        ZonedDateTime lastDay
    ) {
        var sb = new StringBuilder();
        if (!podiumSemaine.isEmpty()) {
            sb.append("*SUTOM classement semaine du ").append(formatWeekDateRange(firstDay, lastDay)).append("*\n");
        }
        int i = 0;
        for (Set<Joueur> joueurs : podiumSemaine) {
            for (Joueur joueur : joueurs) {
                switch (i) {
                    case 0 -> sb.append("\n:trophy: *").append(joueur.nom()).append("*");
                    case 1 -> sb.append("\n:second_place_medal: ").append(joueur.nom());
                    case 2 -> sb.append("\n:third_place_medal: ").append(joueur.nom());
                    default -> sb.append("\n").append(i + 1).append(". ").append(joueur.nom());
                }
            }
            i += joueurs.size();
        }
        return sb.toString();
    }

    private String formatWeekDateRange(ZonedDateTime firstDay, ZonedDateTime lastDay) {
        if (firstDay == null || lastDay == null) {
            return "?-?";
        }
        if (firstDay.truncatedTo(ChronoUnit.DAYS).equals(lastDay.truncatedTo(ChronoUnit.DAYS))) {
            return dayMonthYearDateFormatter.format(firstDay);
        }
        var sb = new StringBuilder();
        if (firstDay.getYear() == lastDay.getYear()) {
            sb.append(dayMonthDateFormatter.format(firstDay));
        } else {
            sb.append(dayMonthYearDateFormatter.format(firstDay));
        }
        sb.append("-");
        sb.append(dayMonthYearDateFormatter.format(lastDay));
        return sb.toString();
    }
}
