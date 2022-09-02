package com.bmaparpaing.sutomslackbot.sutom;

import com.bmaparpaing.sutomslackbot.config.SutomSlackbotProperties;
import com.bmaparpaing.sutomslackbot.model.SutomPartage;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class PodiumJourService {

    public static final String FULL_TEXT_LOCAL_DATE_PATTERN = "EEEE d LLLL";

    private final DateTimeFormatter fullTextLocalDateFormatter;

    public PodiumJourService(SutomSlackbotProperties sutomSlackbotProperties) {
        fullTextLocalDateFormatter = DateTimeFormatter.ofPattern(FULL_TEXT_LOCAL_DATE_PATTERN)
            .withLocale(Locale.forLanguageTag(sutomSlackbotProperties.getLocale()));
    }

    public List<SutomPartage> sortSutomPartages(List<SutomPartage> sutomPartages) {
        return sutomPartages.stream()
            .sorted(Comparator.comparingInt(SutomPartage::coup)
                .thenComparingInt(SutomPartage::lettreCorrecte)
                .thenComparingInt(SutomPartage::lettreMalPlacee)
                .thenComparing(SutomPartage::timestamp))
            .toList();
    }

    public String podiumJourPrettyPrint(List<SutomPartage> sutomPartages, ZonedDateTime zonedDateTime) {
        var sb = new StringBuilder();
        for (int i = 0; i < sutomPartages.size(); i++) {
            switch (i) {
                case 0 -> sb.append("*SUTOM du ").append(fullTextLocalDateFormatter.format(zonedDateTime)).append("*\n")
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
