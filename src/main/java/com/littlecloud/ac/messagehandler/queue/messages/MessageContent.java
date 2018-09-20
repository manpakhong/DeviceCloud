package com.littlecloud.ac.messagehandler.queue.messages;

public interface MessageContent <T> {
	public T getData();
	public void setData(T data);
	public Integer getIanaId();
	public void setIanaId(Integer ianaId);
	public String getSn();
	public void setSn(String sn);
}
