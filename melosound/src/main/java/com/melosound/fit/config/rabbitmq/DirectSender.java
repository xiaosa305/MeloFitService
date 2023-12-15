package com.melosound.fit.config.rabbitmq;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.melosound.fit.constant.Constant;
import com.melosound.fit.utils.RabbitMQConstantUtil;

import java.util.UUID;

/**
 * Routing 路由模型（交换机类型：direct）
 * 消息生成者
 * @author xiaosa
 */
@Component
public class DirectSender {

    private static final Logger log = LoggerFactory.getLogger(DirectSender.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private Constant constant;

    /**
     * 发送消息
     * @param routingKey
     * @param msg
     */
    public void send (String routingKey,String msg,String authorizationCode){
        Message message = MessageBuilder.withBody(msg.getBytes())
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setContentEncoding("utf-8")
                .setMessageId(UUID.randomUUID()+"").build();
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setExpiration("5000"); // 设置过期时间为 5 秒

        log.info("【发送者】消息内容【{}】 交换机【{}】 路由【{}】 消息ID【{}】",msg,RabbitMQConstantUtil.DIRECT_EXCHANGE
                ,routingKey,message.getMessageProperties().getMessageId());
        rabbitTemplate.convertAndSend(RabbitMQConstantUtil.DIRECT_EXCHANGE,routingKey,message,new MessagePostProcessor() {
			@Override
			public Message postProcessMessage(Message message) throws AmqpException {
				 MessageProperties props = message.getMessageProperties();
		            props.setExpiration(Long.toString(constant.getRabbitMQMessageExpirationTime()));
		            props.setHeader("AuthorizationCode", authorizationCode);
		            return message;
			}});
        //rabbitTemplate.convertAndSend(RabbitMQConstantUtil.DIRECT_EXCHANGE,routingKey,message);
    }
}
