package com.littlecloud.control.servlet;

import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.utils.NetUtils;

@WebServlet(name="getDeviceOnlineStatusServlet", urlPatterns="/getDeviceOnlineStatus")
public class GetDeviceOnlineStatusServlet extends HttpServlet
{
	private static final Logger log = Logger.getLogger(GetDeviceOnlineStatusServlet.class);
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	{
		String param_orgId = request.getParameter("orgId");
		String param_devId = request.getParameter("devId");
		
		Integer devId = Integer.parseInt(param_devId);
		Devices device = NetUtils.getDevicesWithoutNetId(param_orgId, devId);
		StringBuffer result = new StringBuffer();
		PrintWriter pw = null;
		try 
		{
			result.append("{\"status\":");
			pw = response.getWriter();
			
			if( device != null )
			{
				DevOnlineObject devOnlineO = new DevOnlineObject();
				devOnlineO.setIana_id(device.getIanaId());
				devOnlineO.setSn(device.getSn());
			
				devOnlineO = ACUtil.getPoolObjectBySn(devOnlineO, DevOnlineObject.class);
				if( devOnlineO != null && devOnlineO.isOnline() )
				{
					result.append("\"online\"");
				}
				else
				{
					result.append("\"offline\"");
				}
			}
			else
			{
				result.append("\"device_not_found\"");
			}
			
			result.append("}");
			pw.println(result.toString());
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
