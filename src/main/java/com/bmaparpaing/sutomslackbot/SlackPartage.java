package com.bmaparpaing.sutomslackbot;

import java.time.Instant;

public record SlackPartage(Joueur joueur, Instant timestamp, int coup, int lettreCorrecte, int lettreMalPlacee) {

    public SlackPartage(Joueur joueur, SlackPartageTexte texte, Instant timestamp) {
        this(joueur, timestamp, texte.getCoup(), texte.getLettreCorrecte(), texte.getLettreMalPlacee());
    }


}
