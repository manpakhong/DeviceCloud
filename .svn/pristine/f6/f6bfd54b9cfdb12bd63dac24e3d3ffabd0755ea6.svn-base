package com.littlecloud.ac.json.model.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;

public class MessageTypePropertyLoader {

	private static final Logger log = Logger.getLogger(MessageTypePropertyLoader.class);
	private static final String propertyFilename = "message.config";
	private static final String propertyDuration = ".duration"; 
	private static final String propertyInterval = ".interval";
	
	private static MessageTypePropertyLoader instance = null;
	private ResourceBundle bundle;

	public synchronized static MessageTypePropertyLoader getInstance() {
		if (instance != null)
			return instance;

		try {
			instance = new MessageTypePropertyLoader();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return instance;
	}
	
	public Integer getDuration(String name) 
	{
		return getInteger(name + propertyDuration);
	}
	
	public Integer getInterval(String name) 
	{
		return getInteger(name + propertyInterval);
	}
	
	private String getString(String name)
	{
		try {
			String result = bundle.getString(name);
			if (result!=null)
				result.trim();
			else
			{
				log.warnf("Property name %s is empty!", name);
			}
			return result;
		} catch (Exception e) {
			log.error("Fail to load properties "+name, e);
			throw e;
		}
	}
	
	private Integer getInteger(String name)
	{
		if (getString(name)!=null)
			return Integer.valueOf(getString(name));
		return null;
	}

	private MessageTypePropertyLoader() throws Exception {
		log.info("MessageTypePropertyLoader is initializing...");

		if (System.getProperty(propertyFilename) == null)
			throw new Exception(propertyFilename+" parameter is not set");

		InputStreamReader reader = null;
		FileInputStream fis = null;
		try {
			log.info("Loading message config " + System.getProperty(propertyFilename) + "...");
			File file = new File(System.getProperty(propertyFilename));

			if (file.exists() && file.isFile()) { // Also checks for existance
				fis = new FileInputStream(file);
				reader = new InputStreamReader(fis, Charset.forName("UTF-8"));
				bundle = new PropertyResourceBundle(reader);

				Enumeration<String> e = bundle.getKeys();
				while (e.hasMoreElements()) {
					String key = e.nextElement();
					log.info(key + ":" + bundle.getString(key));
				}
			} else {
				log.fatalf("%s is not found", System.getProperty(propertyFilename));
			}
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(fis);
		}
	}
	
	public static void main(String args[]) {
		MessageTypePropertyLoader loader = MessageTypePropertyLoader.getInstance();
		log.infof("test = %s", loader.getDuration("PIPE_INFO_TYPE_PEPVPN_ENDPOINT"));
		log.infof("test = %s", loader.getInterval("PIPE_INFO_TYPE_PEPVPN_ENDPOINT"));
		
	}
}
