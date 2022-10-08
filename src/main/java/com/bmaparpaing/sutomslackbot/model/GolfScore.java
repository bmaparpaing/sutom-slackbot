package com.bmaparpaing.sutomslackbot.model;

public record GolfScore(
    int coup,
    int scoreLettre,
    int subScoreLettre) {

    public GolfScore(SutomPartage sutomPartage) {
        this(sutomPartage.echec() ? 7 : sutomPartage.coup(),
            sutomPartage.lettreCorrecte() * 2 + sutomPartage.lettreMalPlacee(),
            sutomPartage.lettreCorrecte());
    }
}
