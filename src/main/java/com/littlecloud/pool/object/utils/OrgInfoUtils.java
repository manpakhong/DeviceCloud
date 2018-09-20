package com.littlecloud.pool.object.utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jboss.logging.Logger;

import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.dao.NetworksDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.pool.object.NetInfoObject;
import com.littlecloud.pool.object.OrgInfoObject;
import com.littlecloud.pool.object.OrgInfoObject.NetIdMapKey;

public class OrgInfoUtils {

	private static Logger log = Logger.getLogger(OrgInfoUtils.class);

	private static final Integer RELOAD_IN_SEC = 86400;
	
	public final static Integer dummyIanaId = 9999;

	private enum OP {
		SAVE, DELETE, SAVEORUPDATE
	};

	private static final Lock lock = new ReentrantLock();
	private static AtomicInteger lockCount = new AtomicInteger();
	
	public static Networks getNetwork(String orgId, Integer netId)
	{
		List<Networks> netLst = null;

		OrgInfoObject orgO = getInfoObject(orgId);
		// netLst = Utils.<Networks> mapToList(orgO.getNetInfoLst());		
		if (orgO == null || orgO.getActNetLst() == null || orgO.getActNetLst().getNetInfoLst() == null)
			return null;		
		
		netLst = orgO.getActNetLst().getNetInfoLst();
		for (Networks net:netLst)
		{
			if (net.getId().intValue()==netId.intValue())
				return net;
		}
		
		return null;
	}
	
	public static List<Networks> getNetworkLst(String orgId)
	{
		List<Networks> netLst = null;

		OrgInfoObject orgO = getInfoObject(orgId);
		// netLst = Utils.<Networks> mapToList(orgO.getNetInfoLst());
		
		if (orgO == null || orgO.getActNetLst() == null || orgO.getActNetLst().getNetInfoLst() == null)
			return new ArrayList<Networks>();
		
//		ActiveNetLst actnet = orgO.getActNetLst();
//		if(actnet != null)
		netLst = orgO.getActNetLst().getNetInfoLst();
		//if (netLst == null)
			//netLst = new ArrayList<Networks>();

		return netLst;
	}

	// public static Networks getOrgInfoNetworks(String orgId, Integer netId)
	// {
	// NetworksDAO netDAO = null;
	// Networks net = null;
	// /
	// OrgInfoObject orgO = getOrgInfoObject(orgId);
	// if (orgO.getActNetLst().get(netId)!=null)
	// {
	// return orgO.getActNetLst().get(netId);
	// }
	// else
	// {
	// try {
	// netDAO = new NetworksDAO(orgId);
	// net = netDAO.findById(netId);
	// if (net!=null)
	// orgO.getActNetLst().add(netId, net);
	// else
	// {
	// log.errorf("Org %s unknown netId %d", orgId, netId);
	// return null;
	// }
	//
	// saveNetworks(orgId, net);
	// } catch (SQLException e) {
	// log.error(e.getMessage(), e);
	// }
	// }
	// return net;
	// }
	
	private synchronized static void loadObjectToCache(OrgInfoObject infoObj, String orgId) {
		NetworksDAO netDAO = null;
		DevicesDAO devDAO = null;
		List<Networks> netLst = null;
		List<Devices> devLst = null;

		try {
			netDAO = new NetworksDAO(orgId);
			devDAO = new DevicesDAO(orgId);
			if (log.isInfoEnabled())
				log.infof("OrgInfoObject - Loading org %s", orgId);
			netLst = netDAO.getAllNetworks();
			if (log.isInfoEnabled())
				log.infof("OrgInfoObject - Loaded org %s netLst_size %d", orgId, netLst==null?0:netLst.size());
			if (netLst != null)
			{												
				infoObj.getActNetLst().clear();
				infoObj.getActNetLst().addAll(netLst);
				if (log.isInfoEnabled())
					log.infof("OrgInfoObject - Loading org %s active networks", orgId);
				devLst = devDAO.getAllDevices();
				for (Devices dev:devLst)
				{
					//infoObj.put(dev.getIanaId(), dev.getSn(), dev.getNetworkId());
					infoObj.saveOrUpdateDevices(dev.getId(), dev.getIanaId(), dev.getSn(), dev.getNetworkId());
					
					//log.debugf("OrgInfoObject - Loaded org %s sn %d %s => net %d", orgId, dev.getIanaId(), dev.getSn(), dev.getNetworkId());
				}	
				if (log.isInfoEnabled())
					log.infof("OrgInfoObject - Loaded org %s active networks", orgId);
			}
			else
			{
				log.errorf("Org %s is unknown", orgId);
				return;
			}
								
			infoObj.setLoaded(true);
			putOrgInfoObject(infoObj);
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
		if (log.isInfoEnabled())
			log.info("NetInfoObject - updated");
	}

	public static void reload(String orgId)
	{
		getInfoObject(orgId, true);
	}
	
	public static OrgInfoObject getInfoObject(String orgId)
	{
		return getInfoObject(orgId, false);
	}
	
	private static OrgInfoObject getInfoObject(String orgId, boolean reload)
	{
		if (log.isDebugEnabled())
			log.debugf("getOrgInfoObject: ENTER %s", orgId);
		
		OrgInfoObject orgO = new OrgInfoObject(orgId);
		try {
			orgO = ACUtil.<OrgInfoObject> getPoolObjectBySn(orgO, OrgInfoObject.class);
			Long now = DateUtils.getUtcDate().getTime();
			if (orgO!=null && orgO.getCreateTime()!=null && (now - orgO.getCreateTime() > RELOAD_IN_SEC * 1000L))
			{
				log.warnf("OrgInfoObject %s reach time to reload", orgId);
				reload = true;
			}
			
			if (reload || orgO == null || !orgO.isLoaded())
			{
				if (lock.tryLock())
				{
					try {
						log.warnf("OrgInfoUtils orgId=%s locked %d", orgId, lockCount.incrementAndGet());
						orgO = new OrgInfoObject(orgId);					
						orgO = ACUtil.<OrgInfoObject> getPoolObjectBySn(orgO, OrgInfoObject.class);
						if (reload || orgO == null || !orgO.isLoaded())
						{
							orgO = new OrgInfoObject(orgId);
							loadObjectToCache(orgO, orgId);
						}
					} catch (Exception e) {
						log.error("OrgInfoUtils orgId="+orgId+", get orginfo object exception ", e);
					} finally {
						lock.unlock();
						log.warnf("OrgInfoUtils orgId=%s released %d", orgId, lockCount.decrementAndGet());
					}
				}
			}
			
//			if (!orgO.isLoaded())
//			{
//				loadObjectToCache(orgO, orgId);
//			}
		} catch (InstantiationException | IllegalAccessException e) {
			log.error(e.getMessage(), e);
			return null;
		} catch (Exception e) {
			log.error("Exception ", e);
			return null;
		}
		if (log.isDebugEnabled())
			log.debugf("getOrgInfoObject: EXIT");
		return orgO;
	}
	
	public static void putOrgInfoObject(OrgInfoObject orgInfo)
	{
		try {
			ACUtil.<OrgInfoObject> cachePoolObjectBySn(orgInfo, OrgInfoObject.class);
		} catch (InstantiationException | IllegalAccessException e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void removeOrgInfoObject(OrgInfoObject orgInfo)
	{
		try {
			ACUtil.<OrgInfoObject> removePoolObjectBySn(orgInfo, OrgInfoObject.class);
		} catch (InstantiationException | IllegalAccessException e) {
			log.error(e.getMessage(), e);
		}
	}

	public static boolean saveNetworks(String orgId, Networks net)
	{
		if (net == null || net.getId() == null)
		{
			log.error("Given network info is incomplete.");
			return false;
		}

		OrgInfoObject orgO = getInfoObject(orgId);
		if (orgO == null) {
			log.error("Given orgId does not exist");
			return false;
		}

		return doNetworks(orgO, orgId, net, OP.SAVE);
	}

	public static boolean saveOrUpdateNetworks(String orgId, Networks net)
	{
		if (net == null || net.getId() == null)
		{
			log.error("Given network info is incomplete.");
			return false;
		}

		OrgInfoObject orgO = getInfoObject(orgId);
		if (orgO == null) {
			log.error("Given orgId does not exist");
			return false;
		}

		if (updateNetworks(orgO, orgId, net))
		{
			log.debugf("net %d is added/updated", net.getId());
			return true;
		}
		else
		{
			log.errorf("net %d add/update failure", net.getId());
			return false;
		}
	}

	public static boolean deleteNetworks(String orgId, Networks net)
	{
		if (net == null || net.getId() == null)
		{
			log.error("Given network info is incomplete.");
			return false;
		}

		OrgInfoObject orgO = getInfoObject(orgId);
		if (orgO == null) {
			log.error("Given orgId does not exist");
			return false;
		}

		if (!deleteNetworks(orgO, orgId, net))
			log.warnf("network %d does not exist", net.getId());

		return true;
	}

	private static boolean updateNetworks(OrgInfoObject orgO, String orgId, Networks net)
	{
		return doNetworks(orgO, orgId, net, OP.SAVEORUPDATE);
	}

	private static boolean deleteNetworks(OrgInfoObject orgO, String orgId, Networks net)
	{
		return doNetworks(orgO, orgId, net, OP.DELETE);
	}

	private static boolean doNetworks(OrgInfoObject orgO, String orgId, Networks net, OP op)
	{
		boolean result = false;
		int index = 0;

		CopyOnWriteArrayList<Networks> netLst = orgO.getActNetLst().getNetInfoLst();

		switch (op)
		{
//		case SAVE:
//			netLst.add(net);
//			result = true;
//			break;
		case SAVE:
		case SAVEORUPDATE:	// for safety
			for (Networks netFind : netLst)
			{
				if (netFind.getId().intValue()==net.getId().intValue())
				{
					netLst.remove(index);					
				}
				else
				{
					index++;
				}
			}
			netLst.add(net);
			result = true;
			break;
		case DELETE:
			for (Networks netFind : netLst)
			{
				if (netFind.getId().intValue()==net.getId().intValue())
				{
					netLst.remove(index);
					result = true;
				}
				else
				{
					index++;
				}
			}
			break;
		default:
			log.error("unknown operation!");
			break;
		}

		orgO.getActNetLst().setNetInfoLst(netLst);
		putOrgInfoObject(orgO);

		return result;
	}
	
	
	/* NetIdMap - COULD ONLY BE CALLED BY NETUTILS!!!!!!!!! */
	public static boolean saveOrUpdateDevices(String orgId, Integer devId, Integer ianaId, String sn, Integer netId) {
		OrgInfoObject orgO = getInfoObject(orgId);
		if (orgO == null) {
			log.error("Given orgId does not exist");
			return false;
		}

		/* only update if netId is changed */
//		log.infof("orgO.lookupDevNet(%d, %s) netId %d", ianaId, sn, netId);
//		Integer lookUp = orgO.lookupDevices(ianaId, sn);		
//		if (lookUp != null && lookUp.equals(netId))
//		{
		if (log.isInfoEnabled())
			log.infof("orgO.saveOrUpdateDevices(%d, %s) netId %d", ianaId, sn, netId);
		orgO.saveOrUpdateDevices(devId, ianaId, sn, netId);
		putOrgInfoObject(orgO);
//		}
		
		return true;
	}
	
	/* NetIdMap - COULD ONLY BE CALLED BY NETUTILS!!!!!!!!! */
	public static boolean deleteDevices(String orgId, Integer devId, Integer ianaId, String sn)
	{
		OrgInfoObject orgO = getInfoObject(orgId);
		if (orgO == null) {
			log.error("Given orgId does not exist");
			return false;
		}
		orgO.deleteDevices(devId, ianaId, sn);
		putOrgInfoObject(orgO);
		return true;
	}
	
	public static Integer lookupDevicesNetIdBySn(String orgId, Integer ianaId, String sn)
	{
		OrgInfoObject orgO = getInfoObject(orgId);
		if (orgO == null) {
			log.error("Given orgId does not exist");
			return -1;
		}
		return orgO.lookupDevices(ianaId, sn);
	}
	
	public static Integer lookupDevicesNetIdByDevId(String orgId, Integer devId)
	{
		Integer result = null;
		
		if (log.isDebugEnabled())
			log.debugf("lookupDevicesNetIdByDevId ENTER %d", devId);
		OrgInfoObject orgO = getInfoObject(orgId);
		if (orgO == null) {
			log.error("Given orgId does not exist");
			return -1;
		}
		if (log.isDebugEnabled())
			log.debugf("before lookupDevices %d", devId);
		result = orgO.lookupDevices(devId);
		
		if (log.isDebugEnabled())
			log.debugf("lookupDevicesNetIdByDevId EXIT %d", devId);
		return result;
	}
}
