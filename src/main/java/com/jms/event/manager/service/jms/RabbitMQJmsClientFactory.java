package com.jms.event.manager.service.jms;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.jms.event.manager.jms.JmsClient;
import com.jms.event.manager.jms.JmsClientFactory;
import com.jms.event.manager.jms.provider.amqp.RabbitMQConfig;
import com.jms.event.manager.jms.provider.amqp.RabbitMQJmsClient;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import lombok.Data;

@ConditionalOnProperty(name="jms.provider", havingValue="RABBIT_MQ")
@Component
@ConfigurationProperties(prefix="jms.rabbitmq")
@Data
public class RabbitMQJmsClientFactory implements JmsClientFactory {

	private boolean automaticRecovery;

	private String hostName;
	
	private int port;

	private String userName;

	private String password;
	
	private RabbitMQConfig config;

	private Connection connection;

	
	@PostConstruct
	public void init() throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(hostName);
		factory.setPort(port);
		factory.setAutomaticRecoveryEnabled(automaticRecovery);
		
		factory.setUsername(userName);
		factory.setPassword(password);
		
		connection = factory.newConnection();
	}
	
	
	@Override
	public JmsClient createClient() throws IOException {
		return new RabbitMQJmsClient(connection, config);
	}


}
