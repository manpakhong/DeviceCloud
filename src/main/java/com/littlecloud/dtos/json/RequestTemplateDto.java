package com.littlecloud.dtos.json;


public class RequestTemplateDto {
	private String msg_id;
	private String msg_type;
	private String sender_email;
	private String sender_name;
	private String recipent;
	private Integer priority;
	private RequestTemplateDataDto data;
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
	public String getRecipent() {
		return recipent;
	}
	public void setRecipent(String recipent) {
		this.recipent = recipent;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public RequestTemplateDataDto getData() {
		return data;
	}
	public void setData(RequestTemplateDataDto data) {
		this.data = data;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RequestTemplateDto [msg_id=");
		builder.append(msg_id);
		builder.append(", msg_type=");
		builder.append(msg_type);
		builder.append(", sender_email=");
		builder.append(sender_email);
		builder.append(", sender_name=");
		builder.append(sender_name);
		builder.append(", recipent=");
		builder.append(recipent);
		builder.append(", priority=");
		builder.append(priority);
		builder.append(", data=");
		builder.append(data);
		builder.append("]");
		return builder.toString();
	}

	
}
