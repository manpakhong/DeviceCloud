package com.littlecloud.control.dao.jdbc;

public class BaseDaoInstances {
	public static enum InstanceType {
		CAPTIVEPORTAL("db.captiveportal.properties"),		
		IC2CORE("db.properties"),
		IC2ROOT("db.root.properties"),
		SUPPORTDB("db.support.properties");
		private String value;
		
		private InstanceType(String value){
			this.value = value;
		}
		
		public String getValue(){
			return this.value;
		}
		
		public void setValue(String value){
			this.value = value;
		}
	}
}
