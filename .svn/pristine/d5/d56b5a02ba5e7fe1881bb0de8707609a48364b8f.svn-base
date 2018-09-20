package com.littlecloud.control.json;

import java.util.Date;

public class JsonResponse<T> {

	public static enum ResponseCode {
		UNDEFINED, SUCCESS, PENDING, MISSING_INPUT, INVALID_INPUT, INTERNAL_ERROR, INVALID_VERSION, INVALID_CALLER_REF, INVALID_OPERATION;
	}

	protected ResponseCode resp_code = ResponseCode.UNDEFINED;
	protected Integer error;
	protected String caller_ref;
	protected String server_ref;
	protected String message;
	protected Long ellapse_time;
	protected Date timestamp;
	protected Date network_time;
	protected Date start_time;
	protected Date end_time;
	protected String page_id;
	protected Boolean isExist;
	protected Date first_date;
	
	protected T data;

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
	
	public Date getNetwork_time() {
		return network_time;
	}

	public void setNetwork_time(Date network_time) {
		this.network_time = network_time;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public ResponseCode getResp_code() {
		return resp_code;
	}

	public void setResp_code(ResponseCode resp_code) {
		this.resp_code = resp_code;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public String getCaller_ref() {
		return caller_ref;
	}

	public void setCaller_ref(String caller_ref) {
		this.caller_ref = caller_ref;
	}

	public String getServer_ref() {
		return server_ref;
	}

	public void setServer_ref(String server_ref) {
		this.server_ref = server_ref;
	}

	public long getEllapse_time() {
		return ellapse_time;
	}

	public void setEllapse_time(long ellapse_time) {
		this.ellapse_time = ellapse_time;
	}

	public Date getStart_time() {
		return start_time;
	}

	public void setStart_time(Date start_time) {
		this.start_time = start_time;
	}

	public Date getEnd_time() {
		return end_time;
	}

	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}

	public String getPage_id() {
		return page_id;
	}

	public void setPage_id(String page_id) {
		this.page_id = page_id;
	}

	public Date getFirst_date() {
		return first_date;
	}

	public void setFirst_date(Date first_date) {
		this.first_date = first_date;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JsonResponse [resp_code=");
		builder.append(resp_code);
		builder.append(", error=");
		builder.append(error);
		builder.append(", caller_ref=");
		builder.append(caller_ref);
		builder.append(", server_ref=");
		builder.append(server_ref);
		builder.append(", message=");
		builder.append(message);
		builder.append(", ellapse_time=");
		builder.append(ellapse_time);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append(", network_time=");
		builder.append(network_time);
		builder.append(", start_time=");
		builder.append(start_time);
		builder.append(", end_time=");
		builder.append(end_time);
		builder.append(", page_id=");
		builder.append(page_id);
		builder.append(", isExist=");
		builder.append(isExist);
		builder.append(", first_date=");
		builder.append(first_date);
		builder.append(", data=");
		builder.append(data);
		builder.append("]");
		return builder.toString();
	}
	
}
