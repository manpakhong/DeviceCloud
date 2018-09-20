package com.littlecloud.dtos.json;

public class TemplateDevDataDto {
	private String name;
	private String sn;
	private String product_name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getProduct_name() {
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TemplateDevDataDto [name=");
		builder.append(name);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", product_name=");
		builder.append(product_name);
		builder.append("]");
		return builder.toString();
	}
	
	
}
