package com.littlecloud.dtos.json;


public class RequestTemplateDataFullDto {
	private String msg_id;
	private String msg_type;
	private RequestTemplateDataDto data;
	private String sender_email;
	private String sender_name;
	private String recipient;
	private Integer priority;
	public String getMsg_id() {
		return msg_id;
	}
	public void setMsg_id(String msg_id) {
		this.msg_id = msg_id;
	}
	public String getMsg_type() {
		return msg_type;
	}
	public void setMsg_type(String msg_type) {
		this.msg_type = msg_type;
	}

	public RequestTemplateDataDto getData() {
		return data;
	}
	public void setData(RequestTemplateDataDto data) {
		this.data = data;
	}
	public String getSender_email() {
		return sender_email;
	}
	public void setSender_email(String sender_email) {
		this.sender_email = sender_email;
	}
	public String getSender_name() {
		return sender_name;
	}
	public void setSender_name(String sender_name) {
		this.sender_name = sender_name;
	}
	public String getRecipient() {
		return recipient;
	}
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RequestTemplateDataFullDto [msg_id=");
		builder.append(msg_id);
		builder.append(", msg_type=");
		builder.append(msg_type);
		builder.append(", data=");
		builder.append(data);
		builder.append(", sender_email=");
		builder.append(sender_email);
		builder.append(", sender_name=");
		builder.append(sender_name);
		builder.append(", recipient=");
		builder.append(recipient);
		builder.append(", priority=");
		builder.append(priority);
		builder.append("]");
		return builder.toString();
	}
	
}
