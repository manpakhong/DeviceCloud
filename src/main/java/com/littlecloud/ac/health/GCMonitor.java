package com.littlecloud.ac.health;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;

import org.jboss.logging.Logger;

import com.littlecloud.control.json.model.Json_GCMonitor;
import com.littlecloud.control.json.model.Json_GCMonitor.GCLog;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.sun.management.GarbageCollectionNotificationInfo;

public class GCMonitor {

	private static final Logger log = Logger.getLogger(GCMonitor.class);
	
	private static final String nwln = "\n";
	private static CopyOnWriteArrayList<String[]> gcLog = new CopyOnWriteArrayList<String[]>();

	public static String popGcLog()
	{
		Json_GCMonitor jsonGc = new Json_GCMonitor();
		List<GCLog> gcLst = jsonGc.getGcLst();
		
		List<String[]> copyLst = new ArrayList<String[]>(gcLog);
		gcLog.clear();
		for (String[] s : copyLst)
		{
			GCLog gcLog = jsonGc.new GCLog();
			gcLog.setTimestamp(Integer.valueOf(s[0]));
			gcLog.setLog(s[1]);
			gcLst.add(gcLog);
		}
		
		return JsonUtils.toJson(jsonGc);
	}

	public static void installGCMonitoring() 
	{
		log.infof("installGCMonitoring...");
		
		List<GarbageCollectorMXBean> gcbeans = java.lang.management.ManagementFactory.getGarbageCollectorMXBeans();

		for (GarbageCollectorMXBean gcbean : gcbeans) {
			// System.out.printf(gcbean.getName()+","+gcbean.getMemoryPoolNames()+","+gcbean.getCollectionTime()+","+gcbean.getCollectionCount()+"\n");

			NotificationEmitter emitter = (NotificationEmitter) gcbean;
			NotificationListener listener = new NotificationListener() {
				// keep a count of the total time spent in GCs
				long totalGcDuration = 0;

				// implement the notifier callback handler
				@Override
				public void handleNotification(Notification notification, Object handback) {

					if (notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {

						StringBuilder sb = new StringBuilder();

						GarbageCollectionNotificationInfo info = GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());

						long duration = info.getGcInfo().getDuration();
						String gctype = info.getGcAction();
						if ("end of minor GC".equals(gctype)) {
							gctype = "Young Gen GC";
						} else if ("end of major GC".equals(gctype)) {
							gctype = "Old Gen GC";
						}
						sb.append(nwln);
						sb.append(gctype + ": - " + info.getGcInfo().getId() + " " + info.getGcName() + " (from " + info.getGcCause() + ") " + duration + " microseconds; start-end times " + info.getGcInfo().getStartTime() + "-" + info.getGcInfo().getEndTime() + nwln);

						// sb.append("GcInfo CompositeType: " + info.getGcInfo().getCompositeType());
						sb.append("GcInfo MemoryUsageAfterGc: " + info.getGcInfo().getMemoryUsageAfterGc() + nwln);
						sb.append("GcInfo MemoryUsageBeforeGc: " + info.getGcInfo().getMemoryUsageBeforeGc() + nwln);

//						Map<String, MemoryUsage> memUsages = info.getGcInfo().getMemoryUsageBeforeGc();
//						for (Entry<String, MemoryUsage> memUsage : memUsages.entrySet()) {
//							sb.append(memUsage.getKey() + "*: " + memUsage.getValue() + nwln);
//						}

						// Get the information about each memory space, and pretty print it
						Map<String, MemoryUsage> membefore = info.getGcInfo().getMemoryUsageBeforeGc();
						Map<String, MemoryUsage> mem = info.getGcInfo().getMemoryUsageAfterGc();
						for (Entry<String, MemoryUsage> entry : mem.entrySet()) {
							String name = entry.getKey();
							MemoryUsage memdetail = entry.getValue();
							long memInit = memdetail.getInit();
							long memCommitted = memdetail.getCommitted();
							long memMax = memdetail.getMax();
							long memUsed = memdetail.getUsed();
							MemoryUsage before = membefore.get(name);
							long beforepercent = ((before.getUsed() * 1000L) / before.getCommitted());
							long percent = ((memUsed * 1000L) / before.getCommitted()); // >100% when it gets expanded

							sb.append(name + (memCommitted == memMax ? "(fully expanded)" : "(still expandable)") + "used: " + (beforepercent / 10) + "." + (beforepercent % 10) + "%->" + (percent / 10) + "." + (percent % 10) + "%(" + ((memUsed / 1048576) + 1) + "MB) / ");
						}
						sb.append(nwln);
						totalGcDuration += info.getGcInfo().getDuration();
						long percent = totalGcDuration * 1000L / info.getGcInfo().getEndTime();
						sb.append("GC cumulated overhead " + (percent / 10) + "." + (percent % 10) + "%" + nwln);

						if (gcLog.size() > 100)
							gcLog.remove(0);

						String result[] = new String[2];
						result[0] = String.valueOf(DateUtils.getUnixtime());
						result[1] = sb.toString();
						gcLog.add(result);
					}
				}
			};

			// Add the listener
			emitter.addNotificationListener(listener, null, null);
		}
	}
}
