package com.littlecloud.services;

import org.jboss.logging.Logger;

public class LcMonitorMgr {
	private static final Logger log = Logger.getLogger(LcMonitorMgr.class);
	private static final String PASSWORD = "abc778899";
	public static boolean isPasswordCorrected(String password){
		boolean isCorrect = false;
		try{
			if (password.equals(PASSWORD)){
				isCorrect = true;
			}
		} catch (Exception e){
			log.error("LcMonitorMgr.isPasswordCorrected - ", e);
		}
		return isCorrect;
	}
}
