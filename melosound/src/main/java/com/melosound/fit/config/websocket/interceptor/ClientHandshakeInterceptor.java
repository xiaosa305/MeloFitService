package com.melosound.fit.config.websocket.interceptor;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

public class ClientHandshakeInterceptor  extends HttpSessionHandshakeInterceptor {

	 @Override
	    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
	                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        attributes.put("authentication", authentication);
	        return super.beforeHandshake(request, response, wsHandler, attributes);
	    }
}
