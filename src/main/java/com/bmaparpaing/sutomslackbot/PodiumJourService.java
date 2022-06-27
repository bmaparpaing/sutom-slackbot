package com.bmaparpaing.sutomslackbot;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class PodiumJourService {

    public List<Joueur> sortJoueurs(List<Joueur> joueurs) {
        joueurs.sort(Comparator.comparingInt(Joueur::getCoup)
                .thenComparingInt(Joueur::getLettreCorrecte)
                .thenComparingInt(Joueur::getLettreMalPlacee));
        return joueurs;
    }
}
