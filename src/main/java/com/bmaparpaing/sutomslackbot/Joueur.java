package com.bmaparpaing.sutomslackbot;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Joueur {
    private Long id;
    private String nom;
    private int coup;
    private int lettreCorrecte;
    private int lettreMalPlacee;

}
