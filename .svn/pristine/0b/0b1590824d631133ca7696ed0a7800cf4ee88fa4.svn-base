package com.littlecloud.ac.handler;

import com.littlecloud.ac.json.model.command.QueryInfo;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.util.DBUtil;

public class MessageInfo {
	private QueryInfo info = null;
	private String json = null;
	private String orgId = null;
	
	private Integer queueSize = 0;		
	private DBUtil dbUtil = null;
	private DBConnection batchConnection = null;

	public QueryInfo getInfo() {
		return info;
	}

	public void setInfo(QueryInfo info) {
		this.info = info;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public Integer getQueueSize() {
		return queueSize;
	}

	public void setQueueSize(Integer queueSize) {
		this.queueSize = queueSize;
	}
	
	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public DBUtil getDbUtil() {
		return dbUtil;
	}

	public void setDbUtil(DBUtil dbUtil) {
		this.dbUtil = dbUtil;
	}

	public DBConnection getBatchConnection() {
		return batchConnection;
	}

	public void setBatchConnection(DBConnection batchConnection) {
		this.batchConnection = batchConnection;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MessageInfo [info=");
		builder.append(info);
		builder.append(", json=");
		builder.append(json);
		builder.append(", orgId=");
		builder.append(orgId);
		builder.append(", queueSize=");
		builder.append(queueSize);
		builder.append(", dbUtil=");
		builder.append(dbUtil);
		builder.append(", batchConnection=");
		builder.append(batchConnection);
		builder.append("]");
		return builder.toString();
	}

}