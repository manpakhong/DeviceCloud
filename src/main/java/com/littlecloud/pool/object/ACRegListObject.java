package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.ArrayList;

public class ACRegListObject extends PoolObject implements PoolObjectIf, Serializable {

//	{
//		"iana_id": 23695,
//		"sn": "2830-E24C-0774",
//		"model": "PWMAXBR1",
//		"variant": "GENERIC"
//	},
//	{
//		"iana_id": 23695,
//		"sn": "1824-C29B-8C5C",
//		"model": "PLB380",
//		"variant": "GENERIC"
//	}

	private String sid;
	private int iana_id;
	private String sn;
	
	private ArrayList<DevInfoObject> dev_list;
	
	@Override
	public String getKey() {
		return this.getClass().getSimpleName() + "sn_pk" + getSn() + "|" + getIana_id();
	}

	@Override
	public void setKey(Integer iana_id, String sn) {
		this.iana_id = iana_id;
		this.sn = sn;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public int getIana_id() {
		return iana_id;
	}

	public void setIana_id(int iana_id) {
		this.iana_id = iana_id;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public ArrayList<DevInfoObject> getDev_list() {
		return dev_list;
	}

	public void setDev_list(ArrayList<DevInfoObject> dev_list) {
		this.dev_list = dev_list;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ACRegListObject [sid=");
		builder.append(sid);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", dev_list=");
		builder.append(dev_list);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}
}
