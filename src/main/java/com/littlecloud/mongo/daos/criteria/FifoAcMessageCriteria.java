package com.littlecloud.mongo.daos.criteria;

import java.util.Date;
import java.util.List;

import com.littlecloud.dtos.MongoCriteriaAnnotation;

public class FifoAcMessageCriteria {

	public static final String DIR_CORE = "Core";
	public static final String DIR_WTP = "WTP";
	
	@MongoCriteriaAnnotation(fieldType="String", fieldName="dir", rangeType=MongoCriteriaAnnotation.RANGE_TYPE_NONE)
	private String dir;
	@MongoCriteriaAnnotation(fieldType="String", fieldName="payload.type", rangeType=MongoCriteriaAnnotation.RANGE_TYPE_NONE)
	private List<String> messageTypeList;
	@MongoCriteriaAnnotation(fieldType="String", fieldName="payload.sn", rangeType=MongoCriteriaAnnotation.RANGE_TYPE_NONE)
	private List<String> snList;
	@MongoCriteriaAnnotation(fieldType="Integer", fieldName="payload.iana_id", rangeType=MongoCriteriaAnnotation.RANGE_TYPE_NONE)
	private Integer ianaId;
	@MongoCriteriaAnnotation(fieldType="String", fieldName="payload.sid", rangeType=MongoCriteriaAnnotation.RANGE_TYPE_NONE)
	private String sid;
	@MongoCriteriaAnnotation(fieldType="Integer", fieldName="timestamp", rangeType=MongoCriteriaAnnotation.RANGE_TYPE_FROM)
	private Date dateFrom;
	@MongoCriteriaAnnotation(fieldType="Integer", fieldName="timestamp", rangeType=MongoCriteriaAnnotation.RANGE_TYPE_TO)
	private Date dateTo;
	private Integer limit;
	public String getDir() {
		return dir;
	}
	public void setDir(String dir) {
		this.dir = dir;
	}
	public List<String> getMessageTypeList() {
		return messageTypeList;
	}
	public void setMessageTypeList(List<String> messageTypeList) {
		this.messageTypeList = messageTypeList;
	}
	public List<String> getSnList() {
		return snList;
	}
	public void setSnList(List<String> snList) {
		this.snList = snList;
	}
	public Integer getIanaId() {
		return ianaId;
	}
	public void setIanaId(Integer ianaId) {
		this.ianaId = ianaId;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public Date getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}
	public Date getDateTo() {
		return dateTo;
	}
	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FifoAcMessageCriteria [dir=");
		builder.append(dir);
		builder.append(", messageTypeList=");
		builder.append(messageTypeList);
		builder.append(", snList=");
		builder.append(snList);
		builder.append(", ianaId=");
		builder.append(ianaId);
		builder.append(", sid=");
		builder.append(sid);
		builder.append(", dateFrom=");
		builder.append(dateFrom);
		builder.append(", dateTo=");
		builder.append(dateTo);
		builder.append(", limit=");
		builder.append(limit);
		builder.append("]");
		return builder.toString();
	}


	
}
