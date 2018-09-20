package com.littlecloud.control.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.littlecloud.control.webservices.WsTasksInfo;

@WebServlet(name="wsTasksMonitor",urlPatterns="/wsTasksMonitor")
public class WsTasksMonitorServlet extends HttpServlet
{
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String s = WsTasksInfo.getLastNWsInfos(100);
		String r = WsTasksInfo.getRunningWsInfos(5000);
		PrintWriter out = response.getWriter();
		out.print("<html>");
		out.print("<body>");
		out.print("<p style=\"font-family:Arial, sans-serif;font-size:14px;\">");
		out.println(r);
		out.println(s);
		out.print("</p>");
		out.print("</body>");
		out.print("</html>");
	}
}
