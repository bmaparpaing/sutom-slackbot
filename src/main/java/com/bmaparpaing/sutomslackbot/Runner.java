package com.bmaparpaing.sutomslackbot;

import com.bmaparpaing.sutomslackbot.config.SutomSlackbotProperties;
import com.bmaparpaing.sutomslackbot.sutom.PodiumController;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class Runner implements CommandLineRunner {

    private final PodiumController podiumController;

    private final SutomSlackbotProperties sutomSlackbotProperties;

    public Runner(PodiumController podiumController, SutomSlackbotProperties sutomSlackbotProperties) {
        this.podiumController = podiumController;
        this.sutomSlackbotProperties = sutomSlackbotProperties;
    }

    @Override
    public void run(String... args) throws Exception {
        if (args != null && args.length > 0) {
            List<String> arguments = Arrays.asList(args);
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of(sutomSlackbotProperties.getTimeZone()));
            if ("jour".equalsIgnoreCase(arguments.get(0))) {
                if (arguments.contains("--golf")) {
                    podiumController.computeAndPostPodiumJourGolf(now);
                } else {
                    podiumController.computeAndPostPodiumJour(now);
                }
            } else if ("semaine".equalsIgnoreCase(arguments.get(0))) {
                if (arguments.contains("--golf")) {
                    podiumController.computeAndPostPodiumSemaineGolf(now);
                } else {
                    podiumController.computeAndPostPodiumSemaine(now, arguments.contains("--printScore"));
                }
            }
        }
    }

}
