package com.littlecloud.pool.object;

import java.io.Serializable;

public class RegListObject extends PoolObject implements PoolObjectIf, Serializable{
	private Integer iana_id;
	private String sn;
	private String model;
	private String variant;
	
	
	public Integer getInna_id() {
		return iana_id;
	}


	public void setInna_id(Integer iana_id) {
		this.iana_id = iana_id;
	}


	public String getSn() {
		return sn;
	}


	public void setSn(String sn) {
		this.sn = sn;
	}


	public String getModel() {
		return model;
	}


	public void setModel(String model) {
		this.model = model;
	}


	public String getVariant() {
		return variant;
	}


	public void setVariant(String variant) {
		this.variant = variant;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RegListObject [iana_id=");
		builder.append(iana_id);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", model=");
		builder.append(model);
		builder.append(", variant=");
		builder.append(variant);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}


	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setKey(Integer iana_id, String sn) {
		// TODO Auto-generated method stub
		
	}
}
