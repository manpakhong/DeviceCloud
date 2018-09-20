package com.littlecloud.utils;

import java.sql.SQLException;

import org.jboss.logging.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.control.dao.branch.DebugLogDAO;
import com.littlecloud.control.entity.branch.DebugLogs;
import com.littlecloud.pool.utils.PropertyService;

public class LogUtils 
{
	private static Logger logger = Logger.getLogger(LogUtils.class);
	private static PropertyService<ACService> ps = new PropertyService<ACService>(ACService.class);
	private static final String SERVER_NAME = ps.getString("SERVER_NAME");
	private static boolean isTurnOn = false;
	
	public static void debug(String type, String msg)
	{
		DebugLogDAO debugLogDAO;
		
		try 
		{
			if( isTurnOn )
			{
				debugLogDAO = new DebugLogDAO();
				DebugLogs log = new DebugLogs();
				log.setMachine_id(SERVER_NAME);
				log.setType(type);
				log.setMsg(msg);
				debugLogDAO.save(log);
			}
		} 
		catch (SQLException e) 
		{			
			logger.error("Log debug log error -",e);
		}
	}
	
	public static void setTurnOn(boolean status)
	{
		isTurnOn = status;
	}
	
	public static void printStackTrace(Logger.Level level)
	{
		StringBuilder sb = new StringBuilder();
		
		StackTraceElement[] elems = Thread.currentThread().getStackTrace();
		for (int k = 0; k < elems.length && k < 10; k++) {
			sb.append(elems[k].toString());
		}
		
		logger.log(level, sb.toString());
	}
}
