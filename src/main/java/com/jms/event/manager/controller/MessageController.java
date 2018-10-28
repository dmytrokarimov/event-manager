package com.jms.event.manager.controller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jms.event.manager.dto.JsonResponse;
import com.jms.event.manager.dto.JsonResponse.OperationStates;
import com.jms.event.manager.dto.OperationResult;
import com.jms.event.manager.jms.JmsClient;
import com.jms.event.manager.service.JmsService;

@RestController
@RequestMapping(value = "/event")
public class MessageController {
	
	@Autowired
	private JmsService jmsService;
	
	@RequestMapping(method = RequestMethod.POST, value = "/send/{clientId}")
	public JsonResponse<OperationResult> send(
			@PathVariable String clientId,
			@RequestBody byte[] body) throws IOException{
		Optional<JmsClient> client = jmsService.getClientFor(clientId);
		
		if (client.isPresent()) {
			boolean state = client.get().send(body);
			if (state) {
				return new JsonResponse<>(OperationStates.SUCCESS, new OperationResult("Message have been sent"));
			}
			return new JsonResponse<>(OperationStates.FAIL, new OperationResult("Error"));
		}
			
		return new JsonResponse<>(OperationStates.NOT_FOUND, new OperationResult("Client [" + clientId +"] is not found"));
	}
	
	@RequestMapping(value = "/receive/{clientId}")
	public JsonResponse<OperationResult> receive(
			@PathVariable String clientId) throws IOException{
		Optional<JmsClient> client = jmsService.getClientFor(clientId);
		
		if (client.isPresent()) {
			byte[] message = client.get().receive();
			return new JsonResponse<>(OperationStates.SUCCESS, new OperationResult(new String(message)));
		}
			
		return new JsonResponse<>(OperationStates.NOT_FOUND, new OperationResult("Client [" + clientId +"] is not found"));
	}
}
