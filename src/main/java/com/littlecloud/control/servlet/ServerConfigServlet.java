package com.littlecloud.control.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.littlecloud.ac.WtpMsgHandler;
import com.littlecloud.control.webservices.DeviceWs;

@WebServlet(name="setServerConfig", urlPatterns = "/DataWs/config")
public class ServerConfigServlet extends HttpServlet
{
	public void doGet(HttpServletRequest request,HttpServletResponse response)
	{
//		String json = request.getParameter("json");   organization_id=8k7BiJ&device_id=8&start=2013-10-15T00:00:00
//		String param_org = request.getParameter("organization_id");
//		String param_devId = request.getParameter("device_id");
//		String param_start = request.getParameter("start");
//		
//		String json = "";
//		if (param_start == null || param_start.isEmpty()){
//			json = "{" + "\"organization_id\":\"" + param_org + "\",\"device_id\":" + param_devId +"}";
//		}else {
//			json = "{" + "\"organization_id\":\"" + param_org + "\",\"device_id\":" + param_devId + ",\"start\":\"" + param_start +"\"}";
//		}
		String param_skip = request.getParameter("skip");
		
		PrintWriter out = null;
		try 
		{
			WtpMsgHandler.UNHEALTHY_SKIP_MESSAGE=param_skip.equalsIgnoreCase("1")?true:false;
			
			out = response.getWriter();
			out.println("set UNHEALTHY_SKIP_MESSAGE="+WtpMsgHandler.UNHEALTHY_SKIP_MESSAGE);
			
			response.sendRedirect("/LittleCloud/sysMonitor");
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally {
			IOUtils.closeQuietly(out);
		}
	}
}
