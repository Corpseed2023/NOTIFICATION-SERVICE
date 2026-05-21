package com.notification.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        /*
         * Native WebSocket endpoint.
         * Easier for frontend testing.
         *
         * Frontend URL:
         * ws://localhost:9010/ws-native
         */
        registry.addEndpoint("/ws-native")
                .setAllowedOriginPatterns("*");

        /*
         * SockJS endpoint.
         * Frontend URL:
         * http://localhost:9010/ws
         */
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }
}