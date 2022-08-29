package com.bmaparpaing.sutomslackbot;

import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.users.UsersInfoResponse;
import com.slack.api.model.Message;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import static com.bmaparpaing.sutomslackbot.SutomPartageTexte.COUP_PATTERN;

@Service
public class SutomPartageService {

    public static final Pattern JOUEUR_PATTERN = Pattern.compile("^([A-Z][A-zÀ-ú ]+ [A-zÀ-ú ]+)( - [\\d .]*)?$");

    private final SlackService slackService;

    public SutomPartageService(SlackService slackService) {
        this.slackService = slackService;
    }

    public List<SutomPartage> readFromFilePath(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        var iterator = lines.iterator();
        String lastJoueur = "";
        List<SutomPartage> sutomPartages = new ArrayList<>();
        long idJoueur = 1;
        while (iterator.hasNext()) {
            String line = iterator.next();
            var matcherJoueur = JOUEUR_PATTERN.matcher(line);
            var matcherSutomPartage = COUP_PATTERN.matcher(line);
            if (matcherJoueur.find()) {
                lastJoueur = matcherJoueur.group(1);
            } else if (matcherSutomPartage.find()) {
                int coups = Integer.parseInt(matcherSutomPartage.group(1));
                sutomPartages.add(new SutomPartage(
                    new Joueur(idJoueur++ + "", lastJoueur),
                    Instant.now(),
                    new SutomPartageTexte(line + takeNLines(iterator, coups))));
            }
        }
        return sutomPartages;
    }

    private String takeNLines(Iterator<String> iterator, int n) {
        var sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(iterator.next());
        }
        return sb.toString();
    }

    public List<SutomPartage> readTodayConversationFromSlackApi()
        throws SlackApiException, IOException {
        List<Message> messages = slackService.fetchTodayConversation();
        return getSutomPartagesFromMessages(messages);
    }

    public List<SutomPartage> readConversationOfDayFromSlackApi(ZonedDateTime zonedDateTime)
        throws SlackApiException, IOException {
        List<Message> messages = slackService.fetchConversationOfDay(zonedDateTime);
        return getSutomPartagesFromMessages(messages);
    }

    private List<SutomPartage> getSutomPartagesFromMessages(List<Message> messages)
        throws SlackApiException, IOException {
        List<SutomPartage> partages = new ArrayList<>();
        for (Message message : messages) {
            if (COUP_PATTERN.matcher(message.getText()).find()) {
                UsersInfoResponse usersInfoResponse = slackService.fetchUserInfo(message.getUser());
                partages.add(new SutomPartage(new Joueur(message.getUser(), usersInfoResponse.getUser().getRealName()),
                    Instant.ofEpochSecond(Long.parseLong(message.getTs().split("\\.")[0])),
                    new SutomPartageTexte(message.getText())));
            }
        }
        return partages;
    }

}
