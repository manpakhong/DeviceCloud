package com.littlecloud.control.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.littlecloud.ac.handler.BranchServerRedirectionHandler;

@WebServlet(name="updateBranchInfo", urlPatterns = "/redirect/updateBranchInfo")
public class UpdateBranchRedirectionServlet extends HttpServlet
{
	public void doGet(HttpServletRequest request,HttpServletResponse response)
	{
		PrintWriter out = null;
		try 
		{
			BranchServerRedirectionHandler.getInstance().reloadMap();
			
			out = response.getWriter();
			out.println("BranchInfo reload successfully");
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
