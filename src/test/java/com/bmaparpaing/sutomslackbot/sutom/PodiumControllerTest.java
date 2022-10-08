package com.bmaparpaing.sutomslackbot.sutom;

import com.bmaparpaing.sutomslackbot.model.Joueur;
import com.bmaparpaing.sutomslackbot.model.SutomPartage;
import com.bmaparpaing.sutomslackbot.slack.SlackService;
import com.slack.api.methods.SlackApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PodiumControllerTest {

    @Mock
    private PodiumJourService podiumJourService;

    @Mock
    private PodiumSemaineService podiumSemaineService;

    @Mock
    private SutomPartageService sutomPartageService;

    @Mock
    private SlackService slackService;

    @InjectMocks
    private PodiumController podiumController;

    @Test
    void computeAndPostPodiumJour_givenEmptyTodayConversation_shouldDoNothing() throws SlackApiException, IOException {
        var zonedNow = ZonedDateTime.now();
        when(sutomPartageService.readConversationFromSlackApiOfDay(zonedNow)).thenReturn(Collections.emptyList());

        podiumController.computeAndPostPodiumJour(zonedNow);

        verify(podiumJourService, never()).sortSutomPartages(any());
        verify(podiumJourService, never()).podiumJourPrettyPrint(any(), eq(zonedNow));
        verify(slackService, never()).postMessage(any());
    }

    @Test
    void computeAndPostPodiumJour_givenTodayConversation_shouldPostPodiumPrettyPrint()
        throws SlackApiException, IOException {
        var partages = List.of(new SutomPartage(new Joueur("1", "Joueur 1"),
            Instant.now(), 3, 12, 4));
        var podium = "PODIUM TEST JOUR";
        var zonedNow = ZonedDateTime.now();
        when(sutomPartageService.readConversationFromSlackApiOfDay(zonedNow)).thenReturn(partages);
        when(podiumJourService.sortSutomPartages(partages)).thenReturn(partages);
        when(podiumJourService.podiumJourPrettyPrint(partages, zonedNow)).thenReturn(podium);

        podiumController.computeAndPostPodiumJour(zonedNow);

        verify(slackService, times(1)).postMessage(podium);
    }

    @Test
    void computeAndPostPodiumJourGolf_givenEmptyTodayConversation_shouldDoNothing()
        throws SlackApiException, IOException {
        var zonedNow = ZonedDateTime.now();
        when(sutomPartageService.readConversationFromSlackApiOfDay(zonedNow)).thenReturn(Collections.emptyList());

        podiumController.computeAndPostPodiumJourGolf(zonedNow);

        verify(podiumJourService, never()).sortSutomPartagesGolf(any());
        verify(podiumJourService, never()).podiumJourPrettyPrintGolf(any(), eq(zonedNow));
        verify(slackService, never()).postMessage(any());
    }

    @Test
    void computeAndPostPodiumJourGolf_givenTodayConversation_shouldPostPodiumPrettyPrintGolf()
        throws SlackApiException, IOException {
        var joueur = new Joueur("1", "Joueur 1");
        var partages = List.of(new SutomPartage(joueur, Instant.now(),
            3, 12, 4));
        var podiumList = List.of(Set.of(joueur));
        var podium = "PODIUM TEST JOUR";
        var zonedNow = ZonedDateTime.now();
        when(sutomPartageService.readConversationFromSlackApiOfDay(zonedNow)).thenReturn(partages);
        when(podiumJourService.sortSutomPartagesGolf(partages)).thenReturn(podiumList);
        when(podiumJourService.podiumJourPrettyPrintGolf(podiumList, zonedNow)).thenReturn(podium);

        podiumController.computeAndPostPodiumJourGolf(zonedNow);

        verify(slackService, times(1)).postMessage(podium);
    }

    @Test
    void computeAndPostPodiumSemaine_givenEmptyConversation_shouldDoNothing()
        throws SlackApiException, IOException {
        var zonedNow = ZonedDateTime.now();
        when(sutomPartageService.readConversationFromSlackApiOfDay(zonedNow)).thenReturn(Collections.emptyList());

        podiumController.computeAndPostPodiumSemaine(zonedNow, false);

        verify(podiumJourService, never()).sortSutomPartages(any());
        verify(podiumSemaineService, never()).computeScoreSemaine(any());
        verify(podiumSemaineService, never()).sortScoreSemaine(any());
        verify(podiumSemaineService, never()).podiumSemainePrettyPrint(any(), any(), any());
        verify(slackService, never()).postMessage(any());
        verify(slackService, never()).postCodeBlockMessage(any());
    }

    @Test
    void computeAndPostPodiumSemaine_givenThisWeekConversation_shouldPostPodiumPrettyPrint()
        throws SlackApiException, IOException {
        var partages = List.of(new SutomPartage(new Joueur("1", "Joueur 1"),
            Instant.now(), 3, 12, 4));
        var podium = "PODIUM TEST SEMAINE";
        var zonedDateTime = ZonedDateTime.parse("2022-09-02T12:00:00+02:00[Europe/Paris]");
        when(sutomPartageService.readConversationFromSlackApiOfDay(any())).thenReturn(partages);
        when(podiumJourService.sortSutomPartages(partages)).thenReturn(partages);
        when(podiumSemaineService.computeScoreSemaine(any())).thenReturn(Collections.emptyMap());
        when(podiumSemaineService.sortScoreSemaine(any())).thenReturn(Collections.emptyList());
        when(podiumSemaineService.podiumSemainePrettyPrint(any(), any(), any())).thenReturn(podium);

        podiumController.computeAndPostPodiumSemaine(zonedDateTime, false);

        verify(sutomPartageService, times(5)).readConversationFromSlackApiOfDay(any());
        verify(slackService, times(1)).postMessage(podium);
        verify(podiumSemaineService, times(1)).podiumSemainePrettyPrint(
            any(), eq(zonedDateTime.minus(4, ChronoUnit.DAYS)), eq(zonedDateTime));
        verify(slackService, never()).postCodeBlockMessage(any());
    }

    @Test
    void computeAndPostPodiumSemaine_givenThisWeekConversationAndPrintScore_shouldPostPodiumPrettyPrintAndScore()
        throws SlackApiException, IOException {
        var partages = List.of(new SutomPartage(new Joueur("1", "Joueur 1"),
            Instant.now(), 3, 12, 4));
        var podium = "PODIUM TEST SEMAINE";
        var score = "SCORE TEST";
        var zonedDateTime = ZonedDateTime.parse("2022-09-02T12:00:00+02:00[Europe/Paris]");
        when(sutomPartageService.readConversationFromSlackApiOfDay(any())).thenReturn(partages);
        when(podiumJourService.sortSutomPartages(partages)).thenReturn(partages);
        when(podiumSemaineService.computeScoreSemaine(any())).thenReturn(Collections.emptyMap());
        when(podiumSemaineService.sortScoreSemaine(any())).thenReturn(Collections.emptyList());
        when(podiumSemaineService.podiumSemainePrettyPrint(any(), any(), any())).thenReturn(podium);
        when(podiumSemaineService.scoreSemainePrettyPrint(any(), any())).thenReturn(score);

        podiumController.computeAndPostPodiumSemaine(zonedDateTime, true);

        verify(sutomPartageService, times(5)).readConversationFromSlackApiOfDay(any());
        verify(slackService, times(1)).postMessage(podium);
        verify(podiumSemaineService, times(1)).podiumSemainePrettyPrint(
            any(), eq(zonedDateTime.minus(4, ChronoUnit.DAYS)), eq(zonedDateTime));
        verify(slackService, times(1)).postCodeBlockMessage(score);
    }

    @Test
    void computeAndPostPodiumSemaineGolf_givenEmptyConversation_shouldDoNothing()
        throws SlackApiException, IOException {
        var zonedNow = ZonedDateTime.now();
        when(sutomPartageService.readConversationFromSlackApiOfDay(zonedNow)).thenReturn(Collections.emptyList());

        podiumController.computeAndPostPodiumSemaineGolf(zonedNow);

        verify(podiumJourService, never()).sortSutomPartagesGolf(any());
        verify(podiumSemaineService, never()).computeScoreSemaineGolf(any());
        verify(podiumSemaineService, never()).sortScoreSemaineGolf(any());
        verify(podiumSemaineService, never()).podiumSemainePrettyPrintGolf(any(), any(), any());
        verify(slackService, never()).postMessage(any());
    }

    @Test
    void computeAndPostPodiumSemaineGolf_givenThisWeekConversation_shouldPostPodiumPrettyPrintGolf()
        throws SlackApiException, IOException {
        var joueur = new Joueur("1", "Joueur 1");
        var partages = List.of(new SutomPartage(joueur, Instant.now(),
            3, 12, 4));
        var podiumList = List.of(Set.of(joueur));
        var podium = "PODIUM TEST SEMAINE";
        var zonedDateTime = ZonedDateTime.parse("2022-09-02T12:00:00+02:00[Europe/Paris]");
        when(sutomPartageService.readConversationFromSlackApiOfDay(any())).thenReturn(partages);
        when(podiumSemaineService.computeScoreSemaineGolf(any())).thenReturn(Collections.emptyMap());
        when(podiumSemaineService.sortScoreSemaineGolf(any())).thenReturn(podiumList);
        when(podiumSemaineService.podiumSemainePrettyPrintGolf(eq(podiumList), any(), any())).thenReturn(podium);

        podiumController.computeAndPostPodiumSemaineGolf(zonedDateTime);

        verify(sutomPartageService, times(5)).readConversationFromSlackApiOfDay(any());
        verify(slackService, times(1)).postMessage(podium);
        verify(podiumSemaineService, times(1)).podiumSemainePrettyPrintGolf(
            any(), eq(zonedDateTime.minus(4, ChronoUnit.DAYS)), eq(zonedDateTime));
    }
}
