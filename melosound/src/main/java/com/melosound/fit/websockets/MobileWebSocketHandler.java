package com.melosound.fit.websockets;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import com.melosound.fit.config.rabbitmq.DirectSender;
import com.melosound.fit.utils.RabbitMQConstantUtil;
import com.melosound.fit.utils.RedisKeyUtil;
import com.rabbitmq.client.Channel;

import cn.hutool.core.util.StrUtil;

@Component
public class MobileWebSocketHandler  extends AbstractWebSocketHandler {
	private static final Logger logger = LoggerFactory.getLogger(MobileWebSocketHandler.class);
	private static Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
	private static Map<String,String> authorizationCodeMap = new ConcurrentHashMap<>();
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	private DirectSender sender;

	@Autowired
	private RedisKeyUtil keyUtil;
	
	

//	@Override
//	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
//		logger.info("handleBinaryMessage服务器接收收到消息:");
//		DataPrinterUtil.printHexadecimalByteBuffer(message.getPayload());
//		message.getPayload().rewind();
//	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		logger.info("Mobile webSocket {} receive text message",session.getId());
		String msg = message.getPayload();
		if(MobileWebSocketHandler.authorizationCodeMap.containsKey(session.getId())) {
			sender.send(RabbitMQConstantUtil.CLIENT_MESSAGE, msg,MobileWebSocketHandler.authorizationCodeMap.get(session.getId()));
		}
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		Authentication authentication = (Authentication) session.getAttributes().get("authentication");
		if (Objects.nonNull(authentication)) {
			String username = authentication.getName();
			if(StrUtil.isNotEmpty(username)) {
				String authorizationCode = (String) redisTemplate.opsForValue().get(keyUtil.getfitAuthorizationCodeKey(username));
				if(StrUtil.isNotEmpty(authorizationCode)) {
					MobileWebSocketHandler.authorizationCodeMap.put(session.getId(), authorizationCode);
					MobileWebSocketHandler.sessionMap.put(authorizationCode, session);
					redisTemplate.delete(username);
					logger.info("Mobile webSocket connected->username: {},AuthorizationCode: {}",username,authorizationCode);
					return;
				}
			}
		}
		if(session.isOpen()) {
			session.close();
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		logger.info("Mobile WebSocket {} 断开连接",session.getId());
		if(MobileWebSocketHandler.authorizationCodeMap.containsKey(session.getId())) {
			String authorizationCode = authorizationCodeMap.get(session.getId());
			if(MobileWebSocketHandler.sessionMap.containsKey(authorizationCode)) {
				MobileWebSocketHandler.sessionMap.remove(authorizationCode);
			}
			authorizationCodeMap.remove(session.getId());
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		// 传输过程中出现了错误
		if (session.isOpen()) {
			session.close();
		}
		if(MobileWebSocketHandler.authorizationCodeMap.containsKey(session.getId())) {
			String authorizationCode = authorizationCodeMap.get(session.getId());
			if(MobileWebSocketHandler.sessionMap.containsKey(authorizationCode)) {
				MobileWebSocketHandler.sessionMap.remove(authorizationCode);
			}
			authorizationCodeMap.remove(session.getId());
		}
	}
	
	@RabbitHandler
	@RabbitListener(queues = RabbitMQConstantUtil.MOBILE_MESSAGE)
	public void receiveMessage(Channel channel, Message message) throws IOException {
		
		String msg = new String(message.getBody());
		
		try {
			String authorizationCode = message.getMessageProperties().getHeader("AuthorizationCode");
			if(StrUtil.isNotEmpty(authorizationCode)) {
				if(MobileWebSocketHandler.sessionMap.containsKey(authorizationCode)) {
					MobileWebSocketHandler.sessionMap.get(authorizationCode).sendMessage(new TextMessage(msg));
					logger.info("【消费者】 消息内容：【{}】。messageId 【{}】",msg,message.getMessageProperties().getMessageId());
				}
			}
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (Exception ex) {
			logger.error("WebSocket received rabbitmq message Error: {}",ex.getMessage());
			channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
		}
		// 在这里处理接收到的消息
	}
}
