package com.bmaparpaing.sutomslackbot;


import com.slack.api.methods.SlackApiException;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Controller
public class PodiumController {

    private final PodiumJourService podiumJourService;

    private final PodiumSemaineService podiumSemaineService;

    private final SutomPartageService sutomPartageService;

    private final SlackService slackService;

    private final SutomSlackbotProperties sutomSlackbotProperties;

    public PodiumController(
        PodiumJourService podiumJourService,
        PodiumSemaineService podiumSemaineService,
        SutomPartageService sutomPartageService,
        SlackService slackService,
        SutomSlackbotProperties sutomSlackbotProperties
    ) {
        this.podiumJourService = podiumJourService;
        this.podiumSemaineService = podiumSemaineService;
        this.sutomPartageService = sutomPartageService;
        this.slackService = slackService;
        this.sutomSlackbotProperties = sutomSlackbotProperties;
    }

    public void computeAndPostPodiumJour() throws SlackApiException, IOException {
        var zonedNow = ZonedDateTime.now(ZoneId.of(sutomSlackbotProperties.getTimeZone()));
        List<SutomPartage> slackPartages = sutomPartageService.readConversationFromSlackApiOfDay(zonedNow);
        if (!slackPartages.isEmpty()) {
            List<SutomPartage> podium = podiumJourService.sortSutomPartages(slackPartages);
            String text = podiumJourService.podiumJourPrettyPrint(podium, zonedNow);
            slackService.postMessage(text);
        }
    }

    public void computeAndPostPodiumSemaine() throws SlackApiException, IOException {
        var zonedNow = ZonedDateTime.now(ZoneId.of(sutomSlackbotProperties.getTimeZone()));
        int dayOfWeek = zonedNow.getDayOfWeek().getValue();
        List<List<SutomPartage>> podiumJours = new ArrayList<>();
        for (int i = 0; i < dayOfWeek; i++) {
            List<SutomPartage> slackPartages = sutomPartageService.readConversationFromSlackApiOfDay(
                zonedNow.minus(i, ChronoUnit.DAYS));
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
