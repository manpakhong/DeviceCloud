package com.littlecloud.control.json.model.config.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jboss.logging.Logger;

import com.littlecloud.control.json.model.config.JsonConf_System;
import com.littlecloud.pool.utils.MD5Manager;

public class CommonConfigUtils {
	
	public static enum CONFIG_TAG_TYPE {
		TYPE_ALL_MANAGED, TYPE_MVPN, TYPE_WLAN, TYPE_SYSTEM
	}

	private static final Logger log = Logger.getLogger(CommonConfigUtils.class);

	public static Properties parsePropertiesString(String s)
	{
		if (s == null)
			return null;
	
		final Properties p = new Properties();
		try {
			p.load(new StringReader(s.replace("\\", "\\\\")));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return p;
	}

	public static String getConfChecksumFromHwConf(String hwconfig)
	{
		return md5(getOrderedConfig(parsePropertiesString(hwconfig)));		
	}
	
	public static String getConfChecksumFromHwConf(Properties hwconfig)
	{
		return md5(getOrderedConfig(hwconfig));		
	}

	public static String getIcmgAndConfigTypeChecksum(Properties config, CommonConfigUtils.CONFIG_TAG_TYPE configType)
	{		
		boolean isWlcEnabled = RadioConfigUtils.isWlcEnabled(config);
		
		return md5(getOrderedConfig(CommonConfigUtils.getIcmgManagedProfileType(config, configType, isWlcEnabled)));
	}

	private static String md5(String config)
	{
		try {
			return MD5Manager.md5(config.trim() + "\n");
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			return null;
		}
	}
	
	public static String getOrderedConfig(Properties config)
	{
		final String nwln = "\n";
		StringBuilder sb = new StringBuilder();
		SortedMap<String, String> sp = new TreeMap(config);
		Set<String> keySet = sp.keySet();
		Iterator<String> iterator = keySet.iterator();
		while (iterator.hasNext()) {
			String Name = (String) iterator.next();
			String Value = config.getProperty(Name);
			sb.append(Name);
			sb.append("=");
			sb.append(Value);
			sb.append(nwln);
		}
		return sb.toString();
	}

	public static Properties getIcmgManagedProfileType(Properties config, CommonConfigUtils.CONFIG_TAG_TYPE configType, boolean isWlcEnabled)
	{
		if (log.isDebugEnabled())
			log.debugf("getIcmgManagedProfileType - getting %s", configType);
	
		if (config == null || config.size() == 0)
			return new Properties();
	
		Properties result = new Properties();
		List<String> CONFIG_KEY_PATTERN_LIST = new ArrayList<String>();
	
		/* get icmg list */
		String orderTag = null;
		String[] icmgLst = null;
	
		switch (configType)
		{
		case TYPE_ALL_MANAGED:
			Properties tmp = null;			
			for (CONFIG_TAG_TYPE type:CONFIG_TAG_TYPE.values())
			{
				/* traverse to add all types to filter */
				if (type!=CONFIG_TAG_TYPE.TYPE_ALL_MANAGED)
				{
					tmp = getIcmgManagedProfileType(config, type, isWlcEnabled);
					if (tmp != null)
						result.putAll(tmp);
				}
			}			
			return result;
		case TYPE_MVPN:
			orderTag = RadioConfigUtils.MVPN_ORDER_TAG;
			
			/* checksum additional to icmg */
			CONFIG_KEY_PATTERN_LIST.add("^MVPN_ROUTE$");
			CONFIG_KEY_PATTERN_LIST.add("^MVPN_HC_MODE$");
			CONFIG_KEY_PATTERN_LIST.add("^MVPN_ROUTE_DNS_CUSTOM_SERVERS$");
			
			break;
		case TYPE_WLAN:
			orderTag = RadioConfigUtils.WLAN_ORDER_TAG;
	
			CONFIG_KEY_PATTERN_LIST.add("^AP_COUNTRY_CODE$");
			CONFIG_KEY_PATTERN_LIST.add("^AP_RADIO_RATE$");			
			CONFIG_KEY_PATTERN_LIST.add("^AP_RADIO_TXLEVEL$");
			CONFIG_KEY_PATTERN_LIST.add("^AP_RADIO_TXBOOST$");
			CONFIG_KEY_PATTERN_LIST.add("^AP_RADIO_POLICY$");
			CONFIG_KEY_PATTERN_LIST.add("^PROFILE_BG_CHANNEL$");
			CONFIG_KEY_PATTERN_LIST.add("^R2_AP_RADIO_TXLEVEL$");
			CONFIG_KEY_PATTERN_LIST.add("^R2_AP_RADIO_TXBOOST$");
			CONFIG_KEY_PATTERN_LIST.add("^R2_AP_RADIO_POLICY$");
			CONFIG_KEY_PATTERN_LIST.add("^AP_R2_RADIO_TXLEVEL$");
			CONFIG_KEY_PATTERN_LIST.add("^AP_R2_RADIO_TXBOOST$");
			CONFIG_KEY_PATTERN_LIST.add("^AP_R2_RADIO_POLICY$");
	
			break;
		case TYPE_SYSTEM:
			CONFIG_KEY_PATTERN_LIST.addAll(convertFilterFromKey(JsonConf_System.FULL_CONFIG_KEY));			
			break;
		}
	
		//List<Integer> resultLst = new ArrayList<Integer>();
		if (orderTag!=null)
			icmgLst = CommonConfigUtils.orderToLst(config.getProperty(orderTag));
		
		if (icmgLst != null)
		{
			if (icmgLst != null && icmgLst.length != 0)
			{
				for (int i = 0; i < icmgLst.length; i++)
				{
					String icmgKey = CommonConfigUtils.createICMGKey(Integer.valueOf(icmgLst[i]), configType);
					if (config.containsKey(icmgKey))
					{
						//resultLst.add(Integer.valueOf(icmgLst[i]));	
						String icmgFilter = CommonConfigUtils.createICMGKeyFilter(Integer.valueOf(icmgLst[i]), configType);
						CONFIG_KEY_PATTERN_LIST.add(icmgFilter);
					}
				}
			}
		}
	
		/* parse only icmg list */
		
		if (configType==CONFIG_TAG_TYPE.TYPE_WLAN && isWlcEnabled) {
			if (log.isDebugEnabled())
				log.debugf("wlc enabled - no need to process wlan config configType %s isWlcEnabled", configType, isWlcEnabled);
		} else {
			Enumeration enumeration = (Enumeration<String>) config.propertyNames();
			while (enumeration.hasMoreElements()) {
				String key = (String) enumeration.nextElement();
				for (String filter : CONFIG_KEY_PATTERN_LIST) {
					// log.debugf("matching %s %s", key, filter);
					if (key.matches(filter)) {
						// log.debugf("matched key value %s %s", key, config.get(key));
						result.put(key, config.get(key));
					}
				}
			}
		}
	
		if (log.isDebugEnabled())
			log.debugf("IcmgManagedProfile=%s", result);
		return result;
	}
	
	private static List<String> convertFilterFromKey(List<String> keyLst)
	{
		List<String> filterLst = new ArrayList<String>();		
		StringBuilder sb = new StringBuilder();
		
		for (String key:keyLst)
		{
			sb.setLength(0);
			sb.append("^");
			sb.append(key);
			sb.append("$");
			filterLst.add(sb.toString());
		}
		
		return filterLst;
	}

	/*** convert values to Device table values ***/
	public static Integer createDeviceIcmg(boolean bICMG_WLAN, boolean bICMG_MVPN)
	{
		int icmg = 0;
		
		icmg = bICMG_WLAN ? 1 : 0;
		icmg = icmg << 1;
		icmg += (bICMG_MVPN ? 1: 0);
		
		return icmg;
	}
	/*********************************************/
	
	public static String createICMGKey(Integer id, CommonConfigUtils.CONFIG_TAG_TYPE type)
	{
		final String wlan = "WLAN";
		final String mvpn = "MVPN";
	
		if (id == null)
		{
			log.warn("id is null");
			return "";
		}
	
		StringBuilder key = new StringBuilder();
		switch (type)
		{
		case TYPE_WLAN:
			key.append(wlan);
			key.append(String.valueOf(id));
			key.append(RadioConfigUtils.ICMG_TAG);
			break;
		case TYPE_MVPN:
			key.append(mvpn);
			key.append(String.valueOf(id));
			key.append(RadioConfigUtils.ICMG_TAG);
			break;
		}
	
		return key.toString();
	}

	public static String createICMGKeyFilter(Integer id, CommonConfigUtils.CONFIG_TAG_TYPE type) {
		if (id == null)
		{
			log.warn("id is null");
			return "";
		}
	
		StringBuilder key = new StringBuilder();
		switch (type)
		{
		case TYPE_WLAN:
			key.append(RadioConfigUtils.WLAN_FILTER_P1);
			key.append(String.valueOf(id));
			key.append(RadioConfigUtils.WLAN_FILTER_P2);
			break;
		case TYPE_MVPN:
			key.append(RadioConfigUtils.MVPN_FILTER_P1);
			key.append(String.valueOf(id));
			key.append(RadioConfigUtils.MVPN_FILTER_P2);
			break;
		}
	
		return key.toString();
	}

	public static String[] orderToLst(String tag)
	{
		if (tag == null || tag.length() == 0)
			return null;
	
		tag = tag.replaceAll("\"", "").trim();
		if (tag.length() == 0)
			return null;
	
		return tag.split(" ");
	}

	public static boolean getDeviceIcmgWlan(Integer icmg)
	{
		int mask = 0b10;
		
		int result = icmg & mask;
		result = result >> 1;
		return (result==1?true:false);
	}

	public static boolean getDeviceIcmgMvpn(Integer icmg)
	{
		int mask = 0b01;
		
		int result = icmg & mask;
		return (result==1?true:false);
	}

	public static boolean isMatchIcmgConfigChecksum(Properties config1, Properties config2, CommonConfigUtils.CONFIG_TAG_TYPE configType)
	{
		if (getIcmgAndConfigTypeChecksum(config1, configType).compareToIgnoreCase(getIcmgAndConfigTypeChecksum(config2, configType)) == 0)
			return true;
	
		return false;
	}
}
