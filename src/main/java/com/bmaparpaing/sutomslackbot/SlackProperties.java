package com.bmaparpaing.sutomslackbot;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "slack")
public class SlackProperties {

    public static final int DEFAULT_FETCH_LIMIT = 200;

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
    private int fetchLimit = DEFAULT_FETCH_LIMIT;

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

    public void setFetchLimit(Integer fetchLimit) {
        this.fetchLimit = fetchLimit;
    }
}
