package com.littlecloud.control.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.littlecloud.utils.LogUtils;

@WebServlet(name="turnOnLogServlet",urlPatterns="/turnOnLog")
public class TurnOnOffLogUtilsServlet extends HttpServlet
{
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	{
		String status = request.getParameter("status");
		if( status.trim().equals("on") )
			LogUtils.setTurnOn(true);
		else
			LogUtils.setTurnOn(false);
	}
}
