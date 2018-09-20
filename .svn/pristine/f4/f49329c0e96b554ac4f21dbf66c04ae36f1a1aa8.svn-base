package com.littlecloud.control.json.model.config;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.logging.Logger;

public class JsonConf_System extends JsonConf {

	//private static final Logger log = Logger.getLogger(JsonConf_System.class);

	public static final List<String> FULL_CONFIG_KEY = Arrays.asList("SNMP_NAME", "TIME_TZV");
	
	@ConfigValue(name = "SNMP_NAME", valueMap = "")
	private String device_name;

	@ConfigValue(name = "TIME_TZV", valueMap = "")
	private String time_zone;

	public ConcurrentHashMap<Integer, String> generateHardwareConfig(Class cls, CONFIG_TYPE configType) throws Exception
	{
		ConcurrentHashMap<Integer, String> confJsonMap = new ConcurrentHashMap<Integer, String>();
		if (traverseConfig(confJsonMap, "", this, cls, GLOBAL_DEV_INDEX, configType))
		{
			String endptConfig = confJsonMap.get(GLOBAL_DEV_INDEX);
			confJsonMap.put(GLOBAL_DEV_INDEX, endptConfig);
			
			//log.debug("endptConfig="+endptConfig);

			return confJsonMap;
		}
		return null;
	}

	public String getDevice_name() {
		return device_name;
	}

	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}

	public String getTime_zone() {
		return time_zone;
	}

	public void setTime_zone(String time_zone) {
		this.time_zone = time_zone;
	}

}
