package com.littlecloud.controllers.results;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.apache.struts2.ServletActionContext;

import com.littlecloud.controllers.CommonMonitorController;
import com.littlecloud.controllers.FifoReplayMonitorController;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;

public class CheckPasswordCorrectResult implements Result {

	@Override
	public void execute(ActionInvocation invocation) throws Exception {
		CommonMonitorController commonMonitorController = (CommonMonitorController) invocation.getAction();
		  HttpServletResponse response = ServletActionContext.getResponse();
			response.setStatus(Response.Status.OK.getStatusCode());
			
		  //response.setContentType(action.getContentType());
		  //response.setContentLength(action.getContentLength());
		  PrintWriter pw = response.getWriter();
		  Boolean isPasswordCorrected = commonMonitorController.isPasswordVerified();
		  pw.write("{\"isPasswordCorrected\":" + isPasswordCorrected + "}");
		  pw.flush();

	}

}
