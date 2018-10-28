package com.jms.event.manager.service.jms;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import com.jms.event.manager.jms.JmsClient;
import com.jms.event.manager.jms.JmsClientFactory;
import com.jms.event.manager.jms.provider.amqp.RabbitMQConfig;
import com.jms.event.manager.jms.provider.amqp.RabbitMQJmsClient;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

@ConditionalOnProperty(name="jms.provider", havingValue="RABBIT_MQ")
@Component
@ConfigurationProperties(prefix="jms")
public class RabbitMQJmsClientFactory implements JmsClientFactory {

	@NotNull
	private boolean automaticRecovery;

	@NotNull
	private String hostName;
	
	@NestedConfigurationProperty
	private RabbitMQConfig config;

	private Connection connection;
	
	@PostConstruct
	public void init() throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(hostName);
		factory.setAutomaticRecoveryEnabled(automaticRecovery);
		connection = factory.newConnection();
	}
	
	
	@Override
	public JmsClient createClient() throws IOException {
		return new RabbitMQJmsClient(connection, config);
	}

}
