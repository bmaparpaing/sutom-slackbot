package com.bmaparpaing.sutomslackbot.sutom;

import com.bmaparpaing.sutomslackbot.config.SutomSlackbotProperties;
import com.bmaparpaing.sutomslackbot.model.GolfScore;
import com.bmaparpaing.sutomslackbot.model.Joueur;
import com.bmaparpaing.sutomslackbot.model.JoueurGolfScore;
import com.bmaparpaing.sutomslackbot.model.SutomPartage;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    /**
     * Calcule le score semaine de chaque joueur participant au moins une fois sur l'ensemble des podiums jour fournis.
     * Exemple de résultat obtenu une fois le calcul effectué :
     * <pre>
     *                 L M M J V
     *     Joueur 1    1 2 1 3 1
     *     Joueur 2    2 1 3 2 3
     *     Joueur 3    3 3 2 1 2
     * </pre>
     * Chaque numéro correspond à la place du joueur dans le podium jour.
     *
     * @param podiumJours Une liste de podium jours, un pour chaque jour de la semaine. Un podium jour correspond à une
     *                    liste de partage sutom triés selon le classement jour.
     * @return Une Map associant à chaque joueur un tableau représentant le score du joueur sur chaque journée.
     */
    public Map<Joueur, int[]> computeScoreSemaine(List<List<SutomPartage>> podiumJours) {
        Integer[] nombreJoueursParJour = podiumJours.stream().map(List::size).toArray(Integer[]::new);
        var scoreSemaine = new HashMap<Joueur, int[]>();
        for (int i = 0; i < podiumJours.size(); i++) {
            var podiumJour = podiumJours.get(i);
            for (int j = 0; j < podiumJour.size(); j++) {
                scoreSemaine.putIfAbsent(podiumJour.get(j).joueur(), new int[podiumJours.size()]);
                // Le joueur reçoit le score de sa position (= l'index j) dans le classement : 1 pour premier, etc.
                scoreSemaine.get(podiumJour.get(j).joueur())[i] = j + 1;
            }
        }
        // Pour chaque jour manqué (score à 0), le joueur reçoit automatiquement le score de la dernière place
        for (int[] scores : scoreSemaine.values()) {
            for (int i = 0; i < scores.length; i++) {
                if (scores[i] == 0) {
                    scores[i] = nombreJoueursParJour[i] + 1;
                }
            }
        }
        return scoreSemaine;
    }

    public Map<Joueur, GolfScore> computeScoreSemaineGolf(List<List<SutomPartage>> sutomPartages) {
        List<List<JoueurGolfScore>> joueurGolfScores =
            sutomPartages.stream().map(jour -> jour.stream().map(JoueurGolfScore::new).toList()).toList();

        Integer[] maxCoupParJour = joueurGolfScores.stream()
            .map(podiumJour -> podiumJour.stream().map(JoueurGolfScore::golfScore).mapToInt(GolfScore::coup).max())
            .map(optionalMax -> optionalMax.orElse(0))
            .toArray(Integer[]::new);
        var scoreSemaine = new HashMap<Joueur, int[]>();
        for (int i = 0; i < joueurGolfScores.size(); i++) {
            final int index = i;
            joueurGolfScores.get(index).forEach(joueurGolfScore -> {
                scoreSemaine.putIfAbsent(joueurGolfScore.joueur(), new int[joueurGolfScores.size()]);
                scoreSemaine.get(joueurGolfScore.joueur())[index] = joueurGolfScore.golfScore().coup();
            });
        }
        for (int[] scores : scoreSemaine.values()) {
            for (int i = 0; i < scores.length; i++) {
                if (scores[i] == 0) {
                    scores[i] = maxCoupParJour[i] + 1;
                }
            }
        }
        var golfScoreSemaine = new HashMap<Joueur, GolfScore>();
        scoreSemaine.forEach((joueur, coups) -> golfScoreSemaine.put(joueur, new GolfScore(
            IntStream.of(coups).sum(),
            joueurGolfScores.stream()
                .flatMap(Collection::stream)
                .filter(golfScore -> golfScore.joueur().equals(joueur))
                .map(JoueurGolfScore::golfScore)
                .mapToInt(GolfScore::scoreLettre)
                .sum(),
            joueurGolfScores.stream()
                .flatMap(Collection::stream)
                .filter(golfScore -> golfScore.joueur().equals(joueur))
                .map(JoueurGolfScore::golfScore)
                .mapToInt(GolfScore::subScoreLettre)
                .sum())));
        return golfScoreSemaine;
    }

    public List<Set<Joueur>> sortScoreSemaineGolf(Map<Joueur, GolfScore> scoreSemaine) {
        Map<GolfScore, Set<Joueur>> joueursByScore = scoreSemaine.entrySet().stream()
            .collect(Collectors.groupingBy(
                Map.Entry::getValue,
                Collectors.mapping(Map.Entry::getKey, Collectors.toSet())));
        return joueursByScore.keySet().stream()
            .sorted(Comparator.comparingInt(GolfScore::coup)
                .thenComparing(Comparator.comparingInt(GolfScore::scoreLettre).reversed())
                .thenComparing(Comparator.comparingInt(GolfScore::subScoreLettre).reversed()))
            .map(joueursByScore::get)
            .toList();
    }

    /**
     * Calcule le score total de chaque joueur pour la semaine et classe les joueurs du plus petit score au plus grand.
     *
     * @param scoreSemaine Une Map associant à chaque joueur le score du joueur sur chaque journée.
     * @return Une liste de joueurs groupés en Set pour s'accommoder d'une égalité le cas échéant.
     */
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

    public String podiumSemainePrettyPrintGolf(
        List<Set<Joueur>> podiumSemaine,
        ZonedDateTime firstDay,
        ZonedDateTime lastDay
    ) {
        return podiumSemainePrettyPrint(podiumSemaine, firstDay, lastDay, true);
    }

    public String podiumSemainePrettyPrint(
        List<Set<Joueur>> podiumSemaine,
        ZonedDateTime firstDay,
        ZonedDateTime lastDay
    ) {
        return podiumSemainePrettyPrint(podiumSemaine, firstDay, lastDay, false);
    }

    private String podiumSemainePrettyPrint(
        List<Set<Joueur>> podiumSemaine,
        ZonedDateTime firstDay,
        ZonedDateTime lastDay,
        boolean golf
    ) {
        var sb = new StringBuilder();
        if (!podiumSemaine.isEmpty()) {
            sb.append("*SUTOM classement semaine du ").append(formatWeekDateRange(firstDay, lastDay))
                .append(golf ? " mode golf :golf:" : "").append("*\n");
        }
        int i = 0;
        for (Set<Joueur> joueurs : podiumSemaine) {
            boolean linebreak = i == 3;
            for (Joueur joueur : joueurs) {
                switch (i) {
                    case 0 -> sb.append("\n:trophy: *").append(joueur.nom()).append("*");
                    case 1 -> sb.append("\n:second_place_medal: ").append(joueur.nom());
                    case 2 -> sb.append("\n:third_place_medal: ").append(joueur.nom());
                    default -> sb.append(linebreak ? "\n\n" : "\n").append(i + 1).append(". ").append(joueur.nom());
                }
                linebreak = false;
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

    public String scoreSemainePrettyPrint(Map<Joueur, int[]> scoreSemaine, List<Set<Joueur>> podiumSemaine) {
        var sb = new StringBuilder();
        long nombreJoueurs = podiumSemaine.stream().mapToLong(Collection::size).sum();
        int maxNameLength = podiumSemaine.stream().flatMap(Collection::stream)
            .map(Joueur::nom).mapToInt(String::length).max().orElse(0);
        Map<Joueur, Integer> totalScore = scoreSemaine.entrySet().stream().collect
            (Collectors.toMap(Map.Entry::getKey, entry -> Arrays.stream(entry.getValue()).sum()));
        int maxTotalScore = totalScore.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        try (var formatter = new Formatter(sb)) {
            podiumSemaine.stream().flatMap(Collection::stream).forEach(joueur -> {
                var score = scoreSemaine.get(joueur);
                var format = "%-" + (maxNameLength + 4) + "s"
                    + ("%-" + ((int) Math.log10(nombreJoueurs) + 2) + "s").repeat(score.length)
                    + "%" + ((int) Math.log10(maxTotalScore) + 2) + "s"
                    + "%n";
                formatter.format(format, Stream.concat(
                    Stream.of(joueur.nom()), Stream.concat(
                        Arrays.stream(score).boxed(),
                        Stream.of(totalScore.get(joueur)))).toArray());
            });
        }
        return sb.toString();
    }

    public String scoreSemainePrettyPrintGolf(Map<Joueur, GolfScore> scoreSemaine, List<Set<Joueur>> podiumSemaine) {
        var sb = new StringBuilder();
        int nameColumnSize = podiumSemaine.stream().flatMap(Collection::stream)
            .map(Joueur::nom).mapToInt(String::length).max().orElse(0) + 4;
        var headerCoup = "coups";
        var headerScore = "score";
        var headerSubScore = "score2";
        int coupColumnSize = Math.max(
            (int) Math.log10(
                scoreSemaine.values().stream().mapToInt(GolfScore::coup).max().orElse(0)) + 1,
            headerCoup.length()) + 1;
        int scoreColumnSize = Math.max(
            (int) Math.log10(
                scoreSemaine.values().stream().mapToInt(GolfScore::scoreLettre).max().orElse(0)) + 1,
            headerScore.length()) + 1;
        int subScoreColumnSize = Math.max(
            (int) Math.log10(
                scoreSemaine.values().stream().mapToInt(GolfScore::subScoreLettre).max().orElse(0)) + 1,
            headerSubScore.length()) + 1;
        var format = "%-" + nameColumnSize + "s%" + coupColumnSize + "s%" + scoreColumnSize + "s%"
            + subScoreColumnSize + "s%n";
        try (var formatter = new Formatter(sb)) {
            if (!podiumSemaine.isEmpty()) {
                formatter.format(format, "", headerCoup, headerScore, headerSubScore);
            }
            podiumSemaine.stream().flatMap(Collection::stream).forEach(joueur -> {
                var score = scoreSemaine.get(joueur);
                formatter.format(format, joueur.nom(), score.coup(), score.scoreLettre(), score.subScoreLettre());
            });
        }
        return sb.toString();
    }
}
