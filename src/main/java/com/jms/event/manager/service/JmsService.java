package com.jms.event.manager.service;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jms.event.manager.jms.JmsClient;
import com.jms.event.manager.jms.JmsClientFactory;

@Service
public class JmsService {

	private Map<String, JmsClient> clients = new ConcurrentHashMap<>();

	@Autowired
	private JmsClientFactory clientFactory;
	
	public JmsClient createClient(String id) throws IOException {
		JmsClient jmsClient = clientFactory.createClient();
		clients.put(id, jmsClient);
		
		return jmsClient;
	}
	
	public Set<String> getAllPublishers() {
		return clients.entrySet().stream().filter(entry -> entry.getValue().isPublisher()).map(Entry::getKey).collect(Collectors.toSet());
	}

	public Set<String> getAllConsumers() {
		return clients.entrySet().stream().filter(entry -> entry.getValue().isConsumer()).map(Entry::getKey).collect(Collectors.toSet());
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
