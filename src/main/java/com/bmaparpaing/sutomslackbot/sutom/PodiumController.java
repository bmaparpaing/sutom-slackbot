package com.bmaparpaing.sutomslackbot.sutom;


import com.bmaparpaing.sutomslackbot.model.Joueur;
import com.bmaparpaing.sutomslackbot.model.SutomPartage;
import com.bmaparpaing.sutomslackbot.slack.SlackService;
import com.slack.api.methods.SlackApiException;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Controller
public class PodiumController {

    private final PodiumJourService podiumJourService;

    private final PodiumSemaineService podiumSemaineService;

    private final SutomPartageService sutomPartageService;

    private final SlackService slackService;

    public PodiumController(
        PodiumJourService podiumJourService,
        PodiumSemaineService podiumSemaineService,
        SutomPartageService sutomPartageService,
        SlackService slackService
    ) {
        this.podiumJourService = podiumJourService;
        this.podiumSemaineService = podiumSemaineService;
        this.sutomPartageService = sutomPartageService;
        this.slackService = slackService;
    }

    public void computeAndPostPodiumJour(ZonedDateTime zonedDateTime) throws SlackApiException, IOException {
        List<SutomPartage> slackPartages = sutomPartageService.readConversationFromSlackApiOfDay(zonedDateTime);
        if (!slackPartages.isEmpty()) {
            List<SutomPartage> podium = podiumJourService.sortSutomPartages(slackPartages);
            String text = podiumJourService.podiumJourPrettyPrint(podium, zonedDateTime);
            slackService.postMessage(text);
        }
    }

    public void computeAndPostPodiumSemaine(ZonedDateTime zonedDateTime) throws SlackApiException, IOException {
        int dayOfWeek = zonedDateTime.getDayOfWeek().getValue();
        List<List<SutomPartage>> podiumJours = new ArrayList<>();
        // Pour chaque jour depuis la date en paramètre jusqu'au lundi précédant
        for (int i = 0; i < dayOfWeek; i++) {
            List<SutomPartage> slackPartages = sutomPartageService.readConversationFromSlackApiOfDay(
                zonedDateTime.minus(i, ChronoUnit.DAYS));
            if (!slackPartages.isEmpty()) {
                podiumJours.add(podiumJourService.sortSutomPartages(slackPartages));
            }
        }
        if (!podiumJours.isEmpty()) {
            // Mettre la collection dans l'ordre chronologique : du lundi jusqu'à la date en paramètre
            Collections.reverse(podiumJours);
            Map<Joueur, int[]> scoreSemaine = podiumSemaineService.computeScoreSemaine(podiumJours);
            List<Set<Joueur>> podium = podiumSemaineService.sortScoreSemaine(scoreSemaine);
            String text = podiumSemaineService.podiumSemainePrettyPrint(
                podium, zonedDateTime.minus(dayOfWeek - 1L, ChronoUnit.DAYS), zonedDateTime);
            slackService.postMessage(text);
        }
    }
}
