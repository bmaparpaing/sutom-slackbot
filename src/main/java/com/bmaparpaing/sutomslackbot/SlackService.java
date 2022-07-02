package com.bmaparpaing.sutomslackbot;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.conversations.ConversationsHistoryResponse;
import com.slack.api.methods.response.users.UsersInfoResponse;
import com.slack.api.model.Message;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class SlackService {

    private static final String TOKEN = "TOKEN";
    private static final String CHANNEL = "CHANNEL";
    private static final int DEFAULT_FETCH_LIMIT = 200;
    private static final MethodsClient client = Slack.getInstance().methods(TOKEN);

    public List<Message> fetchTodayConversation() throws SlackApiException, IOException {
        String todayTimestamp = String.valueOf(Instant.now().truncatedTo(ChronoUnit.DAYS).getEpochSecond());
        ConversationsHistoryResponse conversation = client.conversationsHistory(req ->
            req.channel(CHANNEL).limit(DEFAULT_FETCH_LIMIT).oldest(todayTimestamp));

        List<Message> messages = new ArrayList<>(conversation.getMessages());

        boolean hasMore = conversation.isHasMore();
        while (hasMore) {
            String nextCursor = conversation.getResponseMetadata().getNextCursor();
            conversation = client.conversationsHistory(req ->
                req.channel(CHANNEL).limit(DEFAULT_FETCH_LIMIT).oldest(todayTimestamp).cursor(
                    nextCursor));
            messages.addAll(conversation.getMessages());
            hasMore = conversation.isHasMore();
        }

        return messages;
    }

    public UsersInfoResponse fetchUserInfo(String userId) throws SlackApiException, IOException {
        return client.usersInfo(req -> req.token(TOKEN).user(userId));
    }

    public ChatPostMessageResponse postMessage(String texte) throws SlackApiException, IOException {
        return client.chatPostMessage(req -> req.channel(CHANNEL).text(texte));
    }

}
