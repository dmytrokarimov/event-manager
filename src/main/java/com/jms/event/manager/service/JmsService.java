package com.jms.event.manager.service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jms.event.manager.jms.JmsClient;
import com.jms.event.manager.jms.JmsClientFactory;

@Service
public class JmsService {

	private Map<String, JmsClient> clients = new ConcurrentHashMap<>();

	@Autowired
	private JmsClientFactory clientFactory;
	
	public JmsClient createClient(String id) {
		JmsClient jmsClient = clientFactory.createClient();
		clients.put(id, jmsClient);
		
		return jmsClient;
	}
	
	public Optional<JmsClient> getClientFor(String id) {
		return Optional.ofNullable(clients.get(id));
	}
	
	public boolean closeClient(String id) {
		return getClientFor(id)
			.map(client -> {
				try {
					client.close();
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
				return clients.remove(id);
			})
			.isPresent();
	}
}
