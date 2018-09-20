package com.littlecloud.ac.messagehandler.queue.messages.impl;

import com.littlecloud.ac.messagehandler.queue.messages.Message;
import com.littlecloud.ac.messagehandler.queue.messages.MessageContent;

public class CaptivePortalMessageImpl<T> implements Message<T>{
	
	private MessageContent<T> messageContent;
	private String messageType;
	@Override
	public MessageContent<T> getMessageContent() {
		return messageContent;
	}
	@SuppressWarnings("unchecked")
	@Override
	public void setMessage(MessageContent<?> messageContent) {
		this.messageContent =  (MessageContent<T>) messageContent;
	}
	@Override
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	@Override
	public String getMessageType() {
		return messageType;
	}
	
	@Override
	public String toString() {
		return "CaptivePortalMessageImpl [messageContent=" + messageContent
				+ "]";
	}
	
}
