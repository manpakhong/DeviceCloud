package com.littlecloud.control.webservices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.littlecloud.ac.health.info.WebServiceInfo;
import com.littlecloud.control.json.util.DateUtils;

public class WsTasksInfo 
{
	boolean started;
	private static Logger log = Logger.getLogger(WsTasksInfo.class);
	public static ConcurrentHashMap<String, WebServiceInfo> wsTaskMap = new ConcurrentHashMap<String, WebServiceInfo>();

	private String orgId = "none";
	private String netId = "none";
	private String devId = "none";
	private String ws_name;
	
	protected WsTasksInfo(String json, Class<?> clazzREQ, String ws_name) {
		super();
		this.ws_name = ws_name;
		
		JsonElement elem = null;
		JsonObject obj = null;
		try
		{
			JsonParser parser = new JsonParser();
			elem = parser.parse(json);
			if(elem != null)
			{
				obj = elem.getAsJsonObject();
				if(obj != null && obj.has("organization_id"))
				{
					elem = obj.get("organization_id");
					if( elem != null )
						orgId = elem.getAsString();
				}
				
				if(obj != null && obj.has("network_id"))
				{
					elem = obj.get("network_id");
					if( elem != null )
						netId =  elem.getAsString();
				}
				
				if(obj != null && obj.has("device_id"))
				{
					elem = obj.get("device_id");
					if(elem != null)
						devId =  elem.getAsString();
				}
			}
		}
		catch(Exception e)
		{
			log.error("BaseWs fetch", e);
		}
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public String startTask()
	{
		Date start_time = DateUtils.getUtcDate();
		String key = start_time.getTime()+"|"+orgId+"_"+netId+"_"+devId+"|"+ws_name;
		
		WebServiceInfo value = new WebServiceInfo();
		value.setKey(key);
		value.setMethod(ws_name);
		value.setStatus("running");
		value.setStart_time(start_time);
		value.setRequest(orgId+"_"+netId+"_"+devId);
		value.setOrgId(orgId);
		value.setNetwork_id(netId);
		value.setDevice_id(devId);
		value.setThread_id(Thread.currentThread().getId());
		
		if( wsTaskMap.size() >= 5000 )
		{
			this.cleanMap(wsTaskMap);
		}
		
		wsTaskMap.put(key, value);
		this.started = true;
		
		return key;
	}
	
	public void endTask(String key)
	{
		WebServiceInfo value = wsTaskMap.get(key);
		this.started = false;
		Date endTime = DateUtils.getUtcDate();
		long diff = endTime.getTime()-value.getStart_time().getTime();
		
		value.setStatus("completed");
		value.setEnd_time(endTime);
		value.setDuration(diff);
		if(wsTaskMap.size() >= 5000)
		{
			this.cleanMap(wsTaskMap);
		}
		
		wsTaskMap.put(key, value);
	}
	
	public void cleanMap(ConcurrentHashMap<String,WebServiceInfo> map)
	{
		Set<String> kSet = map.keySet();
		List<String> list = new ArrayList<String>();
		list.addAll(kSet);
		Collections.sort(list);
		int loop = map.size() / 2;
		for(int i = 0; i < loop ; i++)
		{
			String key = list.get(i);
			WebServiceInfo value = map.get(key);
			if( value != null )
			{
				if(value.getStatus()!=null && value.getStatus().equals("completed"))
				{
					map.remove(key);
				}
			}
			else
			{
				// log.warn("Value of " + key + " is null");
				map.remove(key);
			}
		}
	}
	
	public static String getLastNWsInfos(Integer limit)
	{
		Set<String> kSet = wsTaskMap.keySet();
		List<String> keyLst = new ArrayList<String>();
		keyLst.addAll(kSet);
		Collections.sort(keyLst);
		Collections.reverse(keyLst);
		int i = 0;
		StringBuffer result = new StringBuffer();
//		result.append("WS Tasks Size : ");
//		result.append(kSet.size());
		result.append("<br><br>");
		result.append("<font color=\"red\">");
		result.append("Top ");
		result.append(limit);
		result.append(" Tasks Info:");
		result.append("</font>");
		result.append("<br>");
		for(String key : keyLst)
		{
			i++;
			WebServiceInfo value = wsTaskMap.get(key);
			
			if(value != null && value.getStatus()!=null)
			{
				if( value.getStatus().equals("running")  )
				{
					result.append("<font color=\"green\">");
					result.append("WebServiceInfo "+i);					
					result.append(value.toString());
					result.append("<br>");
					result.append("</font>");
				}
				else if(value.getStatus().equals("completed"))
				{
					result.append("<font color=\"blue\">");
					result.append("WebServiceInfo "+i);
					result.append("</font>");					
					result.append(value.toString());
					result.append("<br>");
				}
			}
			
			if( i == limit )
				break;
		}
		return result.toString();
	}
	
	public static String getRunningWsInfos(Integer limit)
	{
		Set<String> kSet = wsTaskMap.keySet();
		List<String> keyLst = new ArrayList<String>();
		keyLst.addAll(kSet);
		Collections.sort(keyLst);
		Collections.reverse(keyLst);
		int i = 0;
		StringBuffer result = new StringBuffer();
		result.append("WS Tasks Size : ");
		result.append(kSet.size());
		result.append("<br><br>");
		result.append("<font color=\"red\">");		
		result.append(" Running Tasks Info:");
		result.append("</font>");
		result.append("<br>");
		result.append("<font color=\"green\">");
		for(String key : keyLst)
		{
			
			WebServiceInfo value = wsTaskMap.get(key);
			
			if(value != null && value.getStatus()!=null)
			{
				if( value.getStatus().equals("running")  )
				{
					i++;					
					result.append("WebServiceInfo "+i);					
					result.append(value.toString());
					result.append("<br>");
					
				}				
			}			
		}
		result.append("</font>");
		return result.toString();
	}
}
