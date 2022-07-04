package com.bmaparpaing.sutomslackbot;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PodiumSemaineService {

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
                Collectors.flatMapping(joueurEntry -> Stream.of(joueurEntry.getKey()), Collectors.toSet())));
        return joueursByScore.keySet().stream()
            .sorted()
            .map(joueursByScore::get)
            .toList();
    }

    public String podiumSemainePrettyPrint(List<Set<Joueur>> podiumSemaine) {
        var sb = new StringBuilder(podiumSemaine.isEmpty() ? "" : "SUTOM\n");
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
}
