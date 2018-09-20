package com.littlecloud.control.json.model;

import java.util.HashMap;
import java.util.List;

public class Json_Web_Tunneling 
{
	private String command_id;
	private String command;
	private List<HashMap<String,String>> arg;
	private String action;
	private Integer version;
	
	public String getCommand_id() {
		return command_id;
	}
	public void setCommand_id(String command_id) {
		this.command_id = command_id;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public List<HashMap<String,String>> getArgc() {
		return arg;
	}
	public void setArgc(List<HashMap<String,String>> argc) {
		this.arg = argc;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	
}
