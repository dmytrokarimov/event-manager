package com.jms.event.manager.service.jms;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.jms.event.manager.jms.JmsClient;
import com.jms.event.manager.jms.JmsClientFactory;

@ConditionalOnProperty(name="jms.provider", havingValue="rabbitmq")
@Service
public class RabbitMQJmsClientFactory implements JmsClientFactory {

	@Override
	public JmsClient createClient() {
		// TODO Auto-generated method stub
		return null;
	}

}
