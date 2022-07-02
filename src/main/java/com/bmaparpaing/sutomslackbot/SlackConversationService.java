package com.bmaparpaing.sutomslackbot;

import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.users.UsersInfoResponse;
import com.slack.api.model.Message;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import static com.bmaparpaing.sutomslackbot.SlackPartageTexte.COUP_PATTERN;

@Service
public class SlackConversationService {

    public static final Pattern JOUEUR_PATTERN = Pattern.compile("^([A-Z][A-zÀ-ú ]+ [A-zÀ-ú ]+)( - [\\d .]*)?$");

    private final SlackService slackService;

    public SlackConversationService(SlackService slackService) {
        this.slackService = slackService;
    }

    public List<SlackPartage> readFromFilePath(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        var iterator = lines.iterator();
        String lastJoueur = "";
        List<SlackPartage> slackPartages = new ArrayList<>();
        long idJoueur = 1;
        while (iterator.hasNext()) {
            String line = iterator.next();
            var matcherJoueur = JOUEUR_PATTERN.matcher(line);
            var matcherSlackPartage = COUP_PATTERN.matcher(line);
            if (matcherJoueur.find()) {
                lastJoueur = matcherJoueur.group(1);
            } else if (matcherSlackPartage.find()) {
                int coups = Integer.parseInt(matcherSlackPartage.group(1));
                slackPartages.add(new SlackPartage(
                    new Joueur(idJoueur++ + "", lastJoueur),
                    new SlackPartageTexte(line + takeNLines(iterator, coups)),
                    Instant.now()));
            }
        }
        return slackPartages;
    }

    private String takeNLines(Iterator<String> iterator, int n) {
        var sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(iterator.next());
        }
        return sb.toString();
    }

    public List<SlackPartage> readTodayConversationFromSlackApi()
        throws SlackApiException, IOException {
        List<Message> messages = slackService.fetchTodayConversation();
        List<SlackPartage> partages = new ArrayList<>();
        for (Message message : messages) {
            if (COUP_PATTERN.matcher(message.getText()).find()) {
                UsersInfoResponse usersInfoResponse = slackService.fetchUserInfo(message.getUser());
                partages.add(new SlackPartage(new Joueur(message.getUser(), usersInfoResponse.getUser().getRealName()),
                    new SlackPartageTexte(message.getText()),
                    Instant.ofEpochSecond(Long.parseLong(message.getTs().split("\\.")[0]))));
            }
        }
        return partages;
    }

}
