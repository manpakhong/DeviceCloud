package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SystemInfoObject extends PoolObject implements PoolObjectIf, Serializable
{
	private Integer sharedram;
	private Long totalram;
	private CopyOnWriteArrayList<Integer> loads;
	private Integer totalswap;
	private Long uptime;
	private Long freeram;
	private Integer freeswap;
	private Integer procs;
	private Long bufferram;
	
	/* key */
	private Integer iana_id;
	private String sn;

	public Integer getSharedram() {
		return sharedram;
	}

	public void setSharedram(Integer sharedram) {
		this.sharedram = sharedram;
	}

	public Long getTotalram() {
		return totalram;
	}

	public void setTotalram(Long totalram) {
		this.totalram = totalram;
	}

	public CopyOnWriteArrayList<Integer> getLoads() {
		return loads;
	}

	public void setLoads(CopyOnWriteArrayList<Integer> loads) {
		this.loads = loads;
	}

	public Integer getTotalswap() {
		return totalswap;
	}

	public void setTotalswap(Integer totalswap) {
		this.totalswap = totalswap;
	}

	public Long getUptime() {
		return uptime;
	}

	public void setUptime(Long uptime) {
		this.uptime = uptime;
	}

	public Long getFreeram() {
		return freeram;
	}

	public void setFreeram(Long freeram) {
		this.freeram = freeram;
	}

	public Integer getFreeswap() {
		return freeswap;
	}

	public void setFreeswap(Integer freeswap) {
		this.freeswap = freeswap;
	}

	public Integer getProcs() {
		return procs;
	}

	public void setProcs(Integer procs) {
		this.procs = procs;
	}

	public Long getBufferram() {
		return bufferram;
	}

	public void setBufferram(Long bufferram) {
		this.bufferram = bufferram;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SystemInfoObject [sharedram=");
		builder.append(sharedram);
		builder.append(", totalram=");
		builder.append(totalram);
		builder.append(", loads=");
		builder.append(loads);
		builder.append(", totalswap=");
		builder.append(totalswap);
		builder.append(", uptime=");
		builder.append(uptime);
		builder.append(", freeram=");
		builder.append(freeram);
		builder.append(", freeswap=");
		builder.append(freeswap);
		builder.append(", procs=");
		builder.append(procs);
		builder.append(", bufferram=");
		builder.append(bufferram);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public String getKey() {
		return this.getClass().getSimpleName() + "sn_pk" + getSn() + "|" + getIana_id();
	}

	@Override
	public void setKey(Integer iana_id, String sn) {
		this.iana_id = iana_id;
		this.sn = sn;
	}

}
