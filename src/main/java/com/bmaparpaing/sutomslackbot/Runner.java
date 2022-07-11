package com.bmaparpaing.sutomslackbot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

    private final PodiumController podiumController;

    public Runner(PodiumController podiumController) {
        this.podiumController = podiumController;
    }

    @Override
    public void run(String... args) throws Exception {
        if (args != null && args.length > 0 && "jour".equalsIgnoreCase(args[0])) {
            podiumController.computeAndPostPodiumJour();
        }
    }

}
