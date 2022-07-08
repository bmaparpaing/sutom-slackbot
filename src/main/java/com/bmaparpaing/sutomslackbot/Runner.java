package com.bmaparpaing.sutomslackbot;

import com.slack.api.methods.SlackApiException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class Runner implements CommandLineRunner {

    private final PodiumJourService podiumJourService;

    private final SutomPartageService sutomPartageService;

    private final SlackService slackService;

    public Runner(
        PodiumJourService podiumJourService,
        SutomPartageService sutomPartageService,
        SlackService slackService
    ) {
        this.podiumJourService = podiumJourService;
        this.sutomPartageService = sutomPartageService;
        this.slackService = slackService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (args != null && args.length > 0 && "jour".equalsIgnoreCase(args[0])) {
            computeAndPostPodiumJour();
        }
    }

    private void computeAndPostPodiumJour() throws SlackApiException, IOException {
        List<SutomPartage> slackPartages = sutomPartageService.readTodayConversationFromSlackApi();
        if (!slackPartages.isEmpty()) {
            List<SutomPartage> podium = podiumJourService.sortSutomPartages(slackPartages);
            String text = podiumJourService.podiumJourTodayPrettyPrint(podium);
            slackService.postMessage(text);
        }
    }
}
