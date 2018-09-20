package com.littlecloud.ac.json.model;

public class Json_DiagReport {
	private String filepath;

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Json_DiagReport [filepath=");
		builder.append(filepath);
		builder.append("]");
		return builder.toString();
	}
	
}
