package com.bmaparpaing.sutomslackbot;

import com.slack.api.methods.SlackApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

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
        when(sutomPartageService.readTodayConversationFromSlackApi()).thenReturn(Collections.emptyList());

        podiumController.computeAndPostPodiumJour();

        verify(podiumJourService, never()).sortSutomPartages(any());
        verify(podiumJourService, never()).podiumJourTodayPrettyPrint(any());
        verify(slackService, never()).postMessage(any());
    }

    @Test
    void computeAndPostPodiumJour_givenTodayConversation_shouldPostPodiumPrettyPrint()
        throws SlackApiException, IOException {
        var partages = List.of(new SutomPartage(new Joueur("1", "Joueur 1"),
            Instant.now(), 3, 12, 4));
        var podium = "PODIUM TEST JOUR";
        when(sutomPartageService.readTodayConversationFromSlackApi()).thenReturn(partages);
        when(podiumJourService.sortSutomPartages(partages)).thenReturn(partages);
        when(podiumJourService.podiumJourTodayPrettyPrint(partages)).thenReturn(podium);

        podiumController.computeAndPostPodiumJour();

        verify(slackService, times(1)).postMessage(podium);
    }

    @Test
    void computeAndPostPodiumSemaine_givenEmptyConversation_shouldDoNothing()
        throws SlackApiException, IOException {
        when(sutomPartageService.readConversationOfDayFromSlackApi(any())).thenReturn(Collections.emptyList());

        podiumController.computeAndPostPodiumSemaine();

        verify(podiumJourService, never()).sortSutomPartages(any());
        verify(podiumSemaineService, never()).computeScoreSemaine(any());
        verify(podiumSemaineService, never()).sortScoreSemaine(any());
        verify(podiumSemaineService, never()).podiumSemainePrettyPrint(any());
        verify(slackService, never()).postMessage(any());
    }

    @Test
    void computeAndPostPodiumSemaine_givenThisWeekConversation_shouldPostPodiumPrettyPrint()
        throws SlackApiException, IOException {
        var partages = List.of(new SutomPartage(new Joueur("1", "Joueur 1"),
            Instant.now(), 3, 12, 4));
        var podium = "PODIUM TEST SEMAINE";

        when(sutomPartageService.readConversationOfDayFromSlackApi(any())).thenReturn(partages);
        when(podiumJourService.sortSutomPartages(partages)).thenReturn(partages);
        when(podiumSemaineService.computeScoreSemaine(any())).thenReturn(Collections.emptyMap());
        when(podiumSemaineService.sortScoreSemaine(any())).thenReturn(Collections.emptyList());
        when(podiumSemaineService.podiumSemainePrettyPrint(any())).thenReturn(podium);

        podiumController.computeAndPostPodiumSemaine();

        verify(sutomPartageService, atLeastOnce()).readConversationOfDayFromSlackApi(any());
        verify(slackService, times(1)).postMessage(podium);
    }
}
