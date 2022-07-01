package com.bmaparpaing.sutomslackbot;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class PodiumJourService {

    public List<SlackPartage> sortSlackPartages(List<SlackPartage> slackPartages) {
        slackPartages.sort(Comparator.comparingInt(SlackPartage::coup)
            .thenComparingInt(SlackPartage::lettreCorrecte)
            .thenComparingInt(SlackPartage::lettreMalPlacee)
            .thenComparing(SlackPartage::timestamp));
        return slackPartages;
    }

    public String podiumJourPrettyPrint(List<SlackPartage> slackPartages) {
        var sb = new StringBuilder();
        for (int i = 0; i < slackPartages.size(); i++) {
            switch (i) {
                case 0 -> sb.append("SUTOM\n\n:trophy: *").append(slackPartages.get(i).joueur().nom())
                    .append("*");
                case 1 -> sb.append("\n:second_place_medal: ").append(slackPartages.get(i).joueur().nom());
                case 2 -> sb.append("\n:third_place_medal: ").append(slackPartages.get(i).joueur().nom());
                case 3 -> sb.append("\n\n").append(i + 1).append(". ").append(slackPartages.get(i).joueur().nom());
                default -> sb.append("  ").append(i + 1).append(". ").append(slackPartages.get(i).joueur().nom());
            }
        }
        return sb.toString();
    }
}
