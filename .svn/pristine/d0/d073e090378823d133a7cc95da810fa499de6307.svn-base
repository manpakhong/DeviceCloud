package com.littlecloud.controllers.results;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.apache.struts2.ServletActionContext;
import org.jboss.logging.Logger;

import com.littlecloud.controllers.FifoReplayMonitorController;
import com.littlecloud.utils.CommonUtils;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;

public class GetMessageTextFileResult implements Result {
	private static final Logger log = Logger.getLogger(GetMessageTextFileResult.class);
	private static final int BUFSIZE = 4096;
	@Override
	public void execute(ActionInvocation invocation) throws Exception {
		FifoReplayMonitorController fifoReplayMonitorController = (FifoReplayMonitorController) invocation.getAction();
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setStatus(Response.Status.OK.getStatusCode());
		InputStream in = null;
		try{
			String text = fifoReplayMonitorController.getAllMessageTextFileResult();
			// response.setContentType(action.getContentType());
			// response.setContentLength(action.getContentLength());
		
			
	
			
			ServletOutputStream outStream = response.getOutputStream();
			ServletContext context = ServletActionContext.getServletContext();
			String mimetype = context.getMimeType("application/octet-stream");
			response.setContentType(mimetype);
			response.setHeader("Content-Disposition",  "attachment;filename=fifo_" + CommonUtils.genTimestampString() + ".txt");
			byte[] byteBuffer = new byte[BUFSIZE];



			in = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
			int length = 0;
			while ((in != null) && ((length = in.read(byteBuffer)) != -1)){
				outStream.write(byteBuffer, 0, length);
			}
			in.close();
			outStream.close();
		} catch (Exception e){
			log.error("GetMessageTextFileResult.execute() - ", e);
		}
	}

}
