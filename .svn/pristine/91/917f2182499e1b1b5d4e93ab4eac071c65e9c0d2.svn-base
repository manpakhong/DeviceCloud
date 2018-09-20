package com.littlecloud.pool.object.utils;

import org.jboss.logging.Logger;

import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.pool.Cluster.CACHE_ACTION;
import com.littlecloud.pool.ClusterOption;
import com.littlecloud.pool.object.DevicePendingChangesObject;

public class DevicePengingChangesUtils {

	private static Logger log = Logger.getLogger(DevicePengingChangesUtils.class);
	
	public static DevicePendingChangesObject getObject() {
		
		DevicePendingChangesObject devChange = new DevicePendingChangesObject();
		try {
			return ACUtil.<DevicePendingChangesObject> getPoolObjectBySn(devChange, DevicePendingChangesObject.class);
		} catch (Exception e) {
			log.error("getObject "+e, e);
			return null;
		}
	}

	public static boolean putObject(DevicePendingChangesObject devChange)
	{
		ClusterOption opt = new ClusterOption();
		opt.setAction(CACHE_ACTION.put);
		return doObject(devChange, opt);
	}
	
	public static boolean putIfAbsentObject(DevicePendingChangesObject devChange)
	{
		ClusterOption opt = new ClusterOption();
		opt.setAction(CACHE_ACTION.putIfAbsent);
		return doObject(devChange, opt);
	}
	
	public static boolean replaceObject(DevicePendingChangesObject devChange)
	{
		ClusterOption opt = new ClusterOption();
		opt.setAction(CACHE_ACTION.replace);
		return doObject(devChange, opt);
	}
	
	public static boolean doObject(DevicePendingChangesObject devChange, ClusterOption opt)
	{
		if (devChange==null)
		{
			log.warnf("trying to do %s devChange is null!", opt);
			return false;	
		}			
		
		try {
			if (ACUtil.<DevicePendingChangesObject> cachePoolObjectBySn(devChange, DevicePendingChangesObject.class, opt)!=null)
			{
				return true;
			}
			else
			{
				if (opt.getAction()==CACHE_ACTION.putIfAbsent)
					return true;
			}
		} catch (Exception e) {
			log.errorf("putObject exception %s", e.getMessage());
			return false;
		}
		return false;
	}
}
