package com.littlecloud.ac.messagehandler.threads;

public interface MessageHandlerThread <T> extends Runnable {
	public void setMessageObject (T messageObject);
	public boolean handleMessageObject();
}
