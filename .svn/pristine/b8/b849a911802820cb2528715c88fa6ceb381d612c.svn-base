package com.littlecloud.pool;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.jboss.logging.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.health.GCMonitor;
import com.littlecloud.ac.health.ThreadPoolManager;
import com.littlecloud.control.devicechange.DeviceChangeService;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.pool.utils.PropertyLoader;

@WebListener
public class ServiceListener implements ServletContextListener {

	private static final Logger logger = Logger.getLogger(ServiceListener.class);	
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		logger.info("contextInitialing...");
				
		PropertyLoader.getInstance();		
		////ACService.getInstance(); /* start by servlet init */
	    DateUtils.loadTimeZones();
	    
	    ////GCMonitor.installGCMonitoring();
	    
		logger.warn("Initializing LittleCloudCache and LittleCloudCacheRepl");
		try {
			Cluster.init();
			Cluster cluster = Cluster.getInstance(Cluster.CACHENAME.LittleCloudCache);
			if(cluster == null)
				logger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!! LittleCloudCache Cluster startup failure !!!!!!!!!!!!!!!!!!!!!!!!!!! ");
			else
				logger.warn("LittleCloudCache Cluster is inited");
			
			Cluster clusterRepl = Cluster.getInstance(Cluster.CACHENAME.LittleCloudCacheRepl);
			if(clusterRepl == null)
				logger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!! LittleCloudCacheRepl Cluster startup failure !!!!!!!!!!!!!!!!!!!!!!!!!!! ");
			else
				logger.warn("LittleCloudCacheRepl Cluster is inited");
			logger.warn("ContextInitialized");
		} catch(Exception e) {
			logger.error("contextInitialized: e="+e, e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {		
		ACService.shutdown();
		DeviceChangeService.stopService();
		Cluster.shutdownAll();
		logger.info("contextDestroyed.");
		/*
		 * If call ThreadPoolManager.terminateAll(), ThreadPoolExecutors including 
		 * ==========================================
		 *  DeviceChangeService
		 *  WtpMsgHandlerPool
		 *  CaptivePortalMessageHandleExecutorController
		 *  MessageExecutorsController
		 *  DeviceConfigScheduler
		 * ==========================================
		 *  Would be shutdown!
		 */
		ThreadPoolManager manager = ThreadPoolManager.getInstance();
		manager.terminateAll();
		logger.info("ThreadPoolManager terminateAll() ExecutorServices [DeviceChangeService WtpMsgHandlerPool CaptivePortalMessageHandleExecutorController MessageExecutorsController DeviceConfigScheduler]");
	}
}