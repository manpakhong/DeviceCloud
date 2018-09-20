package com.littlecloud.control.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.littlecloud.control.webservices.DeviceWs;

@WebServlet(name="getLocationServlet", urlPatterns = "/DataWs/loc")
public class GetGPSServlet extends HttpServlet
{
	public void doGet(HttpServletRequest request,HttpServletResponse response)
	{
//		String json = request.getParameter("json");   organization_id=8k7BiJ&device_id=8&start=2013-10-15T00:00:00
		String param_org = request.getParameter("organization_id");
		String param_devId = request.getParameter("device_id");
		String param_start = request.getParameter("start");
		String param_caller_ref = request.getParameter("caller_ref");
		param_caller_ref=(param_caller_ref==null?"0":param_caller_ref);
		
		String json = "";
		if (param_start == null || param_start.isEmpty()){
			json = "{" + "\"organization_id\":\"" + param_org + "\",\"device_id\":" + param_devId + ",\"caller_ref\":\"" + param_caller_ref + "\"}";
		}else {
			json = "{" + "\"organization_id\":\"" + param_org + "\",\"device_id\":" + param_devId + ",\"caller_ref\":\"" + param_caller_ref + "\",\"start\":\"" + param_start +"\"}";
		}
		
		PrintWriter out = null;
		try 
		{
			out = response.getWriter();
//			out.println("This is json:"+ json);
//			long startServlet = System.currentTimeMillis();
			String s = new DeviceWs().getGPSLocation(json);
//			long endServlet = System.currentTimeMillis();
			out.println(s);
//			out.println("====start servlet: "+startServlet+" ====end servlet: "+endServlet+ "==== "+ (endServlet - startServlet) );
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			IOUtils.closeQuietly(out);
		}
	}
}
