package com.littlecloud.pool.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;

public class PropertyLoader {

	private static final Logger logger = Logger.getLogger(PropertyLoader.class);	
		
	//private static int clusterAutoRestartMinSecond;
		
	private static PropertyLoader ppb = null;
	
	private ResourceBundle bundle;
	
	public synchronized static PropertyLoader getInstance(){		
		if (ppb == null){
			try {
				ppb = new PropertyLoader();
			} catch (Exception e) {
				logger.fatal("Failed to init PropertyLoader."+e, e);				
				return null;
			};
		}
		return ppb;
	}
	
	
	private boolean validateConfig(String configPath)
	{
		if (configPath != null)
		{		
			File file = new File(configPath);
			if (file.exists() && file.isFile()) { // Also checks for existance
	        	return true;
	        }
		}
		
		logger.fatalf("%s is not found", configPath);
		return false;		
	}
	
	private PropertyLoader() throws Exception {
		logger.info(" is initializing...");
		
		if (System.getProperty("littlecloud.config")==null)
			throw new Exception("Littlecloud.config parameter is not set");
				
		InputStreamReader reader = null;
	    FileInputStream fis = null;
	    //ResourceBundle bundle = null;
	    try {
	    	logger.info("Loading proxy config "+System.getProperty("littlecloud.config")+"...");
	        File file = new File(System.getProperty("littlecloud.config"));

	        if (file.exists() && file.isFile()) { // Also checks for existance
	            fis = new FileInputStream(file);
	            reader = new InputStreamReader(fis, Charset.forName("UTF-8"));
	            bundle = new PropertyResourceBundle(reader);
	            
	            Enumeration<String> e = bundle.getKeys();
                while (e.hasMoreElements()) {
                	String key = e.nextElement();
                	logger.info(key + ":" + bundle.getString(key)); 
                } 
	        }
	        else
	        {
	        	logger.fatalf("%s is not found", System.getProperty("littlecloud.config"));
	        }
	        
	        /* verify other config file */
	        validateConfig(System.getProperty("db.properties"));
	        validateConfig(System.getProperty("cluster.config"));
	        validateConfig(System.getProperty("jgroups.config"));	        	        
	    } finally {
	        IOUtils.closeQuietly(reader);
	        IOUtils.closeQuietly(fis);
	    }

		//clusterAutoRestartMinSecond = Integer.parseInt(bundle.getString("cluster.auto.restart.minimum.second"));
				
		try
		{
		    InetAddress addr;
		    addr = InetAddress.getLocalHost();
		    String hostname = addr.getHostName();
		    Utils.setMACHINE_NAME(hostname);
		    logger.info("Hostname resolved: "+hostname);
		}
		catch (UnknownHostException ex)
		{
		    logger.fatal("Hostname can not be resolved");
		}
		
		////CacheCluster.init();	// keep this for loading the cache!
	}
	
	public ResourceBundle getBundle() {
		return bundle;
	}

//	public static int getClusterAutoRestartMinSecond() {
//		return clusterAutoRestartMinSecond;
//	}

	@Override
	public String toString() {
		return "PropertyLoader [bundle=" + bundle + "]";
	}
}