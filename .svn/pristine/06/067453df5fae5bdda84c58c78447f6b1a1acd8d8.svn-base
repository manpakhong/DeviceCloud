package com.littlecloud.control.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.littlecloud.control.webservices.DeviceWs;

@WebServlet(name="getWarratyUpdatedServlet", urlPatterns = "/getWarratyUpdated")
public class GetWarrantyUpdatedServlet extends HttpServlet
{
	public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
	{	
		String orgId = request.getParameter("organization_id");
		String devId = request.getParameter("device_id");
		String expiry_date = request.getParameter("expiry_date");
		
		PrintWriter out = null;
		try 
		{
			//"{\"organization_id\":\""+orgId+"\",\"device_id\":"+devId+",\"expiry_date\":\""+expiry_date+" 00:00:00\"}"
			StringBuffer json = new StringBuffer();
			json.append("{");
			json.append("\"organization_id\":\"");
			json.append(orgId);
			json.append("\",\"device_id\":");
			json.append(devId);
			if( expiry_date != null && expiry_date.isEmpty() != true )
			{
				json.append(",\"expiry_date\":\"");
				json.append(expiry_date);
				json.append(" 00:00:00\"");
			}
			json.append("}");
			out = response.getWriter();
			String s = new DeviceWs().updateDeviceInformation(json.toString());
			out.println(s);
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
