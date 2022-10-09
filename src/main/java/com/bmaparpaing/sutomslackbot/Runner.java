package com.bmaparpaing.sutomslackbot;

import com.bmaparpaing.sutomslackbot.config.SutomSlackbotProperties;
import com.bmaparpaing.sutomslackbot.sutom.PodiumController;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Component
public class Runner implements CommandLineRunner {

    public static final String ARG_JOUR = "jour";
    public static final String ARG_SEMAINE = "semaine";
    public static final String ARG_ALTERNATE = "--alternate";
    public static final String ARG_GOLF = "--golf";
    public static final String ARG_PRINT_SCORE = "--printScore";
    public static final ZonedDateTime ALTERNATE_MODE_GOLF_WEEK_REFERENCE =
        ZonedDateTime.parse("2022-10-03T00:00:00+00:00");

    private final PodiumController podiumController;

    private ZonedDateTime now;

    public Runner(PodiumController podiumController, SutomSlackbotProperties sutomSlackbotProperties) {
        this.podiumController = podiumController;
        now = ZonedDateTime.now(ZoneId.of(sutomSlackbotProperties.getTimeZone()));
    }

    public void setNow(ZonedDateTime zonedDateTime) {
        this.now = zonedDateTime;
    }

    @Override
    public void run(String... args) throws Exception {
        if (args != null && args.length > 0) {
            List<String> arguments = Arrays.asList(args);
            if (ARG_JOUR.equalsIgnoreCase(arguments.get(0))) {
                if (isGolfMode(arguments)) {
                    podiumController.computeAndPostPodiumJourGolf(now);
                } else {
                    podiumController.computeAndPostPodiumJour(now);
                }
            } else if (ARG_SEMAINE.equalsIgnoreCase(arguments.get(0))) {
                if (isGolfMode(arguments)) {
                    podiumController.computeAndPostPodiumSemaineGolf(now, arguments.contains(ARG_PRINT_SCORE));
                } else {
                    podiumController.computeAndPostPodiumSemaine(now, arguments.contains(ARG_PRINT_SCORE));
                }
            }
        }
    }

    private boolean isGolfMode(List<String> arguments) {
        if (arguments.contains(ARG_ALTERNATE)) {
            return ChronoUnit.WEEKS.between(ALTERNATE_MODE_GOLF_WEEK_REFERENCE, now) % 2 != 0;
        }
        return arguments.contains(ARG_GOLF);
    }

}
