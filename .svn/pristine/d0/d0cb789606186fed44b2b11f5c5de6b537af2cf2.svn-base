package com.littlecloud.ac.root;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.json.JSONObject;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

import com.google.gson.Gson;
import com.littlecloud.ac.ACService;
import com.littlecloud.ac.DebugManager;
import com.littlecloud.ac.WtpMsgHandlerPool;
import com.littlecloud.ac.WtpMsgHandlerUtils;
import com.littlecloud.ac.handler.BranchServerRedirectionHandler;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.ac.json.model.command.QueryInfo;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.jdbc.BaseDaoInstances.InstanceType;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.pool.object.ACRegListObject;
import com.littlecloud.pool.object.BranchInfoObject;
import com.littlecloud.pool.object.DevInfoObject;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.utils.BranchUtils;
import com.littlecloud.pool.object.utils.DeviceUtils;
import com.littlecloud.pool.utils.PropertyService;
//import com.littlecloud.control.dao.util.HibernateUtil;
import com.peplink.api.db.util.DBUtil;

public class WtpMsgHandlerRoot implements Runnable {

	private static boolean PROD_MODE = DebugManager.isPROD_MODE();
	private static final int MAX_CALL_TIME = 15; // 15 sec
	private static final int MAX_DEV_ONLINE_CALL_TIME = 5;
	private static final int MAX_JSON_MSG = 10240;
	
	private static final Logger log = Logger.getLogger(WtpMsgHandlerRoot.class);	
	
	private static PropertyService<WtpMsgHandlerRoot> ps = new PropertyService<WtpMsgHandlerRoot>(WtpMsgHandlerRoot.class);
	public static BlockingQueue<String> jsonQueue = new ArrayBlockingQueue<String>(MAX_JSON_MSG);
	
	private static final int EVENT_EXPIRED_TIME_IN_SEC = ps.getInteger("EVENT_EXPIRED_TIME_IN_SEC");	//TODO 
	public static final int MAX_CONCURRENT_MESSAGE = ps.getInteger("MAX_CONCURRENT_MESSAGE");
	private static final int MAX_CURRENT_ONLINEEVENT_COUNTER = ps.getInteger("MAX_CURRENT_ONLINEEVENT_COUNTER");
	private static final WtpMsgHandlerPool wtppool = WtpMsgHandlerPool.getInstance();	
	
	public static final String DATA_TAG = "data";
	
	private String json;
	private Long threadid;

	private static AtomicInteger counter = new AtomicInteger(0);
	private static AtomicInteger counterMax = new AtomicInteger(0);
	//private static AtomicInteger currentOnlineEventCounter = new AtomicInteger(0);	
	private static AtomicInteger msg_process_counter = new AtomicInteger(0);
	
	public static int getMaxJsonPipeInfoTypeDevLocations() {
		return MAX_JSON_MSG;
	}

	public String getJson() {
		return json;
	}
	
	public String getMessageInfo() {
		StringBuilder sb = null;
		
		Gson gson = new Gson();
		QueryInfo<Object> info = null;
		try {
			info = gson.fromJson(json, QueryInfo.class);
			
			if (info!=null)
			{
				sb = new StringBuilder();
				MessageType mt = info.getType();
				if(mt != null){
					sb.append(mt);
					sb.append(" ");
				}
				
				sb.append(info.getSid());
				sb.append(" ");
				sb.append(info.getIana_id());
				sb.append(" ");
				sb.append(info.getSn());
				sb.append(" ");
				sb.append("starttime:");
				sb.append(DateUtils.getUtcDate());
				
				return sb.toString();
			}
		} catch (Exception e) {
			return json;
		}
		
		return json;
	}

	public static int getEventExpiredTimeInSec() {
		return EVENT_EXPIRED_TIME_IN_SEC;
	}

	public static int getCounter() {
		return counter.intValue();
	}

	public static int getCounterMax() {
		return counterMax.intValue();
	}

//	public static AtomicInteger getCurrentOnlineEventCounter() {
//		return currentOnlineEventCounter;
//	}

	public static void setCounter(boolean bAdd) {
		if (bAdd)
		{
			counter.incrementAndGet();

			if (counter.intValue() > counterMax.intValue())
				counterMax.set(counter.intValue());
		}
		else
		{
			counter.decrementAndGet();
		}
	}
	
	public static int getMsg_process_counter() {
		return msg_process_counter.get();
	}

	public static void setMsg_process_counter(int msg_process_counter) {
		WtpMsgHandlerRoot.msg_process_counter.set(msg_process_counter);
	}
	
	public static void addMsgProcessCounter()
	{
		WtpMsgHandlerRoot.msg_process_counter.incrementAndGet();
	}

	public static void reQueue(String msgJson)
	{
		try {
			jsonQueue.add(msgJson);
			if (log.isDebugEnabled())
				log.debug("ReQueued json: " + msgJson);
		} catch (IllegalStateException ie) {
			log.error("Error in reQueueing json: " + msgJson + ", e=" + ie, ie);
		} catch (Exception e) {
			log.error("Unknown error in reQueueing json: " + msgJson + ", e=" + e, e);
		}
	}

	public WtpMsgHandlerRoot(String json)
	{
		this.json = json;
	}
	
	@Override
	public void run() {
		
		if(json.indexOf("PIPE_INFO_AC_REG_LIST") !=-1) {
			Iterator<Map.Entry<Long, String>> iter = WtpMsgHandlerPool.threadExecInfoMap.entrySet().iterator();
			while(iter.hasNext()) { 
				Map.Entry<Long, String> threadInfo = iter.next();
				if (threadInfo.getValue()==null)
					log.warnf("threadInfo.getValue() is null for key %s", threadInfo.getKey());
				
				if(threadInfo.getValue()!=null && threadInfo.getValue().startsWith("PIPE_INFO_AC_REG_LIST")) {
					log.warn("Skip PIPE_INFO_AC_REG_LIST as last msg is still processing in thread "+ threadInfo.getKey());
					return;
				}
			}
		}
		
		this.threadid = Thread.currentThread().getId();		
		wtppool.startThread(threadid, this.getMessageInfo());

		int callStartTime = DateUtils.getUnixtime();
		int callMsgTime = 0;
		boolean isOnlineEventCounted = false;
		
		JSONObject object;
		JSONObject data;
		DevOnlineObject devOnlineO = null;
		String logMsg = null; 		
		
		if (log.isDebugEnabled())
			log.debugf("received wtp msg %s", json);
		
		if (json == null)
		{
			log.warn("received json is null!");
			wtppool.endThread(this.threadid);
			return;
		}

		/* parse info type from WTP before assigning to object */
		Gson gson = new Gson();
		QueryInfo<Object> info = null;
		try {
			info = gson.fromJson(json, QueryInfo.class);
		} catch (Exception e) {
			log.errorf("Json from WTP format exception (json=%s)", json, e);
			wtppool.endThread(this.threadid);
			return;
		}
		
		MessageType mt = info.getType();
		if(mt == null){
			if(log.isDebugEnabled())
				log.debugf("Invalid message received, info=%s", info);
			wtppool.endThread(this.threadid);
			return;
		}

		setCounter(true);
		
		try {
			addMsgProcessCounter();
			int status = info.getStatus();						
			callMsgTime = DateUtils.getUnixtime();

			switch (mt)
			{
			/* AC scheduler sends in fix interval e.g.30s */
			case PIPE_INFO_AC_REG_LIST:	
			{
				/* use batch insert */
				DBUtil dbUtil = null;
				StringBuilder logWarn = new StringBuilder();
						
				try {
					BranchServerRedirectionHandler redirectHandler = BranchServerRedirectionHandler.getInstance();
					
					 /* Noted: DBUtil mentioned startSession should be called before getConnection! */
					dbUtil = DBUtil.getInstance(InstanceType.IC2ROOT.getValue());
					dbUtil.startSession();
					
					object = JSONObject.fromObject(info);
					data = object.getJSONObject(DATA_TAG);
					ACRegListObject regO = gson.fromJson(data.toString(), ACRegListObject.class);
					List<DevInfoObject> devInfoLst = regO.getDev_list();
					if (log.isDebugEnabled())
						log.debugf("REG_LIST start - devInfoLst.size %d", devInfoLst!=null?devInfoLst.size():0);
					
					for (DevInfoObject devInfo: devInfoLst)
					{
						if (redirectHandler.isBranchredirectionenabled())
						{
							if (redirectHandler.redirectDevToBranchIfRegistered(devInfo.getIana_id(), devInfo.getSn()))
							{
								logMsg = String.format("Branch Redirection - %d %s is not in redirection map", devInfo.getIana_id(), devInfo.getSn());
								DeviceUtils.logDevInfoObject(devInfo, log, Level.INFO, logMsg);
							}
						}							
					}
					
					/* cache for lookup */				 
					regO.setIana_id(9999);
					regO.setSn(ACService.getServerName());
					ACUtil.<ACRegListObject> cachePoolObjectBySn(regO, ACRegListObject.class);
					
				} catch (Exception e) {
					log.error("AC_REG_LIST error in startSession", e);
					return;
				} finally {
					
					if (logWarn.toString().length()!=0)
						log.warnf("AC_REG_LIST WARN: %s", logWarn.toString());
					
					try {
						if (dbUtil!=null && dbUtil.isSessionStarted()) {
							dbUtil.endSession();
						}
					} catch (Exception e2) {
						log.error("AC_REG_LIST - error in endSession", e2);
					}
				}				
				if (log.isDebugEnabled())
					log.debugf("REG_LIST end");				
				return;
			}
					
			default:
			{
				if (log.isDebugEnabled()) log.debugf("Message not processed %s", mt);
			}break;
			}
		} catch (net.sf.json.JSONException je) {
			log.error("JSONException " + json, je);
		} catch (com.google.gson.JsonSyntaxException e) {
			log.warn("incorrect json format data is received from AC: " + e.getMessage() + " - " + json ,e);
		} catch (NullPointerException e) {
			log.warn("Exception to be investigated ... (" + e.getMessage() + ") (json=" + json + ")", e);
		} catch (Exception e) {
			log.error("Other exceptions", e);
		} finally
		{
			setCounter(false);

			int callEndTime = DateUtils.getUnixtime();
			int callUseTime = callEndTime - callStartTime;
			int callHandleTime = callEndTime - callMsgTime;
			/* log message with processing time more than 60 */
			if (callUseTime > MAX_CALL_TIME)
				log.warnf("message type %s from dev %d %s has processed for %d,%d seconds", info.getType(), info.getIana_id(), info.getSn(), callUseTime, callHandleTime);
			
			if (mt==MessageType.PIPE_INFO_TYPE_DEV_ONLINE && callUseTime > MAX_DEV_ONLINE_CALL_TIME)
				log.warnf("PIPE_INFO_TYPE_DEV_ONLINE from dev %d %s has processed for %d,%d seconds", info.getIana_id(), info.getSn(), callUseTime, callHandleTime);			
			
			wtppool.endThread(Thread.currentThread().getId());
		}
				
		log.infof("WtpMsgHandler counter end = %d", counter);
		return;
	}
}