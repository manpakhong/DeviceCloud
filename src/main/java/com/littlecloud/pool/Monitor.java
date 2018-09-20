package com.littlecloud.pool;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

import com.littlecloud.ac.util.RootBranchRedirectManager;
import com.littlecloud.control.dao.branch.SnsOrganizationsDAO;
import com.littlecloud.pool.Cluster.CACHE_ACTION;
import com.littlecloud.pool.Cluster.CacheException;
import com.littlecloud.pool.object.Session;
import com.littlecloud.services.RootBranchesMgr;


public class Monitor {
	private static Logger logger = Logger.getLogger(Monitor.class);
	
	//private static SessionControl userLog = new SessionControl();	
	
	private static final String TAG_MONITOR = "CLUSTERMONITOR";

	public static void debug(String message)
	{
		logger.log(Level.DEBUG, message);
	}
	
	public static void info(String message)
	{
		logger.log(Level.INFO, message);
	}
	
	public static void warn(String message)
	{
		logger.log(Level.WARN, message);
	}
	
	public static void error(String message)
	{
		logger.log(Level.ERROR, message);
	}
	
	public static void fatal(String message)
	{
		logger.log(Level.FATAL, message);
	}	
	
	public static boolean monitorTest(String type)
	{
		InetAddress IP;
		try {
			IP = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			logger.error("getLocalHost "+e1,e1);
			return false;
		}		
		String key = System.getProperty("user.name")+"_"+type + "-" + (System.getProperty("jboss.bind.address")==null?IP.getHostAddress():System.getProperty("jboss.bind.address"));
		Session sess = new Session(TAG_MONITOR, System.getProperty("jboss.bind.address"), null, null, null, new SimpleDateFormat(Utils.dateFormat).format(new Date()), 99);
		try {
			ClusterOption opt = new ClusterOption();
			opt.setAction(CACHE_ACTION.put);
			opt.setExpireInSeconds(null);
			opt.setRetry(null);
			Cluster.put(Cluster.CACHENAME.LittleCloudCache, key, sess, opt);
		} catch (CacheException e) {
			logger.error("CacheException "+e,e);
			return false;
		}		
		return true;
	}
	
	public static boolean dbTest(boolean bReadonly) throws SQLException
	{
		if (RootBranchRedirectManager.isRootServerMode())
		{
			if (new RootBranchesMgr().getMonitorResult()==1)
				return true;
			else
				return false;
		}
		else
		{
			SnsOrganizationsDAO dao = new SnsOrganizationsDAO(bReadonly);
			try {
				logger.debug("dbTest(readonly="+bReadonly+") count="+dao.getCount());
			} catch (Exception e) {
				logger.error("transaction is rollback - " + e, e);
				return false;
			}
		}
		return true;
	}
}
