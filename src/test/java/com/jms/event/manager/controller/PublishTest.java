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
@WebMvcTest(PublisherController.class)
public class PublishTest {

	@Autowired
    private MockMvc mockMvc;
	
	@Autowired
	private JmsService jmsService;
    
	@Test
	public void checkGetAllId() throws Exception {
		jmsService.createClient("test").register("", "");
		
		mockMvc.perform(MockMvcRequestBuilders.get("/publisher"))
        	.andExpect(status().isOk())
        	.andExpect(jsonPath("$.body", hasSize(1)))
            .andDo(print());
	}
	
	@Test
	public void checkRegister() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/publisher/register?appId=test&exchangeName=test"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.body", not(isEmptyString())))
			.andDo(print());
	}
	
	@Test
	public void checkDelete() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/publisher/" + (new ArrayList<>(jmsService.getAllPublishers()).get(0))))
			.andExpect(status().isOk())
			.andDo(print());
	}
}
