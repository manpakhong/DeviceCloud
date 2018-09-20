package com.littlecloud.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.littlecloud.ac.WtpMsgHandler;
import com.littlecloud.dtos.FifoReplayParametersDto;
import com.littlecloud.helpers.JspParameterHelper;
import com.littlecloud.mongo.daos.criteria.FifoAcMessageCriteria;
import com.littlecloud.mongo.eos.FifoAcMessage;
import com.littlecloud.services.FifoAcMessageMgr;
import com.littlecloud.utils.CommonUtils;
import com.opensymphony.xwork2.ActionSupport;

public class FifoReplayMonitorController extends ActionSupport implements ServletRequestAware, ServletResponseAware {
	private static final Logger log = Logger.getLogger(FifoReplayMonitorController.class);
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	public static final String ACTION_TYPE = "action_type";
	public static final String ACTION_TYPE_GET_MESSAGE_TYPES = "ACTION_TYPE_GET_MESSAGE_TYPES";
	public static final String ACTION_TYPE_GET_CAPWAP_TEXT_FILE = "ACTION_TYPE_GET_CAPWAP_TEXT_FILE";
	public static final String ACTION_TYPE_DO_FIFO_REPLAY = "ACTION_TYPE_DO_FIFO_REPLAY";
	
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
				case ACTION_TYPE_GET_MESSAGE_TYPES:
					result = getAllMessageTypeListJson();
					return "getAllMessageTypeResult";
				case ACTION_TYPE_GET_CAPWAP_TEXT_FILE:
					result = getAllMessageTextFileResult();
					return "getAllMessageTextFileResult";
				case ACTION_TYPE_DO_FIFO_REPLAY:
					Integer noOfMessagesReplay = doFifoReplay();
					result = noOfMessagesReplay.toString();
					return "doFifoReplayResult";
			}
		}
		return result;
	}
		
	public String getFifoReplayHtml(FifoAcMessageCriteria criteria, String orgId, Integer networkId){
		StringBuilder sb = new StringBuilder();
		String header3Html = "<h3>fifo logs</h3>";
		String totalRecordHtml = "";
		try{
			FifoAcMessageMgr fifoAcMessageMgr = new FifoAcMessageMgr();
			Integer totalNumberOfRecord = null;
			List<FifoAcMessage> fifoAcMessageList = fifoAcMessageMgr.getFifoAcMessageList(criteria, orgId, networkId);

			if (fifoAcMessageList != null && fifoAcMessageList.size() > 0){
				sb.append("<table class=\"fifoReplayResultTable\">");
				sb.append("<tr>");
					sb.append("<th>");
						sb.append("timestamp");
					sb.append("</th>");
					sb.append("<th>");
						sb.append("iana_id");
					sb.append("</th>");
					sb.append("<th>");
						sb.append("sn");
					sb.append("</th>");
					sb.append("<th>");
						sb.append("message_type");
					sb.append("</th>");
					sb.append("<th>");
						sb.append("message");
					sb.append("</th>");
				sb.append("</tr>");
				for (FifoAcMessage fifoAcMessage: fifoAcMessageList){
					if (fifoAcMessage != null){
						if (totalNumberOfRecord == null && fifoAcMessage.getTotalRecordCount() != null){
							totalNumberOfRecord = fifoAcMessage.getTotalRecordCount();
						}
						sb.append("<tr>");
							sb.append("<td>");
								sb.append(CommonUtils.convertUnixTime2DateString(fifoAcMessage.getTimestamp()));
							sb.append("</td>");
							sb.append("<td>");
								sb.append(fifoAcMessage.getIanaId());
							sb.append("</td>");
							sb.append("<td>");
								sb.append(fifoAcMessage.getSn());
							sb.append("</td>");
							sb.append("<td>");
								sb.append(fifoAcMessage.getType());
							sb.append("</td>");
							sb.append("<td>");
								sb.append(fifoAcMessage.getPayload());
							sb.append("</td>");
						sb.append("</tr>");
					}
				}
				sb.append("</table>");
				if (totalNumberOfRecord != null){
					String limitRecordShown = "";
					if (criteria.getLimit() != null){
						limitRecordShown = criteria.getLimit().toString();
					}
					totalRecordHtml = "<span class=\"normalBoldTextSpan\">show record(s):" + limitRecordShown + "<br/>total record(s) found:" + totalNumberOfRecord + "</span><br/>";
				}
			} else {
				sb.append("<span class=\"alertSpan\">no record found</span>");
			}
//			sb.append("</div>");
		} catch (Exception e){
			log.error("FifoReplayMonitorController.renderFifoReplayHtml()", e);
		}
		return header3Html + "<div class=\"fifoReplayResultDiv\">" + totalRecordHtml + sb.toString() + "</div>";
	}
	
	public String getAllMessageTypeListJson(){
		String json = "";
		try{
			FifoAcMessageMgr fifoAcMessageMgr = new FifoAcMessageMgr();
			List<String> messageTypeList = fifoAcMessageMgr.getAllMessageTypes();
			
			Gson gson = new Gson();
			json = gson.toJson(messageTypeList);
		} catch (Exception e){
			log.error("FifoReplayMonitorController.getAllMessageTypeListJson()", e);
		}
		return json;
	}
	
	public int doFifoReplay(){
		int numberOfMessagesReplay = 0;
		try{
			List<FifoAcMessage> fifoAcMessageList = getFifoAcMessageListByGettingHttpParams();
			if (fifoAcMessageList != null && fifoAcMessageList.size() > 0){
				
				for (FifoAcMessage fifoAcMessage: fifoAcMessageList){
					if (numberOfMessagesReplay >= WtpMsgHandler.getMaxJsonPipeInfoTypeDevLocations()){
						break;
					}
					if (fifoAcMessage != null){
						log.debugf("FifoReplayMonitorController.doFifoReplay() - %s", fifoAcMessage);
						WtpMsgHandler.reQueue(fifoAcMessage.getFullMessage());
						numberOfMessagesReplay++;
					}
				}
			}
		} catch (Exception e){
			log.error("FifoReplayMonitorController.doFifoReplay()", e);
			return numberOfMessagesReplay;
		}
		return numberOfMessagesReplay;
	}
	
	private List<FifoAcMessage> getFifoAcMessageListByGettingHttpParams(){
		List<FifoAcMessage> fifoAcMessageList = null;
		try{
			// ----- paramerter and criteria 
			String data = request.getParameter("data");


			String orgId = null;
			Integer networkId = null;
			List<String> messageTypeList = null;
			Calendar calTimestampFrom = null;
			Calendar calTimestampTo = null;
			String sn = null;
			FifoAcMessageCriteria criteria = new FifoAcMessageCriteria();
			
			if (data != null){
				JsonParser jsonParser = new JsonParser();
				JsonObject jo = (JsonObject) jsonParser.parse(data);
				JsonObject resObject = jo.get("parameters").getAsJsonObject();
				Gson gson = new Gson();
				FifoReplayParametersDto fifoReplayParametersDto = gson.fromJson(resObject, FifoReplayParametersDto.class);
		
				if (fifoReplayParametersDto.getCreateAtFrom() != null && !fifoReplayParametersDto.getCreateAtFrom().isEmpty()){
					calTimestampFrom = JspParameterHelper.convertMonitorPostDateTimeString2Date(fifoReplayParametersDto.getCreateAtFrom(), JspParameterHelper.CALENDAR_MIN_DAY_VALUE);				
					criteria.setDateFrom(calTimestampFrom.getTime());
				}
	
				if (fifoReplayParametersDto.getCreateAtTo() != null && !fifoReplayParametersDto.getCreateAtTo().isEmpty()){
					calTimestampTo = JspParameterHelper.convertMonitorPostDateTimeString2Date(fifoReplayParametersDto.getCreateAtTo(), JspParameterHelper.CALENDAR_MAX_DAY_VALUE);
					criteria.setDateTo(calTimestampTo.getTime());
				}
				
				orgId = fifoReplayParametersDto.getOrgId();
				messageTypeList = fifoReplayParametersDto.getMessageTypeList();
				String networkIdString = fifoReplayParametersDto.getNetworkId();
	
				if (networkIdString != null && CommonUtils.isInteger(networkIdString)){
					networkId = new Integer(networkIdString);
				}
				sn = fifoReplayParametersDto.getSn();
							
				if (sn != null){
					List<String> snList = new ArrayList<String>();
					snList.add(sn);
					criteria.setSnList(snList);
				}
				criteria.setMessageTypeList(messageTypeList);
				
				// ----- paramerter and criteria 
			} else {
				if (request.getParameter("createAtFrom") != null && !request.getParameter("createAtFrom").isEmpty()){
					calTimestampFrom = JspParameterHelper.convertMonitorPostDateTimeString2Date(request.getParameter("createAtFrom"), JspParameterHelper.CALENDAR_MIN_DAY_VALUE);				
					criteria.setDateFrom(calTimestampFrom.getTime());
				}
	
				if (request.getParameter("createAtTo") != null && !request.getParameter("createAtTo").isEmpty()){
					calTimestampTo = JspParameterHelper.convertMonitorPostDateTimeString2Date(request.getParameter("createAtTo"), JspParameterHelper.CALENDAR_MAX_DAY_VALUE);
					criteria.setDateTo(calTimestampTo.getTime());
				}
				
				orgId = request.getParameter("orgId");

				String[] messageTypeArray = null;
				if (request.getParameterValues("messageType") != null){
					messageTypeArray = request.getParameterValues("messageType");
					messageTypeList = Arrays.asList(messageTypeArray);
					if (messageTypeList != null && messageTypeList.size() > 0){
						criteria.setMessageTypeList(messageTypeList);
					}
				}
				

				String networkIdString = request.getParameter("networkId");
	
				if (networkIdString != null && CommonUtils.isInteger(networkIdString)){
					networkId = new Integer(networkIdString);
				}
				
				
				sn = request.getParameter("sn");
							
				if (sn != null){
					List<String> snList = new ArrayList<String>();
					snList.add(sn);
					criteria.setSnList(snList);
				}
			}
			
			
			FifoAcMessageMgr fifoAcMessageMgr = new FifoAcMessageMgr();
			fifoAcMessageList = fifoAcMessageMgr.getFifoAcMessageList(criteria, orgId, networkId);
		} catch (Exception e){
			log.error("FifoReplayMonitorController.getFifoAcMessageListByGettingHttpParams()", e);
		}
		return fifoAcMessageList;
	}
	
	public String getAllMessageTextFileResult(){
		StringBuilder result = new StringBuilder();
		try{
			List<FifoAcMessage> fifoAcMessageList = getFifoAcMessageListByGettingHttpParams();
			if (fifoAcMessageList != null && fifoAcMessageList.size() > 0){
				for (FifoAcMessage fifoAcMessage: fifoAcMessageList){
					if (fifoAcMessage != null){
						result.append(fifoAcMessage.getPayload() + "\n");
					}
				}
			}
			
		} catch (Exception e){
			log.error("FifoReplayMonitorController.getAllMessageTypeListJson()", e);
		}
		return result.toString();
	}
	
}
