package com.littlecloud.alert.client;

import javax.servlet.http.HttpServlet;

public class StartSchedulerHandler extends HttpServlet
{
	public void init()
	{
		new Thread(StartThread.getInstance()).start();
	}
}
