package com.bmaparpaing.sutomslackbot;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class PodiumJourService {

    public List<SlackPartage> sortSlackPartages(List<SlackPartage> slackPartages) {
        slackPartages.sort(Comparator.comparingInt(SlackPartage::getCoup)
            .thenComparingInt(SlackPartage::getLettreCorrecte)
            .thenComparingInt(SlackPartage::getLettreMalPlacee)
            .thenComparing(SlackPartage::getTimestamp));
        return slackPartages;
    }

    public String podiumJourPrettyPrint(List<SlackPartage> slackPartages) {
        var sb = new StringBuilder();
        for (int i = 0; i < slackPartages.size(); i++) {
            switch (i) {
                case 0 -> sb.append("SUTOM\n\n:trophée: *").append(slackPartages.get(i).getJoueur().getNom())
                    .append("*");
                case 1 -> sb.append("\n:médaille_argent: ").append(slackPartages.get(i).getJoueur().getNom());
                case 2 -> sb.append("\n:médaille_bronze: ").append(slackPartages.get(i).getJoueur().getNom());
                case 3 -> sb.append("\n\n").append(i + 1).append(". ").append(slackPartages.get(i).getJoueur().getNom());
                default -> sb.append("  ").append(i + 1).append(". ").append(slackPartages.get(i).getJoueur().getNom());
            }
        }
        return sb.toString();
    }
}
