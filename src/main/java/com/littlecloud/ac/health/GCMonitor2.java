package com.littlecloud.ac.health;

import java.lang.management.MemoryMXBean;
import java.util.List;

import com.littlecloud.control.json.model.Json_GCMonitor;
import com.littlecloud.control.json.model.Json_GCMonitor.GCLog;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;

public class GCMonitor2 {
	public static String getCurrentMemoryStatus() {
		String jsonResult = "";

//		MBeanServer mbeanServer = java.lang.management.ManagementFactory
//				.getPlatformMBeanServer();
//
//		List<GarbageCollectorMXBean> bean = java.lang.management.ManagementFactory
//				.getGarbageCollectorMXBeans();
		
		MemoryMXBean mem = java.lang.management.ManagementFactory
				.getMemoryMXBean();

		String log = mem.getObjectName() + "," + mem.getHeapMemoryUsage()
				+ "," + mem.getNonHeapMemoryUsage() + ","
				+ mem.getObjectPendingFinalizationCount();
		Json_GCMonitor jsonGc = new Json_GCMonitor();
		GCLog gcLog = jsonGc.new GCLog();
		
		List<GCLog> gcLst = jsonGc.getGcLst();
		gcLog.setTimestamp(DateUtils.getUnixtime());
		gcLog.setLog(log);
		gcLst.add(gcLog);
		
		jsonResult = JsonUtils.toJson(jsonGc);

		return jsonResult;
	}
}
