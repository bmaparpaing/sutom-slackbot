package com.bmaparpaing.sutomslackbot;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class PodiumJourService {

    public List<SutomPartage> sortSutomPartages(List<SutomPartage> sutomPartages) {
        sutomPartages.sort(Comparator.comparingInt(SutomPartage::coup)
            .thenComparingInt(SutomPartage::lettreCorrecte)
            .thenComparingInt(SutomPartage::lettreMalPlacee)
            .thenComparing(SutomPartage::timestamp));
        return sutomPartages;
    }

    public String podiumJourPrettyPrint(List<SutomPartage> sutomPartages) {
        var sb = new StringBuilder();
        for (int i = 0; i < sutomPartages.size(); i++) {
            switch (i) {
                case 0 -> sb.append("SUTOM\n\n:trophy: *").append(sutomPartages.get(i).joueur().nom()).append("*");
                case 1 -> sb.append("\n:second_place_medal: ").append(sutomPartages.get(i).joueur().nom());
                case 2 -> sb.append("\n:third_place_medal: ").append(sutomPartages.get(i).joueur().nom());
                case 3 -> sb.append("\n\n").append(i + 1).append(". ").append(sutomPartages.get(i).joueur().nom());
                default -> sb.append("  ").append(i + 1).append(". ").append(sutomPartages.get(i).joueur().nom());
            }
        }
        return sb.toString();
    }
}
