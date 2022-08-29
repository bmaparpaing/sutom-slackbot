package com.bmaparpaing.sutomslackbot;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sutom-slackbot")
public class SutomSlackbotProperties {

    /**
     * Fuseau horaire pour définir les limites des journées, de minuit à minuit heure locale
     */
    private String timeZone = "Europe/Paris";

    /**
     * Langue à utiliser pour la date inscrite dans le podium texte
     */
    private String locale = "fr-FR";

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
