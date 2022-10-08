package com.bmaparpaing.sutomslackbot.slack;

import com.bmaparpaing.sutomslackbot.config.SlackProperties;
import com.slack.api.RequestConfigurator;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.conversations.ConversationsHistoryRequest;
import com.slack.api.methods.request.users.UsersInfoRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.conversations.ConversationsHistoryResponse;
import com.slack.api.methods.response.users.UsersInfoResponse;
import com.slack.api.model.Message;
import com.slack.api.model.ResponseMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlackServiceTest {

    @Mock
    private MethodsClient client;

    private final SlackProperties slackProperties = new SlackProperties();

    private SlackService slackService;

    @Captor
    private ArgumentCaptor<RequestConfigurator<ConversationsHistoryRequest.ConversationsHistoryRequestBuilder>>
        requestBuilderLambdaCaptor;

    @Captor
    private ArgumentCaptor<RequestConfigurator<ChatPostMessageRequest.ChatPostMessageRequestBuilder>>
        chatPostBuilderLambdaCaptor;

    @BeforeEach
    void setUp() {
        slackService = new SlackService(client, slackProperties);
    }

    @Test
    void fetchConversationOfDay_givenMultiplePageResponse_shouldReturnAllMessagesOfDay()
        throws SlackApiException, IOException {
        var message1 = new Message();
        var conversation1 = new ConversationsHistoryResponse();
        conversation1.setMessages(List.of(message1));
        conversation1.setHasMore(true);
        var responseMetadata1 = new ResponseMetadata();
        responseMetadata1.setNextCursor("epzcvbsdf");
        conversation1.setResponseMetadata(responseMetadata1);
        var message2 = new Message();
        var conversation2 = new ConversationsHistoryResponse();
        conversation2.setMessages(List.of(message2));
        conversation2.setHasMore(true);
        var responseMetadata2 = new ResponseMetadata();
        responseMetadata2.setNextCursor("sodujeoih");
        conversation2.setResponseMetadata(responseMetadata2);
        var message3 = new Message();
        var conversation3 = new ConversationsHistoryResponse();
        conversation3.setMessages(List.of(message3));
        conversation3.setHasMore(false);
        when(client.conversationsHistory(
            ArgumentMatchers.<RequestConfigurator<ConversationsHistoryRequest.ConversationsHistoryRequestBuilder>>any()
        )).thenReturn(conversation1).thenReturn(conversation2).thenReturn(conversation3);
        var builder1 = ConversationsHistoryRequest.builder();
        var builder2 = ConversationsHistoryRequest.builder();
        var builder3 = ConversationsHistoryRequest.builder();
        var zonedDateTime = Instant.parse("2022-07-01T23:10:01.00Z").atZone(ZoneId.of("Europe/Paris"));

        var result = slackService.fetchConversationOfDay(zonedDateTime);
        verify(client, times(3)).conversationsHistory(requestBuilderLambdaCaptor.capture());
        var lambdas = requestBuilderLambdaCaptor.getAllValues();
        var request1 = lambdas.get(0).configure(builder1).build();
        var request2 = lambdas.get(1).configure(builder2).build();
        var request3 = lambdas.get(2).configure(builder3).build();

        assertThat(result).containsExactly(message1, message2, message3);
        assertThat(request1)
            .extracting(
                ConversationsHistoryRequest::getOldest,
                ConversationsHistoryRequest::getLatest,
                ConversationsHistoryRequest::getCursor)
            .containsExactly(
                String.valueOf(LocalDate.parse("2022-07-02")
                    .atStartOfDay(ZoneId.of("Europe/Paris"))
                    .toEpochSecond()),
                String.valueOf(LocalDate.parse("2022-07-03")
                    .atStartOfDay(ZoneId.of("Europe/Paris"))
                    .toEpochSecond()),
                null
            );
        assertThat(request2)
            .extracting(
                ConversationsHistoryRequest::getOldest,
                ConversationsHistoryRequest::getLatest,
                ConversationsHistoryRequest::getCursor)
            .containsExactly(
                String.valueOf(LocalDate.parse("2022-07-02")
                    .atStartOfDay(ZoneId.of("Europe/Paris"))
                    .toEpochSecond()),
                String.valueOf(LocalDate.parse("2022-07-03")
                    .atStartOfDay(ZoneId.of("Europe/Paris"))
                    .toEpochSecond()),
                "epzcvbsdf"
            );
        assertThat(request3)
            .extracting(
                ConversationsHistoryRequest::getOldest,
                ConversationsHistoryRequest::getLatest,
                ConversationsHistoryRequest::getCursor)
            .containsExactly(
                String.valueOf(LocalDate.parse("2022-07-02")
                    .atStartOfDay(ZoneId.of("Europe/Paris"))
                    .toEpochSecond()),
                String.valueOf(LocalDate.parse("2022-07-03")
                    .atStartOfDay(ZoneId.of("Europe/Paris"))
                    .toEpochSecond()),
                "sodujeoih"
            );
    }

    @Test
    void fetchConversationOfDay_shouldReturnMessagesOfDay() throws SlackApiException, IOException {
        var message = new Message();
        var conversation = new ConversationsHistoryResponse();
        conversation.setMessages(List.of(message));
        conversation.setHasMore(false);
        when(client.conversationsHistory(
            ArgumentMatchers.<RequestConfigurator<ConversationsHistoryRequest.ConversationsHistoryRequestBuilder>>any()
        )).thenReturn(conversation);
        var builder = ConversationsHistoryRequest.builder();
        var zonedDateTime = Instant.parse("2022-07-01T23:10:01.00Z").atZone(ZoneId.of("Europe/Paris"));

        List<Message> messages = slackService.fetchConversationOfDay(zonedDateTime);
        verify(client, times(1)).conversationsHistory(requestBuilderLambdaCaptor.capture());
        var lambda = requestBuilderLambdaCaptor.getValue();
        var request = lambda.configure(builder).build();

        assertThat(messages).containsExactly(message);
        assertThat(request)
            .extracting(
                ConversationsHistoryRequest::getLimit,
                ConversationsHistoryRequest::getOldest,
                ConversationsHistoryRequest::getLatest)
            .containsExactly(
                slackProperties.getFetchLimit(),
                String.valueOf(LocalDate.parse("2022-07-02")
                    .atStartOfDay(ZoneId.of("Europe/Paris"))
                    .toEpochSecond()),
                String.valueOf(LocalDate.parse("2022-07-03")
                    .atStartOfDay(ZoneId.of("Europe/Paris"))
                    .toEpochSecond())
            );
    }

    @Test
    void fetchUserInfo_shouldReturnUserInfo() throws SlackApiException, IOException {
        var usersInfoResponse = new UsersInfoResponse();
        when(client.usersInfo(
            ArgumentMatchers.<RequestConfigurator<UsersInfoRequest.UsersInfoRequestBuilder>>any()
        )).thenReturn(usersInfoResponse);

        var result = slackService.fetchUserInfo("A1");

        assertThat(result).isEqualTo(usersInfoResponse);
    }

    @Test
    void postMessage_shouldReturnChatPostMessageResponse() throws SlackApiException, IOException {
        var response = new ChatPostMessageResponse();
        when(client.chatPostMessage(
            ArgumentMatchers.<RequestConfigurator<ChatPostMessageRequest.ChatPostMessageRequestBuilder>>any()
        )).thenReturn(response);

        var result = slackService.postMessage("TEST");

        assertThat(result).isEqualTo(response);
    }

    @Test
    void postCodeBlockMessage_shouldReturnChatPostMessageResponse() throws SlackApiException, IOException {
        var response = new ChatPostMessageResponse();
        when(client.chatPostMessage(
            ArgumentMatchers.<RequestConfigurator<ChatPostMessageRequest.ChatPostMessageRequestBuilder>>any()
        )).thenReturn(response);
        var builder = ChatPostMessageRequest.builder();

        var result = slackService.postCodeBlockMessage("TEST");
        verify(client).chatPostMessage(chatPostBuilderLambdaCaptor.capture());
        var lambda = chatPostBuilderLambdaCaptor.getValue();
        var request = lambda.configure(builder).build();

        assertThat(request).extracting(ChatPostMessageRequest::getText).isEqualTo("```TEST```");
        assertThat(result).isEqualTo(response);
    }
}
