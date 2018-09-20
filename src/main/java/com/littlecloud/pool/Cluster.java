package com.littlecloud.pool;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.transaction.TransactionManager;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.remoting.transport.Address;
import org.jboss.logging.Logger;

import com.littlecloud.pool.utils.PropertyService;
import com.littlecloud.pool.utils.XmlParser;

public class Cluster {
	private static Logger log = Logger.getLogger(Cluster.class);

	private static PropertyService<Cluster> ps = new PropertyService<Cluster>(Cluster.class);
	
	public static enum CACHENAME {
		LittleCloudCache, LittleCloudCacheRepl
	}
	
	public static final String NODE_CONTROL_HASHKEY = "KEY";
	private static final long TIMEWAIT_BETWEEN_EACHRETRY = 3000; // wait for 10s to assume data sync is completed
	private static final int DEFAULT_MAXRETRY = 1;
	private static Hashtable<CACHENAME, Cluster> clusterTbl = new Hashtable<CACHENAME, Cluster>();
	private static EmbeddedCacheManager cacheManager = null;
	private static Object myListener = null;
	private static boolean bRestarting = false;

	private CACHENAME cacheId;
	private Cache<String, Object> cache = null;
	private Object cacheListener =  null;
	
	public static enum CACHE_ACTION {
		put, putIfAbsent, replace, 
		remove 
		//putAsync,
		//removeAsync
	}
	
	public static final List<String> CONFIG_MEMBERS = initCONFIG_MEMBERS();
	
	private static List<String> initCONFIG_MEMBERS() {
		
		List<String> result = new ArrayList<String>();
		
		String jgPath = System.getProperty("jgroups.config");
		if (jgPath == null || jgPath.isEmpty()) {			
			log.error("jgroups.config is not specified");
		}
		String xPath = ps.getString("XPATH_INITIAL_HOSTS");		
		String[] servers = XmlParser.getXmlPath(jgPath, xPath).split(":")[1].split(",");
		if (servers==null || servers.length==0)
		{
			log.error("jgroups.config problems");
		}
		
		for (String s:servers)
		{
			if (s.indexOf("[")>0)
				s = s.substring(0, s.indexOf("["));
			result.add(s);
		}
		log.warnf("CONFIG_MEMBERS=%s", result);
		
		return result;
	}

	/* record state of server startup. one way */
	public enum ClusterState {
		Initial, Standalone, ClusterMember
	};

	private static ClusterState myState = ClusterState.Initial;
	
	public synchronized static Cluster getInstance(CACHENAME myid) {
		Cluster cluster = clusterTbl.get(myid);
		if (cluster == null) {
			try {
				cluster = new Cluster(myid);
				clusterTbl.put(myid, cluster);
				log.warn("Initialized Cluster=" + myid);
			} catch (Exception e) {
				log.error("Failed to init Cluster=" + myid + ", e=" + e, e);
				return null;
			}
		}
		return cluster;
	}

	private Cluster(CACHENAME myid) {
		cacheId = myid;
		log.warn("Cluster: myid = " + myid);

		cache = cacheManager.getCache(myid.toString());
		log.warnf("Cluster<%s>: got Cache = %s", myid, cache);
		
		if(myid == Cluster.CACHENAME.LittleCloudCacheRepl) {
			cacheListener = new CacheReplListener();
		} else {
			cacheListener = new CacheListener();
		}
		cache.addListener(cacheListener);
		
		log.warnf("Cluster<%s>: added Cache Listener = %s", myid, cacheListener);
		
		try {
			ClusterOption opt = new ClusterOption();
			opt.setAction(CACHE_ACTION.put);
			
			put("PERMANENT_OBJECT_KEY", "PERMANENT_OBJECT_VALUE", opt);
		} catch (CacheException e) {
			log.error(e.getMessage(), e);
		}

		log.warnf("Cluster<%s>: -----Cache config-----\n%s", myid, cache.getCacheConfiguration().toString());
	}
	
	public static void init() throws IOException {
		String configFile = System.getProperty("cluster.config");
		if (configFile == null) {
			log.error("Cluster: cluster.config is not specified");
			return;
		}
		
		log.warnf("Cluster: Loading cluster config = %s", configFile);
		cacheManager = new DefaultCacheManager(configFile);
		myListener = new ClusterListener();
		cacheManager.addListener(myListener);
		
		for (CACHENAME c:CACHENAME.values())
		{
			getInstance(c);
		}
		
		log.warnf("Cluster: added Cluster Listener = %s", myListener);
	}

	public class CacheException extends Exception
	{
		private static final long serialVersionUID = -3085212819645489287L;

		public CacheException(String msg) {
			super(msg);
		}
	}

	public static TransactionManager getTransactionManager(CACHENAME myid) {
		Cluster cluster = clusterTbl.get(myid);
		if(cluster != null)
			return cluster.getTransactionManager();
		else {
			log.warnf("getTransactionManager: cluster==null for id=%s", myid);
			return null;
		}
	}
	
	public TransactionManager getTransactionManager()
	{
		return cache.getAdvancedCache().getTransactionManager();
	}

	public static EmbeddedCacheManager getCacheManager()
	{
		return cacheManager;
	}
	
	public static String getUniqueLocalId()
	{
		String uid = null;
		try
		{
			InetAddress addr = InetAddress.getLocalHost();
			uid = addr.getHostName();
		} catch (UnknownHostException ex)
		{
			log.error("Hostname can not be resolved");
		}

		UUID uuid = UUID.randomUUID();
		if (uid == null || uid.length() == 0)
			uid = uuid.toString();
		else
			uid += uuid.toString();

		return uid;
	}

	public static boolean isbRestarting() {
		return bRestarting;
	}

	public static String getClusterName() {
		return cacheManager.getClusterName();
	}

	public static Address getClusterAddress() {
		return cacheManager.getAddress();
	}

	public static Object get(CACHENAME myid, String arg0) throws CacheException {
		Cluster cluster = clusterTbl.get(myid);
		if(cluster != null)
			return cluster.get(arg0);
		else {
			log.warnf("get: cluster==null for id=%s, arg0=%s", myid, arg0);
			return null;
		}
	}
	
	public Object get(String arg0) throws CacheException {
		boolean bSuccess = false;
		int tryCount = 0;
		Object v = null;
		while (!bSuccess && tryCount < DEFAULT_MAXRETRY)
		{
			try {
				v = cache.get(arg0);
				bSuccess = true;
			} catch (NullPointerException e) {
				if(log.isInfoEnabled())
					log.infof("get: cluster<%s> (%d) - no record found in cache", cacheId, tryCount);
				bSuccess = true;
			} catch (Exception e) {
				log.warnf("get: cluster<%s> (%d) - Cache exception retry for get, e=%s", cacheId, tryCount, e);
				bSuccess = false;
				try {
					Thread.sleep(TIMEWAIT_BETWEEN_EACHRETRY);
				} catch (InterruptedException e1) {
					log.error("get: e="+e1.getMessage());
				}
			}
			tryCount++;
		}

		if (!bSuccess) {
			throw new CacheException("cluster <" + cacheId + "> (" + tryCount + ") - Cache exception for get on key " + arg0);
		}
		return v;
	}

	public static Set<String> getKeys(CACHENAME myid) throws CacheException {
		Cluster cluster = clusterTbl.get(myid);
		if(cluster != null)
			return cluster.getKeys();
		else {
			log.warnf("getKeys: cluster==null for id=%s", myid);
			return null;
		}
	}

	public Set<String> getKeys() throws CacheException {
		boolean bSuccess = false;
		int tryCount = 0;
		Set<String> set = null;
		while (!bSuccess && tryCount < DEFAULT_MAXRETRY)
		{
			try {
				set = cache.keySet();
				bSuccess = true;
			} catch (Exception e) {
				log.warn("getKeys: cluster(" + tryCount + ") - Cache exception retry for getKeys (" + tryCount + ") - " + e.getMessage());
				bSuccess = false;
				try {
					Thread.sleep(TIMEWAIT_BETWEEN_EACHRETRY);
				} catch (InterruptedException e1) {
					log.error("getKeys: e=" + e1.getMessage());
				}
			}
			tryCount++;
		}

		if (!bSuccess)
			throw new CacheException("cluster(" + tryCount + ") - Cache exception for get all keys");

		return set;
	}

	public void refresh(String arg0, int expireInSeconds) throws CacheException
	{
		boolean bSuccess = false;
		int tryCount = 0;
		//TransactionManager tm = cache.getAdvancedCache().getTransactionManager();

		Object arg1;
		try {
			arg1 = get(arg0);
		} catch (CacheException e) {
			log.error("refresh: ex=" + e, e);
			throw e;
		}

		while (!bSuccess && tryCount < DEFAULT_MAXRETRY)
		{
			try {
				if (log.isDebugEnabled())
					log.debug("prepare to put " + arg0);
				cache.put(arg0, arg1, expireInSeconds, TimeUnit.SECONDS);
				if (log.isDebugEnabled())
					log.debug("key " + arg0 + " is added.");
				bSuccess = true;
			} catch (Exception e) {
				log.warn("refresh: cluster(" + tryCount + ") - Cache exception retry for put(refresh) with expire (" + tryCount + ") - " + e.getLocalizedMessage() + "," + e.getCause() + "," + e.getStackTrace() + " on key " + arg0, e);
				bSuccess = false;
				try {
					Thread.sleep(TIMEWAIT_BETWEEN_EACHRETRY);
				} catch (InterruptedException e1) {
					log.error(e1.getMessage());
				}
			}
			tryCount++;
		}

		if (!bSuccess)
			throw new CacheException("cluster(" + tryCount + ") refresh - Cache exception for put on key " + arg0);
	}
	
//	public static Object put(String arg0, Object arg1) throws CacheException {
//		return put(arg0, arg1, null, null, CACHE_ACTION.put);
//	}	
	public static Object put(CACHENAME myid, String arg0, Object arg1, ClusterOption opt) throws CacheException {
		Cluster cluster = clusterTbl.get(myid);
		if(cluster!=null) {
//			if(log.isDebugEnabled())
//				log.debugf("put: cluster=%s, id=%s, arg0=%s, arg1=%s, opt=%s", cluster, myid, arg0, arg1, opt);
			return cluster.put(arg0, arg1, opt);
		}
		else {
			log.warnf("put: cluster==null for id=%s, arg0=%s, arg1=%s, opt=%s", myid, arg0, arg1, opt);
			return null;
		}
	}
	
	public Object put(String arg0, Object arg1, ClusterOption opt) throws CacheException {
		boolean bSuccess = false;
		int tryCount = 0;
		Object result = null;
		int lifespan = (opt.getExpireInSeconds()==null?-9999:opt.getExpireInSeconds());
		int maxRetry = (opt.getRetry()==null?DEFAULT_MAXRETRY:opt.getRetry());
		String errMsg = null;
		
		//TransactionManager tm = cache.getAdvancedCache().getTransactionManager();

		while (!bSuccess && tryCount < maxRetry)
		{
			try {
				switch (opt.getAction())
				{
				case put:
					result = cache.put(arg0, arg1, lifespan, TimeUnit.SECONDS);
					break;
				case putIfAbsent:
					result = cache.putIfAbsent(arg0, arg1, lifespan, TimeUnit.SECONDS);
					break;
				case replace:
					result = cache.replace(arg0, arg1, lifespan, TimeUnit.SECONDS);
					break;
//				case putAsync:
//					opt.setRet_futureAsync(cache.putAsync(arg0, arg1, lifespan, TimeUnit.SECONDS));
//					break;
				default:
					throw new CacheException("Invalid put action "+opt.getAction());
				}
				
				if (log.isInfoEnabled())
					log.infof("put: key %s opt %s is done ", arg0, opt);
				bSuccess = true;
			} catch (Exception e) {
				errMsg = String.format("Cluster(%d) - Cache with key %s exception retry for %s err %s %s %s", 
						tryCount, arg0, opt, 
						e.getLocalizedMessage(), e.getCause(), e.getStackTrace(),
						e);
				log.warnf(errMsg);
				bSuccess = false;
				try {
					Thread.sleep(TIMEWAIT_BETWEEN_EACHRETRY);
				} catch (InterruptedException e1) {
					log.error(e1.getMessage());
				}
			}
			tryCount++;
		}

		if (!bSuccess)
			throw new CacheException(errMsg);
		
		return result;
	}

	public static void remove(CACHENAME myid, String arg0, ClusterOption opt) throws CacheException {
		Cluster cluster = clusterTbl.get(myid);
		if(cluster != null)
			cluster.remove(arg0, opt);
		else
			log.warnf("remove: cluster==null for id=%s, arg0=%s, opt=%s", myid, arg0, opt);
	}
	
	public void remove(String arg0, ClusterOption opt) throws CacheException {
		boolean bSuccess = false;
		int tryCount = 0;
		while (!bSuccess && tryCount < DEFAULT_MAXRETRY)
		{
			try {
				switch (opt.getAction())
				{
				case remove:
					cache.remove(arg0);
					break;
//				case removeAsync:
//					opt.setRet_futureAsync(cache.removeAsync(arg0));
//					break;
				default:
					throw new CacheException("Invalid remove action "+opt.getAction());
				}
				if(log.isDebugEnabled())
					log.debug("remove: cluster(" + tryCount + ") - key " + arg0 + " is removed with opt = " + opt);
				bSuccess = true;
			} catch (NullPointerException e)
			{
				log.warn("remove: cluster(" + tryCount + ") - record " + arg0 + " does not exist");
				bSuccess = true;
			} catch (Exception e)
			{
				log.warn("remove: cluster(" + tryCount + ") - Cache exception retry for remove - " + e.getMessage() + " on key " + arg0);
				bSuccess = false;
				try {
					Thread.sleep(TIMEWAIT_BETWEEN_EACHRETRY);
				} catch (InterruptedException e1) {
					log.error("remove: cluster(" + tryCount + ") - " + e1.getMessage());
				}
			}
			tryCount++;
		}

		if (!bSuccess)
			throw new CacheException("Cluster(" + tryCount + ") - Cache exception for remove on key " + arg0);
	}
	
	public static boolean lock(CACHENAME myid, String key) {
		Cluster cluster = clusterTbl.get(myid);
		if(cluster != null)
			return cluster.lock(key);
		else {
			log.warnf("lock: cluster==null for id=%s, key=%s", myid, key);
			return false;
		}
	}
	
	public boolean lock(String key) {
		boolean ret = false;
				
		if (cache != null) {
			try {
				ret = cache.getAdvancedCache().lock(key);
				if (log.isDebugEnabled())
					log.debugf("Lock key %s ret %s", key, ret);
			} catch (org.infinispan.util.concurrent.TimeoutException e)
			{
				log.warnf("TimeoutException on acquiring lock for key "+key);
			}
		} else {
			log.warnf("lock: cache == null for id=%s, key=%s", cacheId, key);
		}
		return ret;
	}
	
	public static void beginTransaction(CACHENAME myid) throws Exception {
		Cluster cluster = clusterTbl.get(myid);
		if(cluster != null)
			cluster.beginTransaction();
		else
			throw new NullPointerException("beginTransaction: cluster<"+myid+"> not found");
	}
	
	public void beginTransaction() throws Exception {
		cache.getAdvancedCache().getTransactionManager().begin();
	}
	
	public static void commitTransaction(CACHENAME myid) throws Exception {
		Cluster cluster = clusterTbl.get(myid);
		if(cluster != null)
			cluster.commitTransaction();
		else
			throw new NullPointerException("commitTransaction: cluster<"+myid+"> not found");
	}
	
	public void commitTransaction() throws Exception {
		cache.getAdvancedCache().getTransactionManager().commit();
	}
	
	public static void rollbackTransaction(CACHENAME myid) throws Exception {
		Cluster cluster = clusterTbl.get(myid);
		if(cluster != null)
			cluster.rollbackTransaction();
		else
			throw new NullPointerException("rollbackTransaction: cluster<"+myid+"> not found");
	}

	public void rollbackTransaction() throws Exception {
		cache.getAdvancedCache().getTransactionManager().rollback();	
	}

	public static ClusterState getMyState() {
		return myState;
	}

	public static void setMyState(ClusterState newState) {
		myState = newState;
	}

	public static Cache<String, Object> getCache(CACHENAME myid) {
		Cluster cluster = clusterTbl.get(myid);
		if(cluster != null)
			return cluster.getCache();
		else {
			log.warnf("getCache: cluster==null for id=%s", myid);
			return null;
		}
	}
	
	public Cache<String, Object> getCache() {
		return cache;
	}

	public static void shutdownAll() {
		log.warn("shutdownAll: clusters are shutting down");
		cacheManager.removeListener(myListener);
		if(clusterTbl != null) {
			for(Cluster cluster: clusterTbl.values()) {
				cluster.shutdown();
			}
		}
		if (cacheManager != null) {
			cacheManager.stop();
			log.warn("shutdownAll: cachemanager terminated (" + cacheManager.getStatus().isTerminated() + ")");
			cacheManager = null;
		}
	}

	private void shutdown () {
		log.warnf("shutdown: cluster<%s> is shutting down", cacheId);
		cache.removeListener(cacheListener);
		stop();
	}
	
	private void stop() {
		if (cache != null) {
			cache.stop();
			log.warn("stop: cache terminated (" + cache.getStatus().isTerminated() + ")");
			cache = null;
		}
	}

	public static int getConfigClusterSize(CACHENAME myid) {
		Cluster cluster = clusterTbl.get(myid);
		if(cluster != null)
			return cluster.getConfigClusterSize();
		else {
			log.warnf("getConfigClusterSize: cluster==null for id=%s", myid);
			return 0;
		}
	}
	
	public int getConfigClusterSize() {
		if (CONFIG_MEMBERS!=null)
			return CONFIG_MEMBERS.size();
		else {
			log.warnf("getConfigClusterSize: CONFIG_MEMBERS==null for id=%s", cacheId);
			return 0;
		}
	}

	public static int getClusterSize(CACHENAME myid) {
		Cluster cluster = clusterTbl.get(myid);
		if(cluster != null)
			return cluster.getClusterSize();
		else {
			log.warnf("getClusterSize: cluster==null for id=%s", myid);
			return 0;
		}
	}
	
	public int getClusterSize() {
		if (cacheManager != null)
			return cacheManager.getMembers().size();
		else {
			log.warnf("getClusterSize: cacheManager==null for id=%s", cacheId);
			return 0;
		}
	}
	
	public static int getCacheObjectSize(CACHENAME myid) {
		Cluster cluster = clusterTbl.get(myid);
		if(cluster != null)
			return cluster.getCacheObjectSize();
		else {
			log.warnf("getCacheObjectSize: cluster==null for id=%s", myid);
			return 0;
		}
	}
	
	public int getCacheObjectSize() {
		if (cache != null)
			return cache.size();
		else {
			log.warnf("getCacheObjectSize: cache==null for id=%s", cacheId);
			return 0;
		}
	}

	public static void DetermineMyState() {
		ClusterState prevState = myState;

		if (cacheManager.getMembers().size() < 2)
		{
			switch (myState) {
			case Initial:
				myState = ClusterState.Standalone;
				break;
			default:
				break;
			}
		}
		else
		{
			switch (myState) {
			case Initial:
			case Standalone:
				myState = ClusterState.ClusterMember;
			default:
				break;
			}
		}
		if(log.isInfoEnabled())
			log.info("DetermineMyState [" + cacheManager.getMembers() + "] - " + prevState.toString() + " ==> " + myState.toString());
	}

}
