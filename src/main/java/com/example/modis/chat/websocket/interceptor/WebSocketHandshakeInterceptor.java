package com.example.modis.chat.websocket.interceptor;

import com.example.modis.security.jwt.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Slf4j
@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {
    private final JwtTokenProvider jwtTokenProvider;

    public WebSocketHandshakeInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //here we have to fix the beforeHandshake method to extract the username from the query parameters
    //because the headers are not being set correctly by the client
    //we must not put username in headers but in query parameters
    //we will use jwt token to check the username in the future
    //and we can use redis to store the jwt tokens for the users and store status of the users (online/offline)
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String query = request.getURI().getQuery();
//        String token = extractTokenFromQuery(query);
        String token = UriComponentsBuilder
                .fromUri(request.getURI())
                .build()
                .getQueryParams()
                .getFirst("token");
        log.info("token" + token);
        if (token == null) {
            log.warn("WebSocket handshake failed: token not found");
            return false;
        }
        if (!jwtTokenProvider.validateToken(token)) {
            log.warn("WebSocket handshake failed: invalid token");
            //return false;
//            attributes.put("userId", "u001");
//            attributes.put("userName", "u002");
//            System.out.println("put successfully");
            return false;
        }
        String userId = jwtTokenProvider.getUserId(token);
        log.info("userId in handshake interceptor:" + userId);
        System.out.println("userId in handshake interceptor: " + userId);
        attributes.put("userId", userId);
        log.info("WebSocket handshake successful for userId: {}", userId);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, @Nullable Exception exception) {
        if (exception != null) {
            log.error("WebSocket handshake failed", exception);
        } else {
            log.info("WebSocket handshake completed successfully");
        }
    }

    public String extractTokenFromQuery(String query) {
        if (query == null || query.isEmpty()) {
            log.error("WebSocket handshake failed: query parameter is empty");
            return null;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2 && keyValue[0].equals("token")) {
                try {
                    log.info("Extracted token: {}", keyValue[1]);
                    return java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                } catch (Exception e) {
                    log.error("Error decoding token from query parameter", e);
                    return null;
                }
            }
        }
        return null;
    }
}
