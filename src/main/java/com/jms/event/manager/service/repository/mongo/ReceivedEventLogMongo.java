package com.jms.event.manager.service.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.jms.event.manager.service.repository.dto.ReceivedMessage;

public interface ReceivedEventLogMongo extends MongoRepository<ReceivedMessage, String>{
}
