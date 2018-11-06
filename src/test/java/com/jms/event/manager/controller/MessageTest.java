package com.jms.event.manager.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.jms.event.manager.SpringMongoConfiguration;
import com.jms.event.manager.jms.JmsClient;
import com.jms.event.manager.service.JmsService;
import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

@RunWith(SpringRunner.class)
@WebMvcTest(MessageController.class)
@ContextConfiguration(classes = SpringMongoConfiguration.class)
@EnableAutoConfiguration
public class MessageTest {

	@Autowired
    private MockMvc mockMvc;
	
	@Autowired
	private JmsService jmsService;
    
	private static MongodExecutable mongodExecutable;
 
    @AfterClass
    public static void clean() {
        mongodExecutable.stop();
    }
 
    @BeforeClass
    public static void setup() throws Exception {
        String ip = "localhost";
        int port = 27017;
 
        IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.DEVELOPMENT)
            .net(new Net(ip, port, Network.localhostIsIPv6()))
            .build();
 
        MongodStarter starter = MongodStarter.getDefaultInstance();
        mongodExecutable = starter.prepare(mongodConfig);
        mongodExecutable.start();
    }
    
	@Test
	public void checkSend() throws Exception {
		String clientId = "test";
		JmsClient client = jmsService.createClient(clientId);
		
		String body = "body";
		
		mockMvc.perform(MockMvcRequestBuilders.post("/event/send/" + clientId).content(body.getBytes()))
        	.andExpect(status().isOk())
            .andDo(print());
		
		assertEquals(body, new String(client.receive()));
	}
	
	@Test
	public void checkReceive() throws Exception {
		String clientId = "test";
		JmsClient client = jmsService.createClient(clientId);
		
		String body = "body";
		client.send(body.getBytes());
		
		mockMvc.perform(MockMvcRequestBuilders.post("/event/receive/" + clientId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.body.data", equalTo(body)))
			.andDo(print());
	}
}
