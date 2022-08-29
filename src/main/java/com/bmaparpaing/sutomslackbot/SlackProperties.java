package com.bmaparpaing.sutomslackbot;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "slack")
public class SlackProperties {

    /**
     * Token pour se connecter à l'API Slack
     */
    private String token;

    /**
     * ID du channel sur lequel récupérer les messages et publier
     */
    private String channel;

    /**
     * Taille de la pagination à appliquer lors de la récupération des messages
     */
    private int fetchLimit = 200;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public int getFetchLimit() {
        return fetchLimit;
    }

    public void setFetchLimit(int fetchLimit) {
        this.fetchLimit = fetchLimit;
    }
}
