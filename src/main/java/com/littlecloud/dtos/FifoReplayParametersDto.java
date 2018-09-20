package com.littlecloud.dtos;

import java.util.List;

public class FifoReplayParametersDto {
	private String createAtFrom;
	private String createAtTo;
	private String networkId;
	private String orgId;
	private String sn;
	private List<String>messageTypeList;
	public String getCreateAtFrom() {
		return createAtFrom;
	}
	public void setCreateAtFrom(String createAtFrom) {
		this.createAtFrom = createAtFrom;
	}
	public String getCreateAtTo() {
		return createAtTo;
	}
	public void setCreateAtTo(String createAtTo) {
		this.createAtTo = createAtTo;
	}
	public String getNetworkId() {
		return networkId;
	}
	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public List<String> getMessageTypeList() {
		return messageTypeList;
	}
	public void setMessageTypeList(List<String> messageTypeList) {
		this.messageTypeList = messageTypeList;
	}
	
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FifoReplayParametersDto [createAtFrom=");
		builder.append(createAtFrom);
		builder.append(", createAtTo=");
		builder.append(createAtTo);
		builder.append(", networkId=");
		builder.append(networkId);
		builder.append(", orgId=");
		builder.append(orgId);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", messageTypeList=");
		builder.append(messageTypeList);
		builder.append("]");
		return builder.toString();
	}

	
}
