package com.melosound.fit.utils;

/**
 * RabbitMQ RoutingKey 常量工具类
 * @author xiaosa
 */
public class RabbitMQConstantUtil {
	 /**
     * 交换机名称
     */
    public static final String DIRECT_EXCHANGE = "directExchange";

    /**
     * 取消订单 队列名称 routingkey
     */
    public static final String CLIENT_MESSAGE = "client-message";

    /**
     * 自动确认订单 队列名称\routingkey
     */
    public static final String MOBILE_MESSAGE = "mobile-message";
}
