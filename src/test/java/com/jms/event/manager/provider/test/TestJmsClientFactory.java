package com.jms.event.manager.provider.test;

import java.io.IOException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.jms.event.manager.jms.JmsClient;
import com.jms.event.manager.jms.JmsClientFactory;

@ConditionalOnProperty(name="jms.provider", havingValue="TEST")
@Component
public class TestJmsClientFactory implements JmsClientFactory {

	@Override
	public JmsClient createClient() throws IOException {
		return new TestJmsClient();
	}


}
