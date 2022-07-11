package com.bmaparpaing.sutomslackbot;


import com.slack.api.methods.SlackApiException;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;

@Controller
public class PodiumController {

    private final PodiumJourService podiumJourService;

    private final SutomPartageService sutomPartageService;

    private final SlackService slackService;

    public PodiumController(
        PodiumJourService podiumJourService,
        SutomPartageService sutomPartageService,
        SlackService slackService
    ) {
        this.podiumJourService = podiumJourService;
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
}
