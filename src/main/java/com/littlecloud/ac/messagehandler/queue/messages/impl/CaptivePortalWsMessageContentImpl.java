package com.littlecloud.ac.messagehandler.queue.messages.impl;

import com.littlecloud.ac.messagehandler.queue.messages.MessageContent;
import com.littlecloud.control.json.request.JsonCaptivePortalRequest;

public class CaptivePortalWsMessageContentImpl implements MessageContent<JsonCaptivePortalRequest> {
	private JsonCaptivePortalRequest data;
	private Integer ianaId;
	private String sn;

	@Override
	public JsonCaptivePortalRequest getData() {
		return data;
	}

	@Override
	public void setData(JsonCaptivePortalRequest data) {
		this.data = data;
	}

	@Override
	public Integer getIanaId() {
		return ianaId;
	}

	@Override
	public void setIanaId(Integer ianaId) {
		this.ianaId = ianaId;
	}

	@Override
	public String getSn() {
		return sn;
	}

	@Override
	public void setSn(String sn) {
		this.sn = sn;
	}

	@Override
	public String toString() {
		return "CaptivePortalWsMessageContentImpl [data=" + data + ", ianaId="
				+ ianaId + ", sn=" + sn + "]";
	}

}
