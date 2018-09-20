package com.littlecloud.control.json.model.config.util.exception;

/* This exception occurs when device feature is not received from device */ 
public class SystemConfigException extends Exception {
	
	public SystemConfigException(String message) {
		super(message);
	}

	public SystemConfigException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
