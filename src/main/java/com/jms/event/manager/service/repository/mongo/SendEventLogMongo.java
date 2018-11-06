package com.jms.event.manager.service.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.jms.event.manager.service.repository.dto.SendMessage;

public interface SendEventLogMongo extends MongoRepository<SendMessage, String>{
}
