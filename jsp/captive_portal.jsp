<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.littlecloud.control.dao.criteria.CaptivePortalActivitiesCriteria"%>
<%@page import="com.littlecloud.control.dao.criteria.CaptivePortalAccessLogCriteria"%>
<%@page import="com.littlecloud.services.CaptivePortalActivitiesMgr"%>
<%@page import="java.util.Date" %>
<%@page import="java.util.Calendar" %>
<%@page import="com.littlecloud.utils.CalendarUtils" %>
<%@page import="com.littlecloud.helpers.JspParameterHelper" %>
<%@page import="com.littlecloud.controllers.CaptivePortalMonitorController" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>		
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<script type="text/javascript" src="javascript/jquery.js"></script>
		<script type="text/javascript" src="javascript/jquery-ui.js"></script>
		<script type="text/javascript" src="javascript/commonUtils.js"></script>
		<link href="css/jquery-ui.css" rel="stylesheet" type="text/css"/>
		<script src="javascript/captivePortal.js" type="text/javascript"></script>
		<link href="css/captivePortal.css" rel="stylesheet" type="text/css"/>
	</head>
	<title>Captive Portal</title>

	<body>
		<%
			String createAtFrom =
				request.getParameter("createAtFrom"); String createAtTo
				= request.getParameter("createAtTo"); String orgId =
				request.getParameter("orgId");
				
				String ssid = "";
				if (request.getParameter("ssid") != null){
			ssid = request.getParameter("ssid");
				}
				String mac = "";
				if (request.getParameter("mac") != null){
			mac = request.getParameter("mac");
				}
				
				Calendar calCreateAtFrom =
				JspParameterHelper.convertMonitorPostDateTimeString2Date(createAtFrom,
				JspParameterHelper.CALENDAR_MIN_DAY_VALUE);
				Calendar calCreateAtTo =
				JspParameterHelper.convertMonitorPostDateTimeString2Date(createAtTo,
				JspParameterHelper.CALENDAR_MAX_DAY_VALUE);
				CaptivePortalMonitorController
				captivePortalMonitorController = new
				CaptivePortalMonitorController(orgId);
		%>
		<div class="resultContentDiv1">
		<%
			String healthCheckHtml =
			captivePortalMonitorController.generateCaptivePortalHealthCheckResultHtml();
			out.print(healthCheckHtml); 
			out.print("<h3>Input parameters</h3><div>createAtFrom: " + createAtFrom +
			"</br>createAtTo: " + createAtTo + "<br/>orgId: " +
			orgId + "</div>"); 
		%>
		</div>
		<div class="resultContentDiv">
		<%
			CaptivePortalActivitiesCriteria cpaCriteria = new
			CaptivePortalActivitiesCriteria();
			cpaCriteria.setCreateAtFrom(calCreateAtFrom.getTime());
			cpaCriteria.setCreateAtTo(calCreateAtTo.getTime());
			if (!mac.isEmpty()){
				cpaCriteria.setClientMac(mac);
			}
			if (!ssid.isEmpty()){
				cpaCriteria.setSsid(ssid);
			}
			String cpaHtml =
			captivePortalMonitorController.generateCaptivePortalActivityListHtml(cpaCriteria);
			out.print(cpaHtml);
			CaptivePortalAccessLogCriteria cpalCriteria = new CaptivePortalAccessLogCriteria();
			cpalCriteria.setDateFrom(calCreateAtFrom.getTime());
			cpalCriteria.setDateTo(calCreateAtTo.getTime());
			if (!mac.isEmpty()){
				cpalCriteria.setClientMac(mac);
			}
			if (!ssid.isEmpty()){
				cpalCriteria.setSsid(ssid);
			}
			
			String cpalHtml = captivePortalMonitorController.generateCaptivePortalAccessLogsResultHtml(cpalCriteria);
			out.print(cpalHtml);
		%>
		</div>
		<!--  <button id="toggle">Toggle icons</button> -->
	</body>
</html>