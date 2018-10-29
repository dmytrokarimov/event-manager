package com.jms.event.manager.jms.provider.amqp;

import java.io.IOException;
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
	public void register(String appId, String exchangeName) throws IOException {
		this.exchangeName = exchangeName;
		this.queueName = appId;
		
		channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT, config.isDurable());

		//generate random queue name
//		this.queueName = channel.queueDeclare("", config.isDurable(), config.isExclusive(), config.isAutoDelete(), null).getQueue();
//		channel.queueBind(queueName, exchangeName, appId);
	}

	@Override
	public void subscribe(String appId, String exchangeName) throws IOException {
		this.queueName = appId;
		this.exchangeName = exchangeName;

//		channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT, config.isDurable());
		
		//generate random queue name
		this.queueName = channel.queueDeclare("", config.isDurable(), config.isExclusive(), config.isAutoDelete(), null).getQueue();
		channel.queueBind(queueName, exchangeName, appId);
		
		receiveQueue = new LinkedBlockingQueue<>();
		
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				try {
					System.out.println("New message: " + envelope.getDeliveryTag());
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
		channel.basicPublish(exchangeName, queueName, null, body);
		return true;
	}
}