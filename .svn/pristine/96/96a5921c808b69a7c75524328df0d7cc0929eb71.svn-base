package com.littlecloud.pool.control;

import org.jboss.logging.Logger;

import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.pool.Cluster;
import com.littlecloud.pool.Cluster.CACHE_ACTION;
import com.littlecloud.pool.Cluster.CacheException;
import com.littlecloud.pool.ClusterOption;
import com.littlecloud.pool.object.ACCommandObject;
import com.littlecloud.pool.object.ACLicenseObject;
import com.littlecloud.pool.object.BranchInfoObject;
import com.littlecloud.pool.object.ClientInfoObject;
import com.littlecloud.pool.object.ConfigSettingsObject;
import com.littlecloud.pool.object.DevBandwidthObject;
import com.littlecloud.pool.object.DevDetailObject;
import com.littlecloud.pool.object.DevGeoFencesObject;
import com.littlecloud.pool.object.DevInfoObject;
import com.littlecloud.pool.object.DevLocationsObject;
import com.littlecloud.pool.object.DevLocationsReportObject;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.DeviceLastLocObject;
import com.littlecloud.pool.object.DevicePendingChangesObject;
import com.littlecloud.pool.object.DevicesObject;
import com.littlecloud.pool.object.DevicesTrimObject;
import com.littlecloud.pool.object.EventLogListObject;
import com.littlecloud.pool.object.FeatureGetObject;
import com.littlecloud.pool.object.FirmwareScheduleObject;
import com.littlecloud.pool.object.IcmgPutObject;
import com.littlecloud.pool.object.NetInfoObject;
import com.littlecloud.pool.object.OrgInfoObject;
import com.littlecloud.pool.object.PoolObjectIf;
import com.littlecloud.pool.object.ProductInfoObject;
import com.littlecloud.pool.object.StationListObject;
import com.littlecloud.pool.object.StationUsageObject;
//import com.littlecloud.pool.object.StationZObject;
import com.littlecloud.pool.object.TunnelObject;
import com.littlecloud.pool.object.UpdatedClientInfoObject;
import com.littlecloud.pool.utils.PropertyService;

public class PoolControl {
	public static final Logger log  = Logger.getLogger(PoolControl.class);
	
	private static final PropertyService<PoolControl> ps = new PropertyService<PoolControl>(PoolControl.class);
	
	private static final int ONLINE_EVENT_MAX_RETRY = ps.getInteger("online_event_max_retry");
	
	private static final int DEFAULT_CACHE_EXPIRE_SECOND = 120;	
	private static final int ACCOMMAND_EXPIRE_SECOND = 5;
	
	private static final int LAST_LOC_OBJECT_EXPIRE_SECOND = 600;
	private static final int CRITICAL_CACHE_EXPIRE_SECOND = ps.getInteger("CRITICAL_CACHE_EXPIRE_SECOND");
	private static final int TUNNEL_OBJECT_EXPIRE_SECOND = 600;
	private static final int DEFAULT_MINIMUM_REFRESH_TIME = 30;
	private static final int EXPIRE_RANDOM_SECOND = 600;
	
	public static <J extends PoolObjectIf> boolean isNeedRefresh(J jsonObj)
	{
		if (jsonObj == null)
			return true;
		
		if (jsonObj.isRefreshing())
			return false;
		
		//Long now = new Date().getTime();
		Long now = DateUtils.getUtcDate().getTime();
		log.debugf("now %d - createtime %d = %d; refresh sec %d", now/1000, jsonObj.getCreateTime()/1000, (now - jsonObj.getCreateTime())/1000, DEFAULT_CACHE_EXPIRE_SECOND - DEFAULT_MINIMUM_REFRESH_TIME);
		if ( (now - jsonObj.getCreateTime())/1000 > (DEFAULT_CACHE_EXPIRE_SECOND - DEFAULT_MINIMUM_REFRESH_TIME) )
		{
			log.infof("jsonObj %s needs refresh.", jsonObj);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public static <J extends PoolObjectIf> J add(J jsonObj)
	{
		ClusterOption opt = new ClusterOption();
		opt.setAction(CACHE_ACTION.put);
		opt.setExpireInSeconds(null);
		opt.setRetry(null);
		return add(jsonObj, opt);
	}
	
	/* 1 min refresh */
	//public static <J extends PoolObjectIf> J add(J jsonObj, Integer lifetimeInSec, CACHE_ACTION action)
	@SuppressWarnings("unchecked")
	public static <J extends PoolObjectIf> J add(J jsonObj, ClusterOption opt)
	{
		J poolobject = (J)jsonObj;
		
		try {
			//log.debug("here poolobject("+poolobject.getKey()+")="+poolobject);
			if (poolobject.getCreateTime()==null || poolobject.getCreateTime()==0)
				poolobject.setCreateTime(DateUtils.getUtcDate().getTime());
			
			/* cache control for expiry */
			if (jsonObj instanceof DevOnlineObject)
			{
				poolobject = (J) Cluster.put(Cluster.CACHENAME.LittleCloudCache, poolobject.getKey(), poolobject, opt);
			}
			else if (jsonObj instanceof DevGeoFencesObject 
					|| jsonObj instanceof EventLogListObject 
					|| jsonObj instanceof DevLocationsObject || jsonObj instanceof DevLocationsReportObject
					|| jsonObj instanceof FirmwareScheduleObject
					|| jsonObj instanceof ACLicenseObject
					|| jsonObj instanceof DevicePendingChangesObject)
			{						
				poolobject = (J) Cluster.put(Cluster.CACHENAME.LittleCloudCache, poolobject.getKey(), poolobject, opt);
			}
			else if (jsonObj instanceof DevicesObject || jsonObj instanceof DevicesTrimObject || jsonObj instanceof FeatureGetObject 
					|| jsonObj instanceof StationListObject || jsonObj instanceof StationUsageObject
					|| jsonObj instanceof UpdatedClientInfoObject 
					)
			{
				//opt.setAction(CACHE_ACTION.putAsync);
				Cluster.put(Cluster.CACHENAME.LittleCloudCache, poolobject.getKey(), poolobject, opt);
			}
			else if (jsonObj instanceof ClientInfoObject || jsonObj instanceof DevDetailObject || jsonObj instanceof DevInfoObject 
					|| jsonObj instanceof IcmgPutObject || jsonObj instanceof ConfigSettingsObject
					|| jsonObj instanceof DevBandwidthObject)
			{
				opt.setExpireInSeconds(DEFAULT_CACHE_EXPIRE_SECOND);
				//opt.setAction(CACHE_ACTION.putAsync);
				Cluster.put(Cluster.CACHENAME.LittleCloudCache, poolobject.getKey(), poolobject, opt);
			}
			else if (jsonObj instanceof BranchInfoObject || jsonObj instanceof OrgInfoObject 
					|| jsonObj instanceof NetInfoObject || jsonObj instanceof ProductInfoObject)					
			{
				//int RANDOM_SEC = (int)(Math.random()*EXPIRE_RANDOM_SECOND);
				//Cluster.put(poolobject.getKey(), poolobject, CRITICAL_CACHE_EXPIRE_SECOND * RANDOM_SEC);
				poolobject = (J) Cluster.put(Cluster.CACHENAME.LittleCloudCache, poolobject.getKey(), poolobject, opt);
			}
			else if (jsonObj instanceof TunnelObject)
			{
				opt.setExpireInSeconds(TUNNEL_OBJECT_EXPIRE_SECOND);
				poolobject = (J) Cluster.put(Cluster.CACHENAME.LittleCloudCache, poolobject.getKey(), poolobject, opt);				
			}
			else if (jsonObj instanceof DeviceLastLocObject)
			{
				opt.setExpireInSeconds(LAST_LOC_OBJECT_EXPIRE_SECOND);
				//opt.setAction(CACHE_ACTION.putAsync);
				Cluster.put(Cluster.CACHENAME.LittleCloudCache, poolobject.getKey(), poolobject, opt);				
			}			
			else if (jsonObj instanceof ACCommandObject) 
			{
				opt.setExpireInSeconds(ACCOMMAND_EXPIRE_SECOND);
				Cluster.put(Cluster.CACHENAME.LittleCloudCacheRepl, poolobject.getKey(), poolobject, opt);
			}
			else
			{
				opt.setExpireInSeconds(DEFAULT_CACHE_EXPIRE_SECOND);
				poolobject = (J) Cluster.put(Cluster.CACHENAME.LittleCloudCache, poolobject.getKey(), poolobject, opt);
			}			
		} catch (CacheException e) {
			log.errorf("fail to add cache object %s", e.getMessage());
			return null;
		}
		return poolobject;		
	}
	
	public static <J extends PoolObjectIf> boolean remove(J jsonObj)
	{
		ClusterOption opt = new ClusterOption();
		opt.setAction(CACHE_ACTION.remove);
		return remove(jsonObj, opt);
	}
	
	public static <J extends PoolObjectIf> boolean remove(J jsonObj, ClusterOption opt)
	{
		J poolobject = (J)jsonObj;
		
		try {
			if (jsonObj instanceof StationListObject 
					|| jsonObj instanceof StationUsageObject
					|| jsonObj instanceof UpdatedClientInfoObject)
			{
				//opt.setAction(CACHE_ACTION.removeAsync);
				Cluster.remove(Cluster.CACHENAME.LittleCloudCache, poolobject.getKey(), opt);
			}
			else if (jsonObj instanceof ACCommandObject) 
			{
				//opt.setAction(CACHE_ACTION.removeAsync);
				Cluster.remove(Cluster.CACHENAME.LittleCloudCacheRepl, poolobject.getKey(), opt);
			}
			else
			{
				Cluster.remove(Cluster.CACHENAME.LittleCloudCache, poolobject.getKey(), opt);
			}
		} catch (CacheException e) {
			log.error("fail to remove cache object");			
			return false;
		}
		return true;		
	}
	
	@SuppressWarnings("unchecked")
	public static <J extends PoolObjectIf> J get(J poolobject)
	{
		try {
			if (poolobject instanceof ACCommandObject) 
			{
				return (J) Cluster.get(Cluster.CACHENAME.LittleCloudCacheRepl, poolobject.getKey());
			}
			else
				return (J) Cluster.get(Cluster.CACHENAME.LittleCloudCache, poolobject.getKey());
		} catch (CacheException e) {
			log.error("fail to get cache object");
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends PoolObjectIf> T getByKey(String key)
	{
		if (key == null || key.length() == 0)
		{
			log.warn("key is empty");
			throw new IllegalArgumentException("key is empty");
		}
		log.debugf("poolObject get %s", key);

		try {
			if (key.startsWith("ACCommandObjectsn_pk"))
				return (T) Cluster.get(Cluster.CACHENAME.LittleCloudCacheRepl, key);
			else
				return (T) Cluster.get(Cluster.CACHENAME.LittleCloudCache, key);
		} catch (CacheException e) {
			log.error("fail to Cluster.get",e);
			return null;
		}
	}
}
