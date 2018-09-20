package com.littlecloud.ac.messagehandler.queue.messages;

public interface Message <T>{
	public final static String MESSAGE_TYPE_AC = "MESSAGE_TYPE_AC";
	public final static String MESSAGE_TYPE_WEBSERVICE = "MESSAGE_TYPE_WEBSERVICE";
	public MessageContent<T> getMessageContent();
	public void setMessage(MessageContent<?> messageContent);
	public void setMessageType(String messageType);
	public String getMessageType();
}
