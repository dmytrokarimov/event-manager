package com.jms.event.manager.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.jms.event.manager.jms.JmsClient;
import com.jms.event.manager.service.JmsService;

@RunWith(SpringRunner.class)
@WebMvcTest(MessageController.class)
public class MessageTest {

	@Autowired
    private MockMvc mockMvc;
	
	@Autowired
	private JmsService jmsService;
    
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
