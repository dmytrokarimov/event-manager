package com.jms.event.manager.jms.provider.amqp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;

import com.jms.event.manager.jms.JmsClient;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class RabbitMQJmsClient implements JmsClient{

	private String queueName;

	private String exchangeName;

	private Channel channel;

	private RabbitMQConfig config;
	
	private BlockingQueue<RabbitMQMessage> receiveQueue;
	
	private boolean publisher;
	
	public RabbitMQJmsClient(Connection connection, RabbitMQConfig config) throws IOException {
		channel = connection.createChannel();
		
		this.config = config;
	}
	
	@Override
	public void close() throws IOException {
		try {
			channel.close();
		} catch (TimeoutException e) {
			throw new IOException(e);
		}
	}

	@Override
	public boolean isPublisher() {
		return publisher;
	}

	@Override
	public boolean isConsumer() {
		return !publisher;
	}
	
	@Override
	public void register(String appId, String exchangeName) throws IOException {
		this.exchangeName = exchangeName;
		this.queueName = appId;
		this.publisher = true;
		
		exchangeDeclare(exchangeName, true);
	}
	
	private void exchangeDeclare(String exchangeName, boolean createDLQ) throws IOException {
		channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC, config.isDurable(), config.isAutoDelete(), null);
		
		if (createDLQ) {
			String dlqExchangeName = config.getDlqExchangePrefix() + "." + exchangeName;
			channel.exchangeDeclare(dlqExchangeName, BuiltinExchangeType.TOPIC, config.isDurable(), config.isAutoDelete(), null);
		}
	}
	
	@Override
	public void subscribeToDLQ(String appId, String exchangeName) throws IOException {
		subscribe(appId, config.getDlqExchangePrefix() + "." + exchangeName, false);
	}

	@Override
	public void subscribe(String appId, String exchangeName) throws IOException {
		subscribe(appId, exchangeName, true);
	}
	
	private void subscribe(String appId, String exchangeName, boolean createDLQ) throws IOException {
		this.queueName = appId;
		this.exchangeName = exchangeName;

		exchangeDeclare(exchangeName, createDLQ);
		
		Map<String, Object> args = new HashMap<String, Object>();
		if (createDLQ) {
			String dlqExchangeName = config.getDlqExchangePrefix() + "." + exchangeName; 
			args.put("x-dead-letter-exchange", dlqExchangeName);
		}
		
		//generate random queue name
		this.queueName = channel.queueDeclare("", config.isDurable(), config.isExclusive(), config.isAutoDelete(), args).getQueue();
		channel.queueBind(queueName, exchangeName, appId);
		
		channel.basicQos(1);
		
		receiveQueue = new LinkedBlockingQueue<>();
		
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				try {
					receiveQueue.put(new RabbitMQMessage(envelope.getDeliveryTag(), body));
				} catch (InterruptedException e) {
					channel.basicNack(envelope.getDeliveryTag(), false, false);
					throw new IOException(e);
				}
			}
		};
		channel.basicConsume(queueName, false, consumer);
	}

	@Override
	public byte[] receive() throws IOException {
		try {
			RabbitMQMessage message = receiveQueue.poll(config.getReceiveTimeout(), config.getReceiveTimeoutTimeUnit());
			if (message == null) {
				return null;
			}
			channel.basicAck(message.getId(), false);
			return message.getBody();
		} catch (InterruptedException e) {
			throw new IOException(e);
		}
	}

	@Override
	public boolean send(byte[] body) throws IOException {
		//send to routingKey
		AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                .expiration(config.getMessageTTL())
                .build();
		channel.basicPublish(exchangeName, queueName, properties, body);
		return true;
	}

}