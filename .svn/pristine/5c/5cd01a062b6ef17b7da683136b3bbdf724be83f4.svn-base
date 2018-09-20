package com.littlecloud.ac.util;

import java.util.Iterator;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.infinispan.Cache;
import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.littlecloud.ac.json.model.command.QueryInfo;
import com.littlecloud.pool.Cluster;
import com.littlecloud.pool.Cluster.CACHE_ACTION;
import com.littlecloud.pool.Cluster.CacheException;
import com.littlecloud.pool.ClusterOption;
import com.littlecloud.pool.control.PoolControl;
import com.littlecloud.pool.object.BranchInfoObject;
import com.littlecloud.pool.object.NetInfoObject;
import com.littlecloud.pool.object.OrgInfoObject;
import com.littlecloud.pool.object.PoolObjectIf;
import com.littlecloud.pool.object.ProductInfoObject;

public class ACUtil {
	private static final Logger log = Logger.getLogger(ACUtil.class);

	public static final String DATA_TAG = "data";
	
	public static <J extends PoolObjectIf> boolean isNeedRefresh(J jsonObj)
	{
		return PoolControl.isNeedRefresh(jsonObj);
	}
	
	private enum UTIL_OBJECT {
		NetInfoObject, OrgInfoObject, BranchInfoObject, ProductInfoObject
	}
	
	public static <T extends PoolObjectIf> T getPoolObjectByKey(String key)
	{
		if (key == null || key.length() == 0)
		{
			log.warn("poolObject is empty or key is null");
			throw new IllegalArgumentException("poolObject is empty or key is null");
		}
		log.debugf("poolObject get %s",key);

		return PoolControl.<T> getByKey(key);
	}
	
	public static <T extends PoolObjectIf> T getPoolObjectBySn(T poolObject, Class<?> clazzT) throws InstantiationException, IllegalAccessException
	{
		if(poolObject == null)
			throw new IllegalArgumentException("getPoolObjectBySn: poolObject is null");

		if(poolObject.getKey() == null)
			throw new IllegalArgumentException("getPoolObjectBySn: poolObject's key is null");

		if(poolObject.getKey().length() == 0)
			throw new IllegalArgumentException("getPoolObjectBySn: poolObject's len = 0");

		return PoolControl.<T> get(poolObject);
	}

	public static <T extends PoolObjectIf> T cachePoolObjectBySn(T poolObject, Class<?> clazzT) throws InstantiationException, IllegalAccessException
	{
		ClusterOption opt = new ClusterOption();
		opt.setAction(CACHE_ACTION.put);
		return cachePoolObjectBySn(poolObject, clazzT, opt);
	}
	
	public static <T extends PoolObjectIf> void cachePoolObjectBySnWithLifetime(T poolObject, Class<?> clazzT, Integer lifetime) throws InstantiationException, IllegalAccessException
	{
		ClusterOption opt = new ClusterOption();
		opt.setAction(CACHE_ACTION.put);
		opt.setExpireInSeconds(lifetime);
		cachePoolObjectBySn(poolObject, clazzT, opt);
	}
	
	public static <T extends PoolObjectIf> T cachePoolObjectBySn(T poolObject, Class<?> clazzT, ClusterOption opt) throws InstantiationException, IllegalAccessException
	{
		if(poolObject == null)
			throw new IllegalArgumentException("cachePoolObjectBySn: poolObject is null "+opt);

		if(poolObject.getKey() == null)
			throw new IllegalArgumentException("cachePoolObjectBySn: poolObject's key is null "+opt);

		if(poolObject.getKey().length() == 0)
			throw new IllegalArgumentException("cachePoolObjectBySn: poolObject's len = 0 "+opt);

		return PoolControl.<T> add(poolObject, opt);
	}

	public static <T extends PoolObjectIf> void removePoolObjectBySn(T poolObject, Class<?> clazzT) throws InstantiationException, IllegalAccessException
	{
		if(poolObject == null)
			throw new IllegalArgumentException("removePoolObjectBySn: poolObject is null");

		if(poolObject.getKey() == null)
			throw new IllegalArgumentException("removePoolObjectBySn: poolObject's key is null");

		if(poolObject.getKey().length() == 0)
			throw new IllegalArgumentException("removePoolObjectBySn: poolObject's len = 0");
		
		if (!PoolControl.<T> remove(poolObject)) {
//			log.error("fail to remove poolObject");
		}
		else {
//			log.debug("poolObject removed");
		}
	}

	public static <T extends PoolObjectIf> void cacheDataBySn(QueryInfo<Object> info, Class<?> clazzT) throws InstantiationException, IllegalAccessException
	{
		Gson gson = new Gson();

		/* assign json to object */
		JSONObject object = JSONObject.fromObject(info);
		JSONObject data = object.getJSONObject(DATA_TAG);
		log.debugf(DATA_TAG + "=%s", data.toString());

		T poolObject = (T) gson.fromJson(data.toString(), clazzT);	// Note: this line will convert number field from null to zero!

		if (poolObject == null)
		{
			/* create empty object for caching, let FE/WS knows the query is completed. */
			log.debug("create empty cache object");
			poolObject = (T) clazzT.newInstance();
		}

		/************ specific handling for pepvpn serial number *****************/
		//poolObject.setKey(info.getIana_id(), info.getSn());
		/*************************************************************************/

		// if (!PepvpnControl.add(pepvpnStatusObject)) {
		if (PoolControl.<T> add(poolObject)!=null) {
//			log.error("fail to add poolObject obj"); 	// already logged
		}
		else {
			log.debug("poolObject added");

			//printAllStatusCaches();
		}
	}
	
	public static int getStatusCaches(String type, StringBuilder sb) {
		return getStatusCaches(type, sb, null);
	}
	
	public static int evictAllCaches(StringBuilder sb) {
		int count = 0;
		
		count += evictAllCaches(Cluster.CACHENAME.LittleCloudCache, sb);
		count += evictAllCaches(Cluster.CACHENAME.LittleCloudCacheRepl, sb);
		
		return count;
	}
	
	private static int evictAllCaches(Cluster.CACHENAME cacheName, StringBuilder sb) {
		// ***************************
		int total = 0;
		int count = 0;
		int evicted = 0;
		Cache cache = Cluster.getCache(cacheName);
		sb.append("***************** evictAllCaches: "+cacheName+" *****************<br>");
		
		Iterator<String> itr = null;
		itr = cache.keySet().iterator();
		total = cache.keySet().size();
		while (itr.hasNext())
		{				
			String key = (String) itr.next();				
			try
			{					
				if (cache.containsKey(key))
					count++;
				else
					evicted++;
				
			} catch (NullPointerException e)
			{
				log.error("evictAllCaches() - key entry " + key + " does not exist.");
			}
			
		}
		
		sb.append(StringUtils.join("origin:", total, " - new:", count, " = evicted:", evicted, "<br>"));
		
		sb.append("***************************************************<br>");

		return evicted;
	}
	
	public static int getStatusCaches(String type, StringBuilder sb, Integer record_count) {
		int maxcount = 100;
		
		if (record_count!=null)
			maxcount = record_count;
		
		// ***************************
		int count = 0;

		sb.append("***************** getStatusCaches *****************<br>");
		try {
			Iterator<String> itr = null;
			if (type!=null && type.startsWith("ACCommandObject"))
				itr = Cluster.getKeys(Cluster.CACHENAME.LittleCloudCacheRepl).iterator();
			else
				itr = Cluster.getKeys(Cluster.CACHENAME.LittleCloudCache).iterator();
			
			while (itr.hasNext())
			{				
				String key = (String) itr.next();				
				if (type.compareToIgnoreCase("all")==0 || key.contains(type))
				{
					try
					{
						if (count<maxcount)
						{
							Object value = Cluster.get(Cluster.CACHENAME.LittleCloudCache, key);
							sb.append(key);
							sb.append("|");
							sb.append(value);
							sb.append("\n<hr>");
						}
						count++;
					} catch (NullPointerException e)
					{
						log.error("printAllStatusCaches() - key entry " + key + " does not exist.");
					}
				}
			}
			
			if (count>=maxcount)
				sb.append("...\n<hr>");
		} catch (CacheException e) {
			log.error("getStatusCaches() - CacheExceptioin " + e);
		}
		sb.append("***************************************************<br>");

		return count;
	}
	
	public static int deleteStatusCaches(String type, StringBuilder sb) {
		// ***************************
		int count = 0;

		sb.append("***************** deleteStatusCaches *****************<br>");
		try {
			Iterator<String> itr = null;
			if (type!=null && type.startsWith("ACCommandObject"))
				itr = Cluster.getKeys(Cluster.CACHENAME.LittleCloudCacheRepl).iterator();
			else
				itr = Cluster.getKeys(Cluster.CACHENAME.LittleCloudCache).iterator();
			while (itr.hasNext())
			{
				String key = (String) itr.next();				
				if (key.contains(type))
				{
					try
					{						
						ClusterOption opt = new ClusterOption();
						//opt.setAction(CACHE_ACTION.removeAsync);
						Cluster.remove(Cluster.CACHENAME.LittleCloudCache, key, opt);
						sb.append(key);
						sb.append("\n<hr>");
						count++;
					} catch (NullPointerException e)
					{
						log.error("deleteStatusCaches() - key entry " + key + " is not removed.", e);
					}
				}
			}
		} catch (CacheException e) {
			log.error("deleteStatusCaches() - CacheExceptioin " + e);
		}
		sb.append("***************************************************<br>");

		return count;
	}
	
	public static int reloadStatusCaches(String type, StringBuilder sb) {
		// ***************************
		UTIL_OBJECT reloadType = null;
		
		for (UTIL_OBJECT objType:UTIL_OBJECT.values())
		{
			if (type.equalsIgnoreCase(objType.toString()))
				reloadType = objType;
		}
		
		if (reloadType == null)
			return 0;
				
		int count = 0;

		sb.append("***************** reloadStatusCaches *****************<br>");
		try {
			Iterator<String> itr = null;			
			if (type!=null && type.startsWith("ACCommandObject"))
				itr = Cluster.getKeys(Cluster.CACHENAME.LittleCloudCacheRepl).iterator();
			else
				itr = Cluster.getKeys(Cluster.CACHENAME.LittleCloudCache).iterator();
				
			while (itr.hasNext())
			{				
				String key = (String) itr.next();				
				if (key.contains(type))
				{
					try
					{						
						switch(reloadType)
						{
						case NetInfoObject:
							NetInfoObject netObj = ACUtil.getPoolObjectByKey(key);
							if (netObj!=null)
							{
								netObj.setLoaded(false);
								ACUtil.cachePoolObjectBySn(netObj, BranchInfoObject.class);
							}
							break;
						case OrgInfoObject:
							OrgInfoObject orgObj = ACUtil.getPoolObjectByKey(key);
							if (orgObj!=null)
							{
								orgObj.setLoaded(false);
								ACUtil.cachePoolObjectBySn(orgObj, BranchInfoObject.class);
							}
							break;
						case BranchInfoObject:
							BranchInfoObject branchObj = ACUtil.getPoolObjectByKey(key);
							if (branchObj!=null)
							{
								branchObj.setLoaded(false);
								ACUtil.cachePoolObjectBySn(branchObj, BranchInfoObject.class);
							}
							break;
						case ProductInfoObject:
							ProductInfoObject prodObj = ACUtil.getPoolObjectByKey(key);
							if (prodObj!=null)
							{
								prodObj.setLoaded(false);
								ACUtil.cachePoolObjectBySn(prodObj, ProductInfoObject.class);
							}
							break;
						}

						sb.append(key);
						sb.append("\n<hr>");
						count++;
					} catch (NullPointerException | InstantiationException | IllegalAccessException e)
					{
						log.error("reloadStatusCaches() exception - key entry " + key + ".", e);
					}
				}
			}
		} catch (CacheException e) {
			log.error("reloadStatusCaches() - CacheExceptioin " + e);
		}
		sb.append("***************************************************<br>");

		return count;
	}
}
