package com.bmaparpaing.sutomslackbot;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class PodiumJourService {

    public static final DateTimeFormatter FULL_TEXT_DATE_FORMATTER =
        DateTimeFormatter.ofPattern("EEEE d LLLL").withZone(ZoneId.of("Europe/Paris")).withLocale(Locale.FRANCE);

    public List<SutomPartage> sortSutomPartages(List<SutomPartage> sutomPartages) {
        return sutomPartages.stream()
            .sorted(Comparator.comparingInt(SutomPartage::coup)
                .thenComparingInt(SutomPartage::lettreCorrecte)
                .thenComparingInt(SutomPartage::lettreMalPlacee)
                .thenComparing(SutomPartage::timestamp))
            .toList();
    }

    public String podiumJourTodayPrettyPrint(List<SutomPartage> sutomPartages) {
        return podiumJourPrettyPrint(sutomPartages, Instant.now());
    }

    public String podiumJourPrettyPrint(List<SutomPartage> sutomPartages, Instant instant) {
        var sb = new StringBuilder();
        for (int i = 0; i < sutomPartages.size(); i++) {
            switch (i) {
                case 0 -> sb.append("*SUTOM du ").append(FULL_TEXT_DATE_FORMATTER.format(instant)).append("*\n")
                    .append("\n:trophy: *").append(sutomPartages.get(i).joueur().nom()).append("*");
                case 1 -> sb.append("\n:second_place_medal: ").append(sutomPartages.get(i).joueur().nom());
                case 2 -> sb.append("\n:third_place_medal: ").append(sutomPartages.get(i).joueur().nom());
                case 3 -> sb.append("\n\n").append(i + 1).append(". ").append(sutomPartages.get(i).joueur().nom());
                default -> sb.append("  ").append(i + 1).append(". ").append(sutomPartages.get(i).joueur().nom());
            }
        }
        return sb.toString();
    }
}
