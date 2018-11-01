package com.jms.event.manager.controller;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jms.event.manager.dto.IDToken;
import com.jms.event.manager.dto.JsonResponse;
import com.jms.event.manager.dto.JsonResponse.OperationStates;
import com.jms.event.manager.service.JmsService;
import com.jms.event.manager.service.TokenService;

@RestController
@RequestMapping(value = "/consumer")
public class ConsumerController {
	
	@Autowired
	private TokenService tokenService;

	@Autowired
	private JmsService jmsService;

	@RequestMapping(method = RequestMethod.GET, value = "")
	public JsonResponse<Set<String>> getAllIds() throws IOException{
		return new JsonResponse<>(OperationStates.SUCCESS, jmsService.getAllConsumers());
	}
	
	@PostMapping(path = "/subscribe")
	public JsonResponse<IDToken> subscribe(
			@RequestParam(value = "appId") String appId,
			@RequestParam(value = "exchangeName") String exchangeName) throws IOException{
		IDToken token = tokenService.generateToken();
		
		jmsService
			.createClient(token.getId())
			.subscribe(appId, exchangeName);
		
		return new JsonResponse<>(OperationStates.SUCCESS, token);
	}
	
	@PostMapping(path = "/subscribe", params = "target=dlq")
	public JsonResponse<IDToken> subscribeDLQ(
			@RequestParam(value = "appId") String appId,
			@RequestParam(value = "exchangeName") String exchangeName) throws IOException{
		IDToken token = tokenService.generateToken();
		
		jmsService
			.createClient(token.getId())
			.subscribeToDLQ(appId, exchangeName);
		
		return new JsonResponse<>(OperationStates.SUCCESS, token);
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/{clientId}")
	public JsonResponse<IDToken> subscribe(
			@PathVariable String clientId){
		boolean result = jmsService.closeClient(clientId);
		
		return new JsonResponse<>(result ? OperationStates.SUCCESS : OperationStates.NOT_FOUND, new IDToken(clientId));
	}
}
