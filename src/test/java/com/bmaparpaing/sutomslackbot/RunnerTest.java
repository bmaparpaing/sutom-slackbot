package com.bmaparpaing.sutomslackbot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RunnerTest {

    @Mock
    private PodiumController podiumController;

    @InjectMocks
    private Runner runner;

    @Test
    void run_givenNoArgument_shouldDoNothing() throws Exception {
        runner.run();

        verify(podiumController, never()).computeAndPostPodiumJour();
        verify(podiumController, never()).computeAndPostPodiumSemaine();
    }

    @Test
    void run_givenCaseInsensitiveArgumentJour_shouldRunPodiumJour() throws Exception {
        runner.run("JoUr");

        verify(podiumController, only()).computeAndPostPodiumJour();
    }

    @Test
    void run_givenCaseInsensitiveArgumentSemaine_shouldRunPodiumSemaine() throws Exception {
        runner.run("SemAINe");

        verify(podiumController, only()).computeAndPostPodiumSemaine();
    }

    @Test
    void run_givenUnrecognizedArgument_shouldDoNothing() throws Exception {
        runner.run("SemAINe");

        verify(podiumController, only()).computeAndPostPodiumSemaine();
    }
}
