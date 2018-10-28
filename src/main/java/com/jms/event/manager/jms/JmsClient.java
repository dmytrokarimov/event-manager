package com.jms.event.manager.jms;

import java.io.Closeable;
import java.io.IOException;

public interface JmsClient extends Closeable{
	
	void register(String appId, String exchangeName) throws IOException;
	
	void subscribe(String appId, String exchangeName) throws IOException;
	
	byte[] receive() throws IOException;	
	
	boolean send(byte[] body) throws IOException;

}
