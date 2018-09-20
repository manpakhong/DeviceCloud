package com.littlecloud.control.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.littlecloud.alert.client.AlertClient;

@WebServlet(name="sendCmd",urlPatterns="/sendCmd")
public class CommandSendServlet extends HttpServlet
{
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	{
		String cmd = request.getParameter("cmd");
		AlertClient client = new AlertClient();
		client.sendCommand(request.getLocalAddr(), 28300, cmd);
	}
}
