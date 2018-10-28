package com.jms.event.manager.jms.provider.amqp;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.jms.event.manager.jms.JmsClient;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class RabbitMQJmsClient implements JmsClient{

	private String queueName;

	private Channel channel;

	private RabbitMQConfig config;
	
	private BlockingQueue<RabbitMQMessage> receiveQueue;
	
	public RabbitMQJmsClient(Connection connection, RabbitMQConfig config) throws IOException {
		channel = connection.createChannel();
		
		this.config = config;
	}
	
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void register(String appId, String exchangeName) throws IOException {
		this.queueName = exchangeName + "_" + appId;
				
		channel.queueDeclare(queueName, config.isDurable(), config.isExclusive(), config.isAutoDelete(), null);
	}

	@Override
	public void subscribe(String appId, String exchangeName) throws IOException {
		this.queueName = exchangeName + "_" + appId;
		
		channel.queueDeclare(queueName, config.isDurable(), config.isExclusive(), config.isAutoDelete(), null);
		
		receiveQueue = new LinkedBlockingQueue<>();
		
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				try {
					receiveQueue.put(new RabbitMQMessage(envelope.getDeliveryTag(), body));
				} catch (InterruptedException e) {
					throw new IOException(e);
				}
			}
		};
		channel.basicConsume(queueName, false, consumer);
	}

	@Override
	public byte[] receive() throws IOException {
		try {
			RabbitMQMessage message = receiveQueue.poll(config.getTimeout(), config.getTimeoutTimeUnit());
			channel.basicAck(message.getId(), false);
			return message.getBody();
		} catch (InterruptedException e) {
			throw new IOException(e);
		}
	}

	@Override
	public boolean send(byte[] body) {
		channel.basicPublish
		return false;
	}
}