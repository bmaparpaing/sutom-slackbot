package com.bmaparpaing.sutomslackbot;

import java.util.regex.Pattern;

public class SlackPartageTexte {

    private static final String LETTRE_CORRECTE = ":large_red_square:";
    private static final String LETTRE_CORRECTE_FR = ":grand_carré_rouge:";
    private static final String LETTRE_MAL_PLACEE = ":large_yellow_circle:";
    private static final String LETTRE_MAL_PLACEE_FR = ":grand_cercle_jaune:";
    public static final Pattern COUP_PATTERN = Pattern.compile("SUTOM #\\d+ (\\d)/6");

    private final int coup;
    private final int lettreCorrecte;
    private final int lettreMalPlacee;

    public SlackPartageTexte(String texte) {
        var matcher = COUP_PATTERN.matcher(texte);
        coup = matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
        lettreCorrecte = count(texte, LETTRE_CORRECTE) + count(texte, LETTRE_CORRECTE_FR); // test de ligne qui dépasse la taille autorisé
        lettreMalPlacee = count(texte, LETTRE_MAL_PLACEE) + count(texte, LETTRE_MAL_PLACEE_FR);
    }

    private int count(String texte, String recherche) {
        return (texte.length() - texte.replace(recherche, "").length()) / recherche.length();
    }

    public int getCoup() {
        return coup;
    }

    public int getLettreCorrecte() {
        return lettreCorrecte;
    }

    public int getLettreMalPlacee() {
        return lettreMalPlacee;
    }
}
