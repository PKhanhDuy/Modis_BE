package com.example.modis.chat.websocket.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Slf4j
public class UserHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String userId = (String) attributes.get("userId");
        log.info("userId in handshake interceptor: " + userId);
        System.out.println("Determining user for WebSocket connection, userId: " + userId);

        if (userId == null) {
            log.info("Determining user for WebSocket connection, userId is null");
            userId = "unknown_user";
        }

        final String finalUserId = userId;
        return new Principal() {
            @Override
            public String getName() {
                return finalUserId;
            }
        };
    }
}
