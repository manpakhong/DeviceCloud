package com.littlecloud.control.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;

import com.littlecloud.services.DeviceDiagnosticReportMgr;

public class DeviceDiagnosticReportServlet extends HttpServlet implements Servlet{
	private static final Logger log = Logger.getLogger(DeviceDiagnosticReportServlet.class);
	private static final long serialVersionUID = 1L;
	   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	        doPost(request, response);
	    }
	    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    	postHandler(request, response);
	    }
	    private void postHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
	    	try{
	    		Integer ianaId = 23695;
	    		String sn = "";
	    		if (request.getParameter("ianaId") != null){
	    			ianaId = Integer.parseInt(request.getParameter("ianaId"));
	    		}
	    		if (request.getParameter("sn") != null){
	    			sn = request.getParameter("sn");
	    		}
	    		
	    		if (log.isDebugEnabled()){
	    			log.debugf("DeviceDiagnosticReportServlet.postHandler() - received device diagnostic report request - ianaId:%s, sn:%s", ianaId, sn);
	    		}
	    		
	    		DeviceDiagnosticReportMgr deviceDiagnosticReportMgr = new DeviceDiagnosticReportMgr();
	    		boolean isCommandPlaced = deviceDiagnosticReportMgr.placeDiagReportCommand(ianaId, sn);
	    	    response.setContentType("text/html");
	    		PrintWriter pw = response.getWriter();
    			
	    		if (isCommandPlaced){
	    			pw.write("command placed");
	    		} else {
	    			pw.write("command not placed with problem");
	    		}
	    		
	    	} catch (Exception e){
	    		log.error("DeviceDiagnosticReportServlet.postHandler() - exception - ", e);
	    	}
	    }

}
