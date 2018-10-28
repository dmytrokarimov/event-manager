package com.jms.event.manager.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jms.event.manager.dto.AccessToken;
import com.jms.event.manager.dto.JsonResponse;
import com.jms.event.manager.dto.JsonResponse.OperationStates;
import com.jms.event.manager.service.JmsService;
import com.jms.event.manager.service.TokenService;

@RestController
@RequestMapping(value = "/subscribe")
public class SubscribeController {
	
	@Autowired
	private TokenService tokenService;

	@Autowired
	private JmsService jmsService;
	
	@RequestMapping(method = RequestMethod.POST, value = "/")
	public JsonResponse<AccessToken> register(
			@RequestParam(value = "appId") String appId,
			@RequestParam(value = "exchangeName") String exchangeName) throws IOException{
		AccessToken token = tokenService.generateToken();
		
		jmsService
			.createClient(token.getAccessToken())
			.subscribe(appId, exchangeName);
		
		return new JsonResponse<>(OperationStates.SUCCESS, token);
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/{clientId}")
	public JsonResponse<AccessToken> subscribe(
			@PathVariable String clientId){
		boolean result = jmsService.closeClient(clientId);
		
		return new JsonResponse<>(result ? OperationStates.SUCCESS : OperationStates.NOT_FOUND, new AccessToken(clientId));
	}
}
