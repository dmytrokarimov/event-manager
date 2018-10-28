package com.jms.event.manager.jms.provider.amqp;

import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotNull;

import lombok.Value;

@Value
public class RabbitMQConfig {
	
	@NotNull
	private boolean durable;

	@NotNull
	private boolean exclusive;
	
	@NotNull
	private boolean autoDelete;
	
	@NotNull
	private int timeout;
	
	@NotNull
	private TimeUnit timeoutTimeUnit;
}
