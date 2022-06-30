package com.bmaparpaing.sutomslackbot;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.regex.Pattern;

@Getter
@AllArgsConstructor
public class SlackPartage {

    private static final String LETTRE_CORRECTE = ":grand_carré_rouge:";
    private static final String LETTRE_MAL_PLACEE = ":grand_cercle_jaune:";
    public static final Pattern COUP_PATTERN = Pattern.compile("SUTOM #\\d+ (\\d)/6");

    private final Joueur joueur;
    private final Instant timestamp;
    private final int coup;
    private final int lettreCorrecte;
    private final int lettreMalPlacee;

    public SlackPartage(Joueur joueur, String texte, Instant timestamp) {
        this.joueur = joueur;
        var matcher = COUP_PATTERN.matcher(texte);
        coup = matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
        lettreCorrecte = count(texte, LETTRE_CORRECTE);
        lettreMalPlacee = count(texte, LETTRE_MAL_PLACEE);
        this.timestamp = timestamp;
    }

    private int count(String texte, String recherche) {
        return (texte.length() - texte.replace(recherche, "").length()) / recherche.length();
    }
}
