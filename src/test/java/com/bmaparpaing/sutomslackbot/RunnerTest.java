package com.bmaparpaing.sutomslackbot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RunnerTest {

    @Mock
    private PodiumController podiumController;

    private final SutomSlackbotProperties sutomSlackbotProperties = new SutomSlackbotProperties();

    private Runner runner;

    @BeforeEach
    void setUp() {
        runner = new Runner(podiumController, sutomSlackbotProperties);
    }

    @Test
    void run_givenNoArgument_shouldDoNothing() throws Exception {
        runner.run();

        verify(podiumController, never()).computeAndPostPodiumJour(any());
        verify(podiumController, never()).computeAndPostPodiumSemaine(any());
    }

    @Test
    void run_givenCaseInsensitiveArgumentJour_shouldRunPodiumJour() throws Exception {
        runner.run("JoUr");

        verify(podiumController, only()).computeAndPostPodiumJour(any());
    }

    @Test
    void run_givenCaseInsensitiveArgumentSemaine_shouldRunPodiumSemaine() throws Exception {
        runner.run("SemAINe");

        verify(podiumController, only()).computeAndPostPodiumSemaine(any());
    }

    @Test
    void run_givenUnrecognizedArgument_shouldDoNothing() throws Exception {
        runner.run("oervQZniosn");

        verify(podiumController, never()).computeAndPostPodiumJour(any());
        verify(podiumController, never()).computeAndPostPodiumSemaine(any());
    }
}
