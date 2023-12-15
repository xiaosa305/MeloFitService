package com.melosound.fit.config.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.melosound.fit.utils.RabbitMQConstantUtil;

/**
 * rabbitmq配置类：配置Exchange、Queue、以及绑定交换机
 * @author xiaosa
 */
@Configuration
@EnableRabbit
public class RabbitMQConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        //SimpleRabbitListenerContainerFactory发现消息中有content_type有text就会默认将其转换成string类型的
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
         /**
         * 比较常用的 Converter 就是 Jackson2JsonMessageConverter,在发送消息时，它会先将自定义的消息类序列化成json格式，
         * 再转成byte构造 Message，在接收消息时，会将接收到的 Message 再反序列化成自定义的类
         */
        //factory.setMessageConverter(new Jackson2JsonMessageConverter());
        //开启手动ACK
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }
    
    @Bean
    public AmqpTemplate amqpTemplate(){
        rabbitTemplate.setEncoding("UTF-8");
        rabbitTemplate.setMandatory(true);
        /**
         * ReturnsCallback消息没有正确到达队列时触发回调，如果正确到达队列不执行
         * config : 需要开启rabbitmq发送失败回退 
         * yml配置publisher-returns: true 
         * 或rabbitTemplate.setMandatory(true);设置为true
         */
        rabbitTemplate.setReturnsCallback(returnedMessage -> {
            String messageId = returnedMessage.getMessage().getMessageProperties().getMessageId();
            byte[] message = returnedMessage.getMessage().getBody();
            Integer replyCode = returnedMessage.getReplyCode();
            String replyText = returnedMessage.getReplyText();
            String exchange = returnedMessage.getExchange();
            String routingKey = returnedMessage.getRoutingKey();

            log.info("消息：{} 发送失败，消息ID：{} 应答码：{} 原因：{} 交换机：{} 路由键：{}",
                    new String(message),messageId,replyCode,replyText,exchange,routingKey);

        });
        return rabbitTemplate;
    }

    /**
     * 声明直连交换机  支持持久化
     * @return
     */
    @Bean(RabbitMQConstantUtil.DIRECT_EXCHANGE)
    public Exchange directExchange(){
        return ExchangeBuilder.directExchange(RabbitMQConstantUtil.DIRECT_EXCHANGE).durable(true).build();
    }

    /**
     * PC客户端 消息队列
     * @return
     */
    @Bean(RabbitMQConstantUtil.CLIENT_MESSAGE)
    public Queue clientReceiveQueue(){
        return new Queue(RabbitMQConstantUtil.CLIENT_MESSAGE,true,false,true);
    }

    /**
     * 把PC客户端消息队列绑定到交换机上
     * @param queue
     * @param directExchange
     * @return
     */
    @Bean
    public Binding clientReceiveBinding(@Qualifier(RabbitMQConstantUtil.CLIENT_MESSAGE) Queue queue,
                                      @Qualifier(RabbitMQConstantUtil.DIRECT_EXCHANGE) Exchange directExchange){
        //RoutingKey :RabbitMQConstantUtil.CLIENT_MESSAGE,这里设置与消息队列 同名
        return BindingBuilder.bind(queue).to(directExchange).with(RabbitMQConstantUtil.CLIENT_MESSAGE).noargs();
    }

    /**
     * 移动客户端 消息队列
     * @return
     */
    @Bean(RabbitMQConstantUtil.MOBILE_MESSAGE)
    public Queue mobileReceiveQueue(){
        return new Queue(RabbitMQConstantUtil.MOBILE_MESSAGE,true,false,true);
    }

    /**
     * 把移动客户端消息队列绑定到交换机上
     * @param queue
     * @param directExchange
     * @return
     */
    @Bean
    public Binding mobileReceiveBinding(@Qualifier(RabbitMQConstantUtil.MOBILE_MESSAGE) Queue queue,
                                       @Qualifier(RabbitMQConstantUtil.DIRECT_EXCHANGE) Exchange directExchange){
        //RoutingKey :RabbitMQConstantUtil.MOBILE_MESSAGE,这里设置与消息队列 同名
        return BindingBuilder.bind(queue).to(directExchange).with(RabbitMQConstantUtil.MOBILE_MESSAGE).noargs();
    }
}

