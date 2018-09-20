/*
 * 
 * Pending orgId from cache for persisting data 
 * Use sn to find/verify device
 * 
 */

package com.littlecloud.control.firmware;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.branch.DeviceFirmwareSchedulesDAO;
import com.littlecloud.control.entity.branch.DeviceFirmwareSchedules;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.pool.object.FirmwareScheduleObject;
import com.peplink.api.db.util.DBUtil;

public class FirmwareQueue {
	public static Logger log = Logger.getLogger(FirmwareQueue.class);
	public static Logger logJson = Logger.getLogger(FirmwareQueue.class + "Json");

	private static final int MAX_QUEUE_SIZE = 20000;
	private static final int MAX_BATCH_SIZE = 100;
	private static final long RETRY_UPGRADE_TIMEOUT_IN_SEC = 60; 
	
	private static final Level logINFO = Level.INFO;
	private static final Level logDEBUG = Level.DEBUG;

	private static BlockingQueue<DeviceFirmwareSchedules> queue = new ArrayBlockingQueue<DeviceFirmwareSchedules>(MAX_QUEUE_SIZE);
	// /* move to grid */private static Set<Integer> scheduleIdSet = new HashSet<Integer>();

	private static int counter = 0;
	
	public static BlockingQueue<DeviceFirmwareSchedules> getQueue() {
		return queue;
	}

	public synchronized static void setCounter(boolean bAdd)
	{
		if (bAdd)
		{
			counter++;
			//log.debugf("added counter=%d", counter);
		}
		else
		{
			counter--;
//			log.debugf("minus counter=%d", counter);
		}
	}

	public static boolean put(DeviceFirmwareSchedules dfs)
	{
		log.debug("[FirmwareQueue] add dfs " + dfs);

		Boolean result = true;

		/* create or get corresponding queue */
		if (queue == null)
		{
			log.error("[FirmwareQueue] queue is null!");
			return false;
		}

		// if (!scheduleIdSet.add(dfs.getId()))
		// {
		// log.error("scheduleIdSet - fail to add " + dfs.getId());
		// result = false;
		// }

		if (!queue.contains(dfs))
		{
			if (!queue.add(dfs))
			{
				log.error("queue - fail to add " + dfs);
				result = false;
			}
		}

		return result;
	}

	// public static synchronized BlockingQueue<DeviceFirmwareSchedules> startScheduler()
	// {
	// /* create new queue */
	// queue = new ArrayBlockingQueue<DeviceFirmwareSchedules>(MAX_QUEUE_SIZE);
	//
	// /* start scheduler to persist queue */
	// // FirmwareScheduler fws = new FirmwareScheduler();
	// // fws.startFirmwareScheduler();
	// FirmwareScheduler.startFirmwareScheduler();
	//
	// log.info("[FirmwareQueue] Queue started.");
	//
	// return queue;
	// }

	public static DeviceFirmwareSchedules poll()
	{
		// log.debug("[FirmwareQueue] Queue poll");

		if (queue == null)
			return null;

		DeviceFirmwareSchedules dfs = queue.poll();
		return dfs;
	}

	public static DeviceFirmwareSchedules peek()
	{
		// log.debug("[FirmwareQueue] Queue peek");

		if (queue == null)
			return null;

		DeviceFirmwareSchedules dfs = queue.peek();
		return dfs;
	}

	public static synchronized void processQueue(long unixtime)
	{
		setCounter(true);
		if (log.isDebugEnabled())
			log.debugf("vvvvvvvvvvvvvv [FirmwareQueue] processQueue - BATCH unixtime %d (counter=%d) vvvvvvvvvvvvvv", unixtime, counter);

		DBUtil dbUtil = null;
		boolean isOwnSession = false;
		
		try {
			dbUtil = DBUtil.getInstance();
			if (!dbUtil.isSessionStarted())
			{
				dbUtil.startSession();
				isOwnSession = true;
			}
			
			/* get current queue size */
			if (queue == null || queue.size() == 0)
			{
				if (log.isDebugEnabled())
					log.debug("processQueue queue is empty");
				setCounter(false);
				return;
			}
			if (log.isInfoEnabled())
				log.info("processQueue curQueueSize =" + queue.size());
	
			/* process queue - get current schedules from queue */
			List<DeviceFirmwareSchedules> processLst = getCurrentSchedule(unixtime);
			if (log.isInfoEnabled())
				log.infof("processLst.size()=" + processLst.size());
			FirmwareUpgradeUtils.performFirmwareUpgradeScheduleLst(processLst);
	
			setCounter(false);
			if (log.isInfoEnabled())
				log.infof("^^^^^^^^^^^^^^ [FirmwareQueue] processQueue - BATCH is unixtime %d successful! (counter=%d) ^^^^^^^^^^^^^^", unixtime, counter);
		} catch (Exception e)
		{
			log.error("processQueue error in startSession", e);
			return;
		} finally {
			try {
				if (isOwnSession)
				{
					if (dbUtil!=null && dbUtil.isSessionStarted()) {
						dbUtil.endSession();
					}
				}
			} catch (Exception e2) {
				log.error("processQueue - error in endSession", e2);
			}
		}
	}

	public static synchronized void updateQueue(long unixtime)
	{		
		log.logf(logINFO, " ********* updateQueue - unixtime %d start", unixtime);
		/* update queue from db - get all schedule before latter 1 mins */
		CopyOnWriteArrayList<Integer> scheduledIdList;
		
		try {
			List<DeviceFirmwareSchedules> newLst = null;
			List<DeviceFirmwareSchedules> retryDfsLst = new ArrayList<DeviceFirmwareSchedules>();
			List<Integer> exemptIdLst = new ArrayList<Integer>();

			DeviceFirmwareSchedulesDAO dfsDAO = new DeviceFirmwareSchedulesDAO(true);

			/* get scheduleIdLst from grid */
			FirmwareScheduleObject fsO = new FirmwareScheduleObject();
			try {
				fsO = ACUtil.<FirmwareScheduleObject> getPoolObjectBySn(fsO, FirmwareScheduleObject.class);
			} catch (InstantiationException | IllegalAccessException e) {
				log.errorf("Cache get exception", e);
			}
			if (fsO==null || (DateUtils.getUnixtime() - fsO.getCreateTime()/1000 > 3600))	/* clear object if not updated for 60 mins */
			{
				log.warnf("FirmwareScheduleObject is cleared.");
				fsO = new FirmwareScheduleObject();				
			}
			scheduledIdList = fsO.getScheduleIdList();

			/* get retry schedule id */
			retryDfsLst = dfsDAO.getTimeoutSchedulesBeforeTime(unixtime, RETRY_UPGRADE_TIMEOUT_IN_SEC); 
			for (DeviceFirmwareSchedules retryDfs:retryDfsLst)
			{

//				if (retryDfs.getSn().equalsIgnoreCase("2831-5CA2-4681"))
//					log.warnf("DEBUG 2831-5CA2-4681 is in the retryDfsLst! dfs %s unixtime %d", retryDfs, unixtime);				
				if (retryDfs!=null)
				{
					scheduledIdList.remove(retryDfs.getId());
				}
			}
			
			/* prevent duplicate schedule id */
			exemptIdLst.addAll(scheduledIdList);
			newLst = dfsDAO.getSchedulesBeforeTime(unixtime, exemptIdLst);
			if (newLst!=null && newLst.size()!=0)
			{
				for (DeviceFirmwareSchedules dfs : newLst)
				{	
//					if (dfs.getSn().equalsIgnoreCase("2831-5CA2-4681"))
//						log.warnf("DEBUG 2831-5CA2-4681 is in the newLst! dfs %s unixtime %d", dfs, unixtime);
					
					put(dfs);
					if (!scheduledIdList.contains(dfs.getId()))
					{
//						if (dfs.getSn().equalsIgnoreCase("2831-5CA2-4681"))
//							log.warnf("DEBUG 2831-5CA2-4681 is not in the scheduledIdList! dfs %s unixtime %d", dfs, unixtime);
						
						scheduledIdList.add(dfs.getId());
					}
					else
					{
//						if (dfs.getSn().equalsIgnoreCase("2831-5CA2-4681"))
//							log.warnf("DEBUG 2831-5CA2-4681 is in the scheduledIdList! dfs %s unixtime %d", dfs, unixtime);
					}
				}
				
				/* update running schedule list */
				try {
					ACUtil.<FirmwareScheduleObject> cachePoolObjectBySn(fsO, FirmwareScheduleObject.class);
				} catch (InstantiationException | IllegalAccessException e) {
					log.errorf("Cache put exception", e);
				}
			}			
		} catch (SQLException e)
		{
			log.error("SQLException ", e);
		}
		if (log.isInfoEnabled())
		log.logf(logINFO, " ********* updateQueue - unixtime %d is successful (queue size %d)!", unixtime, queue.size());
	}

	private static synchronized List<DeviceFirmwareSchedules> getCurrentSchedule(long unixtime)
	{
		List<DeviceFirmwareSchedules> scheduleLst = new ArrayList<DeviceFirmwareSchedules>();
		int curBatchSize = 0;
		
		DeviceFirmwareSchedules dfs = peek();
		while (dfs != null && dfs.getSchedule_time() <= unixtime && curBatchSize < MAX_BATCH_SIZE)
		{
			curBatchSize++;

//			if (dfs.getSn().equalsIgnoreCase("2831-5CA2-4681"))
//				log.warnf("DEBUG 2831-5CA2-4681 is in the queue! dfs %s unixtime %d", dfs, unixtime);
			
			dfs = poll();
			scheduleLst.add(dfs);

			dfs = peek();
		}
		return scheduleLst;
	}
}
