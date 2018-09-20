package com.littlecloud.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.dao.branch.ProductsDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.branch.Products;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.dtos.json.AlertEmailContactDto;
import com.littlecloud.dtos.json.AlertEmailContactListDto;
import com.littlecloud.dtos.json.EmailTemplateObjectDto;
import com.littlecloud.dtos.json.RequestGeoFencesTemplateDto;
import com.littlecloud.dtos.json.RequestTemplateDataDto;
import com.littlecloud.dtos.json.RequestTemplateDataFullDto;
import com.littlecloud.dtos.json.RequestTemplateDto;
import com.littlecloud.dtos.json.TemplateDevDataDto;
import com.littlecloud.pool.utils.PropertyService;

public class RailsMailMgr {
	private static final Logger log = Logger.getLogger(RailsMailMgr.class);
	private static PropertyService<RailsMailMgr> ps = new PropertyService<RailsMailMgr>(RailsMailMgr.class);
	public static final String ALERT_TYPE_GEO_FENCES_ENTERED_ZONE = "entered_zone";
	public static final String ALERT_TYPE_GEO_FENCES_LEFT_ZONE = "left_zone";
	public static final String ALERT_TYPE_GEO_FENCES_SPEED_LIMIT = "over_speed_limit";
	public static final String ALERT_TYPE_GEO_FENCES_SPEED_NORMAL = "speed_normal";
	
	public List<AlertEmailContactDto> getAlertEmailContactList(String orgId, Integer networkId, String userId, String recipientType){
		List<AlertEmailContactDto> alertEmailContactDtoList = null;
		try{
			AlertEmailContactListDto alertEmailAdminListDto = getAlertEmailAdminListDtoFromRails(orgId, networkId, userId, recipientType);
			
			if (alertEmailAdminListDto != null && alertEmailAdminListDto.getAdmins() != null){
				alertEmailContactDtoList =  alertEmailAdminListDto.getAdmins();
			}
			
		} catch (Exception e){
			log.error("ALERT201408211034 - RailsMailMgr.getAlertEmailContactList", e);
		}
		
		return alertEmailContactDtoList;
	}
	
	public AlertEmailContactListDto getAlertEmailAdminListDtoFromRails(String orgId, Integer networkId, String userId, String recipientType) {
		if (log.isDebugEnabled()){
			log.debugf("ALERT201408211034 - RailsMailMgr.getAlertEmailAdminListDtoFromRails orgId: %s, netId: %d, userId: %s, recipientType: %s", orgId, networkId, userId, recipientType);
		}
		if (orgId == null || orgId.isEmpty()) {
			return null;
		}

		if (networkId == null) {
			return null;
		}

		// ----------------------------------------------------
		// userId is equal to network_email_notifications.info
		// e.g.
		// one_admin - FiFSW8
		// emails - davem@peplink.com
		// all_admin - (null)
		// all_group_admin - (null)
		// ----------------------------------------------------
		
		String emailJson = getAlertEmailAdminListJsonFromRails(orgId, networkId, userId, recipientType);
		if (log.isDebugEnabled()){
			log.debugf("ALERT201408211034 - RailsMailMgr.getAlertEmailAdminListDtoFromRails() - orgId: %s, networkId: %s, userId: %s, emailJson: %s",orgId, networkId, userId, emailJson);
		}
		
		if (emailJson.isEmpty()) {
			return null;
		}


		Gson gson = new Gson();
		return gson.fromJson(emailJson, AlertEmailContactListDto.class);
	}
	
	public String getAlertEmailAdminListJsonFromRails(String orgId, Integer networkId, String userId, String recipientType) {
		String ret = "";
		// Properties props = new Properties();
		// String url = props.getProperty("com.littlecloud.control.alertservices.util.path");
		String url = ps.getString("getEmailUrl");
		url = url + "?organization_id=" + orgId + "&network_id=" + networkId + "&recipient_type=" + recipientType;
		if (userId != null && !userId.isEmpty()) {
			url = url + "&user_ids=" + userId;
		}
		if (log.isDebugEnabled()){
			log.debugf("ALERT201408211034 - RailsMailMgr.getAlertEmailAdminListJsonFromRails() - get email url: %s", url);
		}
		URL urlobj;
		try {
			urlobj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) urlobj.openConnection();
			con.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
			String lines;
			while ((lines = reader.readLine()) != null) {
				ret += lines;
			}
			reader.close();
			con.disconnect();
		} catch (MalformedURLException e) {
			log.error("ALERT201408211034 - RailsMailMgr.getAlertEmailAdminListJsonFromRails() - Get email address failed ", e);
			return "";
		} catch (IOException e) {
			log.error("ALERT201408211034 - RailsMailMgr.getAlertEmailAdminListJsonFromRails() - Get email address failed ", e);
			return "";
		}

		log.debug("ALERT201408211034 - RailsMailMgr.getAlertEmailAdminListJsonFromRails() - get email ret = " + ret);
		return ret;
	}

	public String getTemplateJson(EmailTemplateObjectDto eto) {
		
		String msgId = eto.getMsgId(); 
		String msgType = eto.getMsgType(); 
		String orgId = eto.getOrgId(); 
		String organization = eto.getOrganization_name();
		String network = eto.getNetwork_name();
//		AlertEmailContactList contactList; 
		List<Integer> devList = eto.getDevList();
		String eventTime = eto.getEventTime();
		Integer duration = eto.getDuration(); 
		String wanName = eto.getWanName(); 
		String vpnName = eto.getVpnName();
				
		List<AlertEmailContactDto> admins = new ArrayList<AlertEmailContactDto>();
		admins = eto.getContactList().getAdmins();
		String ret = "";
		if (log.isDebugEnabled()){
			log.debugf("ALERT201408211034 - RailsMailMgr.getTemplateJson() - getting email template msgType: %s, msgId: %s, admins: %s, devList: %s ",
					msgType, msgId, admins, devList);
		}
		String url = ps.getString("getTemplateUrl");

		RequestTemplateDto requestTemplate = new RequestTemplateDto();
		requestTemplate.setMsg_id(msgId);
		requestTemplate.setMsg_type(msgType);
		String recipientList = "";
		List<String> name_list = new ArrayList<String>();
		for (AlertEmailContactDto admin : admins) {
			recipientList = recipientList + admin.getEmail() + "|";
			name_list.add(admin.getFull_name());
		}
		requestTemplate.setRecipent(recipientList);
		requestTemplate.setPriority(5);
		requestTemplate.setSender_email("noreply@peplink.com");
		RequestTemplateDataFullDto requestTemplateDataFull = new RequestTemplateDataFullDto();
		RequestTemplateDataDto requestTemplateData = new RequestTemplateDataDto();
		requestTemplateData.setName_list(name_list);
		requestTemplateData.setOrganization(organization);
		requestTemplateData.setNetwork(network);
		requestTemplateData.setEvent_time(eventTime);
		requestTemplateData.setDuration(duration);
		requestTemplateData.setWan_name(wanName);
		requestTemplateData.setPepvpn_connection_name(vpnName);

		// HibernateUtil hutilBranchRO = new HibernateUtil(true);
		List<Products> pdtList = null;
		try {
			// hutilBranchRO.beginBranchTransaction();
			ProductsDAO productsDAO = new ProductsDAO(true);

			pdtList = productsDAO.getAllProducts();

			// hutilBranchRO.commitBranchTransaction();
		} catch (Exception e) {
			if (log.isDebugEnabled()){
				log.debug("ALERT201408211034 - RailsMailMgr.getTemplateJson() - before transaction is rollback", e);
			}
			// hutilBranchRO.rollbackBranchTransaction();
			log.error("ALERT201408211034 - RailsMailMgr.getTemplateJson() - transaction is rollback - " + e, e);
		}

		// HibernateUtil hutilRO = new HibernateUtil(orgId, true);
		List<TemplateDevDataDto> tempDevDataList = null;
		boolean devListIsEmpty = false;
		if (devList != null && !devList.isEmpty()) {
			devListIsEmpty = true;
		}
		try {
			// hutilRO.beginTransaction();
			DevicesDAO devicesDAO = new DevicesDAO(orgId, true);
			Devices devices = null;
			tempDevDataList = new ArrayList<TemplateDevDataDto>();
			for (Integer devId : devList) {
				devices = devicesDAO.findById(devId);
				if (devices != null) {
					TemplateDevDataDto devData = new TemplateDevDataDto();
					devData.setName(devices.getName());
					devData.setSn(devices.getSn());
					if (pdtList != null) {
						for (Products pdt : pdtList) {
							if (pdt.getId() == devices.getProductId()) {
								devData.setProduct_name(pdt.getName());
								break;
							}
						}
					}
					tempDevDataList.add(devData);
				}
			}
			// hutilRO.commitTransaction();
			if (!devListIsEmpty && tempDevDataList.isEmpty()) {
				// input is not null, but failed to get any of the devices info
				log.error("ALERT201408211034 - RailsMailMgr.getTemplateJson() - Cannot get any device info for device list =" + devList);
				return "";
			}
			requestTemplateData.setDevice_list(tempDevDataList);
		} catch (Exception e) {
			if (log.isDebugEnabled()){
				log.debug("ALERT201408211034 - RailsMailMgr.getTemplateJson() - before transaction is rollback", e);
			}
			// hutilRO.rollbackTransaction();
			log.error("ALERT201408211034 - RailsMailMgr.getTemplateJson() - transaction is rollback - " + e, e);
		}

//		requestTemplateDataFull.setSender_name("InControl2"); // don't enter it. it is used to authenticate smtp username
		requestTemplateDataFull.setMsg_id(msgId);
		requestTemplateDataFull.setMsg_type(msgType);
		requestTemplateDataFull.setData(requestTemplateData);
		String email_recipient = "";
		for (AlertEmailContactDto admin : admins) {
			email_recipient = email_recipient + admin.getEmail() + "|";
		}
		requestTemplateDataFull.setRecipient(email_recipient);
		requestTemplateDataFull.setPriority(5);
		requestTemplateDataFull.setSender_email("noreply@peplink.com");
		if (log.isDebugEnabled()){
			log.debug("ALERT201408211034 - RailsMailMgr.getTemplateJson() - get template with json (1) = " + JsonUtils.toJsonCompact(requestTemplateDataFull));
		}
		HttpClient httpClient = new HttpClient();
		httpClient.setConnectionTimeout(5000);
		httpClient.setTimeout(5000);
		PostMethod postMethod = new PostMethod(url);
		postMethod.setRequestBody(JsonUtils.toJsonCompact(requestTemplateDataFull));
		postMethod.addRequestHeader("Content-Type", "application/json; charset=UTF-8");

		try {
			httpClient.executeMethod(postMethod);
			if (postMethod != null && postMethod.getStatusCode() == HttpStatus.SC_OK) {
//				System.out.println("request template url " + url);
//				ret = postMethod.getResponseBodyAsString();

				InputStream is = postMethod.getResponseBodyAsStream();
				if(is != null){
					int ch = is.read();
					while(ch != -1){
						ret += (char)ch;
						ch = is.read();
					}
				}
//				log.debug("response body: " + postMethod.getResponseBodyAsString());
			} else {
				if (log.isDebugEnabled()){
					log.debug("ALERT201408211034 - RailsMailMgr.getTemplateJson() - get template return non-200 " + postMethod.getStatusCode());
					log.debug("ALERT201408211034 - RailsMailMgr.getTemplateJson() - response body: " + postMethod.getResponseBodyAsString());
				}
			}
			
		} catch (Exception e) {
			if (e instanceof HttpException){
				log.error("ALERT201408211034 - RailsMailMgr.getTemplateJson(), HttpException - error requesting email template ", e);
			} else if (e instanceof IOException){
				log.error("ALERT201408211034 - RailsMailMgr.getTemplateJson(), IOException - error requesting email template ", e);				
			} else {
				log.error("ALERT201408211034 - RailsMailMgr.getTemplateJson(), Other Exception - error requesting email template ", e);	
			}
			return ret;
		}
		if (log.isDebugEnabled()){
			log.debug("ALERT201408211034 - RailsMailMgr.getTemplateJson() - get template with result = " + ret);
		}
		return ret;
	}

	public String getGeoFencesTemplateJson(RequestGeoFencesTemplateDto requestGeoFencesTemplate) {
		String ret = "";

		String url = ps.getString("getTemplateUrl");
		// url = "http://10.8.168.2:3000/private/root/api/compile_html";

		String reqGeoFencesTemplateJsonString = JsonUtils.toJsonCompact(requestGeoFencesTemplate);
		if (log.isDebugEnabled()){
			log.debug("GEO20140204 - get template with json (2) = " + reqGeoFencesTemplateJsonString);
		}
		HttpClient httpClient = new HttpClient();
		httpClient.setConnectionTimeout(5000);
		httpClient.setTimeout(5000);
		PostMethod postMethod = new PostMethod(url);
		postMethod.setRequestBody(reqGeoFencesTemplateJsonString);
		postMethod.addRequestHeader("Content-Type", "application/json; charset=UTF-8");

		try {
			httpClient.executeMethod(postMethod);
		} catch (HttpException e) {
			log.error("GEO20140204 - error requesting email template " + e, e);
		} catch (IOException e) {
			log.error("GEO20140204 - error requesting email template " + e, e);
		}

		if (postMethod != null && postMethod.getStatusCode() == HttpStatus.SC_OK) {
			try{
				InputStream is = postMethod.getResponseBodyAsStream();
				if(is != null){
					int ch = is.read();
					while(ch != -1){
						ret += (char)ch;
						ch = is.read();
					}
				}
			} 
			catch (IOException e){
				e.printStackTrace();
			}
		} else {
			if (log.isDebugEnabled()){
				log.debug("GEO20140204 - get template return non-200 " + postMethod.getStatusCode());
				log.debug("GEO20140204 - response body: " + postMethod.getResponseBodyAsString());
			}
		}
		if (log.isDebugEnabled()){
			log.debugf("GEO20140204 - get template with result = %s", ret);
		}

		return ret;
	}
	
}
