package com.littlecloud.control.dao.support.criteria;

import java.util.Date;

public class DeviceDiagReportCriteria {
	private Integer id;
	private String orgId;
	private Integer ianaId;
	private String sn;
	private Integer unixtimeFrom;
	private Integer unixtimeTo;
	private Date createDateFrom;
	private Date createDateTo;
	private boolean isReportContentIncluded;
	
	public DeviceDiagReportCriteria(){
		isReportContentIncluded = false;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public Integer getIanaId() {
		return ianaId;
	}

	public void setIanaId(Integer ianaId) {
		this.ianaId = ianaId;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public Integer getUnixtimeFrom() {
		return unixtimeFrom;
	}

	public void setUnixtimeFrom(Integer unixtimeFrom) {
		this.unixtimeFrom = unixtimeFrom;
	}

	public Integer getUnixtimeTo() {
		return unixtimeTo;
	}

	public void setUnixtimeTo(Integer unixtimeTo) {
		this.unixtimeTo = unixtimeTo;
	}

	public Date getCreateDateFrom() {
		return createDateFrom;
	}

	public void setCreateDateFrom(Date createDateFrom) {
		this.createDateFrom = createDateFrom;
	}

	public Date getCreateDateTo() {
		return createDateTo;
	}

	public void setCreateDateTo(Date createDateTo) {
		this.createDateTo = createDateTo;
	}
	
	public boolean isReportContentIncluded() {
		return isReportContentIncluded;
	}

	public void setReportContentIncluded(boolean isReportContentIncluded) {
		this.isReportContentIncluded = isReportContentIncluded;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DeviceDiagReportCriteria [id=");
		builder.append(id);
		builder.append(", orgId=");
		builder.append(orgId);
		builder.append(", ianaId=");
		builder.append(ianaId);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", unixtimeFrom=");
		builder.append(unixtimeFrom);
		builder.append(", unixtimeTo=");
		builder.append(unixtimeTo);
		builder.append(", createDateFrom=");
		builder.append(createDateFrom);
		builder.append(", createDateTo=");
		builder.append(createDateTo);
		builder.append(", isReportContentIncluded=");
		builder.append(isReportContentIncluded);
		builder.append("]");
		return builder.toString();
	}
}
