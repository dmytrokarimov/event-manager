package com.jms.event.manager.jms.provider.amqp;

import java.util.concurrent.TimeUnit;

import lombok.Value;

@Value
public class RabbitMQConfig {
	
	private boolean durable;

	private boolean exclusive;

	private boolean autoDelete;
	
	private int timeout;
	
	private TimeUnit timeoutTimeUnit;
}