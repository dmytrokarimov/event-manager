package com.jms.event.manager.dto;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@Value
@AllArgsConstructor
public class JsonResponse <T>{
	
	@NonNull
	private OperationStates state;
	
	private T body;
	
	public static enum OperationStates {
		SUCCESS, FAIL, NOT_FOUND
	}
}
