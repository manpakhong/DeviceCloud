package com.littlecloud.pool.utils;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

public class PropertyService<T> {

	private static final Logger log = Logger.getLogger(PropertyService.class);
	
	private static PropertyLoader pl = PropertyLoader.getInstance();
	private String className;

	public PropertyService(Class<T> cls)
	{		
		this.className = cls.getCanonicalName()+".";
		log.debug(this.className);
	}
	
	public Boolean getBoolean(String name)
	{
		String value = getString(name);
		if (StringUtils.isEmpty(value))
			return false;
		
		return value.equalsIgnoreCase("true")?true:false;
	}
	
	public String getString(String name)
	{
		try {
			String result = pl.getBundle().getString(className+name);
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
	
	public Integer getInteger(String name)
	{
		if (getString(name)!=null)
			return Integer.valueOf(getString(name));
		return null;
	}
}
