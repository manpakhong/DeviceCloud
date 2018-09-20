package com.littlecloud.pool;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.jboss.logging.Logger;

import com.littlecloud.control.firmware.FirmwareScheduler;
import com.opensymphony.xwork2.ActionSupport;

public class FirmwareAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

	private static final long serialVersionUID = 424115174489128522L;

	private static Logger logger = Logger.getLogger(FirmwareAction.class);

	protected HttpServletRequest request;
	protected HttpServletResponse response;

	protected static final String RESULT_TRUE = "true";
	protected static final String RESULT_FALSE = "false";

	// protected static QPService qpService = new QPServiceImpl();
	// protected ProxyPropertyBean proxyPropertyBean = ProxyPropertyBean.getInstance();

	public HttpServletRequest getServletRequest() {
		return request;
	}

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getServletResponse() {
		return response;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}	

	@SkipValidation
	public String doFirmwareSchedule() throws IOException
	{
		logger.warn("INFO doFirmwareSchedule is called");
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 1);
		Date d = cal.getTime();
		
		FirmwareScheduler.startFirmwareScheduler(d.getTime()/1000);
		
		logger.warn("INFO doFirmwareSchedule done");
		return SUCCESS;
	}
	

}