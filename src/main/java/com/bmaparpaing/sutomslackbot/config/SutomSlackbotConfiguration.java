package com.bmaparpaing.sutomslackbot.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SutomSlackbotProperties.class)
public class SutomSlackbotConfiguration {
}
