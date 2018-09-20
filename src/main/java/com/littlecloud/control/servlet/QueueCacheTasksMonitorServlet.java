package com.littlecloud.control.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.littlecloud.pool.control.QueueTaskInfo;

@WebServlet(name="QueueCacheTasksMonitor",urlPatterns="/qCacheTasksMonitor")
public class QueueCacheTasksMonitorServlet extends HttpServlet
{
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String s = QueueTaskInfo.getLastNQueueCacheInfos(100);
		PrintWriter out = response.getWriter();
		out.print("<html>");
		out.print("<body>");
		out.println("Queue cache control tasks : ");
		out.println(s);
		out.print("</body>");
		out.print("</html>");
	}
}
