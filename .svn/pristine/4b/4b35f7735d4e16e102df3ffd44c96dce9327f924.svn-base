package com.littlecloud.ac;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.logging.Logger;

import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.ClientInfosDAO;
import com.littlecloud.control.dao.ClientIpMacMappingsDAO;
import com.littlecloud.control.dao.ClientMonthlyUsagesDAO;
import com.littlecloud.control.dao.ClientSsidUsagesDAO;
import com.littlecloud.control.dao.ClientUsagesDAO;
import com.littlecloud.control.dao.DailyClientSsidUsagesDAO;
import com.littlecloud.control.dao.DailyClientUsagesDAO;
import com.littlecloud.control.dao.DailyDeviceSsidUsagesDAO;
import com.littlecloud.control.dao.DailyDeviceUsagesDAO;
import com.littlecloud.control.dao.DeviceDailyDpiUsagesDAO;
import com.littlecloud.control.dao.DeviceDpiUsagesDAO;
import com.littlecloud.control.dao.DeviceMonthlyDpiUsagesDAO;
import com.littlecloud.control.dao.DeviceMonthlyUsagesDAO;
import com.littlecloud.control.dao.DeviceSsidUsagesDAO;
import com.littlecloud.control.dao.DeviceUsagesDAO;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.dao.NetworksDAO;
import com.littlecloud.control.dao.branch.OuiInfosDAO;
import com.littlecloud.control.dao.branch.ReportConsolidateJobsDAO;
import com.littlecloud.control.dao.branch.SnsOrganizationsDAO;
import com.littlecloud.control.entity.ClientInfos;
import com.littlecloud.control.entity.ClientInfosId;
import com.littlecloud.control.entity.ClientIpMacMappings;
import com.littlecloud.control.entity.ClientIpMacMappingsId;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.DpiUsageObject;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.entity.branch.ReportConsolidateJobs;
import com.littlecloud.control.entity.report.ClientMonthlyUsages;
import com.littlecloud.control.entity.report.ClientSsidUsages;
import com.littlecloud.control.entity.report.ClientUsages;
import com.littlecloud.control.entity.report.DailyClientSsidUsages;
import com.littlecloud.control.entity.report.DailyClientUsages;
import com.littlecloud.control.entity.report.DailyDeviceSsidUsages;
import com.littlecloud.control.entity.report.DailyDeviceUsages;
import com.littlecloud.control.entity.report.DeviceDailyDpiUsages;
import com.littlecloud.control.entity.report.DeviceMonthlyDpiUsages;
import com.littlecloud.control.entity.report.DeviceMonthlyUsages;
import com.littlecloud.control.entity.report.DeviceUsages;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.pool.model.DailyClientSsidUsageResult;
import com.littlecloud.pool.model.DailyDevSsidUsageResult;
import com.littlecloud.pool.object.ClientInfoObject;
import com.littlecloud.pool.object.DistributedQueueObject;
import com.littlecloud.pool.object.StationList;
import com.littlecloud.pool.object.StationListObject;
import com.littlecloud.pool.object.StationUsageObject;
import com.littlecloud.pool.object.StationUsageObject.StationUsageList;
import com.littlecloud.pool.object.UpdatedClientInfoObject;
import com.littlecloud.pool.object.utils.BranchUtils;
import com.littlecloud.pool.object.utils.NetUtils;
import com.littlecloud.pool.object.utils.OrgInfoUtils;
import com.littlecloud.services.CaptivePortalDailyUsagesMgr;
import com.littlecloud.services.CaptivePortalUserDailyUsagesMgr;
import com.littlecloud.services.SnsOrganizationsMgr;
import com.littlecloud.utils.CalendarUtils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.util.DBUtil;

public class ReportConsolidateService {

	private static final Logger logger = Logger.getLogger(ReportConsolidateService.class);
	private BlockingQueue<String> clientDailyUsagesQueue = new ArrayBlockingQueue<String>(50000); // max 50k orgs
	private BlockingQueue<String> deviceDailyUsagesQueue = new ArrayBlockingQueue<String>(50000); // max 50k orgs
	private final static int NUM_CLIENT_DAILY_USAGES_THREADS = 5;
	private final static int NUM_DEVICE_DAILY_USAGES_THREADS = 5;
	private final static int MAX_DEVICE_INACTIVE = 7200; // 2 hrs
	private final static int MAX_STATION_INACTIVE = 7200; // 2 hrs
	private final static int MAX_CLIENTINFO_KEPT = 86400000; // 1 day
	private final static int MAX_BATCH_SIZE = 100; // max batch before commit
	
	private static boolean monthly_dev_consolidate = false;
	private static boolean monthly_client_consolidate = false;
	private static boolean monthly_dpi = false;
	private static long monthly_dev_consolidate_time = 0;
	private static long monthly_client_consolidate_time = 0;
	private static long monthly_dpi_time = 0;
	private static final String VER = "2.0.21.5";
	
	public void StartConsolidateReport(Long reportTime){
		long currentTimeInMillis = 0;
		if (reportTime == null || reportTime == 0){
			Calendar currentTime = Calendar.getInstance();
			currentTimeInMillis = currentTime.getTimeInMillis();
		}else {
			currentTimeInMillis = reportTime;
		}
		//long RoundToHr = currentTimeInMillis/3600000 * 3600000;
		long RoundToHalfHr = currentTimeInMillis/1800000 * 1800000;
		int RounfToHalfHrKey = (int)(RoundToHalfHr/1000);
		//check jobs table to see if
		//--same job time is already exists
		//--job with later time already exists
		ReportConsolidateJobsDAO reportConsolidateJobsDAO = null;
		ReportConsolidateJobs reportConsolidateJobs = null;
		try {
			reportConsolidateJobsDAO = new ReportConsolidateJobsDAO();
			reportConsolidateJobs = reportConsolidateJobsDAO.findCurrentOrLaterTimeJob(RounfToHalfHrKey);
			if (reportConsolidateJobs == null){
				reportConsolidateJobs = new ReportConsolidateJobs(RounfToHalfHrKey, false, (int)(Calendar.getInstance().getTimeInMillis()/1000), 0);
				reportConsolidateJobsDAO.save(reportConsolidateJobs);				
			}else {
				//already exists, or newer job exists
				logger.info("Current or newer job already done "+RounfToHalfHrKey);
				return;
			}
		}catch (Exception e) {
			logger.error("Error in accessing report consolidate job table 1- " + e,e);
			return;
		}
		
		ReportConsolidateJobs lastReportConsolidateJobs = null;
		try {
			reportConsolidateJobsDAO = new ReportConsolidateJobsDAO();
			lastReportConsolidateJobs = reportConsolidateJobsDAO.findPreviousJobTime(RounfToHalfHrKey);
		}catch (Exception e) {
			logger.error("Error in accessing report consolidate job table 2- " + e,e);
		}
		
		
		if (lastReportConsolidateJobs == null){
			//no previous job
			//just done current time
			boolean result = ConsolidateReport(RoundToHalfHr);
			reportConsolidateJobs.setAllSuccess(result);
			reportConsolidateJobs.setEndTime((int)(Calendar.getInstance().getTimeInMillis()/1000));
			try {
				reportConsolidateJobsDAO = new ReportConsolidateJobsDAO();
				reportConsolidateJobsDAO.saveOrUpdate(reportConsolidateJobs);
			}catch (Exception e) {
				logger.error("Error in accessing report consolidate job table 3- " + e,e);
			}
		}else {
			int beginTime = lastReportConsolidateJobs.getJobTime() + 1800;
			while (beginTime <= RounfToHalfHrKey){
				logger.debug("consolidate report for time = "+ beginTime);
				boolean result = false;
				
				if (beginTime == RounfToHalfHrKey){
					
					result = ConsolidateReport((long)beginTime*1000);
					reportConsolidateJobs.setAllSuccess(result);
					reportConsolidateJobs.setEndTime((int)(Calendar.getInstance().getTimeInMillis()/1000));		
					try {
						reportConsolidateJobsDAO = new ReportConsolidateJobsDAO();
						reportConsolidateJobsDAO.saveOrUpdate(reportConsolidateJobs);
					}catch (Exception e) {
						logger.error("Error in accessing report consolidate job table 4- " + e,e);
					}
				}else {
					int beginTimestamp = (int)(Calendar.getInstance().getTimeInMillis()/1000);
					result = ConsolidateReport((long)beginTime*1000);
					ReportConsolidateJobs reportConsolidateJobsthis = new ReportConsolidateJobs(beginTime, result, beginTimestamp, (int)(Calendar.getInstance().getTimeInMillis()/1000));
					try {
						reportConsolidateJobsDAO.saveOrUpdate(reportConsolidateJobsthis);
					}catch (Exception e) {
						logger.error("Error in accessing report consolidate job table 5- " + e,e);
					}
				}
				beginTime+=1800;					
			}
			
		}
			
		//insert current job time to table
		//check last job time
		
		//do consolidate for each hour count from last job time
	}
	
	public boolean ConsolidateReport(Long reportTime){
		boolean retStatus = true;
		if (reportTime == null || reportTime == 0){
			return false;
		}
		return retStatus;
	}
	
	public void StartPersistReportFromCache() {
		DBUtil dbUtil = null;
		DBConnection batchConnection = null;
		long tstart = System.currentTimeMillis();
		DistributedQueueObject dqueue = DistributedQueueObject.getInstance();
		if(dqueue == null) {
			logger.error("StartPersistReportFromCache: dqueue getInstance null");
			return;
		}
			
		try {
			dbUtil = DBUtil.getInstance();
		} catch (Exception e) { 
			logger.error("StartPersistReportFromCache: DBUtil error, e="+e, e);
			return;
		}
		
		DevicesDAO deviceDAO = null;
		List<Devices> devList = null;
		Date curTime = new Date();
		Integer curUnixTime = (int)(curTime.getTime()/1000);
		curUnixTime = (curUnixTime/3600)*3600; //round to hour
		
		while (true)
		{
			String orgId = null;
			try {
					orgId = (String) dqueue.Pop();
			} catch(Exception e) {
				logger.error("StartPersistReportFromCache: dqueue.Pop() exception e="+e, e);
				break;
			}
			if(logger.isInfoEnabled())
				logger.info("StartPersistReportFromCache: orgId = " + orgId);

			if(orgId == null) {
				if(logger.isInfoEnabled())
					logger.info("StartPersistReportFromCache: no more orgId and exit");
				break;
			}
			
			int orgTotal = 0;
			//must do station_usage first as it needs to get the same hour data from stationList object for the mac, and client name
			// update client usage table
			try {
				dbUtil.startSession();
				batchConnection = dbUtil.getConnection(false, orgId, false);
				
				deviceDAO = new DevicesDAO(orgId);
				devList = deviceDAO.getAllDevices();
				if (devList == null || devList.isEmpty()) {
					//error or no device in the org
					if(logger.isInfoEnabled())
						logger.info("StartPersistReportFromCache: orgId = " + orgId + " null devlist");
					continue;
				}
				StationUsageObject stationUsageObject = null;
				StationListObject stationListObject = null;
				ClientUsages clientUsages = null;
				Date utcTime = null;
				
				for (Devices dev:devList){
					stationUsageObject = new StationUsageObject();
					stationUsageObject.setIana_id(dev.getIanaId());
					stationUsageObject.setSn(dev.getSn());	
					StationUsageObject stationUsageObjectCache = ACUtil.<StationUsageObject>getPoolObjectBySn(stationUsageObject, StationUsageObject.class);
					if (stationUsageObjectCache == null || stationUsageObjectCache.getStation_list() == null){
						//no cache or cache without data
						continue;
					}
					// workaround to override network_id from cache with from database
					stationUsageObjectCache.setNetwork_id(dev.getNetworkId());
					ArrayList<StationUsageList> stationUsageListCache = stationUsageObjectCache.getStation_list();
					
					// next get the Station list
					stationListObject = new StationListObject();
					stationListObject.setIana_id(dev.getIanaId());
					stationListObject.setSn(dev.getSn());
					StationListObject stationListObjectCache = ACUtil.<StationListObject>getPoolObjectBySn(stationListObject, StationListObject.class);
					if (stationListObjectCache == null || stationListObjectCache.getStation_list() == null) {
						//no cache or cache without data
						if(logger.isInfoEnabled())
							logger.info("StartPersistReportFromCache: Persist clientUsages skip usage update as Station List object not found for dev = "+dev.getSn());	
						continue;
					}
					//If station list object in cache without network_id and device_id, this object will be removed from cache
					if( stationListObjectCache.getNetwork_id() == null || stationListObjectCache.getDevice_id() == null ) {
						ACUtil.removePoolObjectBySn(stationListObjectCache, StationListObject.class);
						continue;
					}
					
					if(stationListObjectCache.getTimestamp() == null) {
						int createTime = (int) (stationListObjectCache.getCreateTime() / 1000);
						logger.warn("StartPersistReportFromCache: Persist clientUsages abnormal Station List with null timestamp for dev = " + dev.getSn() + " and replace with "+createTime);
						stationListObjectCache.setTimestamp(curUnixTime);
					}
					// workaround to override network_id from cache with from database
					stationListObjectCache.setNetwork_id(dev.getNetworkId());
					List<StationList> stationListCache =  stationListObjectCache.getStation_list();
					
					HashMap<String, StationList> stationIpMap = new HashMap<String, StationList> ((int)(stationListCache.size() / 0.75 + 1)); 
					for(StationList stationItem: stationListCache) {
						stationIpMap.put(stationItem.getIp(), stationItem);
					}

					//no need to compare time as all station usage date is already the last hour full usage
					//so, after saved to DB, clear the list should be fine
					for (StationUsageList stationUsageItem:stationUsageListCache)
					{	
						if(logger.isInfoEnabled()) {
							logger.info("StartPersistReportFromCache: Persist client usageitem = " + stationUsageItem);
						}

						String type = stationUsageItem.getType();
						// don't save usage if rx & tx = 0 and type is static route client
						if(!type.equals("wireless")) {
							if(stationUsageItem.getRx()==0.0 && stationUsageItem.getTx()==0.0)
								continue; // skip 0/0 and static route client
						}
						
						// if there is no mac in Station usage, then search mac from Station list
						if(stationUsageItem.getMac() == null || stationUsageItem.getMac().isEmpty())
						{
							StationList stationItem = stationIpMap.get(stationUsageItem.getIp());
							if(stationItem !=null) {
								stationUsageItem.setMac(stationItem.getMac());
								stationUsageItem.setName(stationItem.getName());
								type = stationItem.getType();
							}
						}
						
						// if there is still no mac, e.g. static route client, then set mac as IP instead
						String macOrIp = stationUsageItem.getMac();
						if (macOrIp == null || macOrIp.isEmpty()) {
							macOrIp = stationUsageItem.getIp();
						}
						
						utcTime = new Date((long)stationUsageItem.getTimestamp()*1000);
						clientUsages = new ClientUsages(stationListObjectCache.getNetwork_id(), stationListObjectCache.getDevice_id(),
								macOrIp, stationUsageItem.getIp(), stationUsageItem.getName(), stationUsageItem.getTx(),
								stationUsageItem.getRx(), utcTime, type, stationUsageItem.getTimestamp());
						clientUsages.createReplace();	// insert on duplicate then update, duplication checking is based on unique index
						if(logger.isInfoEnabled()) {
							logger.info("StartPersistReportFromCache: Persist clientUsages = "+clientUsages);
						}
						batchConnection.addBatch(clientUsages);
					}
					
					if(logger.isInfoEnabled())
						logger.infof("StartPersistReportFromCache: Persist clientUsages, org=%s iana=%d sn=%s batchSize=%d", orgId, dev.getIanaId(), dev.getSn(), batchConnection.getBatchSize());
					
					orgTotal += batchConnection.getBatchSize();
					try {
						batchConnection.commit();
						if(logger.isInfoEnabled()) {
							StringBuffer sb = new StringBuffer();
							for (String stmt : batchConnection.getStatementList()) {
								sb.append(stmt);
								sb.append("\n");
							}
							logger.info("StartPersistReportFromCache: Persist clientUsages, batch Stmt List: ---------\n" + sb.toString()+"\n---------");
						}
							
						stationUsageListCache.clear();
						stationUsageObjectCache.setStation_list(stationUsageListCache);
						ACUtil.<StationUsageObject>cachePoolObjectBySn(stationUsageObjectCache, StationUsageObject.class);
					} catch (SQLException sqle) {
						logger.error("StartPersistReportFromCache: Persist clientUsage, orgId="+orgId+", sn="+dev.getSn()+", error in commiting batchConnection "+sqle, sqle);
						try {
							batchConnection.close();
						} catch (Exception e) {
							logger.error("StartPersistReportFromCache: Persist clientUsages, error in closing batchConnection "+e, e);
						}
						batchConnection = dbUtil.getConnection(false, orgId, false);
					}
				}
			} catch (Exception e) {
				logger.error("StartPersistReportFromCache: Persist clientUsages exception " + e, e);
			} finally {
				try
				{
					if (batchConnection!=null)
					{
						batchConnection.commit();
						batchConnection.close();
					}
					dbUtil.endSession();
					logger.warnf("StartPersistReportFromCache: Persist clientUsages, completed org=%s, records=%d", orgId, orgTotal);
				}
				catch(Exception e)
				{
					logger.error("StartPersistReportFromCache: Persist clientUsages finally " + e,e);
					try {
						if (dbUtil.isSessionStarted()) {
							dbUtil.endSession();
						}
					} catch (Exception e2) {
						logger.error("StartPersistReportFromCache: Persist clientUsages endSession ", e2);
					}
				}
			}
						
			// update clientSsidUsage and clientIpMacMapping tables
			try {
				dbUtil.startSession();
				batchConnection = dbUtil.getConnection(false, orgId, false);

				StationListObject stationListObject = null;
				ClientIpMacMappingsDAO clientIpMacMappingsDAO = new ClientIpMacMappingsDAO(orgId);
				List<ClientIpMacMappings> clientIpMacList = null;
				
				for (Devices dev:devList){	
					stationListObject = new StationListObject();
					stationListObject.setIana_id(dev.getIanaId());
					stationListObject.setSn(dev.getSn());		
					StationListObject stationListObjectCache = ACUtil.<StationListObject>getPoolObjectBySn(stationListObject, StationListObject.class);
					if (stationListObjectCache == null || stationListObjectCache.getStation_list() == null){
						//no cache or cache without data
						continue;
					}
					
					//If station list object in cache without network_id and device_id, this object will be removed from cache
					if( stationListObjectCache.getNetwork_id() == null || stationListObjectCache.getDevice_id() == null ) {
						ACUtil.removePoolObjectBySn(stationListObjectCache, StationListObject.class);
						continue;
					}
					
					if(stationListObjectCache.getTimestamp() == null) {
						int createTime = (int) (stationListObjectCache.getCreateTime() / 1000);
						logger.warn("StartPersistReportFromCache: Persist clientIpMacMappings, abnormal Station List with null timestamp: " + dev.getSn() + " and replace with "+createTime);
						stationListObjectCache.setTimestamp(createTime);
					}

					// workaround to override network_id from cache with from database
					stationListObjectCache.setNetwork_id(dev.getNetworkId());

					clientIpMacList = clientIpMacMappingsDAO.getAllIpMacMapByNetworkDevice(stationListObjectCache.getNetwork_id(), stationListObjectCache.getDevice_id());
					HashMap <String, ClientIpMacMappings>clientIpMacMap = new HashMap<String, ClientIpMacMappings>((int)(clientIpMacList.size() / 0.75 + 1 ));
					for(ClientIpMacMappings clientIpMacMappingItem: clientIpMacList) {
						clientIpMacMap.put(clientIpMacMappingItem.getId().getIp(), clientIpMacMappingItem);
					}
					
					if(logger.isInfoEnabled())
						logger.info("StartPersistReportFromCache: Persist clientSsidIpMacMappings, clientIpMacList " + clientIpMacList);
					ClientSsidUsages clientSsidUsages = null;
					List<StationList> stationListCache = stationListObjectCache.getStation_list();
					Date utcTime = null;
					int unixTime = 0;
					List<StationList> delList = new ArrayList<StationList>();
					
					for (StationList stationMacListItem:stationListCache) {
						if(stationMacListItem.getType().equals("wireless")) {
							if (stationListObjectCache.getTimestamp() < curUnixTime) {
								//should be disconnected now
								//save to db
								unixTime = stationListObjectCache.getTimestamp() / 3600 * 3600;
								utcTime = new Date((long) unixTime * 1000);
								clientSsidUsages = new ClientSsidUsages(stationListObjectCache.getNetwork_id(), stationListObjectCache.getDevice_id(), 
										stationMacListItem.getMac(), stationMacListItem.getName(), stationMacListItem.getBssid(), 
										stationMacListItem.getEssid(), stationMacListItem.getSecurity(), true, stationMacListItem.getType(), 
										utcTime, unixTime);
								clientSsidUsages.createReplace(); // insert on duplicate then update, duplication checking is based on unique index
								batchConnection.addBatch(clientSsidUsages);
							} else {
								if (stationMacListItem.getFirst_appear_time() != null && stationMacListItem.getFirst_appear_time()>=curUnixTime) {
									//connected since this hour, no need to save to DB now
								} else {
									//connected before, save to DB
									unixTime = curUnixTime - 3600;
									utcTime = new Date((long) unixTime * 1000);
									
									clientSsidUsages = new ClientSsidUsages(stationListObjectCache.getNetwork_id(), stationListObjectCache.getDevice_id(), 
											stationMacListItem.getMac(), stationMacListItem.getName(), stationMacListItem.getBssid(), 
											stationMacListItem.getEssid(), stationMacListItem.getSecurity(), true, stationMacListItem.getType(), 
											utcTime, unixTime);
									clientSsidUsages.createReplace();

									if(logger.isInfoEnabled())
										logger.infof("StartPersistReportFromCache: Persist clientIpMacMappings, addBatch clientSsidUsages org %s dev %d %s", orgId, dev.getIanaId(), dev.getSn());
									batchConnection.addBatch(clientSsidUsages);
								}
							}
						}
						
						boolean foundInTable = false;
						boolean sameRecord = false;

						if(logger.isInfoEnabled())
							logger.infof("StartPersistReportFromCache: Persist clientIpMacMappings, working on org %s dev %d %s", orgId, dev.getIanaId(), dev.getSn());

						if (stationMacListItem.getIp() == null) 
							continue;
						ClientIpMacMappings cimFound = null;
						ClientIpMacMappings clientIpMacMappings = null;
						
						String ip = stationMacListItem.getIp();
						ClientIpMacMappings ipMacMap = clientIpMacMap.get(ip);
						if(ipMacMap!=null) {
							if (ipMacMap.getMac().compareTo(stationMacListItem.getMac()) == 0) {
								sameRecord = true;												
							}
							cimFound = ipMacMap;
							foundInTable = true;
						}
						if (!foundInTable) {
							ClientIpMacMappingsId cimMapId = new ClientIpMacMappingsId();
							cimMapId.setIp(stationMacListItem.getIp());
							cimMapId.setDeviceId(stationListObjectCache.getDevice_id());
							cimMapId.setNetworkId(stationListObjectCache.getNetwork_id());
							clientIpMacMappings = new ClientIpMacMappings(cimMapId, stationMacListItem.getMac(), stationListObjectCache.getTimestamp());
							clientIpMacMappings.replace();
							if(logger.isInfoEnabled())
								logger.infof("StartPersistReportFromCache: addBatch clientIpMacMappings org %s dev %d %s", orgId, dev.getIanaId(), dev.getSn());
							batchConnection.addBatch(clientIpMacMappings);
						} else if (!sameRecord) {
							//update the mac
							if(cimFound != null && stationMacListItem != null && stationMacListItem.getMac() != null)
							{
								cimFound.setMac(stationMacListItem.getMac());
								cimFound.setUnixtime(stationListObjectCache.getTimestamp());
								cimFound.replace();

								if(logger.isInfoEnabled())
									logger.infof("StartPersistReportFromCache: addBatch cimFound org %s dev %d %s", orgId, dev.getIanaId(), dev.getSn());
								batchConnection.addBatch(cimFound);
							}
						}
						
						if(stationMacListItem.getLastUpdateTime()!=null) {
							if((int)(tstart / 1000) - stationMacListItem.getLastUpdateTime() > MAX_STATION_INACTIVE) {
								delList.add(stationMacListItem);
//								logger.warnf("StartPersistReportFromCache removed inactive client: orgId=%s, mac=%s, ip=%s, last=%d" , orgId, stationMacListItem.getMac(), stationMacListItem.getIp(), stationMacListItem.getLastUpdateTime());
							}
						}
					}
					
					if(logger.isInfoEnabled())
						logger.infof("StartPersistReportFromCache: org %s dev %d %s batchSize %d", orgId, dev.getIanaId(), dev.getSn(), batchConnection.getBatchSize());
					
					try {
						batchConnection.commit();
					} catch (SQLException sqle) {
						logger.error("StartPersistReportFromCache: error in commiting batchConnection orgId="+orgId+", sn="+dev.getSn()+", ClientSsidUsages "+sqle, sqle);
						try {
							batchConnection.close();
						} catch (Exception e) {
							logger.error("StartPersistReportFromCache: error in closing batchConnection, ClientSsidUsages "+e, e);
						}
						batchConnection = dbUtil.getConnection(false, orgId, false);
					}
					
					if(delList.size() > 0) {
						if(logger.isInfoEnabled()) {
							StringBuffer sb = new StringBuffer(35 * delList.size());
							for(StationList delItem: delList) {
								sb.append(delItem.getMac());
								sb.append('=');
								sb.append(delItem.getIp());
								sb.append(' ');
							}
							logger.infof("StartPersistReportFromCache: removed inactive clients: orgId=%s, sn=%s, clients=%s" , orgId, dev.getSn(), sb);
						}
						stationListCache.removeAll(delList);
					}
					stationListObjectCache.setStation_list(stationListCache);
					stationListObjectCache.setTotalClient(stationListCache.size());	
					ACUtil.cachePoolObjectBySn(stationListObjectCache, StationListObject.class);
					if(logger.isInfoEnabled())
						logger.infof("StartPersistReportFromCache: cached StationListObject, orgid %s dev %d %s", orgId, dev.getIanaId(), dev.getSn());
				}				
			} catch (Exception e) {
				logger.error("StartPersistReportFromCache: exception in clientSsidUsage and clientIpMacMappings " + e, e);
			}
			finally
			{
//				logger.warn("StartPersistReportFromCache: ClientSsid, ClientIpMacMapping, StationList housekeep finally...");
				try
				{
					if (batchConnection!=null)
					{
						if(logger.isDebugEnabled())
							logger.debug("finally commit...");
						batchConnection.commit();
						batchConnection.close();
					}

					if(logger.isDebugEnabled())
						logger.debug("finally endSession...");
					dbUtil.endSession();
				}
				catch(Exception e)
				{
					logger.error("StartPersistReportFromCache: - " + e,e);
					try {
						if (dbUtil.isSessionStarted()) {
							dbUtil.endSession();
						}
					} catch (Exception e2) {
						logger.error("StartPersistReportFromCache: Error in endSession" + e2, e2);
					}
				}
			}
		}
		logger.warnf("StartPersistReportFromCache: ended");
	}
	
	public int startProcessDeviceDailyUsages()
	{
		List<String> snsOrganizationsList = null;
		deviceDailyUsagesQueue.clear();
		int total = -1;
		
		try
		{
			SnsOrganizationsDAO snsOrganizationsDAO = new SnsOrganizationsDAO(true);
			snsOrganizationsList = snsOrganizationsDAO.getDistinctOrgList();
			if (logger.isDebugEnabled())
				logger.debug("startProcessDeviceDailyUsages snsOrganizationsList" + snsOrganizationsList);
			
			if( snsOrganizationsList != null )
			{
				// first put all orgs to the shared queue
				for (String orgId: snsOrganizationsList) {
					deviceDailyUsagesQueue.put(orgId);
				}
				long tstart = System.currentTimeMillis();
				total = deviceDailyUsagesQueue.size();
				
				ExecutorService executor = Executors.newFixedThreadPool(NUM_DEVICE_DAILY_USAGES_THREADS);
				for (int i = 0; i < NUM_DEVICE_DAILY_USAGES_THREADS; i++) {
					Runnable workerThread = new deviceDailyUsagesThread(i, deviceDailyUsagesQueue);
					deviceDailyUsagesQueue.put("END");
					executor.execute(workerThread);
				}
				executor.shutdown();
				while (!executor.isTerminated()) {
					java.lang.Thread.sleep(5000);
					if (logger.isDebugEnabled())
						logger.debug("startProcessDeviceDailyUsages timeout and wait again");
				}
				long tused = (System.currentTimeMillis() - tstart) / 1000;
				logger.warnf("startProcessDeviceDailyUsages finished all threads, orgs processed = %d, time used = %d sec", total, tused);
			}
		} catch (Exception e) {
			logger.error("startProcessDeviceDailyUsages exception "+e, e);
		}
		int count = total - deviceDailyUsagesQueue.size(); // size() should be 0 
		return count;
	}

	
	public class deviceDailyUsagesThread implements Runnable {
		private int myid = -1;
		private BlockingQueue<String> queue;

		public deviceDailyUsagesThread(int myid, BlockingQueue<String> queue) {
			this.myid = myid;
			this.queue = queue;
		}

		public void run() {
			DeviceUsagesDAO devUsagesDAO = null;
			DailyDeviceUsagesDAO dailyDeviceUsagesDAO = null;
			NetworksDAO networksDAO = null;
			DBUtil dbUtil = null;
			DBConnection batchConnection = null;
			long thread_start = System.currentTimeMillis();
			
			try {
				dbUtil = DBUtil.getInstance();			
				dbUtil.startSession();
				
				while (true) {
					String orgId = queue.poll();
					if(orgId.equals("END"))
						break;
					if (logger.isInfoEnabled())
						logger.infof("[%d] deviceDailyUsagesThread working on: orgId = %s", myid, orgId);

					try {
						batchConnection = dbUtil.getConnection(false, orgId, false);
						devUsagesDAO = new DeviceUsagesDAO(orgId);
						dailyDeviceUsagesDAO = new DailyDeviceUsagesDAO(orgId);
						networksDAO = new NetworksDAO(orgId,true);
						long tstart = System.currentTimeMillis();
						
						List<Networks> networkIds = networksDAO.getAllNetworks();
						if( networkIds != null ) {
							int count = 0;
							for( Networks network : networkIds ) {
								if( network != null ) {
									TimeZone tz = TimeZone.getTimeZone(DateUtils.getTimezoneFromId(Integer.parseInt(network.getTimezone())));
									Date date = DateUtils.getUtcDate();
									Integer start = ((int)(date.getTime()/1000 + tz.getRawOffset()/1000 - 3600))/86400 * 86400 - (tz.getRawOffset()/1000);
//									Integer start = ((int)(date.getTime()/1000)) / 86400 * 86400 - (tz.getRawOffset()/1000);
									Integer end = start + 86400;
//									Integer end = (int)(date.getTime()/1000); // current time
//									Integer start = end - 10800; // current time - 3hrs
									if (logger.isInfoEnabled())
										logger.infof("[%d] deviceDailyUsagesThread working on: orgId = %s, network = %d, offset = %d, start = %d, end = %d", myid, orgId, network.getId(), tz.getRawOffset()/1000, start, end);
									List<DeviceUsages> dailyRecords = devUsagesDAO.getDailyUsagesRecords(network.getId(), DateUtils.getTimezoneFromId(Integer.parseInt(network.getTimezone())),start, end);
									for( DeviceUsages dailyUsage : dailyRecords ) {
										DailyDeviceUsages daily_usage = new DailyDeviceUsages();
										daily_usage.setUnixtime((int)(dailyUsage.getDatetime().getTime()/1000));
										daily_usage.setNetworkId(dailyUsage.getNetworkId());
										daily_usage.setDeviceId(dailyUsage.getDeviceId());
										daily_usage.setWan_id(dailyUsage.getWan_id());
										daily_usage.setWan_name(dailyUsage.getWan_name());
										daily_usage.setRx(dailyUsage.getRx());
										daily_usage.setTx(dailyUsage.getTx());
										daily_usage.setDatetime(dailyUsage.getDatetime());
										
										DailyDeviceUsages preDailyUsage = dailyDeviceUsagesDAO.findByNetworkIdDeviceIdTimeAndWanId(daily_usage.getNetworkId(), daily_usage.getDeviceId(), daily_usage.getId().getUnixtime(), daily_usage.getWan_id());
										if( preDailyUsage != null ) {
											daily_usage.setId(preDailyUsage.getId());
										}
										
										daily_usage.replace();
										batchConnection.addBatch(daily_usage);
										count ++;
									}
								}
							}
						
							batchConnection.commit();
							batchConnection.close();
							long tused = (System.currentTimeMillis() - tstart) / 1000;
							if (logger.isInfoEnabled())
								logger.infof("[%d] deviceDailyUsagesThread completed for orgId = %s,  records = %d, time used = %d sec", myid, orgId, count, tused);
						}
					} catch(Exception e) {
						logger.error("["+myid+"] deviceDailyUsagesThread sql error " + orgId, e);
					}
				}
			} catch( Exception e ) {
				logger.error("["+myid+"] deviceDailyUsagesThread error " + e, e);
			} finally {
				if(dbUtil!=null) {
					if(dbUtil.isSessionStarted())
						dbUtil.endSession();
				}
				long thread_used = (System.currentTimeMillis() - thread_start) / 1000;
				if (logger.isInfoEnabled())
					logger.infof("[%d] deviceDailyUsagesThread end: total time used = %d", myid, thread_used);
			}
			return;
		}
	}
	
	public int startProcessMonthlyUsages()
	{
		int count = -1;
		if(monthly_dev_consolidate) {
			logger.info(VER + " -- SKIP startProcessMonthlyUsages as previous task is still running");
			return count;
		}
		
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		long tmpTime = today.getTimeInMillis();

		if(monthly_dev_consolidate_time == tmpTime) {
			logger.info(VER + " -- SKIP startProcessMonthlyUsages as today's task has run");
			return count;
		}
		
		try
		{
			monthly_dev_consolidate = true;
			logger.info("startProcessMonthlyUsages start, monthly_consolidate_time = " + monthly_dev_consolidate_time);
			
			List<String> snsOrganizationsList = null;
			DeviceMonthlyUsagesDAO devMonthlyUsagesDAO = null;
			DailyDeviceUsagesDAO dailyDeviceUsagesDAO = null;

			SnsOrganizationsDAO snsOrganizationsDAO = new SnsOrganizationsDAO(true);
			snsOrganizationsList = snsOrganizationsDAO.getDistinctOrgList();
			if (logger.isDebugEnabled())
				logger.debug("snsOrganizationsList" + snsOrganizationsList);

			DBUtil dbUtil = DBUtil.getInstance();			
			
			if( snsOrganizationsList != null )
			{
				for( String orgId : snsOrganizationsList )
				{
					long tstart = System.currentTimeMillis();
					try
					{
						if (logger.isInfoEnabled())
							logger.info("Consolidate monthly device usages for org = "+orgId);

						dbUtil.startSession();
						DBConnection batchConnection = dbUtil.getConnection(false, orgId, false);
						
						devMonthlyUsagesDAO = new DeviceMonthlyUsagesDAO(orgId);
						dailyDeviceUsagesDAO = new DailyDeviceUsagesDAO(orgId);
						
						Calendar cal = Calendar.getInstance();
						Date date = DateUtils.getUtcDate();
						int start = (int) (date.getTime() / 1000);
						cal.setTime(date);
						cal.add(Calendar.MONTH, 1);
						int end = (int)(cal.getTimeInMillis() / 1000);
						List<DailyDeviceUsages> monthlyRecords = dailyDeviceUsagesDAO.getMonthlyRecords(start, end);
						for(DailyDeviceUsages monthlyUsage : monthlyRecords)
						{
							DeviceMonthlyUsages monthly_usage = new DeviceMonthlyUsages();
							monthly_usage.setDevice_id(monthlyUsage.getDeviceId());
							monthly_usage.setNetwork_id(monthlyUsage.getNetworkId());
							monthly_usage.setWan_id(monthlyUsage.getWan_id());
							monthly_usage.setWan_name(monthlyUsage.getWan_name());
							monthly_usage.setRx(monthlyUsage.getRx());
							monthly_usage.setTx(monthlyUsage.getTx());
							monthly_usage.setDatetime(monthlyUsage.getDatetime());
							monthly_usage.setUnixtime(monthlyUsage.getUnixtime());
							
							DeviceMonthlyUsages preDevMonthlyUsage = devMonthlyUsagesDAO.findByNetworkIdDeviceIdTimeAndWanId(monthly_usage.getNetwork_id(), monthly_usage.getDevice_id(), monthly_usage.getDatetime(), monthly_usage.getWan_id());
							if( preDevMonthlyUsage != null ) {
								monthly_usage.setId(preDevMonthlyUsage.getId());
							}
							
							monthly_usage.replace();
							batchConnection.addBatch(monthly_usage);
							
							// partially commit the batch, prevent size too large
							if(batchConnection.getBatchSize() >= MAX_BATCH_SIZE) {
								batchConnection.commit();
								batchConnection.close();
								if( dbUtil.isSessionStarted() )
									dbUtil.endSession();
								dbUtil.startSession();
								batchConnection = dbUtil.getConnection(false, orgId, false);
							}
						}
						batchConnection.commit();
						batchConnection.close();
						if( dbUtil.isSessionStarted() )
							dbUtil.endSession();
					} catch(SQLException e)	{
						logger.error("Consolidate monthly device usages error - 53 "+orgId,e);
					} finally {
						if(dbUtil.isSessionStarted())
							dbUtil.endSession();
					}
					count ++;
					long tused = (System.currentTimeMillis() - tstart) / 1000;
					if (logger.isInfoEnabled())
						logger.infof("End Consolidate monthly device usage for org = %s, tused = %d", orgId, tused);
				}
			}
		} catch( Exception e ) {
			logger.error("Error in accessing report consolidate job table 31- " + e,e);
		} finally {
			monthly_dev_consolidate = false;
			monthly_dev_consolidate_time = tmpTime;
			logger.info("startProcessMonthlyUsages end, monthly_consolidate_time = " + monthly_dev_consolidate_time);
		}
		return count;
	}
	
	public int startProcessClientDailyUsages()
	{
		List<String> snsOrganizationsList = null;
		clientDailyUsagesQueue.clear();
		int total = -1;
		
		try
		{
			SnsOrganizationsDAO snsOrganizationsDAO = new SnsOrganizationsDAO(true);
			snsOrganizationsList = snsOrganizationsDAO.getDistinctOrgList();
			if (logger.isDebugEnabled())
				logger.debug("startProcessClientDailyUsages snsOrganizationsList = " + snsOrganizationsList);

			if( snsOrganizationsList != null )
			{
				// first put all orgs to the shared queue
				for (String orgId: snsOrganizationsList) {
					clientDailyUsagesQueue.put(orgId);
				}
				int norgs = clientDailyUsagesQueue.size();
				long tstart = System.currentTimeMillis();
				total = clientDailyUsagesQueue.size();
				
				ExecutorService executor = Executors.newFixedThreadPool(NUM_CLIENT_DAILY_USAGES_THREADS);
				for (int i = 0; i < NUM_CLIENT_DAILY_USAGES_THREADS; i++) {
					Runnable workerThread = new clientDailyUsagesThread(i, clientDailyUsagesQueue);
					clientDailyUsagesQueue.put("END");
					executor.execute(workerThread);
				}
				executor.shutdown();
				while (!executor.isTerminated()) {
					java.lang.Thread.sleep(5000);
					if (logger.isDebugEnabled())
						logger.debug("startProcessClientDailyUsages timeout and wait again");
				}
				long tused = (System.currentTimeMillis() - tstart) / 1000;
				if (logger.isInfoEnabled())
					logger.infof("startProcessClientDailyUsages finished all threads, orgs processed = %d, time used = %d sec", norgs, tused);
			}
		} catch (Exception e) {
			logger.error("startProcessClientDailyUsages exception "+e, e);
		}
		int count = total - clientDailyUsagesQueue.size(); // size() should be 0 
		return count;
	}
		
	public class clientDailyUsagesThread implements Runnable {
		private int myid = -1;
		private BlockingQueue<String> queue;
		
		public clientDailyUsagesThread(int myid, BlockingQueue<String> queue) {
			this.myid = myid;
			this.queue = queue;
		}

		public void run() {
			ClientUsagesDAO clientUsagesDAO = null;
			DailyClientUsagesDAO dailyClientUsagesDAO = null;
			NetworksDAO networksDAO = null;
			DBUtil dbUtil = null;
			long thread_start = System.currentTimeMillis();

			try {
				dbUtil = DBUtil.getInstance();			
				dbUtil.startSession();

				OuiInfosDAO ouiInfosDAO = new OuiInfosDAO(true);
				HashMap<Long, String> ouiMap = ouiInfosDAO.getOuiInfosMap();

				while (true) {
					String orgId = "";
					try
					{
						orgId = queue.poll();
						if(orgId.equals("END"))
							break;
						if (logger.isInfoEnabled())
							logger.infof("[%d] clientDailyUsagesThread working on: orgId = %s", myid, orgId);
					
						DBConnection batchConnection = dbUtil.getConnection(false, orgId, false);
						clientUsagesDAO = new ClientUsagesDAO(orgId);
						dailyClientUsagesDAO = new DailyClientUsagesDAO(orgId);
						networksDAO = new NetworksDAO(orgId);
						
						List<Networks> networkIds = networksDAO.getAllNetworks();
						for(Networks net : networkIds) {
							if( net != null ) {
								int count = 0;
								long tstart = System.currentTimeMillis();
								TimeZone tz = TimeZone.getTimeZone(DateUtils.getTimezoneFromId(Integer.parseInt(net.getTimezone())));
								Date date = DateUtils.getUtcDate();
								Integer start = ((int)(date.getTime()/1000 + tz.getRawOffset()/1000 - 3600))/86400 * 86400 - (tz.getRawOffset()/1000);
								Integer end = start + 86400;
//								Integer end = (int)(date.getTime()/1000); // current time
//								Integer start = end - 86400; // current time - 24hrs

								if (logger.isInfoEnabled())
									logger.infof("[%d] clientDailyUsagesThread working on: orgId = %s, network = %d, offset = %d, start = %d, end = %d", myid, orgId, net.getId(), tz.getRawOffset()/1000, start, end);
								
								List<ClientUsages> dailyRecords = clientUsagesDAO.getDailyRecords(net.getId(), DateUtils.getTimezoneFromId(Integer.parseInt(net.getTimezone())),start,end);
								for( ClientUsages dailyObj : dailyRecords ) {
									if(!dailyObj.getType().equals("wireless")) { // skip non-wireless client if usage = 0
										if(dailyObj.getRx() == 0.0 && dailyObj.getTx() == 0.0)
											continue;
									}
									DailyClientUsages dailyRecord = new DailyClientUsages();
									dailyRecord.setUnixtime((int)(dailyObj.getDatetime().getTime() / 1000));
									dailyRecord.setNetworkId(dailyObj.getNetworkId());
									dailyRecord.setDeviceId(dailyObj.getDeviceId());
									dailyRecord.setMac(dailyObj.getMac());
									dailyRecord.setIp(dailyObj.getIp());
									dailyRecord.setRx(dailyObj.getRx());
									dailyRecord.setTx(dailyObj.getTx());
									dailyRecord.setName(dailyObj.getName());
									dailyRecord.setType(dailyObj.getType());
									dailyRecord.setDatetime(dailyObj.getDatetime());
									if(dailyObj.getMac() != null && !dailyObj.getMac().isEmpty() && dailyObj.getMac().indexOf(".") < 0) {
										String macHex = dailyObj.getMac().substring(0, 8);								
										macHex = macHex.replaceAll(":", "");
										macHex = macHex + "000000";
										Long macLong = Long.parseLong(macHex, 16);
										if (ouiMap != null){
											dailyRecord.setManufacturer(ouiMap.get(macLong));
										}
									}
									
									DailyClientUsages preDailyUsage = dailyClientUsagesDAO.findByMacIpNetIdDevIdAndUnixTime(dailyRecord.getMac(), dailyRecord.getIp(), (int)(dailyRecord.getDatetime().getTime()/1000), dailyRecord.getDeviceId(),dailyRecord.getNetworkId());
									if( preDailyUsage != null ) {
										dailyRecord.setId(preDailyUsage.getId());
									}

									dailyRecord.replace();
									batchConnection.addBatch(dailyRecord);
									count ++;

									if(batchConnection.getBatchSize() >= MAX_BATCH_SIZE) {
										batchConnection.commit();
										batchConnection.close();
										if( dbUtil.isSessionStarted() )
											dbUtil.endSession();
										dbUtil.startSession();
										batchConnection = dbUtil.getConnection(false, orgId, false);
									}
								}
								long tused = (System.currentTimeMillis() - tstart) / 1000;
								if (logger.isInfoEnabled())
									logger.infof("[%d] clientDailyUsagesThread completed for orgId = %s,  network = %d, records = %d, time used = %d sec", myid, orgId, net.getId(), count, tused);
							}
						}
						
						batchConnection.commit();
						batchConnection.close();
					} catch(SQLException e) {
						logger.error("["+myid+"] clientDailyUsagesThread sql error " + orgId, e);
					}
				}
			} catch (Exception e) {
				logger.error("["+myid+"] clientDailyUsagesThread error " + e, e);	
			}
			finally {
				if(dbUtil != null) {
					if(dbUtil.isSessionStarted()) {
						logger.infof("[%d] clientDailyUsagesThread final endSession", myid);						
						dbUtil.endSession();
					}
				}
				long thread_used = (System.currentTimeMillis() - thread_start) / 1000;
				if (logger.isInfoEnabled())
					logger.infof("[%d] clientDailyUsagesThread end: total time used = %d", myid, thread_used);
			}
			return;
		}
	}
	
	public int startProcessCaptivePortalDailyUsages(){
		int count = -1;
		try{
			SnsOrganizationsMgr snsOrganizationsMgr = new SnsOrganizationsMgr();
			List<String> snsOrganizationsList = snsOrganizationsMgr.getSnsOrganizationsList();
			if (logger.isDebugEnabled()){
				logger.debug("snsOrganizationsList" + snsOrganizationsList);
			}
			
			if( snsOrganizationsList != null ){
				for( String orgId : snsOrganizationsList ){
					Calendar calFrom = CalendarUtils.getMinUtcCalendarToday();
					Calendar calTo = CalendarUtils.getMaxUtcCalendarToday();
					CaptivePortalDailyUsagesMgr cpDailyUsageReportsMgr = new CaptivePortalDailyUsagesMgr(orgId);
					cpDailyUsageReportsMgr.doCaptivePortalDailyUsagesConsolidation(calFrom, calTo);
				}
			}
		} catch (Exception e){
			logger.error("ReportConsolidateService.startProcessCaptivePortalDailyUsage() - ",e);
		}
		return count;
	}

	public int startProcessCaptivePortalDailyUserUsages(){
		int count = -1;
		try{
			SnsOrganizationsMgr snsOrganizationsMgr = new SnsOrganizationsMgr();
			List<String> snsOrganizationsList = snsOrganizationsMgr.getSnsOrganizationsList();
			if (logger.isDebugEnabled()){
				logger.debug("snsOrganizationsList" + snsOrganizationsList);
			}
			
			if( snsOrganizationsList != null ){
				for( String orgId : snsOrganizationsList ){
					Calendar calFrom = CalendarUtils.getMinUtcCalendarToday();
					Calendar calTo = CalendarUtils.getMaxUtcCalendarToday();
					CaptivePortalUserDailyUsagesMgr cpDailyUserUsagesMgr = new CaptivePortalUserDailyUsagesMgr(orgId);
					cpDailyUserUsagesMgr.doCaptivePortalUserDailyUsagesConsolidation(calFrom, calTo);
				}
			}
		} catch (Exception e){
			logger.error("ReportConsolidateService.startProcessCaptivePortalDailyUsage() - ",e);
		}
		return count;
	}
	
	public int startProcessClientMonthlyUsage()
	{
		int count = -1;
		if(monthly_client_consolidate) {
			logger.info(VER + " -- SKIP startProcessClientMonthlyUsage as previous task is still running");
			return count;
		}
		
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		long tmpTime = today.getTimeInMillis();

		if(monthly_client_consolidate_time == tmpTime) {
			logger.info(VER + " -- SKIP startProcessClientMonthlyUsage as today's task has run");
			return count;
		}
		
		try
		{
			monthly_client_consolidate = true;
			logger.info("startProcessClientMonthlyUsage start, monthly_client_consolidate_time = " + monthly_client_consolidate_time);
			
			List<String> snsOrganizationsList = null;
			ClientMonthlyUsagesDAO clientMonthlyUsagesDAO = null;
			DailyClientUsagesDAO dailyClientUsagesDAO = null;
		
			SnsOrganizationsDAO snsOrganizationsDAO = new SnsOrganizationsDAO(true);
			snsOrganizationsList = snsOrganizationsDAO.getDistinctOrgList();
			if (logger.isDebugEnabled())
				logger.debug("snsOrganizationsList" + snsOrganizationsList);

			DBUtil dbUtil = DBUtil.getInstance();
		
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
			if( snsOrganizationsList != null )
			{
				for( String orgId : snsOrganizationsList )
				{
					long tstart = System.currentTimeMillis();
					try
					{
						if (logger.isInfoEnabled())
							logger.info("Consolidate client monthly usage for org = "+orgId);
						
						dbUtil.startSession();
						DBConnection batchConnection = dbUtil.getConnection(false, orgId, false);
						
						dailyClientUsagesDAO = new DailyClientUsagesDAO(orgId);
						clientMonthlyUsagesDAO = new ClientMonthlyUsagesDAO(orgId);
						
						Calendar cal = Calendar.getInstance();
						Date date = DateUtils.getUtcDate();
						String start = format.format(date);
						cal.setTime(date);
						cal.add(Calendar.MONTH, 1);
						String end = format.format(cal.getTime());
						List<DailyClientUsages> monthlyRecords = dailyClientUsagesDAO.getMonthlyRecords(start,end);
						for( DailyClientUsages monthlyObj : monthlyRecords )
						{
							ClientMonthlyUsages monthlyRecord = new ClientMonthlyUsages();
							monthlyRecord.setUnixtime((int)(monthlyObj.getDatetime().getTime() / 1000));
							monthlyRecord.setDeviceId(monthlyObj.getDeviceId());
							monthlyRecord.setNetworkId(monthlyObj.getNetworkId());
							monthlyRecord.setMac(monthlyObj.getMac());
							monthlyRecord.setIp(monthlyObj.getIp());
							monthlyRecord.setRx(monthlyObj.getRx());
							monthlyRecord.setTx(monthlyObj.getTx());
							monthlyRecord.setType(monthlyObj.getType());
							monthlyRecord.setDatetime(monthlyObj.getDatetime());
							
							ClientMonthlyUsages preMonthlyClientUsage = clientMonthlyUsagesDAO.findByMacIpNetIdDevIdAndUnixtime(monthlyRecord.getMac(), monthlyRecord.getIp(), (int)(monthlyRecord.getDatetime().getTime()/1000), monthlyRecord.getNetworkId(), monthlyRecord.getDeviceId());
							if( preMonthlyClientUsage != null )
							{
								monthlyRecord.setId(preMonthlyClientUsage.getId());
							}
							
							monthlyRecord.replace();
							batchConnection.addBatch(monthlyRecord);
							
							if(batchConnection.getBatchSize() >= MAX_BATCH_SIZE) {
								batchConnection.commit();
								batchConnection.close();
								if( dbUtil.isSessionStarted() )
									dbUtil.endSession();
								dbUtil.startSession();
								batchConnection = dbUtil.getConnection(false, orgId, false);
							}
						}
						
						batchConnection.commit();
						batchConnection.close();
						if( dbUtil.isSessionStarted() )
							dbUtil.endSession();
					}
					catch(SQLException e)
					{
						logger.error("Consolidate client monthly usage error - 55 "+orgId,e);
					}
					finally
					{
						if(dbUtil.isSessionStarted()) {
							dbUtil.endSession();
						}
					}
					count ++;
					long tused = (System.currentTimeMillis() - tstart) / 1000;
					if (logger.isInfoEnabled())
						logger.infof("End Consolidate client monthly usage for org = %s, tused = %d", orgId, tused);
				}
 			}
		} catch( Exception e ) {
			logger.error("Error in astartProcessClientMonthlyUsages " + e,e);
		} finally {
			monthly_client_consolidate = false;
			monthly_client_consolidate_time = tmpTime;
			logger.info("startProcessClientMonthlyUsages end, monthly_client_consolidate_time = " + monthly_client_consolidate_time);
		}

		return count;
	}

	public int startProcessDailySsidUsages(){
		return startProcessDailySsidUsages(null);
	}
	
	public int startProcessDailySsidUsages(Date inputDate)
	{
		List<String> snsOrganizationsList = null;
		DeviceSsidUsagesDAO deviceSsidUsagesDAO = null;
		DailyDeviceSsidUsagesDAO dailyDeviceSsidUsagesDAO = null;
		DBUtil dbUtil = null;
		DBConnection batchConnection = null;
		NetworksDAO networkDAO = null;
//		Date start = null;
//		Date end = null;
		int count = -1;
		
		try
		{
//			start = new Date();
			dbUtil = DBUtil.getInstance();
			dbUtil.startSession();
		}
		catch (Exception e) 
		{
			
			try 
			{
				if (dbUtil.isSessionStarted()) 
				{
					dbUtil.endSession();
				}
			} 
			catch (Exception e2) 
			{
				logger.error("startProcessDailySsidUsages Error in endSession", e2);
			}
			logger.error("startProcessDailySsidUsages Exception  " + e,e);				
			return count;
		}
		
		try
		{
			SnsOrganizationsDAO snsOrganizationsDAO = new SnsOrganizationsDAO(true);
			snsOrganizationsList = snsOrganizationsDAO.getDistinctOrgList();
			if (logger.isDebugEnabled())
				logger.debug("snsOrganizationsList" + snsOrganizationsList);
		}
		catch (Exception e) 
		{
			logger.error("startProcessDailySsidUsages Error in accessing report consolidate job table 36- " + e,e);
			return count;
		}
		//for each organization, get list of networks
		for (String orgId:snsOrganizationsList){
//			List<Integer> networkList = null;
			if (logger.isInfoEnabled())
				logger.infof("startProcessDailySsidUsages working on: orgId = %s", orgId);
			List<Networks> networkList = null;
			try{				
				batchConnection = dbUtil.getConnection(false, orgId, false);
				deviceSsidUsagesDAO = new DeviceSsidUsagesDAO(orgId, true);
				dailyDeviceSsidUsagesDAO = new DailyDeviceSsidUsagesDAO(orgId);
				networkDAO = new NetworksDAO(orgId, true);
//				networkList = deviceSsidUsagesDAO.getDistinctNetworkIds();
				networkList = networkDAO.getAllNetworks();
			}catch (Exception e) {
				logger.error("startProcessDailySsidUsages Error in accessing report consolidate job table 37- " + orgId,e);					
			}
			
			try{
				if( networkList != null )
				{
//					Networks network = null;
					for (Networks network:networkList){
						//for each network
//						network = networkDAO.findById(netId);
						if(logger.isInfoEnabled())
							logger.infof("startProcessDailySsidUsages working on: orgId = %s, netId = %d", orgId, network.getId());
						if( network != null )
						{
							String timezone = network.getTimezone();
							String timezoneId = DateUtils.getTimezoneFromId(Integer.valueOf(timezone));
							TimeZone tz = TimeZone.getTimeZone(timezoneId);					
							long currentTimeInMillis = new Date().getTime();
							
							if (inputDate != null){
								currentTimeInMillis = inputDate.getTime();
							}
							
							int offsetMillis = tz.getOffset(currentTimeInMillis);
							long timeAdjWithTimezone = currentTimeInMillis + offsetMillis;
							timeAdjWithTimezone = timeAdjWithTimezone/86400000 * 86400000;//round to the start of the day
							long startTimeInMillis = timeAdjWithTimezone - offsetMillis;//startTimeInMillis is in UTC time
							int startTime = (int)(startTimeInMillis/1000);
							long endTimeInMillis = startTimeInMillis + 86400000;
							int endTime = (int)(endTimeInMillis/1000);

							List<DailyDevSsidUsageResult> DevResultList = deviceSsidUsagesDAO.getEssidEncryptionTotalTxRxWithTimePeriod(network.getId(), startTime, endTime);
							if( DevResultList != null )
							{
								for (DailyDevSsidUsageResult result:DevResultList )
								{
									DailyDeviceSsidUsages dailyDeviceSsidUsages = dailyDeviceSsidUsagesDAO.findByDeviceIdEssidEncryptionTime(result.getNetwork_id(), result.getDevice_id(), result.getEssid(), result.getEncryption(), (int)(timeAdjWithTimezone/1000));
									if (dailyDeviceSsidUsages == null)
									{
										dailyDeviceSsidUsages = new DailyDeviceSsidUsages(result.getNetwork_id(), result.getDevice_id(), new Date(timeAdjWithTimezone), result.getEssid(), result.getEncryption(), (float)result.getTx(), (float)result.getRx(), (int)(timeAdjWithTimezone/1000));
										dailyDeviceSsidUsages.create();
									}
									else 
									{
										dailyDeviceSsidUsages.setTx((float)result.getTx());
										dailyDeviceSsidUsages.setRx((float)result.getRx());
										dailyDeviceSsidUsages.saveOrUpdate();
									}
									batchConnection.addBatch(dailyDeviceSsidUsages);
								}
							}
						}
						
					}
				}
				
				batchConnection.commit();
				batchConnection.close();
				
			}
			catch (Exception e) {
				logger.error("startProcessDailySsidUsages Error in accessing report consolidate job table 38- " + orgId,e);				
			}
			count ++;
		}
		
		try
		{
			dbUtil.endSession();
//			end = new Date();
//			logger.info("Consolidate device ssid usage use " + (end.getTime() - start.getTime())/1000 + " s");
		}
		catch(Exception e)
		{
			logger.error("startProcessDailySsidUsages Error in accessing report consolidate job table 39- " + e,e);
			try {
				if (dbUtil.isSessionStarted()) {
					dbUtil.endSession();
				}
			} catch (Exception e2) {
				logger.error("startProcessDailySsidUsages Error in endSession", e2);
			}
		}
		return count;
	}
	
	public int startProcessDailyClientSsidUsages()
	{
		List<String> snsOrganizationsList = null;
		ClientSsidUsagesDAO clientSsidUsagesDAO = null;
		DailyClientSsidUsagesDAO dailyClientSsidUsagesDAO = null;
//		Date start = null;
//		Date end = null;
		DBUtil dbUtil = null;
		DBConnection batchConnection = null;
		int count=-1;
		
		try
		{
//			start = new Date();
			dbUtil = DBUtil.getInstance();
			dbUtil.startSession();
		}
		catch (Exception e) 
		{
			
			try 
			{
				if (dbUtil.isSessionStarted()) 
				{
					dbUtil.endSession();
				}
			} 
			catch (Exception e2) 
			{
				logger.error("startProcessDailyClientSsidUsages Error in endSession " + e2, e2);
			}
			logger.error("startProcessDailyClientSsidUsages " + e,e);	
			return count;
		}
		
		try
		{
			SnsOrganizationsDAO snsOrganizationsDAO = new SnsOrganizationsDAO(true);
			snsOrganizationsList = snsOrganizationsDAO.getDistinctOrgList();
			logger.debug("snsOrganizationsList" + snsOrganizationsList);
		}
		catch (Exception e) 
		{
			logger.error("startProcessDailyClientSsidUsages " + e,e);
			return count;
		}
		//for each organization, get list of networks
		for (String orgId:snsOrganizationsList){
			NetworksDAO networkDAO = null;
			List<Networks> networkList = null;
			try{				
				batchConnection = dbUtil.getConnection(false, orgId, false);
				clientSsidUsagesDAO = new ClientSsidUsagesDAO(orgId, true);
				dailyClientSsidUsagesDAO = new DailyClientSsidUsagesDAO(orgId);
				networkDAO = new NetworksDAO(orgId, true);
			}catch (Exception e) {
				logger.error("startProcessDailyClientSsidUsages Error org = " + orgId, e);					
			}
			
			try
			{
				if (logger.isInfoEnabled())
					logger.info("startProcessDailyClientSsidUsages for org = " + orgId);
				networkList = networkDAO.getAllNetworks();
				if(networkList != null)
				{
					for (Networks network:networkList){
						if( network != null )
						{
							String timezone = network.getTimezone();
							String timezoneId = DateUtils.getTimezoneFromId(Integer.valueOf(timezone));
							TimeZone tz = TimeZone.getTimeZone(timezoneId);					
							long currentTimeInMillis = new Date().getTime();
							int offsetMillis = tz.getOffset(currentTimeInMillis);
							long timeAdjWithTimezone = currentTimeInMillis + offsetMillis;
							timeAdjWithTimezone = timeAdjWithTimezone/86400000 * 86400000;//round to the start of the day
							long startTimeInMillis = timeAdjWithTimezone - offsetMillis;//startTimeInMillis is in UTC time
							int startTime = (int)(startTimeInMillis/1000);
							long endTimeInMillis = startTimeInMillis + 86400000;
							int endTime = (int)(endTimeInMillis/1000);
							
							List<DailyClientSsidUsageResult> clientSsidList = clientSsidUsagesDAO.getSsidEncryptionByNetworkTimePeriod(network.getId(), startTime, endTime);
							if (clientSsidList !=null){
								for(DailyClientSsidUsageResult clientSsid:clientSsidList){
									if(clientSsid.getBssid()==null || clientSsid.getEssid()==null || clientSsid.getEncryption()==null)
										continue; // skip those invalid records
									
									DailyClientSsidUsages dailyClientSsidUsages = dailyClientSsidUsagesDAO.findByDeviceIdEssidEncryptionTime(network.getId(), clientSsid.getDeviceId(), clientSsid.getEssid(), clientSsid.getEncryption(), (int)(timeAdjWithTimezone/1000), clientSsid.getMac());
									if (dailyClientSsidUsages == null) {
										dailyClientSsidUsages = new DailyClientSsidUsages(network.getId(), clientSsid.getDeviceId(), null, clientSsid.getBssid(), clientSsid.getEssid(), clientSsid.getEncryption(), clientSsid.getMac(), clientSsid.getType(), new Date(timeAdjWithTimezone), (int)(timeAdjWithTimezone/1000));
										dailyClientSsidUsages.create();
										batchConnection.addBatch(dailyClientSsidUsages);
									}
								}
							} else {
								if (logger.isDebugEnabled())
									logger.debug("clientSsidList is null " + network.getId() + " " + startTime + " " + endTime);	
							}
						}
					}
				}
				
				batchConnection.commit();
				batchConnection.close();
			}
			catch (Exception e) {
				logger.error("startProcessDailyClientSsidUsages Error org = " + orgId,e);				
			}
			count ++;
		}
		
		try
		{
			dbUtil.endSession();
		}
		catch(Exception e)
		{
			logger.error("startProcessDailyClientSsidUsages Error " + e, e);
			try {
				if (dbUtil.isSessionStarted()) {
					dbUtil.endSession();
				}
			} catch (Exception e2) {
				logger.error("startProcessDailyClientSsidUsages Error in endSession " + e2, e2);
			}
		}
		return count;
	}
	
	public void startProcessDailyDpiUsages()
	{
		List<String> snsOrganizationsList = null;
		DeviceDpiUsagesDAO deviceDpiUsagesDAO = null;
		DeviceDailyDpiUsagesDAO deviceDailyDpiUsagesDAO = null;
		NetworksDAO networksDAO = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try
		{
			SnsOrganizationsDAO snsOrganizationsDAO = new SnsOrganizationsDAO(true);
			snsOrganizationsList = snsOrganizationsDAO.getDistinctOrgList();
			if (logger.isDebugEnabled())
				logger.debug("snsOrganizationsList" + snsOrganizationsList);
		}
		catch (Exception e) 
		{
			logger.error("Error in accessing report consolidate job table 44- " + e,e);
			return ;
		}
		
		try
		{
			if( snsOrganizationsList != null )
			{
				DeviceDailyDpiUsages dailyDpiUsages = null;
				for( String orgId : snsOrganizationsList )
				{
					deviceDpiUsagesDAO = new DeviceDpiUsagesDAO(orgId);
					deviceDailyDpiUsagesDAO = new DeviceDailyDpiUsagesDAO(orgId);
					networksDAO = new NetworksDAO(orgId, true);
					
//					List<Integer> networkIds = deviceDpiUsagesDAO.getDistinctNetworkIds();
					List<Networks> networkIds = networksDAO.getAllNetworks();
					if( networkIds != null )
					{
						for( Networks networks : networkIds )
						{
//							networks = networksDAO.findById(networkId);
							List<DpiUsageObject> dpiLst = deviceDpiUsagesDAO.getDailyDpiRecords(networks.getId(), DateUtils.getTimezoneFromId(Integer.parseInt(networks.getTimezone())));
							if( dpiLst != null )
							{
								for( DpiUsageObject dpi : dpiLst )
								{
//									if (dpi.getSize() == null || (dpi.getSize().longValue() >= (new Double(Math.pow(2, 42) / 2).longValue())  || dpi.getSize().longValue() <= (new Double(-(Math.pow(2, 42) / 2)).longValue()))){									logger.warnf("startProcessDailyDpiUsages() - dpi.getSize() larger than db field int(15), record will not be saved! networkId: %s", networkIds);
//										continue;
//									}
									
									Date datetime = sdf.parse(dpi.getDatetime());
									Date utcTime = DateUtils.getUtcDate(datetime, DateUtils.getTimezoneFromId(Integer.parseInt(networks.getTimezone())));
									int ut = (int)(utcTime.getTime()/1000); 
									dailyDpiUsages = deviceDailyDpiUsagesDAO.findByNetworkIdDeviceIdUnixtimeAndProtocol(dpi.getNetwork_id(), dpi.getDevice_id(), ut, dpi.getService());
									if( dailyDpiUsages != null )
									{
										dailyDpiUsages.setDevice_id(dpi.getDevice_id());
										dailyDpiUsages.setNetwork_id(dpi.getNetwork_id());
										dailyDpiUsages.setService(dpi.getService());
										dailyDpiUsages.setSize(dpi.getSize());
										if (logger.isInfoEnabled())
											logger.info("netwoks dpi : " + dpi.getDatetime());
										dailyDpiUsages.setUnixtime(ut);
										deviceDailyDpiUsagesDAO.update(dailyDpiUsages);
									}
									else
									{
										dailyDpiUsages = new DeviceDailyDpiUsages();
										dailyDpiUsages.setDevice_id(dpi.getDevice_id());
										dailyDpiUsages.setNetwork_id(dpi.getNetwork_id());
										dailyDpiUsages.setService(dpi.getService());
										dailyDpiUsages.setSize(dpi.getSize());
										if (logger.isInfoEnabled())
											logger.info("netwoks dpi : " + dpi.getDatetime());
										dailyDpiUsages.setUnixtime(ut);
										deviceDailyDpiUsagesDAO.save(dailyDpiUsages);
									}
								}
							}
						}
					}
				}
			}
		}
		catch( Exception e )
		{
			logger.error("Error in accessing report consolidate job table 45- " + e,e);
		}
		
	}
	
	public void startProcessMonthlyDpiUsages()
	{
		if(monthly_dpi) {
			logger.info("SKIP startProcessMonthlyDpiUsages as previous task is still running");
			return;
		}
		
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		long tmpTime = today.getTimeInMillis();

		if(monthly_dpi_time == tmpTime) {
			logger.info("SKIP startProcessMonthlyDpiUsages as today's task has run");
			return;
		}
		
		try
		{
			monthly_dpi = true;
			logger.info("startProcessMonthlyDpiUsages start, monthly_dpi_time = " + monthly_dpi_time);
	
			List<String> snsOrganizationsList = null;
			DeviceMonthlyDpiUsagesDAO deviceMonthlyDpiUsagesDAO = null;
			DeviceDailyDpiUsagesDAO deviceDailyDpiUsagesDAO = null;
			NetworksDAO networksDAO = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

			SnsOrganizationsDAO snsOrganizationsDAO = new SnsOrganizationsDAO(true);
			snsOrganizationsList = snsOrganizationsDAO.getDistinctOrgList();
			if (logger.isDebugEnabled())
				logger.debug("snsOrganizationsList" + snsOrganizationsList);
		
			if( snsOrganizationsList != null )
			{
				DeviceMonthlyDpiUsages dailyDpiUsages = null;
				for( String orgId : snsOrganizationsList )
				{
					deviceMonthlyDpiUsagesDAO = new DeviceMonthlyDpiUsagesDAO(orgId);
					deviceDailyDpiUsagesDAO = new DeviceDailyDpiUsagesDAO(orgId);
					networksDAO = new NetworksDAO(orgId, true);
					
//					Networks networks = null;
//					List<Integer> networkIds = deviceDailyDpiUsagesDAO.getDistinctNetworkIds();
					List<Networks> networkIds = networksDAO.getAllNetworks();
					if( networkIds != null )
					{
						for( Networks networks : networkIds )
						{
//							networks = networksDAO.findById(networkId);
							List<DpiUsageObject> dpiLst = deviceDailyDpiUsagesDAO.getMonthlyDpiRecords(networks.getId(), DateUtils.getTimezoneFromId(Integer.parseInt(networks.getTimezone())));
							if( dpiLst != null )
							{
								for( DpiUsageObject dpi : dpiLst )
								{
//									if (dpi.getSize() == null || (dpi.getSize().longValue() >= (new Double(Math.pow(2, 42) / 2).longValue())  || dpi.getSize().longValue() <= (new Double(-(Math.pow(2, 42) / 2)).longValue()))){
//										logger.warnf("startProcessMonthlyDpiUsages() - dpi.getSize() larger than db field int(15), record will not be saved! networkId: %s", networkIds);
//										continue;
//									}
									Date datetime = sdf.parse(dpi.getDatetime());
									Date utcTime = DateUtils.getUtcDate(datetime, DateUtils.getTimezoneFromId(Integer.parseInt(networks.getTimezone())));
									int ut = (int)(utcTime.getTime()/1000); 
									dailyDpiUsages = deviceMonthlyDpiUsagesDAO.findByNetworkIdDeviceIdUnixtimeAndProtocol(dpi.getNetwork_id(), dpi.getDevice_id(), ut, dpi.getService());
									if( dailyDpiUsages != null )
									{
										dailyDpiUsages.setDevice_id(dpi.getDevice_id());
										dailyDpiUsages.setNetwork_id(dpi.getNetwork_id());
										dailyDpiUsages.setService(dpi.getService());
										dailyDpiUsages.setSize(dpi.getSize());
										if (logger.isInfoEnabled())
											logger.info("monthly netwoks dpi : " + dpi.getDatetime());
										dailyDpiUsages.setUnixtime(ut);
										try{
											deviceMonthlyDpiUsagesDAO.update(dailyDpiUsages);
										} catch (Exception e){
											logger.error("dpi.getSize(): " + dpi.getSize() + ",org Id: " + orgId + ", network id: " + networks.getId(), e);
										}
									}
									else
									{
										dailyDpiUsages = new DeviceMonthlyDpiUsages();
										dailyDpiUsages.setDevice_id(dpi.getDevice_id());
										dailyDpiUsages.setNetwork_id(dpi.getNetwork_id());
										dailyDpiUsages.setService(dpi.getService());
										dailyDpiUsages.setSize(dpi.getSize());
										if (logger.isInfoEnabled())
											logger.info("monthly netwoks dpi : " + dpi.getDatetime());
										dailyDpiUsages.setUnixtime(ut);
										try{
											deviceMonthlyDpiUsagesDAO.save(dailyDpiUsages);
										} catch (Exception e){
											logger.error("dpi.getSize(): " + dpi.getSize() + ",org Id: " + orgId + ", network id: " + networks.getId(), e);
										}
									}
								}
							}
						}
					}
				}
			}
		} catch( Exception e ) {
			
			logger.error("Error in accessing report consolidate job table 47- " + e,e);
		} finally {
			monthly_dpi = false;
			monthly_dpi_time = tmpTime;
			logger.info("startProcessMonthlyDpiUsages end, monthly_dpi_time = " + monthly_dpi_time);
		}
	}
	
	public void startPersistClientInfo()
	{
		logger.warn("startPersistClientInfo started");
		long tnow = System.currentTimeMillis();
		DBUtil dbUtil = null;
		DBConnection batchConnection = null;

		try {
			Set<String> orgLst = BranchUtils.getOrgIdSet();
			for( String org : orgLst ) {
				try {
					try {
						dbUtil = DBUtil.getInstance();
						dbUtil.startSession();
						batchConnection = dbUtil.getConnection(false, org, false);
					}
					catch (Exception e) { 
						try { 
							if (dbUtil.isSessionStarted()) 
								dbUtil.endSession();
						} catch (Exception e2) {
							logger.error("Persist client info exception in endSession " + e2, e2);
						}
						logger.error("Persist client info startSession " + e,e);	
						continue; // continue for next Org
					}

					ClientInfosDAO clientInfosDAO = new ClientInfosDAO(org);
					List<Networks> networkLst = OrgInfoUtils.getNetworkLst(org);
					if (logger.isInfoEnabled())
						logger.info("Persist client info orgId " + org);
					
					for(Networks network : networkLst) {
						List<Devices> deviceLst = NetUtils.getDeviceLstByNetId(org, network.getId());
						for( Devices dev : deviceLst ) {
							try {
								UpdatedClientInfoObject clientCache = new UpdatedClientInfoObject();
								clientCache.setIana_id(dev.getIanaId());
								clientCache.setSn(dev.getSn());
								clientCache = ACUtil.getPoolObjectBySn(clientCache, UpdatedClientInfoObject.class);
								
								if( clientCache != null && clientCache.getClientInfoMap() != null) {
									if( clientCache.getLastUpdatedTime() == null || new Date().getTime() - clientCache.getLastUpdatedTime().getTime() >= MAX_DEVICE_INACTIVE ) {
										ACUtil.removePoolObjectBySn(clientCache, UpdatedClientInfoObject.class);
										if (logger.isInfoEnabled())
											logger.info("removing UpdatedClientInfoObject sn="+clientCache.getSn());
										continue;
									}
									
									ConcurrentHashMap<String, ClientInfoObject> clientInfoMap = clientCache.getClientInfoMap();
									
									Iterator<Map.Entry<String, ClientInfoObject>> iter = clientInfoMap.entrySet().iterator();
									while(iter.hasNext()) {
										Map.Entry<String, ClientInfoObject> entry  = iter.next();
										ClientInfoObject clientInfo = entry.getValue();
										
										if(tnow - clientInfo.getLastUpdated() > MAX_CLIENTINFO_KEPT ) { // remove outdated info
											if (logger.isInfoEnabled())
												logger.info("removing ClientInfoObject sn="+clientInfo);
											iter.remove();
										}
										
										if( clientInfo.isFlag() == true ) { // is update to db or not?
											ClientInfos dbInfo  = clientInfosDAO.findByClientId(clientInfo.getClient_id());
											if( dbInfo != null ) {
												dbInfo.setClient_name(clientInfo.getClient_name());
												dbInfo.setLast_device_id(clientInfo.getLast_device_id());
												dbInfo.replace();
												batchConnection.addBatch(dbInfo);
//												clientInfosDAO.update(dbInfo);
												clientInfo.setFlag(false);
												if(logger.isInfoEnabled())
													logger.info("updated client info, clientid="+clientInfo.getClient_id());
											} else {
												ClientInfosId ciId = new ClientInfosId();
												ciId.setClient_id(clientInfo.getClient_id());
												ciId.setLast_updated(new Date(clientInfo.getLastUpdated()));
												dbInfo = new ClientInfos(ciId, clientInfo.getClient_name(), clientInfo.getLast_device_id());
												dbInfo.replace();
												batchConnection.addBatch(dbInfo);
//												clientInfosDAO.save(dbInfo);
												clientInfo.setFlag(false);
												if(logger.isInfoEnabled())
													logger.info("saved new client info, clientid="+clientInfo.getClient_id());
											}
											clientInfoMap.put(clientInfo.getClient_id(), clientInfo);
										}
									}
									clientCache.setClientInfoMap(clientInfoMap);
									ACUtil.cachePoolObjectBySn(clientCache, UpdatedClientInfoObject.class);
								}
							} catch(Exception e) {
								logger.error("Persist client info batch error, org="+org, e);
							}
						}
						batchConnection.commit();
						batchConnection.close();
						if( dbUtil.isSessionStarted() )
							dbUtil.endSession();
						dbUtil.startSession();
						batchConnection = dbUtil.getConnection(false, org, false);
					}
				} catch( Exception e ) {
					logger.error("Persist client info error, org="+org, e);
					continue; // continue for next Org
				} finally {
					try { 
						if (dbUtil.isSessionStarted()) { 
							dbUtil.endSession();
							logger.info("Persist client info completed for orgId=" + org);
						}
					} catch (Exception e2) {
						logger.error("Persist client info exception in endSession2 " + e2, e2);
					}
				}
			}
		} catch(Exception e) {
			logger.error("Persist client infos error : " , e);
		}
		long tused = (System.currentTimeMillis() - tnow ) /1000;
		logger.warnf("startPersistClientInfo ended, tused = %d sec",tused);
	}
	
	public static void main(String[] args) throws ParseException
	{
//		ReportConsolidateService rcs = new ReportConsolidateService();
	}
	
}
