package com.jms.event.manager.jms.provider.amqp;

import java.util.concurrent.TimeUnit;

import lombok.Data;

@Data
public class RabbitMQConfig {
	
	private String dlqExchangePrefix;
	
	private boolean durable;

	private boolean exclusive;
	
	private boolean autoDelete;
	
	private int receiveTimeout;
	
	private TimeUnit receiveTimeoutTimeUnit;
	
	private String messageTTL;
}
