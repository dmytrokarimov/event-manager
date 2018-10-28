package com.jms.event.manager.jms;

import java.io.IOException;

public interface JmsClientFactory {
	
	JmsClient createClient() throws IOException;
}
