package com.bmaparpaing.sutomslackbot;

import com.bmaparpaing.sutomslackbot.config.SutomSlackbotProperties;
import com.bmaparpaing.sutomslackbot.sutom.PodiumController;
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
        verify(podiumController, never()).computeAndPostPodiumSemaine(any(), anyBoolean());
    }

    @Test
    void run_givenCaseInsensitiveArgumentJour_shouldRunPodiumJour() throws Exception {
        runner.run("JoUr");

        verify(podiumController, only()).computeAndPostPodiumJour(any());
    }

    @Test
    void run_givenCaseInsensitiveArgumentSemaineOnly_shouldRunPodiumSemaine() throws Exception {
        runner.run("SemAINe");

        verify(podiumController, only()).computeAndPostPodiumSemaine(any(), eq(false));
    }

    @Test
    void run_givenCaseInsensitiveArgumentSemaineAndPrintScore_shouldRunPodiumSemaineWithScore() throws Exception {
        runner.run("SemAINe", "--printScore");

        verify(podiumController, only()).computeAndPostPodiumSemaine(any(), eq(true));
    }

    @Test
    void run_givenUnrecognizedArgument_shouldDoNothing() throws Exception {
        runner.run("oervQZniosn", "qpoazvn");

        verify(podiumController, never()).computeAndPostPodiumJour(any());
        verify(podiumController, never()).computeAndPostPodiumSemaine(any(), anyBoolean());
    }

    @Test
    void run_givenCaseInsensitiveArgumentJourAndGolfOption_shouldRunPodiumJourGolf() throws Exception {
        runner.run("JoUr", "--golf");

        verify(podiumController, only()).computeAndPostPodiumJourGolf(any());
    }

    @Test
    void run_givenCaseInsensitiveArgumentSemaineAndGolfOption_shouldRunPodiumSemaine() throws Exception {
        runner.run("SemAINe", "--golf");

        verify(podiumController, only()).computeAndPostPodiumSemaineGolf(any());
    }
}
