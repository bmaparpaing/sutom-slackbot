package com.bmaparpaing.sutomslackbot;

import com.bmaparpaing.sutomslackbot.config.SutomSlackbotProperties;
import com.bmaparpaing.sutomslackbot.sutom.PodiumController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;

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

        verifyNoInteractions(podiumController);
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

        verifyNoInteractions(podiumController);
    }

    @Test
    void run_givenCaseInsensitiveArgumentJourAndGolfOption_shouldRunPodiumJourGolf() throws Exception {
        runner.run("JoUr", "--golf");

        verify(podiumController, only()).computeAndPostPodiumJourGolf(any());
    }

    @Test
    void run_givenCaseInsensitiveArgumentSemaineAndGolfOption_shouldRunPodiumSemaineGolf() throws Exception {
        runner.run("SemAINe", "--golf");

        verify(podiumController, only()).computeAndPostPodiumSemaineGolf(any(), eq(false));
    }

    @Test
    void run_givenCaseInsensitiveArgumentSemaineAndGolfPrintScore_shouldRunPodiumSemaineGolfWithScore()
        throws Exception {
        runner.run("SemAINe", "--golf", "--printScore");

        verify(podiumController, only()).computeAndPostPodiumSemaineGolf(any(), eq(true));
    }

    @Test
    void run_givenCaseInsensitiveArgumentJourAlternateRefWeek_shouldRunPodiumJour()
        throws Exception {
        runner.setNow(ZonedDateTime.parse("2022-10-03T00:00:00+00:00"));

        runner.run("jour", "--alternate");

        verify(podiumController, only()).computeAndPostPodiumJour(any());
    }

    @Test
    void run_givenCaseInsensitiveArgumentSemaineAlternateRefWeekPlusOne_shouldRunPodiumSemaineGolf()
        throws Exception {
        runner.setNow(ZonedDateTime.parse("2022-10-10T00:00:00+00:00"));

        runner.run("semaiNe", "--alternate");

        verify(podiumController, only()).computeAndPostPodiumSemaineGolf(any(), eq(false));
    }

    @Test
    void run_givenCaseInsensitiveArgumentSemaineAlternateRefWeekPlusTwoWithScore_shouldRunPodiumSemaineWithScore()
        throws Exception {
        runner.setNow(ZonedDateTime.parse("2022-10-17T00:00:00+00:00"));

        runner.run("SemAINe", "--alternate", "--printScore");

        verify(podiumController, only()).computeAndPostPodiumSemaine(any(), eq(true));
    }
}
