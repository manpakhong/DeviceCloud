package com.littlecloud.pool;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachemanagerlistener.annotation.CacheStarted;
import org.infinispan.notifications.cachemanagerlistener.annotation.CacheStopped;
import org.infinispan.notifications.cachemanagerlistener.annotation.Merged;
import org.infinispan.notifications.cachemanagerlistener.annotation.ViewChanged;
import org.infinispan.notifications.cachemanagerlistener.event.Event;
import org.jboss.logging.Logger;

@Listener(sync=false)
public class ClusterListener
{
	private static Logger logger = Logger.getLogger(ClusterListener.class);
	
	@CacheStarted
	@CacheStopped
	@ViewChanged
	@Merged
	public void logEvent(Event event)
	{
		if(logger.isInfoEnabled())
			logger.info("Event= " + event.getType()
				+ " Coordinator=" + Cluster.getCacheManager().isCoordinator()
				+ " DefaultRunning=" + Cluster.getCacheManager().isDefaultRunning()
				+ " LC_CACHE isRunning=" + Cluster.getCacheManager().isRunning(Cluster.CACHENAME.LittleCloudCache.toString())
				+ " Cluster.State=" + Cluster.getMyState().toString()
				);

		if(logger.isDebugEnabled())
			Monitor.info("Event= " + event.getType()
				+ " Coordinator=" + Cluster.getCacheManager().isCoordinator()
				+ " DefaultRunning=" + Cluster.getCacheManager().isDefaultRunning()
				+ " LC_CACHE isRunning=" + Cluster.getCacheManager().isRunning(Cluster.CACHENAME.LittleCloudCache.toString())
				+ " Cluster.State=" + Cluster.getMyState().toString()
				);

		Cluster.DetermineMyState();
	}
}