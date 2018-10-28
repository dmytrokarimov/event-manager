package com.jms.event.manager.jms.provider.amqp;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RabbitMQMessage {

	private long id;
	
	private byte[] body;
}
