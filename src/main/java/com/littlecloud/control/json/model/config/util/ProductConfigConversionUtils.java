package com.littlecloud.control.json.model.config.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.jboss.logging.Logger;

import com.littlecloud.control.json.model.config.JsonConf.CONFIG_TYPE;
import com.littlecloud.control.json.util.DateUtils;

public class ProductConfigConversionUtils {
	
	private static final Logger log = Logger.getLogger(ProductConfigConversionUtils.class);
	
	private static final String DEFAULT_KEY = "___";
	
	private static Map<String, String> AP_JSON_CONFIG_KEYMAP = initAP_CONFIG_KEYMAP();
	private static Map<String, String> MAX_JSON_CONFIG_KEYMAP = initMAX_CONFIG_KEYMAP();	
	private static Map<String, String> MAX_HW_CONFIG_KEYMAP = initMAX_HW_KEYMAP();
	
	private static Map<String, String> initAP_CONFIG_KEYMAP() {
		Map<String, String> result = new HashMap<String, String>();
		result.put("L2_ISO", "L2_ISOLATION:disable|yes=enable");	/* tag name and value change */
		result.put("MCAST_FILTER_RATE", "MCAST_RATE:");				/* only tag name change */
		result.put("AP_RADIO_TXBOOST", "AP_EDGE_POWER:${setnull}|yes=disable");
		result.put("SNMP_NAME","AP_NAME");
		result.put("TIME_TZV", "AP_TIMEZONE"+":"+initAP_TIMEZONE_VALUEMAP());
		return result;
	}
	
	private static Map<String, String> initMAX_CONFIG_KEYMAP() {
		Map<String, String> result = new HashMap<String, String>();
		result.put("VLANID", "");	/* remove vlan_id  from MAX */
		return result;
	}
	
	private static Map<String, String> initMAX_HW_KEYMAP() {
		Map<String, String> result = new HashMap<String, String>();
		result.put("R2_AP_RADIO_TXLEVEL", "AP_R2_RADIO_TXLEVEL");
		result.put("R2_AP_RADIO_TXBOOST", "AP_R2_RADIO_TXBOOST");
		result.put("R2_AP_RADIO_POLICY", "AP_R2_RADIO_POLICY");		
		return result;
	}
	
	public static String getAP_CONFIG_KEY(String MAXKey) {
		
		String result[] = null;
		
		String lkupResult = AP_JSON_CONFIG_KEYMAP.get(MAXKey);
		if (lkupResult==null)
		{
			log.error("getAP_CONFIG_KEY - Data definition exception(1)!!");
			return null;
		}
		
		result = lkupResult.split(":");
		if (result[0]==null)
		{
			log.error("getAP_CONFIG_KEY - Data definition exception(2)!!");
			return null;
		}
		
		return result[0];
	}
	
	private static String initAP_TIMEZONE_VALUEMAP() {
		DateUtils.loadTimeZones();
		
		StringBuilder sb = new StringBuilder(); 
		String key;
		String value;
		
		Properties timezoneMax2ApMap = DateUtils.getTimezoneMappingMax2Ap();
		Iterator<Object> it = timezoneMax2ApMap.keySet().iterator();
		while(it.hasNext())
		{
			key = (String) it.next();
			value = timezoneMax2ApMap.getProperty(key);
			sb.append(key);
			sb.append("=");
			sb.append(value+"|");
		}
		if (sb.length()>0)
			sb.setLength(sb.length()-1);
		
		return sb.toString();
	}

	public static Properties patchRadioHardwareConfigType(Properties config, CONFIG_TYPE configType) throws Exception 
	{
		Properties result = new Properties();
		String newName = null; 
		String value = null;
		Iterator<Object> iterator = null;
		
		if (config==null)
			return result;
		
		iterator = config.keySet().iterator();
				
		switch (configType)
		{
		case AP:			
			while (iterator.hasNext()) {
				String Name = (String) iterator.next();
				value = config.getProperty(Name);
				newName = Name;
				
				result.put(newName, value);
			}
			break;
		case MAX:
			while (iterator.hasNext()) {
				String Name = (String) iterator.next();
				value = config.getProperty(Name);
				
				newName = MAX_HW_CONFIG_KEYMAP.get(Name);
				newName=(newName==null?Name:newName);
				
				result.put(newName, value);		
				//log.debugf("patchRadioHardwareConfigType patch newName %s oldName %s Value %s", newName, Name, Value);
			}	
			break;
		default:
			log.error("patchHardwareConfigType - Unknown configType here!");
			throw new Exception("patchHardwareConfigType - Unknown configType here!");
		}
				
		return result;		
	}
	
	/* Convert config keys and values to device specific */
	public static ConfigKeyValue convertKeyValue(String name, String value, CONFIG_TYPE configType) throws Exception
	{
		//log.debugf("convertKeyValue name = %s, value = %s, configType %s", name, value, configType);
		String newValueMap = null;
		String newName = null;
		String newValue = null;
		Properties lookup = null;

		if (name == null || name.isEmpty())
		{
			/* dummy parameters are given */
			//log.debugf("dummy parameters are given >> return newName = %s, newValue = %s", name, value);
			return new ConfigKeyValue(name, value);
		}

		switch (configType)
		{
		case AP:			
			newValueMap = AP_JSON_CONFIG_KEYMAP.get(name);
			break;
		case MAX:
			newValueMap = MAX_JSON_CONFIG_KEYMAP.get(name);	
			break;
		default:
			log.error("Unknown configType here!");
			throw new Exception("Unknown configType here!");
		}
		
		if (newValueMap == null)
		{
			/* no conversion is required */
			//log.debugf("no conversion is required >> return newName = %s, newValue = %s", name, value);
			return new ConfigKeyValue(name, value);
		} else if (newValueMap.isEmpty())
		{
			/* attribute has to be removed from config */
			if (log.isDebugEnabled()) log.debugf("attribute %s has to be removed from config", name);
			return null;
		}
		
		String[] keyValues = newValueMap.split("[:]");
		newName = keyValues[0];
		
		lookup = valuesToProperties(keyValues.length>1?keyValues[1]:null);
		if (value!=null)
			newValue = lookup.getProperty(value);
		newValue = newValue==null?lookup.getProperty(DEFAULT_KEY):newValue;

		
		//log.debugf(">> name %s value %s configType %s return newName = %s, newValue = %s", name, value, configType, newName, newValue);
		return new ConfigKeyValue(newName, newValue);
	}
	
	/*public static JsonConf_RadioSettings convertRadioSettingsToConfigType(JsonConf_RadioSettings radioJson, CONFIG_TYPE configType) {
		
		switch(configType)
		{
		case AP:
			break;
		case MAX:
			for (JsonConf_SsidProfiles ssidProfileJson: radioJson.getSsidProfilesLst())
			{
				 VLANID only apply to AP 
				ssidProfileJson.setVlan_id(null);
			}
			break;
		default:
			log.error("Unexpected configType!!");
			break;
		}
		
		return radioJson;
	}*/
	
	private static Properties valuesToProperties(String values)
	{
		if (values==null || values.isEmpty())
			return new Properties();
		
		Properties result = new Properties();
		String[] tmp = values.split("[|]");
		int size = tmp.length;
		result.put(DEFAULT_KEY, tmp[0]);
		for (int i=1; i<size; i++)
		{
			String[] pairs = tmp[i].split("[=]");
			result.put(pairs[0], pairs[1]);
		}
	
		//log.debugf("valuesToProperties = %s", result);
		return result;
	}
	
	public static class ConfigKeyValue {
		private String key;
		private String value;

		public ConfigKeyValue(String key, String value) {
			super();
			this.key = key;
			this.value = value;
		}
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}

}
