package com.littlecloud.pool.object.utils;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jboss.logging.Logger;

import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.dao.branch.SnsOrganizationsDAO;
import com.littlecloud.control.entity.branch.SnsOrganizations;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.pool.Cluster.CacheException;
import com.littlecloud.pool.object.BranchInfoObject;

public class BranchUtils {

	private static Logger log = Logger.getLogger(BranchUtils.class);

	private static final Integer RELOAD_IN_SEC = 86400;
	
	private static final Lock lock = new ReentrantLock();
	private static AtomicInteger lockCount = new AtomicInteger();

	private enum OP {
		SAVE, DELETE, SAVEORUPDATE
	};

	public static boolean isLoaded() {
		BranchInfoObject branchO = getInfoObject();
		if (branchO!=null)
			return branchO.isLoaded();
		else
			return false;
	}
	
	public static void saveOrUpdateSnsOrganizationsByIanaIdSn(Integer ianaId, String sn, String orgId) throws SQLException
	{
		log.infof("saveOrUpdateSnsOrganizationsByIanaIdSn %d %s %s", ianaId, sn, orgId);
		SnsOrganizations snsO = getSnsOrganizationsByIanaIdSn(ianaId, sn);
		if (snsO == null)
		{
			SnsOrganizationsDAO snsDAO = new SnsOrganizationsDAO(true);
			snsO = snsDAO.findByIanaIdSnOrgId(ianaId, sn, orgId);
			if (snsO == null)
			{
				log.errorf("snsO orgId %s dev %d %s should be found in database!!", orgId, ianaId, sn);
				return;
			}
		}
		snsO.setOrganizationId(orgId);

		BranchInfoObject branchO = getInfoObject();
		saveOrUpdateSnsOrganizations(branchO, snsO);
	}
	
	public static void deleteSnsOrganizations(SnsOrganizations snsOrg) throws SQLException
	{
		log.infof("deleteSnsOrganizations SnsOrganizations %s", snsOrg);
		BranchInfoObject branchO = getInfoObject();
		if (branchO!=null)
			deleteSnsOrganizations(branchO, snsOrg);				
	}

	public static SnsOrganizations getSnsOrganizationsByIanaIdSn(Integer ianaId, String sn)
	{
		BranchInfoObject branchO = getInfoObject();
		if (branchO != null)
			return branchO.getSnsOrgObj(ianaId, sn);
		return null;
	}

	public static Set<String> getOrgIdSet()
	{
		Set<String> orgIdSet = null;

		BranchInfoObject branchO = getInfoObject();
		orgIdSet = branchO.getOrgIdSet();

		if (orgIdSet == null)
			orgIdSet = new CopyOnWriteArraySet<String>();

		return orgIdSet;
	}

	private synchronized static void loadObjectToCache(BranchInfoObject infoObj) throws SQLException {
		SnsOrganizationsDAO snsDAO = new SnsOrganizationsDAO();
		List<SnsOrganizations> snsOrgLst = null;

		snsOrgLst = snsDAO.getAllSnsOrganizations();
		infoObj.getSnsOrg().addAll(snsOrgLst);
		if (log.isInfoEnabled())
			log.infof("BranchInfoObject - Loaded branch SnsOrganizations.size %d, branchO=%s", snsOrgLst.size(), infoObj);		

		for (SnsOrganizations sns : snsOrgLst)
			infoObj.getOrgIdSet().add(sns.getOrganizationId());

		infoObj.setLoaded(true);
		putBranchInfoObject(infoObj);
		if (log.isInfoEnabled())
			log.info("BranchInfoObject - updated");
	}

	public static BranchInfoObject getInfoObject()
	{
		boolean reload = false;
		
		BranchInfoObject branchO = new BranchInfoObject();
//		log.warn("getInfoObject called, branchO="+branchO);
	
		try {
			branchO = ACUtil.<BranchInfoObject> getPoolObjectBySn(branchO, BranchInfoObject.class);
			Long now = DateUtils.getUtcDate().getTime();
			if (branchO!=null && branchO.getCreateTime()!=null && (now - branchO.getCreateTime() > RELOAD_IN_SEC * 1000L))
			{
				log.warnf("BranchInfoObject reach time to reload");
				reload = true;
			}
			
			if (reload || branchO == null || !branchO.isLoaded())
			{
//				if(branchO != null)
//					log.warn("getPoolObjectBySn called, not loaded, branchO="+branchO);
//				else
//					log.warn("getPoolObjectBySn called, not loaded, branchO=null");
					
				if (lock.tryLock())
				{				
					try {
						log.warnf("BranchUtils.locked %d", lockCount.incrementAndGet());
						branchO = new BranchInfoObject();
						branchO = ACUtil.<BranchInfoObject> getPoolObjectBySn(branchO, BranchInfoObject.class);
						if (reload || branchO == null || !branchO.isLoaded())
						{
							branchO = new BranchInfoObject();
//							log.debug("new and load a branchO "+branchO);
							loadObjectToCache(branchO);
						}
					} catch (Exception e) {
						log.error("BranchInfoObject - get branch object exception", e);
					} finally {
						lock.unlock();
						log.warnf("BranchUtils.release %d", lockCount.decrementAndGet());
					}
				}
			}
			else
				log.info("getPoolObjectBySn called and found existing branchO");

		} catch (InstantiationException | IllegalAccessException e) {
			log.error(e.getMessage(), e);
			return null;
		} catch (Exception e) {
			log.error("BranchInfoObject - probably get branch object exception", e);
			return null;
		}
//		log.debug("branchO.isloaded="+branchO.isLoaded());
		return branchO;
	}

	public static void putBranchInfoObject(BranchInfoObject branchInfo)
	{
		try {
//			log.debug("branchO="+branchInfo);
			ACUtil.<BranchInfoObject> cachePoolObjectBySn(branchInfo, BranchInfoObject.class);
		} catch (InstantiationException | IllegalAccessException e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void removeBranchInfoObject()
	{
		try {
			BranchInfoObject branchO = new BranchInfoObject();
			ACUtil.<BranchInfoObject> removePoolObjectBySn(branchO, BranchInfoObject.class);
		} catch (InstantiationException | IllegalAccessException e) {
			log.error(e.getMessage(), e);
		}
	}

	private static boolean saveOrUpdateSnsOrganizations(BranchInfoObject snsOrgO, SnsOrganizations snsOrg)
	{
		return doSnsOrganizations(snsOrgO, snsOrg, OP.SAVEORUPDATE);
	}

	private static boolean deleteSnsOrganizations(BranchInfoObject snsOrgO, SnsOrganizations snsOrg)
	{
		return doSnsOrganizations(snsOrgO, snsOrg, OP.DELETE);
	}

	private static boolean doSnsOrganizations(BranchInfoObject snsOrgO, SnsOrganizations snsOrg, OP op)
	{
		log.debugf("BranchUtils doSnsOrganizations op %s %s %s", op, snsOrgO, snsOrg);

		boolean result = false;

		ConcurrentHashMap<String, SnsOrganizations> snsOrgMap = snsOrgO.getSnsOrg().getSnsOrgMap();

		switch (op)
		{
		// case SAVE:
		// devtLst.add(snsOrg);
		// result = true;
		// break;
		case SAVE: // for safety
		case SAVEORUPDATE:
			snsOrg = snsOrgMap.put(createKey(snsOrg), snsOrg);
			if(snsOrg !=null)
				result = true;
			break;
		case DELETE:
			snsOrg = snsOrgMap.remove(createKey(snsOrg));
			if(snsOrg !=null)
				result = true;
			break;
		default:
			log.error("unknown operation!");
			break;
		}

		putBranchInfoObject(snsOrgO);

		return result;
	}
	
	public static String createKey(SnsOrganizations snsOrg)
	{
		return createKey(snsOrg.getIanaId(),snsOrg.getSn());
	}
	
	public static String createKey(Integer iana_id, String sn)
	{
		return iana_id+"_"+sn;
	}
}
