package com.bmaparpaing.sutomslackbot;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class PodiumJourService {

    public List<SlackPartage> sortSlackPartages(List<SlackPartage> slackPartages) {
        slackPartages.sort(Comparator.comparingInt(SlackPartage::getCoup)
            .thenComparingInt(SlackPartage::getLettreCorrecte)
            .thenComparingInt(SlackPartage::getLettreMalPlacee)
            .thenComparing(SlackPartage::getTimestamp));
        return slackPartages;
    }
}
