package com.littlecloud.pool.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jboss.logging.Logger;

import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.control.json.util.DateUtils;

public class QueueTaskInfo {
	
	public static Logger log = Logger.getLogger(QueueTaskInfo.class);
	
	boolean started = false;
	
	private String orgId;
	private MessageType mt;
	private Integer ianaId;
	private String sn;
	private Date startTime;
	private Date endTime;
	private long threadId; 
	
	private MessageType longMt;
	private Integer longIanaId;
	private String longSn;
	private Date longStartTime;
	private Date longEndTime;
	
	public void startTask(String orgId, Integer ianaId, String sn, MessageType mt) {
		Thread t = Thread.currentThread();
		long threadId=t.getId();
		
		if(started)
		{
//			log.warnf("Previous started not yet ended and skip update statistics! (%s, %s, %d, %s) vs (%s, %s, %d, now)", this.orgId, this.mt, this.threadId, this.startTime, orgId, mt, threadId);
			return;
		}
		this.orgId = orgId;
		this.mt = mt;
		this.ianaId = ianaId;
		this.sn = sn;
		this.startTime = DateUtils.getUtcDate();
		this.endTime = null;
		this.threadId = threadId;
		
		QueueControl.getQueueRunningTasks().put(orgId, this);
		started = true;
	}
	
	public void endTask(String orgId, MessageType mt) {		
		Thread t = Thread.currentThread();
		long threadId=t.getId();
		
		if (orgId != this.orgId || mt != this.mt || this.threadId !=threadId)
		{
			// log.warnf("startTask/endTask message type is not consistent!! (%s, %s, %d, %s) vs (%s, %s, %d, now)", this.orgId, this.mt, this.threadId, this.startTime, orgId, mt, threadId);
			return;
		}
		
		started = false;
		this.endTime = DateUtils.getUtcDate();
		if ( (this.longStartTime==null || this.longEndTime==null) 
				|| (this.endTime.getTime() - this.startTime.getTime() >= this.longEndTime.getTime() - this.longStartTime.getTime()) )
		{
			this.longMt = mt;
			this.longIanaId = ianaId;
			this.longSn = sn;
			this.longStartTime = this.startTime;
			this.longEndTime = this.endTime;
		}
		
		QueueControl.getQueueRunningTasks().put(orgId, this);
	}
	
	public String startCacheTask(String sn, MessageType mt) 
	{
		this.mt = mt;
		this.sn = sn;
		this.startTime = DateUtils.getUtcDate();
		this.endTime = null;
		
		Date date = DateUtils.getUtcDate();
		String key = date.getTime()+"|"+sn+"|"+mt.name();
		String value = sn+"|"+Thread.currentThread().getId()+"|"+"running"+"|"+date;
		ConcurrentHashMap<String, String> tasksMap = QueueCacheControl.getQueueRunningTasks();
		
		if( tasksMap.size() >= 5000 )
		{
			this.cleanMap(tasksMap);
		}
			
		tasksMap.put(key, value);
		this.started = true;
		
		return key;
	}
	
	public void endCacheTask(String key) 
	{	
		String value = QueueCacheControl.getQueueRunningTasks().get(key);
		this.started = false;
		
		ConcurrentHashMap<String, String> tasksMap = QueueCacheControl.getQueueRunningTasks();
		String[] keyItems = key.split("\\|");
		String[] valueItems = value.split("\\|");
		long startTimeMilli = Long.parseLong(keyItems[0]);
		Date endTime = DateUtils.getUtcDate();
		long diff = endTime.getTime() - startTimeMilli;
		value = valueItems[0]+"|"+valueItems[1]+"|"+"completed"+"|"+endTime+"|"+diff;
		
		if(tasksMap.size() >= 5000)
		{
			this.cleanMap(tasksMap);
		}
		
		tasksMap.put(key, value);
	}
	
	public void cleanMap(ConcurrentHashMap<String,String> map)
	{
		Set<String> kSet = map.keySet();
		List<String> list = new ArrayList<String>();
		list.addAll(kSet);
		Collections.sort(list);
		int loop = map.size() / 2;
		for(int i = 0; i < loop ; i++)
		{
			String key = list.get(i);
			String value = map.get(key);
			if(value != null)
			{
				String[] items = value.split("\\|");
				if(items != null && items[2]!=null && items[2].equals("completed"))
				{
					map.remove(key);
				}
			}
			else
			{
//				log.warn("Value of " + key + " is null");
				map.remove(key);
			}
		}
	}
	
	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public MessageType getMt() {
		return mt;
	}
	public void setMt(MessageType mt) {
		this.mt = mt;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public MessageType getLongMt() {
		return longMt;
	}
	public void setLongMt(MessageType longMt) {
		this.longMt = longMt;
	}
	public Date getLongStartTime() {
		return longStartTime;
	}
	public void setLongStartTime(Date longStartTime) {
		this.longStartTime = longStartTime;
	}
	public Date getLongEndTime() {
		return longEndTime;
	}
	public void setLongEndTime(Date longEndTime) {
		this.longEndTime = longEndTime;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}		
	
	public String toHtml() {
		return "QueueTaskInfo [<br>started=" + started + ", <br>orgId=" + orgId 
				+ ", <br><br>mt=" + mt 
				+ ", <br>ianaId=" + ianaId + ", <br>sn=" + sn
				+ ", <br>startTime=" + startTime + ", <br>endTime=" + endTime
				+ ", <br><br>longMt=" + longMt
				+ ", <br>longIanaId=" + longIanaId + ", <br>longSn=" + longSn
				+ ", <br>longStartTime=" + longStartTime + ", <br>longEndTime=" + longEndTime + "]";
	}
	
	public static String getLastNQueueCacheInfos(Integer limit)
	{
		ConcurrentHashMap<String,String> tasksMap = QueueCacheControl.getQueueRunningTasks();
		Set<String> kSet = tasksMap.keySet();
		List<String> keyLst = new ArrayList<String>();
		keyLst.addAll(kSet);
		Collections.sort(keyLst);
		Collections.reverse(keyLst);
		int i = 0;
		StringBuffer result = new StringBuffer();
		result.append("Queue cache tasks size : ");
		result.append(kSet.size());
		result.append("<br><br>");
		for(String key : keyLst)
		{
			i++;
			String value = tasksMap.get(key);
			String[] keyItems = key.split("\\|");
			String[] valueItems = value.split("\\|");
			
			if(valueItems != null && valueItems.length > 0)
			{
				if( valueItems[2].equals("running")  )
				{
					result.append("QueueTaskInfo "+i+" [<br>sn=");
					result.append(valueItems[0]);
					result.append("<br>");
					result.append("mt=");
					result.append(keyItems[2]);
					result.append("<br>");
					result.append("thread id=");
					result.append(valueItems[1]);
					result.append("<br>");
					result.append("status=");
					result.append(valueItems[2]);
					result.append("<br>");
					result.append("start time=");
					result.append(valueItems[3]);
					result.append("]<br><br>");
				}
				else if(valueItems[2].equals("completed"))
				{
					result.append("QueueTaskInfo "+i+" [<br>sn=");
					result.append(valueItems[0]);
					result.append("<br>");
					result.append("mt=");
					result.append(keyItems[2]);
					result.append("<br>");
					result.append("thread id=");
					result.append(valueItems[1]);
					result.append("<br>");
					result.append("status=");
					result.append(valueItems[2]);
					result.append("<br>");
					result.append("start time=");
					result.append(valueItems[3]);
					result.append("<br>");
					result.append("duration=");
					result.append(valueItems[4]);
					result.append("]<br><br>");
				}
			}
			
			if( i == limit )
				break;
		}
		return result.toString();
	}
	
	public String getName(Class<?> c)
	{
		return c.getSimpleName();
	}
	
	public static void main(String[] args)
	{
		System.out.println(""+new QueueTaskInfo().getName(QueueControl.class));
	}
}