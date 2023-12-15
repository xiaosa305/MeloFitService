package com.melosound.fit.config.websocket;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.melosound.fit.config.websocket.interceptor.ClientHandshakeInterceptor;
import com.melosound.fit.websockets.ClientWebSocketHandler;
import com.melosound.fit.websockets.MobileWebSocketHandler;

@Configuration
@EnableWebSocket
public class ClientWebSocketConfig implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		// TODO Auto-generated method stub
		registry.addHandler(clientWebsocketHandler(), "/clientWS")
				.addHandler(mobileWebsocketHandler(), "/mobileWS")
				.addInterceptors(customHandshakeInterceptor())
				.setAllowedOrigins("*");
	}

	@Bean
	public WebSocketHandler clientWebsocketHandler() {
		System.out.println("clientWebsocketHandler");
		return new ClientWebSocketHandler();
	}
	
	@Bean
	public WebSocketHandler mobileWebsocketHandler() {
		System.out.println("mobileWebsocketHandler");
		return new MobileWebSocketHandler();
	}

	@Bean
	public ClientHandshakeInterceptor customHandshakeInterceptor() {
		return new ClientHandshakeInterceptor();
	}

}
