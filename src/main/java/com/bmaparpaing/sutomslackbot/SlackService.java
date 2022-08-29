package com.bmaparpaing.sutomslackbot;

import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.conversations.ConversationsHistoryResponse;
import com.slack.api.methods.response.users.UsersInfoResponse;
import com.slack.api.model.Message;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class SlackService {

    private final MethodsClient client;
    private final SlackProperties slackProperties;

    public SlackService(MethodsClient client, SlackProperties slackProperties) {
        this.client = client;
        this.slackProperties = slackProperties;
    }

    public List<Message> fetchTodayConversation() throws SlackApiException, IOException {
        String todayTimestamp = String.valueOf(Instant.now().truncatedTo(ChronoUnit.DAYS).getEpochSecond());
        return fetchConversation(todayTimestamp, null);
    }

    public List<Message> fetchConversationOfDay(ZonedDateTime zonedDateTime) throws SlackApiException, IOException {
        var oldest = zonedDateTime.truncatedTo(ChronoUnit.DAYS);
        var latest = oldest.plus(1, ChronoUnit.DAYS);
        return fetchConversation(String.valueOf(oldest.toEpochSecond()), String.valueOf(latest.toEpochSecond()));
    }

    private List<Message> fetchConversation(String oldest, String latest) throws SlackApiException, IOException {
        ConversationsHistoryResponse conversation = client.conversationsHistory(req ->
            req.channel(slackProperties.getChannel())
                .limit(slackProperties.getFetchLimit())
                .oldest(oldest)
                .latest(latest));

        List<Message> messages = new ArrayList<>(conversation.getMessages());

        boolean hasMore = conversation.isHasMore();
        while (hasMore) {
            String nextCursor = conversation.getResponseMetadata().getNextCursor();
            conversation = client.conversationsHistory(req ->
                req.channel(slackProperties.getChannel())
                    .limit(slackProperties.getFetchLimit())
                    .oldest(oldest)
                    .latest(latest)
                    .cursor(nextCursor));
            messages.addAll(conversation.getMessages());
            hasMore = conversation.isHasMore();
        }

        return messages;
    }

    public UsersInfoResponse fetchUserInfo(String userId) throws SlackApiException, IOException {
        return client.usersInfo(req -> req.token(slackProperties.getToken()).user(userId));
    }

    public ChatPostMessageResponse postMessage(String texte) throws SlackApiException, IOException {
        return client.chatPostMessage(req -> req.channel(slackProperties.getChannel()).text(texte));
    }

}
