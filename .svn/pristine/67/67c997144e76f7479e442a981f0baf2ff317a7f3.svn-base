package com.littlecloud.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.littlecloud.dtos.CommonMonitorParametersDto;
import com.littlecloud.dtos.FifoReplayParametersDto;
import com.littlecloud.services.LcMonitorMgr;
import com.opensymphony.xwork2.ActionSupport;

public class CommonMonitorController extends ActionSupport implements ServletRequestAware, ServletResponseAware{
	private static final Logger log = Logger.getLogger(CommonMonitorController.class);
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	public static final String ACTION_TYPE = "action_type";
	public static final String ACTION_TYPE_CHECK_PASSWORD = "ACTION_TYPE_CHECK_PASSWORD";
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
	public String distributeActions(){
		String result = "";
		if (request.getParameter(ACTION_TYPE) != null){
			String actionType = request.getParameter(ACTION_TYPE);
			switch (actionType){
				case ACTION_TYPE_CHECK_PASSWORD:
					result = isPasswordVerified().toString();
					return "checkPasswordCorrectResult";
			}
		}
		return result;
	}
	
	public Boolean isPasswordVerified(){
		Boolean isPasswordCorrected = false;
		try{
			// ----- paramerter and criteria 
			String data = request.getParameter("data");
			
			if (data != null){
				JsonParser jsonParser = new JsonParser();
				JsonObject jo = (JsonObject) jsonParser.parse(data);
				JsonObject resObject = jo.get("parameters").getAsJsonObject();
				Gson gson = new Gson();
				CommonMonitorParametersDto commonMonitorParametersDto = gson.fromJson(resObject, CommonMonitorParametersDto.class);
				if (commonMonitorParametersDto != null){
					String password = commonMonitorParametersDto.getPassword();
					isPasswordCorrected = LcMonitorMgr.isPasswordCorrected(password);
				}
			}
		} catch (Exception e){
			log.error("CommonMonitorController.isPasswordVerified() - ", e);
			return isPasswordCorrected;
		}
		return isPasswordCorrected;
	}
}
