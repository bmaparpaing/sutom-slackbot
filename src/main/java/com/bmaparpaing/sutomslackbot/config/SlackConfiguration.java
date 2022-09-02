package com.bmaparpaing.sutomslackbot.config;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SlackProperties.class)
public class SlackConfiguration {

    private final SlackProperties slackProperties;

    public SlackConfiguration(SlackProperties slackProperties) {
        this.slackProperties = slackProperties;
    }

    @Bean
    public MethodsClient methodsClient() {
        return Slack.getInstance().methods(slackProperties.getToken());
    }
}
