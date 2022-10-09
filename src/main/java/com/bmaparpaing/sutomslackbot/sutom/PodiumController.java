package com.bmaparpaing.sutomslackbot.sutom;


import com.bmaparpaing.sutomslackbot.model.GolfScore;
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

    public void computeAndPostPodiumJourGolf(ZonedDateTime zonedDateTime) throws SlackApiException, IOException {
        List<SutomPartage> slackPartages = sutomPartageService.readConversationFromSlackApiOfDay(zonedDateTime);
        if (!slackPartages.isEmpty()) {
            List<Set<Joueur>> podium = podiumJourService.sortSutomPartagesGolf(slackPartages);
            String text = podiumJourService.podiumJourPrettyPrintGolf(podium, zonedDateTime);
            slackService.postMessage(text);
        }
    }

    public void computeAndPostPodiumSemaine(ZonedDateTime zonedDateTime, boolean printScore)
        throws SlackApiException, IOException {
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
            if (printScore) {
                String scoreText = podiumSemaineService.scoreSemainePrettyPrint(scoreSemaine, podium);
                slackService.postCodeBlockMessage(scoreText);
            }
        }
    }

    public void computeAndPostPodiumSemaineGolf(ZonedDateTime zonedDateTime, boolean printScore)
        throws SlackApiException, IOException {
        int dayOfWeek = zonedDateTime.getDayOfWeek().getValue();
        List<List<SutomPartage>> sutomPartages = new ArrayList<>();
        // Pour chaque jour depuis la date en paramètre jusqu'au lundi précédant
        for (int i = 0; i < dayOfWeek; i++) {
            List<SutomPartage> slackPartages = sutomPartageService.readConversationFromSlackApiOfDay(
                zonedDateTime.minus(i, ChronoUnit.DAYS));
            if (!slackPartages.isEmpty()) {
                sutomPartages.add(slackPartages);
            }
        }
        if (!sutomPartages.isEmpty()) {
            Map<Joueur, GolfScore> scoreSemaine = podiumSemaineService.computeScoreSemaineGolf(sutomPartages);
            List<Set<Joueur>> podium = podiumSemaineService.sortScoreSemaineGolf(scoreSemaine);
            String text = podiumSemaineService.podiumSemainePrettyPrintGolf(
                podium, zonedDateTime.minus(dayOfWeek - 1L, ChronoUnit.DAYS), zonedDateTime);
            slackService.postMessage(text);
            if (printScore) {
                String scoreText = podiumSemaineService.scoreSemainePrettyPrintGolf(scoreSemaine, podium);
                slackService.postCodeBlockMessage(scoreText);
            }
        }
    }
}
