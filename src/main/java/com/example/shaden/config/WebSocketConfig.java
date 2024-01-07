package com.example.shaden.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{

    @Value("${application.frontend.web-app-url}")
    private String webAppUrl;

    @Value("${spring.rabbitmq.host}")
    private String rabbitmqHost;

    @Value("${spring.rabbitmq.port}")
    private int rabbitmqPort;

    @Value("${spring.rabbitmq.username}")
    private String rabbitmqUsername;

    @Value("${spring.rabbitmq.password}")
    private String rabbitmqPassword;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry
        .setApplicationDestinationPrefixes("/app")
        .enableStompBrokerRelay("/topic", "/queue")
        .setRelayHost(rabbitmqHost)
        .setRelayPort(rabbitmqPort)
        .setClientLogin(rabbitmqUsername)
        .setClientPasscode(rabbitmqPassword);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins(webAppUrl).withSockJS();
    }
    
}
