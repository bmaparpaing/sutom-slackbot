package com.bmaparpaing.sutomslackbot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

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
            if ("jour".equalsIgnoreCase(args[0])) {
                podiumController.computeAndPostPodiumJour(
                    ZonedDateTime.now(ZoneId.of(sutomSlackbotProperties.getTimeZone())));
            } else if ("semaine".equalsIgnoreCase(args[0])) {
                podiumController.computeAndPostPodiumSemaine(
                    ZonedDateTime.now(ZoneId.of(sutomSlackbotProperties.getTimeZone())));
            }
        }
    }

}
