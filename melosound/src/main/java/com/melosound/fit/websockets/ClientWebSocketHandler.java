package com.melosound.fit.websockets;

import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import com.melosound.fit.config.rabbitmq.DirectSender;
import com.melosound.fit.domain.CustomUser;
import com.melosound.fit.utils.RabbitMQConstantUtil;
import com.melosound.fit.utils.RedisKeyUtil;

import cn.hutool.core.util.StrUtil;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;

@Component
public class ClientWebSocketHandler extends AbstractWebSocketHandler{
	private static final Logger logger = LoggerFactory.getLogger(ClientWebSocketHandler.class);
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
		logger.info("Client WebSocket {} handleTextMessage",session.getId());
		String msg = message.getPayload();
		if(authorizationCodeMap.containsKey(session.getId())) {
			sender.send(RabbitMQConstantUtil.MOBILE_MESSAGE, msg,authorizationCodeMap.get(session.getId()));
		}
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		Authentication authentication = (Authentication) session.getAttributes().get("authentication");
		if (Objects.nonNull(authentication)) {
			String username = authentication.getName();
			String authorizationCode = (String) redisTemplate.opsForValue().get(keyUtil.getfitAuthorizationCodeKey(username));
			if(StrUtil.isNotEmpty(authorizationCode)) {
				authorizationCodeMap.put(session.getId(), authorizationCode);
				sessionMap.put(authorizationCode, session);
				redisTemplate.delete(username);
				logger.info("Client WebSocket connected->username: {},AuthorizationCode: {}",username,authorizationCode);
				return;
			}
		}
		if(session.isOpen()) {
			session.close();
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		logger.info("Client WebSocket {} 断开连接",session.getId());
		if(authorizationCodeMap.containsKey(session.getId())) {
			String authorizationCode = authorizationCodeMap.get(session.getId());
			if(sessionMap.containsKey(authorizationCode)) {
				sessionMap.remove(authorizationCode);
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
		if(authorizationCodeMap.containsKey(session.getId())) {
			String authorizationCode = authorizationCodeMap.get(session.getId());
			if(sessionMap.containsKey(authorizationCode)) {
				sessionMap.remove(authorizationCode);
			}
			authorizationCodeMap.remove(session.getId());
		}
	}

	@RabbitHandler
	@RabbitListener(queues = RabbitMQConstantUtil.CLIENT_MESSAGE)
	public void receiveMessage(Channel channel, Message message) throws IOException {
		String msg = new String(message.getBody());
		
		try {
			String authorizationCode = message.getMessageProperties().getHeader("AuthorizationCode");
			if(StrUtil.isNotEmpty(authorizationCode)) {
				if(sessionMap.containsKey(authorizationCode)) {
					sessionMap.get(authorizationCode).sendMessage(new TextMessage(msg));
					logger.info("【消费者】 消息内容：【{}】。messageId 【{}】",msg,message.getMessageProperties().getMessageId());
				}
			}
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (Exception ex) {
			logger.error("WebSocket received rabbitmq message Error: {}",ex.getMessage());
			channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
		}
	}
}
