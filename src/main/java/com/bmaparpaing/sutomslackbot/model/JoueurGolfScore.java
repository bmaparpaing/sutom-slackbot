package com.bmaparpaing.sutomslackbot.model;

public record JoueurGolfScore(
    Joueur joueur,
    GolfScore golfScore) {

    public JoueurGolfScore(SutomPartage sutomPartage) {
        this(sutomPartage.joueur(), new GolfScore(sutomPartage));
    }
}
