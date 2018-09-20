package com.littlecloud.control.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.DevOnlineObject;

@WebServlet(name="cache handler", urlPatterns="/cacheHandler")
public class CacheHandlerServlet extends HttpServlet
{
	private static Logger logger = Logger.getLogger(CacheHandlerServlet.class);
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	{
		String key = request.getParameter("key");
		String oper = request.getParameter("oper");
		
		PrintWriter out = null;
		try 
		{
			out = response.getWriter();
			if(key == null)
			{
				out.write("Invalid Parameters");
				out.flush();
				return ;
			}
			
			String json = null;
			if(oper.equals("get"))
				json = CacheHandlerServlet.get(key);
			else if(oper.equals("put"))
				CacheHandlerServlet.put(key);
			else if(oper.equals("remove"))
				CacheHandlerServlet.remove(key);
			out.println(json);
		} 
		catch (IOException e) 
		{
			logger.error("");
		}
		finally {
			IOUtils.closeQuietly(out);
		}
	}
	
	public static void put(String key)
	{
		
	}
	
	public static String get(String key)
	{
		DevOnlineObject devOnlineO = null;
		
		try 
		{
			devOnlineO = ACUtil.getPoolObjectByKey(key);
		} 
		catch (Exception e) 
		{
			logger.error("Get devOnlineObject error - " + e,e);
		}
		
		return JsonUtils.toJson(devOnlineO);
	}
	
	public static void remove(String key)
	{
		DevOnlineObject devOnlineO = null;
		
		try 
		{
			ACUtil.<DevOnlineObject>removePoolObjectBySn(devOnlineO, DevOnlineObject.class);
		} 
		catch (InstantiationException | IllegalAccessException e) 
		{
			logger.error("Get devOnlineObject error - " + e,e);
		}
	}
}
