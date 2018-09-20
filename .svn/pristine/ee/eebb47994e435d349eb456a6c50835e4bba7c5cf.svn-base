package com.littlecloud.pool;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.jboss.logging.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.ReportConsolidateService;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.DeviceGpsLocationsDAO;
import com.littlecloud.control.dao.DeviceGpsLocationsDatesDAO;
import com.littlecloud.control.dao.DeviceMonthlyUsagesDAO;
import com.littlecloud.control.dao.DeviceUsagesDAO;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.dao.NetworksDAO;
import com.littlecloud.control.dao.branch.SnsOrganizationsDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.entity.report.DeviceGpsLocations;
import com.littlecloud.control.entity.report.DeviceGpsLocationsDates;
import com.littlecloud.control.entity.report.DeviceGpsLocationsDatesId;
import com.littlecloud.control.entity.report.DeviceUsages;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.control.webservices.util.HouseKeepUtils;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.DeviceLastLocObject;
import com.littlecloud.pool.object.DevicesObject;
import com.littlecloud.pool.object.DistributedQueueObject;
import com.littlecloud.pool.object.EventObject;
import com.littlecloud.pool.object.LocationList;
import com.littlecloud.pool.object.PoolObjectDAO;
import com.littlecloud.pool.object.utils.NetUtils;
import com.littlecloud.pool.object.utils.OrgInfoUtils;
import com.littlecloud.pool.utils.PropertyService;
import com.littlecloud.services.OfflineAlertCheckingMgr;
import com.opensymphony.xwork2.ActionSupport;

public class SchedulerAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {
	private static final long serialVersionUID = 424115174489128522L;

	private static Logger logger = Logger.getLogger(SchedulerAction.class);

	protected HttpServletRequest request;
	protected HttpServletResponse response;

	protected static final String RESULT_TRUE = "true";
	protected static final String RESULT_FALSE = "false";

	private static PropertyService<SchedulerAction> ps = new PropertyService<SchedulerAction>(SchedulerAction.class);
		
	private static Thread schedulerPersistsLastLocation_THREAD;
	private static Thread scheduleCheckIdleDevice_THREAD;
	private static Thread scheduleConsolidateReport_THREAD;
	private static Thread schedulePersistReportFromCache_THREAD;
	
	private static Thread scheduleCheckOfflineAlert_THREAD;
	private static Thread scheduleCheckDevicewarrantyexpirydate_THREAD;
	private static Thread schedulerArchiveDeviceUsageHistory_THREAD;
	private static Thread schedulerFetchUsageHistory_THREAD;
	
	private static Thread generateDevLocationDates_THREAD;
	private static Thread schedulerProcessDpiUsages_THREAD;
	private static Thread schedulerDoHouseKeeping_THREAD;
	private static Thread schedulerPersistClientName_THREAD;

	public HttpServletRequest getServletRequest() {
		return request;
	}

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getServletResponse() {
		return response;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}	

	@SkipValidation
	public String scheduleConsolidateReport()
	{	
		StringBuilder sb = null;
		PrintWriter pw = null;
		try
		{
			if (scheduleConsolidateReport_THREAD == null)
				logger.info("scheduleConsolidateReport_STATUS = null");
			else 
				logger.infof("scheduleConsolidateReport_STATUS = %s", scheduleConsolidateReport_THREAD.isAlive());
			
			if (scheduleConsolidateReport_THREAD == null ||!scheduleConsolidateReport_THREAD.isAlive())
			{			
				scheduleConsolidateReport_THREAD = new Thread(new Runnable() {
					@Override
					public void run() {
						try
						{
							logger.warnf("INFO scheduleConsolidateReport_START");
							
							String s = Utils.getContentStr(request);
							String time = Utils.getParameter(request, s, "time");
					
							ReportConsolidateService rcs = new ReportConsolidateService();
							Long reportTime = null;
							if (time == null || time.isEmpty())
							{
								reportTime = Calendar.getInstance().getTimeInMillis();
							}
							else
							{
								reportTime = Long.parseLong(time, 10);
							}
							rcs.StartConsolidateReport(reportTime);
							
							logger.warnf("INFO scheduleConsolidateReport_STOP");
						}
						catch (SocketException se){
							logger.warnf("ALERT201408211034 SocketException scheduleConsolidateReport() "+se);
						}
						catch (IOException ie) {
							logger.warnf("ALERT201408211034 IOException scheduleConsolidateReport() "+ie);
						}
						catch (Exception e)
						{
							logger.warn("ALERT201408211034 - scheduleConsolidateReport()"+ e);
						}
					}
				});
				scheduleConsolidateReport_THREAD.start();
				logger.warnf("INFO scheduleConsolidateReport() starts");
				sb = new StringBuilder();		
				sb.append("Report consoildate finished.\n");
				pw = response.getWriter();
				pw.write(sb.toString());
				logger.debug(sb.toString());
				IOUtils.closeQuietly(pw);
				return SUCCESS;
			}
			else
			{
				sb = new StringBuilder();	
				sb.append("Last Action scheduleConsolidateReport is not finished.\n");
				pw = response.getWriter();
				pw.write(sb.toString());
				logger.debug(sb.toString());
	
				IOUtils.closeQuietly(pw);
				return SUCCESS;
			}
		}		
		catch (SocketException se){
			logger.warnf("ALERT201408211035 SocketException scheduleConsolidateReport() "+se);
		}
		catch (IOException ie) {
			logger.warnf("ALERT201408211035 IOException scheduleConsolidateReport() "+ie);
		}
		catch (Exception e){
			logger.error("ALERT201408211035 - scheduleConsolidateReport() "+e);
			e.printStackTrace();
		}
		finally
		{
			if (pw != null)
				IOUtils.closeQuietly(pw);
		}
		return null;
	}
	
	@SkipValidation
	public String schedulePersistReportFromCache()
	{
		StringBuilder sb = null;
		PrintWriter pw = null;
		try
		{
		if (schedulePersistReportFromCache_THREAD == null)
			logger.info("schedulePersistReportFromCache_STATUS = null");
		else 
			logger.infof("schedulePersistReportFromCache_STATUS = %s", schedulePersistReportFromCache_THREAD.isAlive());
		
		if (schedulePersistReportFromCache_THREAD == null ||!schedulePersistReportFromCache_THREAD.isAlive())
		{	
			schedulePersistReportFromCache_THREAD = new Thread(new Runnable() {
				@Override
				public void run() {
					try
					{
						DistributedQueueObject dqueue = DistributedQueueObject.getInstance();
						if(dqueue != null)
						{
							logger.warnf("INFO schedulePersistReportFromCache_START");
						
						if(dqueue.isEmpty())
						{
							List<String> snsOrganizationsList = null;
							SnsOrganizationsDAO snsOrganizationsDAO = new SnsOrganizationsDAO(true);
							snsOrganizationsList = snsOrganizationsDAO.getDistinctOrgList();
							if(logger.isInfoEnabled())
								logger.info("schedulePersistReportFromCache: newly allocated snsOrganizationsList = " + snsOrganizationsList);
							
							for(String orgId: snsOrganizationsList) {
								dqueue.Push(orgId);
							}
						
							logger.warn("schedulePersistReportFromCache: pushed "+snsOrganizationsList.size() + " orgs into distributed queue");
							if(logger.isInfoEnabled())
								logger.info("schedulePersistReportFromCache: new dqueue = " + dqueue);
						}
						else
						{
							if(logger.isInfoEnabled())
								logger.info("schedulePersistReportFromCache: already a running dqueue = " + dqueue);
						}
					
						EventObject evt1 = new EventObject();
						Cache<String, Object> cacheRepl = Cluster.getCache(Cluster.CACHENAME.LittleCloudCacheRepl);
						cacheRepl.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put("EVENT_PERSISTCACHE_" + evt1.getId(), evt1);
						logger.warnf("schedulePersistReportFromCache: Fired EVENT_PERSISTCACHE for StartPersistReportFromCache");
			
						EventObject evt2 = new EventObject();
						cacheRepl.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put("EVENT_GETKEYS_" + evt2.getId(), evt2);
						logger.warnf("schedulePersistReportFromCache: Fired EVENT_GETKEYS for obtaining keys from all caches");
			
					
						logger.warnf("INFO schedulePersistReportFromCache_STOP");
						}
						else
							logger.warnf("INFO schedulePersistReportFromCache dqueue=null");
					} catch (Exception e) {
						logger.warn("ALERT201408211034 - schedulePersistReportFromCache() abnormal ended, e=" + e);
					}
				}
			});
			schedulePersistReportFromCache_THREAD.start();
			logger.warnf("INFO schedulePersistReportFromCache() starts");
			
			sb = new StringBuilder("Persist Report from cache to DB called.\n");		
			pw = response.getWriter();
			pw.write(sb.toString());
			if(logger.isDebugEnabled())
				logger.debug(sb.toString());
			IOUtils.closeQuietly(pw);
			return SUCCESS;
		}
		else
		{
			sb = new StringBuilder();	
			sb.append("Last Action schedulePersistReportFromCache is not finished.\n");
			pw = response.getWriter();
			pw.write(sb.toString());
			logger.debug(sb.toString());

			IOUtils.closeQuietly(pw);
			return SUCCESS;
		}
		}
		catch (SocketException se){
			logger.warnf("SocketException schedulePersistReportFromCache() "+se);
		}
		catch (IOException ie) {
			logger.warnf("IOException schedulePersistReportFromCache() "+ie);
		}
		
		catch (Exception e){
			logger.error("ALERT201408211035 - schedulePersistReportFromCache() "+e);
			e.printStackTrace();
		}
		finally
		{
			if (pw != null)
				IOUtils.closeQuietly(pw);
		}
		return null;
	}
	
	@SkipValidation
	public String scheduleCheckOfflineAlert()
	{
		StringBuilder sb = null;
		PrintWriter pw = null;
		try
		{
			if (scheduleCheckOfflineAlert_THREAD == null)
				logger.info("scheduleCheckOfflineAlert_STATUS = null");
			else 
				logger.infof("scheduleCheckOfflineAlert_STATUS = %s", scheduleCheckOfflineAlert_THREAD.isAlive());
						
			if (scheduleCheckOfflineAlert_THREAD == null ||!scheduleCheckOfflineAlert_THREAD.isAlive())
			{			
				scheduleCheckOfflineAlert_THREAD = new Thread(new Runnable() {
					@Override
					public void run() {
						try
						{
							logger.warnf("INFO scheduleCheckOfflineAlert_START");
							
							OfflineAlertCheckingMgr.startOfflineAlertChecking();
							
							Integer OFFLINE_ALERT_SLEEP = ps.getInteger("OFFLINE_ALERT_SLEEP");
							Thread.sleep(OFFLINE_ALERT_SLEEP);
							logger.warnf("INFO scheduleCheckOfflineAlert_STOP");
						} catch (Exception e){
							logger.warn("ALERT201408211034 - scheduleCheckOfflineAlert()"+ e);
						}
					}
				});
				scheduleCheckOfflineAlert_THREAD.start();
				logger.warnf("INFO scheduleCheckOfflineAlert() starts");
			
				sb = new StringBuilder();	
				pw = response.getWriter();
				
				sb.append("Offline Alert is called.\n");		
				pw.write(sb.toString());	
				logger.debug(sb.toString());
				IOUtils.closeQuietly(pw);
				return SUCCESS;
			}
			else
			{	
				sb = new StringBuilder();	
				pw = response.getWriter();
				
				sb.append("Last Action scheduleCheckOfflineAlert is not finished.\n");
				pw.write(sb.toString());
				IOUtils.closeQuietly(pw);
				return SUCCESS;
			}
			
		}
		catch (SocketException se){
			logger.warnf("SocketException scheduleCheckOfflineAlert() "+se);
		}
		catch (IOException ie) {
			logger.warnf("IOException scheduleCheckOfflineAlert() "+ie);
		}
		
		catch (Exception e){
			logger.error("ALERT201408211035 - scheduleCheckOfflineAlert() "+e);
			e.printStackTrace();
		}
		finally
		{
			if (pw != null)
				IOUtils.closeQuietly(pw);
		}
		return null;
	}
	
	@SkipValidation
	public String scheduleCheckIdleDevice()
	{
		StringBuilder sb = null;
		PrintWriter pw = null;
		try
		{
		if (scheduleCheckIdleDevice_THREAD == null)
			logger.info("scheduleCheckIdleDevice_STATUS = null");
		else 
			logger.infof("scheduleCheckIdleDevice_STATUS = %s", scheduleCheckIdleDevice_THREAD.isAlive());
		
		if (scheduleCheckIdleDevice_THREAD == null ||!scheduleCheckIdleDevice_THREAD.isAlive())
		{			
			scheduleCheckIdleDevice_THREAD = new Thread(new Runnable() {
				@Override
				public void run() {
					try
					{
						logger.warnf("INFO scheduleCheckIdleDevice_START");

						OfflineAlertCheckingMgr.startCheckIdleDevice();
						logger.warnf("INFO scheduleCheckIdleDevice_STOP");
					} catch (Exception e) {
						logger.warn("ALERT201408211034 - scheduleCheckIdleDevice()"+e);
					}
				}
			});
			scheduleCheckIdleDevice_THREAD.start();
			logger.warnf("INFO scheduleCheckIdleDevice() starts");
			sb = new StringBuilder();
			sb.append("Action CheckIdleDevice finished.\n");
			pw = response.getWriter();
			pw.write(sb.toString());

			logger.debug(sb.toString());
			IOUtils.closeQuietly(pw);
			return SUCCESS;
		}
		else
		{
			sb = new StringBuilder();	
			sb.append("Last Action CheckIdleDevice is not finished.\n");
			pw = response.getWriter();
			pw.write(sb.toString());
			logger.debug(sb.toString());

			IOUtils.closeQuietly(pw);
			return SUCCESS;
		}
	}
	catch (SocketException se){
		logger.warnf("SocketException CheckIdleDevice() "+se);
	}
	catch (IOException ie) {
		logger.warnf("IOException CheckIdleDevice() "+ie);
	}
	
	catch (Exception e){
		logger.error("ALERT201408211035 - CheckIdleDevice() "+e);
		e.printStackTrace();
	}
	finally
	{
		if (pw != null)
			IOUtils.closeQuietly(pw);
	}
	return null;
	}
	
	@SkipValidation
	public String scheduleCheckDevicewarrantyexpirydate()
	{
		StringBuilder sb = null;
		PrintWriter pw = null;
		try
		{
		if (scheduleCheckDevicewarrantyexpirydate_THREAD == null)
			logger.info("scheduleCheckDevicewarrantyexpirydate_STATUS = null");
		else 
			logger.infof("scheduleCheckDevicewarrantyexpirydate_STATUS = %s", scheduleCheckDevicewarrantyexpirydate_THREAD.isAlive());
		
		if (scheduleCheckDevicewarrantyexpirydate_THREAD == null ||!scheduleCheckDevicewarrantyexpirydate_THREAD.isAlive())
		{			
			scheduleCheckDevicewarrantyexpirydate_THREAD = new Thread(new Runnable() {
				@Override
				public void run() {
					try
					{
						logger.warnf("INFO scheduleCheckDevicewarrantyexpirydate_START");
				
						OfflineAlertCheckingMgr.startCheckIdleDevice();
						logger.warnf("INFO scheduleCheckDevicewarrantyexpirydate_STOP");
					} catch (Exception e) {
						logger.warn("ALERT201408211034 - scheduleCheckDevicewarrantyexpirydate()"+e);
					}
				}
			});
			scheduleCheckDevicewarrantyexpirydate_THREAD.start();
			logger.warnf("INFO scheduleCheckDevicewarrantyexpirydate() starts");
			sb = new StringBuilder();	
			sb.append("Idle Device checking finished.\n");
			pw = response.getWriter();
			pw.write(sb.toString());
			logger.debug(sb.toString());
			IOUtils.closeQuietly(pw);
			return SUCCESS;
		}
		else
		{
			sb = new StringBuilder();	
			sb.append("Last Action scheduleCheckDevicewarrantyexpirydate is not finished.\n");
			pw = response.getWriter();
			pw.write(sb.toString());
			logger.debug(sb.toString());

			IOUtils.closeQuietly(pw);
			return SUCCESS;
		}
		}
		catch (SocketException se){
			logger.warnf("SocketException scheduleCheckDevicewarrantyexpirydate() "+se);
		}
		catch (IOException ie) {
			logger.warnf("IOException scheduleCheckDevicewarrantyexpirydate() "+ie);
		}
		
		catch (Exception e){
			logger.error("ALERT201408211035 - scheduleCheckDevicewarrantyexpirydate() "+e);
			e.printStackTrace();
		}
		finally
		{
			if (pw != null)
				IOUtils.closeQuietly(pw);
		}
		return null;
	}
	
	@SkipValidation
	public String schedulerArchiveDeviceUsageHistory()
	{
		StringBuilder sb = null;
		PrintWriter pw = null;
		try
		{
			if (schedulerArchiveDeviceUsageHistory_THREAD == null)
				logger.info("schedulerArchiveDeviceUsageHistory_STATUS = null");
			else 
				logger.infof("schedulerArchiveDeviceUsageHistory_STATUS = %s", schedulerArchiveDeviceUsageHistory_THREAD.isAlive());
			
			if (schedulerArchiveDeviceUsageHistory_THREAD == null ||!schedulerArchiveDeviceUsageHistory_THREAD.isAlive())
			{			
				schedulerArchiveDeviceUsageHistory_THREAD = new Thread(new Runnable() {
					@Override
					public void run() {
						try
						{
							logger.warnf("INFO schedulerArchiveDeviceUsageHistory_START");
			
							Date start = new Date();
							ReportConsolidateService rcs = new ReportConsolidateService();
							rcs.startProcessDeviceDailyUsages();
							rcs.startProcessMonthlyUsages();
							rcs.startProcessClientDailyUsages();
							rcs.startProcessClientMonthlyUsage();
							rcs.startProcessDailySsidUsages();
							rcs.startProcessDailyClientSsidUsages();
							Date end = new Date();
							logger.info("Usage archive consolidate finished! Used "+ (end.getTime() - start.getTime()) + " ms");
							logger.warnf("INFO schedulerArchiveDeviceUsageHistory_STOP");
						} catch (Exception e) {
							logger.warn("ALERT201408211034 - schedulerArchiveDeviceUsageHistory()"+e);
						}
					}
				});
				schedulerArchiveDeviceUsageHistory_THREAD.start();
				logger.warnf("INFO schedulerArchiveDeviceUsageHistory() starts");
				sb = new StringBuilder();
				sb.append("Device Usage History process started!");
				pw = response.getWriter();
				pw.write(sb.toString());
				logger.debug(sb.toString());
				IOUtils.closeQuietly(pw);
				return SUCCESS;
			}
			else
			{
				sb = new StringBuilder();	
				sb.append("Last Action schedulerArchiveDeviceUsageHistory is not finished.\n");
				pw = response.getWriter();
				pw.write(sb.toString());
				logger.debug(sb.toString());
	
				IOUtils.closeQuietly(pw);
				return SUCCESS;
			}
		}
		catch (SocketException se){
			logger.warnf("SocketException schedulerArchiveDeviceUsageHistory() "+se);
		}
		catch (IOException ie) {
			logger.warnf("IOException schedulerArchiveDeviceUsageHistory() "+ie);
		}
		
		catch (Exception e){
			logger.error("ALERT201408211035 - schedulerArchiveDeviceUsageHistory() "+e);
			e.printStackTrace();
		}
		finally
		{
			if (pw != null)
				IOUtils.closeQuietly(pw);
		}
		return null;
	}
	
	@SkipValidation
	public String schedulerFetchUsageHistory()
	{
		StringBuilder sb = null;
		PrintWriter pw = null;
		try
		{
		if (schedulerFetchUsageHistory_THREAD == null)
			logger.info("schedulerFetchUsageHistory_STATUS = null");
		else 
			logger.infof("schedulerFetchUsageHistory_STATUS = %s", schedulerFetchUsageHistory_THREAD.isAlive());
		
		if (schedulerFetchUsageHistory_THREAD == null ||!schedulerFetchUsageHistory_THREAD.isAlive())
		{			
			long tstart = System.currentTimeMillis();
			logger.warn("Fetch Usage History process started!");
		
			schedulerFetchUsageHistory_THREAD = new Thread(new Runnable() {
				@Override
				public void run() {
					try
					{
						logger.warnf("INFO schedulerFetchUsageHistory_START");
						String ref_dev = JsonUtils.genServerRef();
						
						List<String> snsOrganizationsList = null;
						DeviceUsagesDAO devUsagesDAO = null;
						DeviceMonthlyUsagesDAO devMonthlyUsagesDAO = null;
						NetworksDAO networksDAO = null;
						DevicesDAO devicesDAO = null;
						
						
						SnsOrganizationsDAO snsOrganizationsDAO = new SnsOrganizationsDAO(true);
						snsOrganizationsList = snsOrganizationsDAO.getDistinctOrgList();
						if(logger.isDebugEnabled())
							logger.debug("snsOrganizationsList" + snsOrganizationsList);
		
						if( snsOrganizationsList != null )
						{
							for( String orgId : snsOrganizationsList )
							{
								devUsagesDAO = new DeviceUsagesDAO(orgId);
								devMonthlyUsagesDAO = new DeviceMonthlyUsagesDAO(orgId);
								networksDAO = new NetworksDAO(orgId);
								devicesDAO = new DevicesDAO(orgId);
								
								List<Networks> nets = networksDAO.getAllNetworks();
								if( nets != null )
								{
									for( Networks net : nets )
									{
										Calendar cal = Calendar.getInstance();
										Date d = DateUtils.getUtcDate(cal.getTime());
										d = DateUtils.offsetFromUtcTimeZone(d, DateUtils.getTimezoneFromId(Integer.valueOf(net.getTimezone())));
										cal.setTime(d);
										if( cal.get(Calendar.HOUR_OF_DAY) == 4 )
										{
											List<Devices> devices = devicesDAO.getDevicesList(net.getId());
											if( devices != null )
											{
												for( Devices dev : devices )
												{
													DevOnlineObject devOnlineObject = PoolObjectDAO.getDevOnlineObject(dev);
													if( devOnlineObject != null && devOnlineObject.isOnline() )
													{
														DeviceUsages device_usage = devUsagesDAO.getLatestDeviceUsage(dev.getId());
														
														if( device_usage == null || devMonthlyUsagesDAO.getDistinctDatetimeByDevId(dev.getNetworkId(), dev.getId()).size() <= 2	)
														{
															ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_DEV_USAGE_HIST, ref_dev, dev.getIanaId(), dev.getSn());
														}
													}
												}
											}
										}
									}
								}
							}
						}
					
					logger.warnf("INFO schedulerFetchUsageHistory_STOP");
					} catch (Exception e) {
						logger.warn("ALERT201408211034 - schedulerFetchUsageHistory()"+ e);
					}
				}
			});
			schedulerFetchUsageHistory_THREAD.start();
			logger.warnf("INFO schedulerFetchUsageHistory() starts");
		
			sb = new StringBuilder();
			long tused = (int) ((System.currentTimeMillis() - tstart) /1000);
			logger.warnf("Fetch Usage History process finished in %d sec!", tused);
			sb.append("Fetch Usage History process finished!");
			pw = response.getWriter();
			pw.write(sb.toString());
			logger.debug(sb.toString());
			IOUtils.closeQuietly(pw);
			return SUCCESS;
		}
		else
		{
			sb = new StringBuilder();	
			sb.append("Last Action schedulerFetchUsageHistory is not finished.\n");
			pw = response.getWriter();
			pw.write(sb.toString());
			logger.debug(sb.toString());

			IOUtils.closeQuietly(pw);
			return SUCCESS;
		}
		}
		catch (SocketException se){
			logger.warnf("SocketException schedulerFetchUsageHistory() "+se);
		}
		catch (IOException ie) {
			logger.warnf("IOException schedulerFetchUsageHistory() "+ie);
		}
		
		catch (Exception e){
			logger.error("ALERT201408211035 - schedulerFetchUsageHistory() "+e);
			e.printStackTrace();
		}
		finally
		{
			if (pw != null)
				IOUtils.closeQuietly(pw);
		}
		return null;
	}
	
	@SkipValidation
	public String generateDevLocationDates()
	{
		StringBuilder sb = null;
		PrintWriter pw = null;
		try
		{
		if (generateDevLocationDates_THREAD == null)
			logger.info("generateDevLocationDates_STATUS = null");
		else 
			logger.infof("generateDevLocationDates_STATUS = %s", generateDevLocationDates_THREAD.isAlive());
		
		if (generateDevLocationDates_THREAD == null ||!generateDevLocationDates_THREAD.isAlive())
		{			
			generateDevLocationDates_THREAD = new Thread(new Runnable() {
				@Override
				public void run() {
					try
					{
						logger.warnf("INFO generateDevLocationDates_START");
						List<String> snsOrganizationsList = null;
					
						SnsOrganizationsDAO snsOrganizationsDAO = new SnsOrganizationsDAO(true);
						snsOrganizationsList = snsOrganizationsDAO.getDistinctOrgList();
						logger.debug("snsOrganizationsList" + snsOrganizationsList);
								
						for (String snsOrganization:snsOrganizationsList)
						{
							DevicesDAO deviceDAO = null;
							List<Devices> devList = null;
										
							deviceDAO = new DevicesDAO(snsOrganization, true);
							devList = deviceDAO.getAllDevices();
									
							if (devList != null)
							{
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
								for (Devices dev:devList)
								{
									List<DeviceGpsLocations> loclist = new ArrayList<DeviceGpsLocations>();
								
									DeviceGpsLocationsDAO deviceLocationsDAO = new DeviceGpsLocationsDAO(snsOrganization,true);
									DeviceGpsLocationsDatesDAO deviceGpsLocaDatesDAO = new DeviceGpsLocationsDatesDAO(snsOrganization);
									NetworksDAO networkDAO = new NetworksDAO(snsOrganization,true);
									Calendar start_time = Calendar.getInstance();
									start_time.set(Calendar.HOUR_OF_DAY, 0);
									start_time.set(Calendar.MINUTE, 0);
									start_time.set(Calendar.SECOND, 0);
									start_time.set(Calendar.DAY_OF_MONTH, 1);
									Calendar end_time = Calendar.getInstance();
									end_time.setTime(start_time.getTime());
									end_time.add(Calendar.SECOND, -1);
									end_time.add(Calendar.MONTH, -5);
									Integer month = Integer.valueOf((""+end_time.getTimeInMillis()).substring(0, 10));
									logger.info("The month date for GPS:"+month);
									loclist = deviceLocationsDAO.getLocationsByDeviceIdWithMonth(dev.getId(), month);
									
									if( loclist != null && loclist.size() > 0 )
									{
										Networks net = networkDAO.getNetworksByDevId(dev.getId());
										Set<String> dateSet = new LinkedHashSet<String>();
										
										List<DeviceGpsLocationsDates> locDatesList = new ArrayList<DeviceGpsLocationsDates>();
										for( DeviceGpsLocations devloc : loclist )
										{
											Date d = DateUtils.offsetFromUtcTimeZoneId(devloc.getDatetime(), net.getTimezone());
											Calendar cal = Calendar.getInstance();
											cal.setTime(d);
											String date = cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH);
											dateSet.add(date);
											
											Date newDate = sdf.parse(date);
											DeviceGpsLocationsDatesId locDateId = new DeviceGpsLocationsDatesId();
											locDateId.setDeviceId(dev.getId());
											locDateId.setNetworkId(dev.getNetworkId());
											locDateId.setUnixtime((int)(newDate.getTime()/1000));
											DeviceGpsLocationsDates locDate = new DeviceGpsLocationsDates();
											locDate.setId(locDateId);
											locDatesList.add(locDate);
										}
										
										if (!locDatesList.isEmpty())
										{
											for (DeviceGpsLocationsDates locDateItem:locDatesList){
												deviceGpsLocaDatesDAO.saveOrUpdate(locDateItem);
											}
										}	
									}
								}
							}
						}
					
						logger.warnf("INFO generateDevLocationDates_STOP");
					} catch (Exception e) {
						logger.warn("ALERT201408211034 - generateDevLocationDates()"+ e);
					}
				}
			});
			generateDevLocationDates_THREAD.start();
			logger.warnf("INFO generateDevLocationDates() starts");
			
			sb = new StringBuilder();	
			sb.append("generateDevLocationDates process finished!.\n");
			pw = response.getWriter();
			pw.write(sb.toString());
			logger.debug(sb.toString());

			IOUtils.closeQuietly(pw);	
			return SUCCESS;
		}
		else
		{
			sb = new StringBuilder();	
			sb.append("Last Action generateDevLocationDates is not finished.\n");
			pw = response.getWriter();
			pw.write(sb.toString());
			logger.debug(sb.toString());

			IOUtils.closeQuietly(pw);
			return SUCCESS;
		}
		}
		catch (SocketException se){
			logger.warnf("SocketException generateDevLocationDates() "+se);
		}
		catch (IOException ie) {
			logger.warnf("IOException generateDevLocationDates() "+ie);
		}
		
		catch (Exception e){
			logger.error("ALERT201408211035 - generateDevLocationDates() "+e);
			e.printStackTrace();
		}
		finally
		{
			if (pw != null)
				IOUtils.closeQuietly(pw);
		}
		return null;
	}
	
	@SkipValidation
	public String schedulerProcessDpiUsages()
	{
		StringBuilder sb = null;
		PrintWriter pw = null;
		try
		{
		if (schedulerProcessDpiUsages_THREAD == null)
			logger.info("schedulerProcessDpiUsages_STATUS = null");
		else 
			logger.infof("schedulerProcessDpiUsages_STATUS = %s", schedulerProcessDpiUsages_THREAD.isAlive());
		
		if (schedulerProcessDpiUsages_THREAD == null ||!schedulerProcessDpiUsages_THREAD.isAlive())
		{			
			schedulerProcessDpiUsages_THREAD = new Thread(new Runnable() {
				@Override
				public void run() {
					try
					{
						logger.warnf("INFO schedulerProcessDpiUsages_START");
						ReportConsolidateService rcs = new ReportConsolidateService();
						rcs.startProcessDailyDpiUsages();
						rcs.startProcessMonthlyDpiUsages();
						logger.warnf("INFO schedulerProcessDpiUsages_STOP");
					} catch (Exception e){
							logger.warn("ALERT201408211034 - schedulerProcessDpiUsages() "+e);
					}
				}
			});
			schedulerProcessDpiUsages_THREAD.start();
			logger.warnf("INFO schedulerProcessDpiUsages() starts");
			sb = new StringBuilder();
			sb.append("DPI Usage process finished!");
			pw = response.getWriter();
			pw.write(sb.toString());
			logger.debug(sb.toString());
			IOUtils.closeQuietly(pw);
			return SUCCESS;
		}
		else
		{
			sb = new StringBuilder();	
			sb.append("Last Action schedulerProcessDpiUsages is not finished.\n");
			pw = response.getWriter();
			pw.write(sb.toString());
			logger.debug(sb.toString());

			IOUtils.closeQuietly(pw);
			return SUCCESS;
		}
		}
		catch (SocketException se){
			logger.warnf("SocketException schedulerProcessDpiUsages() "+se);
		}
		catch (IOException ie) {
			logger.warnf("IOException schedulerProcessDpiUsages() "+ie);
		}
		
		catch (Exception e){
			logger.error("ALERT201408211035 - schedulerProcessDpiUsages() "+e);
			e.printStackTrace();
		}
		finally
		{
			if (pw != null)
				IOUtils.closeQuietly(pw);
		}
		return null;
	}
	
	@SkipValidation
	public String schedulerPersistsLastLocation()
	{
		StringBuilder sb = null;
		PrintWriter pw = null;
		try
		{
			if (schedulerPersistsLastLocation_THREAD == null)
				logger.infof("schedulerPersistsLastLocation_STATUS = null");
			else
				logger.infof("schedulerPersistsLastLocation_STATUS = %s", schedulerPersistsLastLocation_THREAD.isAlive());
			
			long tstart = System.currentTimeMillis();
			
			
			if (schedulerPersistsLastLocation_THREAD == null || !schedulerPersistsLastLocation_THREAD.isAlive())
			{
				schedulerPersistsLastLocation_THREAD = new Thread(new Runnable() {
					@Override
					public void run() {
						try
						{
							logger.warnf("INFO schedulerPersistsLastLocation_START");
								
							SnsOrganizationsDAO snsDAO = new SnsOrganizationsDAO(true);
							DevicesDAO deviceDAO = null;
							List<String> orgIds = snsDAO.getDistinctOrgList();
							if (orgIds != null)
							{
								DeviceLastLocObject devLastLoc = null;
								for (String orgId : orgIds)
								{
									List<Networks> networksLst = OrgInfoUtils.getNetworkLst(orgId);
									deviceDAO = new DevicesDAO(orgId);
									if (networksLst != null)
									{
										for (Networks networks : networksLst)
										{
											List<Devices> devices = NetUtils.getDeviceLstByNetId(orgId, networks.getId());
											if (devices != null)
											{
												for (Devices dev : devices)
												{
													devLastLoc = new DeviceLastLocObject();
													devLastLoc.setIana_id(dev.getIanaId());
													devLastLoc.setSn(dev.getSn());
	
													devLastLoc = ACUtil.getPoolObjectBySn(devLastLoc, DeviceLastLocObject.class);
													if (devLastLoc != null)
													{
														Devices device = deviceDAO.findById(dev.getId());
														LocationList devloc = devLastLoc.getLastLocation();
														if (devloc == null)
															continue;
														device.setLast_gps_latitude(devloc.getLatitude());
														device.setLast_gps_longitude(devloc.getLongitude());
														if (devLastLoc.getLastLocation().getTimestamp() != null)
															device.setLast_gps_unixtime(devLastLoc.getLastLocation().getTimestamp().intValue());
														deviceDAO.updateDb(device); // no need to update cache, call saveOrUpdateDB instead
	
														/* update devices object */
														DevicesObject devObj = NetUtils.createDevicesObjectFromDev(device);
														if (devObj == null) {
															logger.warnf("schedulerPersistsLastLocation - Fail to update createDevicesObjectFromDev for dev %d %s",dev.getIanaId(),dev.getSn());
														} else {
															NetUtils.putDevicesObject(devObj);
														}
													}
												}
											}
										}
									}
								}
							}
							logger.warnf("INFO schedulerPersistsLastLocation_STOP");
						
						} catch (Exception e){
							logger.warn("ALERT201408211034 - PersistsLastLocation() "+e);
						}						
					}
				});
				schedulerPersistsLastLocation_THREAD.start();
	
				logger.warn("Last location persist started!");
				sb = new StringBuilder();
				sb.append("Action PersistsLastLocation finished!");
				long tused = (int) ((System.currentTimeMillis() - tstart) / 1000);
				logger.warnf("Last location persist finished in %d sec!", tused);
				pw = response.getWriter();
				pw.write(sb.toString());
				logger.debug(sb.toString());
				IOUtils.closeQuietly(pw);
				return SUCCESS;
			}
			else {
				sb = new StringBuilder();
				sb.append("Last Action PersistsLastLocation is not finished!");
				long tused = (int) ((System.currentTimeMillis() - tstart) / 1000);
				logger.warnf("Last location persist finished in %d sec!", tused);
				pw = response.getWriter();
				pw.write(sb.toString());
				logger.debug(sb.toString());
				IOUtils.closeQuietly(pw);
				return SUCCESS;
			}
		
		} catch (SocketException se){
			logger.warnf("SocketException PersistsLastLocation() "+se);
		} catch (IOException ie) {
			logger.warnf("IOException PersistsLastLocation() "+ie);
		} catch (Exception e){
			logger.error("ALERT201408211035 - PersistsLastLocation() "+e);
			e.printStackTrace();
		}
		finally
		{
			if (pw != null)
				IOUtils.closeQuietly(pw);
		}
		return null;
	}
	
	@SkipValidation
	public String schedulerDoHouseKeeping()
	{
		StringBuilder sb = null;
		PrintWriter pw = null;
		try
		{
			if (schedulerDoHouseKeeping_THREAD == null)
				logger.info("schedulerDoHouseKeeping_STATUS = null");
			else 
				logger.infof("schedulerDoHouseKeeping_STATUS = %s", schedulerDoHouseKeeping_THREAD.isAlive());
			
			if (schedulerDoHouseKeeping_THREAD == null ||!schedulerDoHouseKeeping_THREAD.isAlive())
			{			
				schedulerDoHouseKeeping_THREAD = new Thread(new Runnable() {
					@Override
					public void run() {
						try
						{
							logger.warnf("INFO schedulerDoHouseKeeping_START");
							
								
							HouseKeepUtils.doHouseKeepScheduler();
							logger.warnf("INFO schedulerDoHouseKeeping_STOP");
						} catch (Exception e) {
							logger.warn("ALERT201408211034 - schedulerDoHouseKeeping()"+ e);
						}
					}
				});
				schedulerDoHouseKeeping_THREAD.start();
				logger.warnf("INFO schedulerDoHouseKeeping() starts");
				sb = new StringBuilder();
				sb.append("House keeping completed!");
				pw = response.getWriter();
				pw.write(sb.toString());
				logger.info(sb.toString());
				IOUtils.closeQuietly(pw);
				return SUCCESS;
			}
			else
			{
				sb = new StringBuilder();	
				sb.append("Last Action schedulerDoHouseKeeping is not finished.\n");
				pw = response.getWriter();
				pw.write(sb.toString());
				logger.debug(sb.toString());
	
				IOUtils.closeQuietly(pw);
				return SUCCESS;
			}
		}
		catch (SocketException se){
			logger.warnf("SocketException schedulerDoHouseKeeping() "+se);
		} catch (IOException ie) {
			logger.warnf("IOException schedulerDoHouseKeeping() "+ie);
		} 		
		catch (Exception e){
			logger.error("ALERT201408211035 - schedulerDoHouseKeeping() "+e);
			e.printStackTrace();
		}
		finally
		{
			if (pw != null)
				IOUtils.closeQuietly(pw);
		}
		return null;
	}

	
	@SkipValidation
	public String schedulerPersistClientName()
	{
		StringBuilder sb = null;
		PrintWriter pw = null;
		try
		{
		if (schedulerPersistClientName_THREAD == null)
			logger.info("schedulerPersistClientName_STATUS = null");
		else 
			logger.infof("schedulerPersistClientName_STATUS = %s", schedulerPersistClientName_THREAD.isAlive());
		
		if (schedulerPersistClientName_THREAD == null ||!schedulerPersistClientName_THREAD.isAlive())
		{			
			schedulerPersistClientName_THREAD = new Thread(new Runnable() {
				@Override
				public void run() {

						logger.warnf("INFO schedulerPersistClientName_START");
						try
						{							
							ReportConsolidateService rcs = new ReportConsolidateService();
							long tstart = System.currentTimeMillis();
							rcs.startPersistClientInfo();
							long tused = (int) ((System.currentTimeMillis() - tstart) /1000);
							logger.warnf("persist client name completed in %d sec", tused);
							logger.warnf("INFO schedulerPersistClientName_STOP");
						} catch (Exception e) {
							logger.warn("ALERT201408211034 - schedulerPersistClientName()"+ e);
						}
				}
			});
			schedulerPersistClientName_THREAD.start();
			logger.warnf("INFO schedulerPersistClientName() starts");
			sb = new StringBuilder();
			sb.append("Persist client name process finished!");
			pw = response.getWriter();
			pw.write(sb.toString());
			logger.debug(sb.toString());
			IOUtils.closeQuietly(pw);
			return SUCCESS;
		}
		else
		{
			sb = new StringBuilder();	
			sb.append("Last Action schedulerPersistClientName is not finished.\n");
			pw = response.getWriter();
			pw.write(sb.toString());
			logger.debug(sb.toString());

			IOUtils.closeQuietly(pw);
			return SUCCESS;
		}
		}
		catch (SocketException se){
			logger.warnf("SocketException schedulerPersistClientName() "+se);
		}
		catch (IOException ie) {
			logger.warnf("IOException schedulerPersistClientName() "+ie);
		}
		catch (Exception e){
			logger.error("ALERT201408211035 - schedulerPersistClientName() "+e);
			e.printStackTrace();
		}
		finally
		{
			if (pw != null)
				IOUtils.closeQuietly(pw);
		}
		return null;
	}
	
	public static void main(String[] args)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("Asia/Hong_Kong"));
		Date d = DateUtils.getUtcDate(cal.getTime());
		d = DateUtils.offsetFromUtcTimeZone(d, "Etc/GMT-2");
		cal.setTime(d);
		System.out.println(""+cal.get(Calendar.HOUR_OF_DAY));
	}
	
}