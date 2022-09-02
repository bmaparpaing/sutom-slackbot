package com.bmaparpaing.sutomslackbot.model;

import java.time.Instant;

public record SutomPartage(Joueur joueur, Instant timestamp, int coup, int lettreCorrecte, int lettreMalPlacee) {

    public SutomPartage(Joueur joueur, Instant timestamp, SutomPartageTexte texte) {
        this(joueur, timestamp, texte.getCoup(), texte.getLettreCorrecte(), texte.getLettreMalPlacee());
    }


}
