package com.littlecloud.control.servlet;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.littlecloud.control.dao.branch.QzTriggersDAO;
import com.littlecloud.control.entity.viewobject.QuartzTriggers;
import com.littlecloud.control.json.model.Json_Schedule_Info;
import com.littlecloud.control.json.util.JsonUtils;

@WebServlet(name="listSchedules", urlPatterns="/listSchedules")
public class ListScheduleServlet extends HttpServlet
{
	private Logger logger = Logger.getLogger(ListScheduleServlet.class);
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	{
		PrintWriter pw = null;
		try 
		{
			pw = response.getWriter();
			HashMap<String, QuartzTriggers> triggers = QzTriggersDAO.getTriggersInfo();
			Set<String> keys = triggers.keySet();
			List<Json_Schedule_Info> schedLst = new ArrayList<Json_Schedule_Info>();
			Json_Schedule_Info info = null;
			for( String key : keys )
			{
				QuartzTriggers trigger = triggers.get(key);
				info = new Json_Schedule_Info();
				info.setGroup(key);
				info.setDescription(trigger.getDescription());
				info.setStatus(trigger.getStatus());
				schedLst.add(info);
			}
			String result = JsonUtils.toJson(schedLst);
			pw.println(result);
		} 
		catch (Exception e) 
		{
			logger.error("List schedule infos error -"+e,e);
		}
		finally
		{
			IOUtils.closeQuietly(pw);
		}
	}
}
