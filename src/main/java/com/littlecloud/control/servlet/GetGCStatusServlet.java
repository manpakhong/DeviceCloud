package com.littlecloud.control.servlet;

import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.littlecloud.ac.health.GCMonitor;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.utils.NetUtils;

@WebServlet(name="getGCStatusStatusServlet", urlPatterns="/getGCStatus")
public class GetGCStatusServlet extends HttpServlet
{
	private static final Logger log = Logger.getLogger(GetGCStatusServlet.class);
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	{

		PrintWriter pw = null;
		try 
		{
			GCMonitor gcmon = new GCMonitor();
			
			pw = response.getWriter();
			pw.println(gcmon.popGcLog());
		} 
		catch(Exception e) 
		{
			log.error("get device online status servlet error -",e);
		}
		finally {
			IOUtils.closeQuietly(pw);
		}
	}
}
