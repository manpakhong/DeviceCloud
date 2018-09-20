package com.littlecloud.alert.client;

import java.io.FileInputStream;
import java.util.Properties;

public class StartThread implements Runnable
{
	
	private StartThread()
	{
		
	}
	
	private static final StartThread single = new StartThread();
	
	public static StartThread getInstance()
	{
		return single;
	}
	
	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		Properties props = new Properties();
		try
		{
			FileInputStream fis = new FileInputStream(System.getProperty("littlecloud.config"));
			props.load(fis);
			fis.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		while( true )
		{
			int suc = new AlertClient().sendCommand(props.getProperty("alert.server.addr"), Integer.parseInt(props.getProperty("alert.server.port")), "start -a");
			if( suc == 1 )
				break;
			else
			{
				try 
				{
					Thread.sleep(60000);
				} 
				catch (InterruptedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} 
	}

}
