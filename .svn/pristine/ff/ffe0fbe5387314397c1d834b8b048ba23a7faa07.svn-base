package com.littlecloud.ac.json.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Json_Config_Icmg implements Serializable {

	private Integer version = 1;
	
	@SerializedName("feature")
	List<Icmg> icmgLst;

	public List<Icmg> getIcmgLst() {
		return icmgLst;
	}

	public void setIcmgLst(List<Icmg> icmgLst) {
		this.icmgLst = icmgLst;
	}

	public class Icmg implements Serializable {
		private String tag;
		private String value;

		public Icmg(String tag, String value) {
			super();
			this.tag = tag;
			this.value = value;
		}

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return "Icmg [tag=" + tag + ", value=" + value + "]";
		}
	}
}
