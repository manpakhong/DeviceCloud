package com.littlecloud.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class RedirectCapwapUtils {
	private String result;
	private final String CMD_FIRST_PART = "/bin/sh";
	private final String CMD_SECOND_PART = "-c";
	
	
	public static void main(String[] args) {

	}

	
	
	public int getIntResult() {
		try {
			return Integer.parseInt(result);
		} catch (Exception e) {
			return -1;
		}
	}
	
	public int redirectDevices (String sn, String hosts){
		int result = 1;
		
		return result;
	}
	
//	public List<String> runCapwapWtpInfo(){
//		
//	}
	
	public void runCmd(String[] commandArray) {
		try {
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(commandArray);
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String readln = null;

			if ((readln = br.readLine()) != null) {
				result = readln;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
