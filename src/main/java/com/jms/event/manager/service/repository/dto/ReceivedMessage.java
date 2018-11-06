package com.jms.event.manager.service.repository.dto;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = ReceivedMessage.COLLECTION_NAME)
public class ReceivedMessage implements Serializable {
	private static final long serialVersionUID = 4662812446057477185L;

	public static final String COLLECTION_NAME = "receivelog";

	@Id
	private ObjectId _id = ObjectId.get();

	private byte[] message;

	public ReceivedMessage() {
	}

	public ReceivedMessage(byte[] message) {
		this.message = message;
	}
	
	public String get_id() {
		return _id.toHexString();
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public byte[] getMessage() {
		return message;
	}

	public void setMessage(byte[] message) {
		this.message = message;
	}

}
