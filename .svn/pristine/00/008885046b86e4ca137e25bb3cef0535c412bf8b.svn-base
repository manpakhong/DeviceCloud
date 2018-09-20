package com.littlecloud.ac.messagehandler;

import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.EventLogDAO;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.entity.report.EventLog;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.EventLogListObject;
import com.littlecloud.pool.object.EventLogObject;
import com.littlecloud.pool.object.eventlog.JsonEventLogObject;
import com.littlecloud.pool.object.eventlog.JsonTimeSyncObject;
import com.littlecloud.pool.object.eventlog.JsonWanObject;
import com.littlecloud.pool.object.utils.OrgInfoUtils;
import com.peplink.api.db.DBConnection;

public class EventLogMessageHandler {
	private static final Logger log = Logger.getLogger(EventLogMessageHandler.class);
	private static final String JSON_MSG_PATTERN = "\\#JSON_MSG\\#\\s{0,1}\\{.*\\}\\n{0,1}";
	
	// the new defined json in json #JSON_MSG# message type
	public static boolean doJsonMsgEvent(DevOnlineObject devOnlineO, String jsonMsg, DBConnection batchConnection){
		boolean result = false;
		try{
			if (devOnlineO != null && jsonMsg != null && !jsonMsg.isEmpty()){
				if (log.isDebugEnabled()){
					log.debugf("EVTLOG20140414 - JSON_MSG_PATTEN - json msg: %s", jsonMsg);
				}
				Pattern pattern = Pattern.compile(JSON_MSG_PATTERN);
				Matcher matcher = pattern.matcher(jsonMsg);
				JsonWanObject jsonWanObject = null;
				JsonTimeSyncObject jsonTimeSyncObject = null;
				while (matcher.find()) {
					if (log.isDebugEnabled()){
						log.debugf("EVTLOG20140414 - JSON_MSG_PATTEN - pattern matched: %s", matcher.group());
					}
					Object obj = convert2JsonObject(matcher.group());
					
					if (obj instanceof JsonWanObject){
//						jsonWanObject = (JsonWanObject) obj;
//						result = DdnsMessageHandler.doDdnsMessageHandle(devOnlineO, jsonWanObject);
					} else if (obj instanceof JsonTimeSyncObject){
						jsonTimeSyncObject = (JsonTimeSyncObject) obj;
						result = doTimeSyncEventMessage(devOnlineO, jsonTimeSyncObject, batchConnection);
					}
				}
				
			} else {
				if (log.isDebugEnabled()){
					log.debugf("EVTLOG20140414 - doJsonMsgEvent() - not a #JSON_MSG# - json format, iana: %s, sn: %s", devOnlineO.getIana_id(), devOnlineO.getSn());
				}
			}
		}catch (Exception e){
			log.errorf(e, "EVTLOG20140414 - doJsonMsgEvent(), iana: %s, sn: %s", devOnlineO.getIana_id(), devOnlineO.getSn());
		}
		return result;
	}
	
	private static boolean doTimeSyncEventMessage(DevOnlineObject devOnlineO, JsonTimeSyncObject jsonTimeSyncObject, DBConnection batchConnection){
		boolean result = false;
		try{
			Networks networks = OrgInfoUtils.getNetwork(devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());
			EventLog duplicate_log = null;

			
			if (devOnlineO != null && jsonTimeSyncObject != null) {
				Integer timeSyncValue = null;
				if (jsonTimeSyncObject.getShift() == null){
					timeSyncValue = 0;
				} else {
					timeSyncValue = jsonTimeSyncObject.getShift();
				}
				if (log.isDebugEnabled()){
					log.debug("timeSyncValue = " + timeSyncValue);
				}
				EventLogListObject eventLogListObject = new EventLogListObject();
				eventLogListObject.setKey(devOnlineO.getIana_id(), devOnlineO.getSn());
				
				eventLogListObject = ACUtil.<EventLogListObject> getPoolObjectBySn(eventLogListObject, EventLogListObject.class);
				if (eventLogListObject != null) {
					CopyOnWriteArrayList<EventLogObject> eventLogList = eventLogListObject.getEventObjectList();
					Date cur = null;
					if (log.isDebugEnabled()){
						log.debug("eventLogList = " + eventLogList);
					}
					for (EventLogObject evt : eventLogList) {
						cur = new Date();
						if( (evt.getTimestamp() + timeSyncValue) - ((int)(cur.getTime()/1000)) > 300)
						{
							log.info("Future date record found, record ignored, ts="+(evt.getTimestamp() + timeSyncValue));
							continue;
						}
						evt.setTimestamp(evt.getTimestamp() + timeSyncValue);
						Date newUtcTime = new Date((long) evt.getTimestamp() * 1000);
						Date newNetworkTime = DateUtils.offsetFromUtcTimeZoneId(newUtcTime, networks.getTimezone());
						EventLog eventLog = new EventLog(devOnlineO.getNetwork_id(), newNetworkTime, evt.getTimestamp(),
								evt.getDevice_id(), evt.getSsid(), evt.getClient_name(), evt.getClient_mac(),
								evt.getEvent_type(), evt.getDetail());
						eventLog.create();
						if (log.isDebugEnabled()){
							log.debugf("eventLog = %s", eventLog);
						}
						EventLogDAO eventDAO = new EventLogDAO(devOnlineO.getOrganization_id());
						duplicate_log = eventDAO.getDuplicateEventLog(eventLog.getNetworkId(), eventLog.getDeviceId(), eventLog.getUnixtime(), eventLog.getEventType());
						if(duplicate_log == null)
							batchConnection.addBatch(eventLog);
					}

					eventLogList.clear();
					eventLogListObject.setEventObjectList(eventLogList);
					ACUtil.<EventLogListObject> cachePoolObjectBySn(eventLogListObject, EventLogListObject.class);
				}   
			}			
		} catch (Exception e){
			log.errorf(e, "EVTLOG20140414 - doTimeSyncEventMessage(), iana: %s, sn: %s, newIp: %s", devOnlineO.getIana_id(), devOnlineO.getSn(), devOnlineO.getWan_ip());
		}
		
		return result;
	}
	
	public static Object convert2JsonObject(String eventLogDataMessage){
		
		Object returnObject = null;
		
		try{
			String json = eventLogDataMessage.replace("#JSON_MSG#", "");
			Gson gson = new Gson();
			JsonEventLogObject jsonEventLogObject = gson.fromJson(json, JsonEventLogObject.class);
			JsonElement jsonElement = jsonEventLogObject.getEvt_obj();
			
			
			if (jsonEventLogObject.getType().equals(JsonEventLogObject.TYPE_WAN)){
				JsonWanObject jsonWanObject = gson.fromJson(jsonElement.toString(),JsonWanObject.class);
				returnObject = jsonWanObject;
	
			} else if (jsonEventLogObject.getType().equals(JsonEventLogObject.TYPE_TIME_SYNC)){
				JsonTimeSyncObject jsonTimeSyncObject = gson.fromJson(jsonElement.toString(),JsonTimeSyncObject.class);
				returnObject = jsonTimeSyncObject;
			}
		}catch (Exception e){
			log.errorf(e, "EVTLOG20140414 - convert2JsonObject()");
		}
		
		return returnObject;
	}
}
