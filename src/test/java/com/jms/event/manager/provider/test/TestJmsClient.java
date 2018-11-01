package com.jms.event.manager.provider.test;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.jms.event.manager.jms.JmsClient;

import lombok.Getter;

public class TestJmsClient implements JmsClient {

	@Getter
	private boolean publisher;
	
	private BlockingQueue<byte[]> receiveQueue;
	
	public TestJmsClient() {
		receiveQueue = new LinkedBlockingQueue<>();
	}
	
	@Override
	public void close() throws IOException {
	}

	@Override
	public boolean isConsumer() {
		return !publisher;
	}

	@Override
	public void register(String appId, String exchangeName) throws IOException {
		publisher = true;
	}

	@Override
	public void subscribe(String appId, String exchangeName) throws IOException {
	}

	@Override
	public void subscribeToDLQ(String appId, String exchangeName) throws IOException {
	}

	@Override
	public byte[] receive() throws IOException {
		return receiveQueue.poll();
	}

	@Override
	public boolean send(byte[] body) throws IOException {
		try {
			receiveQueue.put(body);
		} catch (InterruptedException e) {
			throw new IOException(e);
		}
		return true;
	}

}
