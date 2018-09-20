package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.littlecloud.ac.ACService;
import com.littlecloud.control.json.util.DateUtils;

public class DevInfoObject extends PoolObject implements PoolObjectIf, Serializable {

	public static final int MAX_LOG_SIZE = 3000;
	public static final int MAX_MISS_COUNT_FOR_ONLINE_EVENT_LOG = 1;

	/* key */
	private String sn;
	private Integer iana_id;
	/* value */
	private Integer ts = 0;
	private Integer missingOnlineObjectCount = 0;
	private StringBuilder debugInfo = new StringBuilder();

	public DevInfoObject(String sn, Integer iana_id) {
		super();
		this.sn = sn;
		this.iana_id = iana_id;
	}
	
	public void addDebugInfo(String msg) {
		String info = formatDebugInfo(msg);
		if (debugInfo==null)
			debugInfo = new StringBuilder();
			
		if (debugInfo.length()>MAX_LOG_SIZE)
		{
			debugInfo.delete(0, debugInfo.length()-1000);
			debugInfo.setCharAt(0, '.');
			debugInfo.setCharAt(1, '.');
			debugInfo.setCharAt(2, '.');
			debugInfo.append("\n");
		}
		debugInfo.append(info);
	}
	
	public StringBuilder getDebugInfo() {
		return debugInfo;
	}

	public void setDebugInfo(String msg) {		
		this.debugInfo = new StringBuilder(formatDebugInfo(msg));
	}
	
	private String formatDebugInfo(String msg)
	{
		return String.format("[%s][%s] %s (%s-%s)\n", DateUtils.getUtcDate().toString(), ACService.getServerName(), msg, 
				Thread.currentThread().getName(), Thread.currentThread().getId());
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public Integer getIana_id() {
		return iana_id;
	}

	public void setIana_id(Integer iana_id) {
		this.iana_id = iana_id;
	}
	
	public Integer getTs() {
		return ts;
	}

	public void setTs(Integer ts) {
		this.ts = ts;
	}
	public Integer getMissingOnlineObjectCount() {
		return missingOnlineObjectCount;
	}

	public void setMissingOnlineObjectCount(Integer missingOnlineObjectCount) {
		this.missingOnlineObjectCount = missingOnlineObjectCount;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DevInfoObject [sn=");
		builder.append(sn);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", ts=");
		builder.append(ts);
		builder.append(", missingOnlineObjectCount=");
		builder.append(missingOnlineObjectCount);
		builder.append(", debugInfo=");
		builder.append(debugInfo);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public void setKey(Integer iana_id, String sn) {
		this.iana_id = iana_id;
		this.sn = sn;
	}

	@Override
	public String getKey() {
		return this.getClass().getSimpleName() + "sn_pk" + getSn() + "|" + getIana_id();
	}

}
