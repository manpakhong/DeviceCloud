package com.littlecloud.control.servlet;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.pool.object.DevOnlineObject;

@WebServlet(name="DeviceNotInCacheServlet",urlPatterns="/findDevServlet")
public class FindOrgDevNotInCache extends HttpServlet 
{
	private static Logger log = Logger.getLogger(FindOrgDevNotInCache.class);
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	{
		String orgId = request.getParameter("orgId");
		DevicesDAO devicesDAO = null;
		StringBuffer sb = new StringBuffer();
		PrintWriter out = null;
		int i = 0;
		try
		{
			out = response.getWriter();
			
			if(orgId != null && !orgId.isEmpty())
			{
				devicesDAO = new DevicesDAO(orgId);
				List<Devices> deviceLst = devicesDAO.getAllDevices();
				for(Devices dev : deviceLst)
				{
					try
					{
						DevOnlineObject devOnlineO = new DevOnlineObject();
						devOnlineO.setIana_id(dev.getIanaId());
						devOnlineO.setSn(dev.getSn());
						
						DevOnlineObject devOnlineObject = ACUtil.getPoolObjectBySn(devOnlineO, DevOnlineObject.class);
						if(devOnlineObject == null)
						{
							i++;
							sb.append(orgId+" ");
							sb.append(dev.getSn()+"\n");
						}
					}
					catch(Exception e)
					{
						log.error("Find dev " + dev.getSn() + " online object error - "+e,e);
					}
				}
				sb.append("\n\n"+i+" devices found.");
			}
			else
			{
				sb.append("Invalid input");
			}
			out.println(sb.toString());
		}
		catch(Exception e)
		{
			log.error("Find organization devices error - "+e,e);
		}
		
	}
}
