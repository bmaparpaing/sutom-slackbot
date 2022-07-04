package com.bmaparpaing.sutomslackbot;

import java.time.Instant;

public record SutomPartage(Joueur joueur, Instant timestamp, int coup, int lettreCorrecte, int lettreMalPlacee) {

    public SutomPartage(Joueur joueur, SutomPartageTexte texte, Instant timestamp) {
        this(joueur, timestamp, texte.getCoup(), texte.getLettreCorrecte(), texte.getLettreMalPlacee());
    }


}
