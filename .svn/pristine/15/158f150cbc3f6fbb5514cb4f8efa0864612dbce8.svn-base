package com.littlecloud.pool;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.jboss.logging.Logger;

import com.littlecloud.control.devicechange.DeviceChangeService;
import com.opensymphony.xwork2.ActionSupport;

public class DeviceChangeServiceAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

	private static Logger log = Logger.getLogger(DeviceChangeServiceAction.class);

	private static Thread doDeviceChangeSchedule_THREAD;
	
	protected HttpServletRequest request;
	protected HttpServletResponse response;

	protected static final String RESULT_TRUE = "true";
	protected static final String RESULT_FALSE = "false";

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
	public String doDeviceChangeSchedule() throws IOException
	{
		log.warn("INFO doDeviceChangeSchedule is called");
	
		if (doDeviceChangeSchedule_THREAD == null || !doDeviceChangeSchedule_THREAD.isAlive())
		{
			doDeviceChangeSchedule_THREAD = new Thread(new Runnable() {
				@Override
				public void run() {
					DeviceChangeService.loadRetryQueue();
				}
			});
			
			doDeviceChangeSchedule_THREAD.start();
		}
		else
		{
			log.warnf("INFO doDeviceChangeSchedule previous thread is still running %d %s", doDeviceChangeSchedule_THREAD.getId(), doDeviceChangeSchedule_THREAD.getName());
		}
		
		log.warn("INFO doDeviceChangeSchedule done");
		return SUCCESS;
	}
	

}