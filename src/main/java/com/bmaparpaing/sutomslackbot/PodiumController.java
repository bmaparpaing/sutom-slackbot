package com.bmaparpaing.sutomslackbot;


import com.slack.api.methods.SlackApiException;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
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

    public void computeAndPostPodiumJour() throws SlackApiException, IOException {
        List<SutomPartage> slackPartages = sutomPartageService.readTodayConversationFromSlackApi();
        if (!slackPartages.isEmpty()) {
            List<SutomPartage> podium = podiumJourService.sortSutomPartages(slackPartages);
            String text = podiumJourService.podiumJourTodayPrettyPrint(podium);
            slackService.postMessage(text);
        }
    }

    public void computeAndPostPodiumSemaine() throws SlackApiException, IOException {
        Instant now = Instant.now();
        int dayOfWeek = now.atZone(ZoneId.of("Europe/Paris")).getDayOfWeek().getValue();
        List<List<SutomPartage>> podiumJours = new ArrayList<>();
        for (int i = 0; i < dayOfWeek; i++) {
            List<SutomPartage> slackPartages = sutomPartageService.readConversationOfDayFromSlackApi(
                now.minus(i, ChronoUnit.DAYS));
            if (!slackPartages.isEmpty()) {
                podiumJours.add(podiumJourService.sortSutomPartages(slackPartages));
            }
        }
        if (!podiumJours.isEmpty()) {
            Collections.reverse(podiumJours);
            Map<Joueur, int[]> scoreSemaine = podiumSemaineService.computeScoreSemaine(podiumJours);
            List<Set<Joueur>> podium = podiumSemaineService.sortScoreSemaine(scoreSemaine);
            String text = podiumSemaineService.podiumSemainePrettyPrint(podium);
            slackService.postMessage(text);
        }
    }
}
