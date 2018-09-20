package com.littlecloud.pool;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.jboss.logging.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.ReportConsolidateService;
import com.littlecloud.pool.object.ClusterMemberInfoObject;

@Listener(sync=false)
public class CacheReplListener
{
	private static Logger logger = Logger.getLogger(CacheReplListener.class);
	private static final int MAX_RAND_SLEEP = 5000;
	private static final int MAX_EVENT_MAP = 100;
	private static Map<String, CacheEntryEvent<String, String>> eventMap = Collections.synchronizedMap(new LinkedHashMap<String, CacheEntryEvent<String, String>>(MAX_EVENT_MAP) {
		@Override
		protected boolean removeEldestEntry(Map.Entry<String, CacheEntryEvent<String, String>> eldest)
		{
			return this.size() > MAX_EVENT_MAP;   
		}
	});
	  
	@CacheEntryCreated
	public void created(CacheEntryCreatedEvent<String, String> event) {
		if( event.isPre() == false )
		{
			String evtKey = event.getKey();
			if(evtKey.startsWith("QUEUE_"))
				return;

			if(logger.isDebugEnabled())
				logger.debugf("CacheEntryCreated[%s] EntryEvent=%s (%s,?)", this, event.getType().name(), evtKey);
			
			// to prevent double fired
			if(eventMap.get(evtKey) != null) {
				if(logger.isDebugEnabled())
					logger.debugf("CacheEntryCreated[%s] Skip event, EntryEvent=%s (%s,?)", this, event.getType().name(), evtKey);
				return;
			}  

			eventMap.put(evtKey, event);
				
			if (evtKey.startsWith("ACCommandObjectsn_pk"))
			{
				ACService.fetchCommand(evtKey);
				if(logger.isDebugEnabled())
					logger.debug("created repl: " + Cluster.getUniqueLocalId() + " fetch command " + evtKey);
				return;
			} else if(evtKey.startsWith("EVENT_PERSISTCACHE_")) {
				Thread t = new Thread(new Runnable() {
					public void run()
					{
						ReportConsolidateService rcs = new ReportConsolidateService();
						long tstart = System.currentTimeMillis();
						Random rand = new Random();
						try {
							java.lang.Thread.sleep(rand.nextInt(MAX_RAND_SLEEP));
						} catch(Exception e) {}
						rcs.StartPersistReportFromCache();
						long tused = (System.currentTimeMillis() - tstart)/1000;
						logger.warnf("EVENT_PERSISTCACHE: Persist cache to db finished in %d sec.", tused);
					}
				});
				logger.warnf("EVENT_PERSISTCACHE: Persist cache to db started, event=%s", evtKey);
				t.start();
			} else if(evtKey.startsWith("EVENT_GETKEYS_")) {
				Thread t = new Thread(new Runnable() {
					public void run()
					{
						try {
							Set<String> objkeys = Cluster.getKeys(Cluster.CACHENAME.LittleCloudCache);
							Set<String> mykeys = new HashSet<String>(objkeys.size());
							mykeys.addAll(objkeys);
							Cache<String, Object> cache = Cluster.getCache(Cluster.CACHENAME.LittleCloudCache);
							String keyName ="CACHE_"+ACService.getServerName();
							ClusterMemberInfoObject clusterInfo = new ClusterMemberInfoObject();
							clusterInfo.setObjkeys(mykeys);
							clusterInfo.setTotalobj(mykeys.size());
							cache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(keyName, clusterInfo);
							logger.warnf("EVENT_GETKEYS: %s, size=%d", keyName, mykeys.size());
						} catch(Exception e) {
							logger.error("EVENT_GETKEYS Exception, e="+e, e);
						}
					}
				});
				logger.warnf("EVENT_GETKEYS: called, event=%s", evtKey);
				t.start();
			}
		}
	}
	
	@CacheEntryModified
	public void modified(CacheEntryModifiedEvent<String, String> event) {
		if( event.isPre() == false )
		{
			String evtKey = event.getKey();
			if(evtKey.startsWith("QUEUE_"))
				return;

			if(logger.isDebugEnabled())
				logger.debugf("CacheEntryModfied[%s] EntryEvent=%s (%s,?)", this, event.getType().name(), evtKey);
			
			// to prevent double fired
			if(eventMap.get(evtKey) != null) {
				if(logger.isDebugEnabled())
					logger.debugf("CacheEntryModified[%s] Skip event, EntryEvent=%s (%s,?)", this, event.getType().name(), evtKey);
				return;
			}
			
			eventMap.put(evtKey, event);
			
			if (evtKey.startsWith("ACCommandObjectsn_pk"))
			{
				ACService.fetchCommand(evtKey);
				if(logger.isDebugEnabled())
					logger.debug("created repl: " + Cluster.getUniqueLocalId() + " fetch command " + evtKey);
				return;
			}else if(evtKey.startsWith("EVENT_PERSISTCACHE")) {
				Thread t = new Thread(new Runnable() {
					public void run()
					{
						ReportConsolidateService rcs = new ReportConsolidateService();
						long tstart = System.currentTimeMillis();
						Random rand = new Random();
						try {
							java.lang.Thread.sleep(rand.nextInt(MAX_RAND_SLEEP));
						} catch(Exception e) {}
						rcs.StartPersistReportFromCache();
						long tused = (System.currentTimeMillis() - tstart)/1000;
						logger.warnf("EVENT_PERSISTCACHE: Persist cache to db finished in %d sec.", tused);
					}
				});
				logger.warnf("EVENT_PERSISTCACHE: Persist cache to db started, event=%s", evtKey);
				t.start();
			} else if(evtKey.startsWith("EVENT_GETKEYS")) {
				Thread t = new Thread(new Runnable() {
					public void run()
					{
						try {
							Set<String> objkeys = Cluster.getKeys(Cluster.CACHENAME.LittleCloudCache);
							Set<String> mykeys = new HashSet<String>(objkeys.size());
							mykeys.addAll(objkeys);
							Cache<String, Object> cache = Cluster.getCache(Cluster.CACHENAME.LittleCloudCache);
							String keyName ="CACHE_"+ACService.getServerName();
							ClusterMemberInfoObject clusterInfo = new ClusterMemberInfoObject();
							clusterInfo.setObjkeys(mykeys);
							clusterInfo.setTotalobj(mykeys.size());
							cache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES).put(keyName, clusterInfo);
							logger.warnf("EVENT_GETKEYS: %s, size=%d", keyName, mykeys.size());
						} catch(Exception e) {
							logger.error("EVENT_GETKEYS Exception, e="+e, e);
						}
					}
				});
				logger.warnf("EVENT_GETKEYS: called, event=%s", evtKey);
				t.start();
			}
		}
	}
}