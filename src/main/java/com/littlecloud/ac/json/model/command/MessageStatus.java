package com.littlecloud.ac.json.model.command;

public enum MessageStatus {
	SUCCESS(200);

	private int code;

	private MessageStatus(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
}
