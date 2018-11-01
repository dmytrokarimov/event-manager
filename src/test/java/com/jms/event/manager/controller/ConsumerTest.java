package com.jms.event.manager.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.jms.event.manager.service.JmsService;

@RunWith(SpringRunner.class)
@WebMvcTest(ConsumerController.class)
public class ConsumerTest {

	@Autowired
    private MockMvc mockMvc;
	
	@Autowired
	private JmsService jmsService;
    
	@Test
	public void checkGetAllId() throws Exception {
		jmsService.createClient("test1").register("", "");
		jmsService.createClient("test2").subscribe("", "");
		jmsService.createClient("test3").subscribe("", "");
		
		mockMvc.perform(MockMvcRequestBuilders.get("/consumer"))
        	.andExpect(status().isOk())
        	.andExpect(jsonPath("$.body", hasSize(2)))
            .andDo(print());
	}
	
	@Test
	public void checkRegister() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/consumer/subscribe?appId=test&exchangeName=test"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.body", not(isEmptyString())))
			.andDo(print());
	}
	
	@Test
	public void checkDelete() throws Exception {
		jmsService.createClient("test").subscribe("", "");
		
		mockMvc.perform(MockMvcRequestBuilders.delete("/consumer/" + (new ArrayList<>(jmsService.getAllPublishers()).get(0))))
			.andExpect(status().isOk())
			.andDo(print());
	}
}
