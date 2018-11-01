package com.jms.event.manager.service;

import java.util.UUID;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import com.jms.event.manager.dto.IDToken;

import lombok.Data;

@Service
@ConfigurationProperties(prefix="manager")
@Data
public class TokenService {

	private String tokenFormat;
	
	public IDToken generateToken() {
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		uuid = String.format(tokenFormat, (Object[]) uuid.split(""));
        return new IDToken(uuid);
	}
}
