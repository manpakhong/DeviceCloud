package com.littlecloud.pool.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import ognl.IteratorEnumeration;

import org.apache.commons.io.IOUtils;
import org.apache.mina.util.CopyOnWriteMap;
import org.jboss.logging.Logger;
//import net.sf.json.JSONObject;
import org.jboss.logging.Logger.Level;


public class Utils {
	private static final String[] privateIpPrefix = { "10.", "172.16", "192.168" };
	private static Logger logger = Logger.getLogger(Utils.class);
	///private ProxyPropertyBean proxyPropertyBean = ProxyPropertyBean.getInstance();
	
	//public static final Integer DEFAULT_IANA_ID = 23695;
	
	public static final String PARAM_ORGANIZATION_DB_PREFIX="peplink_organization_";
	public static final String PARAM_ORGANIZATION_DB_TEST_PREFIX="peplink_org_";
	
	public static final String PARAM_PCANDWP_ONLY_FSA="FSA";	////NEXT// To be replaced by PARAM_CUSTID after player update
	public static final String PARAM_CUSTID="CUSTID";	
	public static final String PARAM_TYPE="TYPE";
	public static final String PARAM_NPID="NPID";
	public static final String PARAM_SRVID="SRVID";
	public static final String PARAM_COMPID="COMPID";
	
	public static final String PARAM_PID="PID";
	public static final String PARAM_CCS="CCS";	// check concurrent streaming
	public static final String PARAM_VIDEOFORMAT="VFMT";
	public static final String PARAM_VIDEOFORMAT_PC="FORMAT";
	public static final String PARAM_SERIESID="SERIESID";
	public static final String PARAM_LIBRARYID="LIBID";
	public static final String PARAM_SCHEDULEID="SCHDID";
	
	public static final String PARAM_VERSIOIN="VERSION";
	public static final String PARAM_REQUESTTIME="REQUESTTIME";
	public static final String PARAM_CHANNELNO="CHANNELNO";
	public static final String PARAM_ACCESSTOKEN="ACCESSTOKEN";	
	public static final String PARAM_DELIM = "&amp;";	
	public static final String HTTPHEADER_CUSTOM = "custom-header";
	public static final String HTTPHEADER_USERAGENT = "User-Agent";
	public static final String PARAM_FORMAT="FMT";
	public static final String PARAM_CALLBACK="CALLBACK";
	public static final String PARAM_TOKEN="TOKEN";
	public static final String PARAM_RETURNURL="URL";
	public static final String PARAM_CLIENTID="CLIENTID";
	public static final String PARAM_TID="TID";
	public static final String PARAMVALUE_FORMAT= "JSON";
	//public static final String PARAM_REDIRECT="REDIRECTED";
	
	public static Vector<String> validCompidList = new Vector<String>();
	public static Vector<String> validCompSecretList = new Vector<String>();

	public static final String SESSION_SRVTYPE_DEFAULT_NOW = "NOW";		// If no type parameter is given, set default for all API except VE only API
	public static final String SESSION_SRVTYPE_DEFAULT_VE = "VEPPV";	// If no type parameter is given, set default for VE only API
	
	public static final String SRVID_NOPLAN = "FSA";
	public static final String SRVID_FTA = "FTA";	
	public static final String dateFormat = "yyyyMMddHHmmss";
	public static final String movieDateFormat = "dd/MM/yyyy HH:mm";
	public static final Integer dataUsageSize1k=1024;
	
	private static String MACHINE_NAME = "UNKNOWN";
	
	public static String getMachineName() {
		return MACHINE_NAME;
	}
	
	public static String getMACHINE_NAME() {
		return MACHINE_NAME;
	}

	public static void setMACHINE_NAME(String mACHINE_NAME) {
		MACHINE_NAME = mACHINE_NAME;
	}

	public static Vector<String> getValidCompidList() {
		return validCompidList;
	}

	public static String getPropertiesInOrder(Properties p) {
		return getPropertiesInOrder(p, null);
	}
	
	public static String getPropertiesInOrder(Properties p, Comparator<String> c) {
		StringBuilder sb = new StringBuilder();
		
		Enumeration<Object> e = p.keys();
        List<String> list = (List<String>) (List<?>) Collections.list(e);
        if (c!=null)
        	Collections.sort(list, c);
        else
        	Collections.sort(list);
		for (String elem:list) {
        	String key = elem;
        	sb.append(String.format("%s:%s\n", key, p.get(key)));
        }		
		return sb.toString();
	}
	
	public static void setValidCompidList(String validCompidList) {
		String[] list = validCompidList.split(",");
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i<list.length; i++)
		{
			Utils.validCompidList.add(list[i]);
			sb.append(list[i]+";");
		}
		
		logger.debug("validCompidList: "+sb.toString());
	}
	
	public static Vector<String> getValidCompSecretList() {
		return validCompSecretList;
	}
	
	public static void setValidCompSecretList(String validCompSecretList) {
		String[] list = validCompSecretList.split(",");
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i<list.length; i++)
		{
			Utils.validCompSecretList.add(list[i]);
			sb.append(list[i]+";");
		}
		
		logger.debug("validCompSecretList: "+sb.toString());
	}
	
	private static boolean isPrivateIp(String ip) {
		ip = ip.trim();
		for (String s : privateIpPrefix) {
			if (ip.startsWith(s))
				return true;
		}
		return false;
	}
	
	public static String replaceIsNullToX(String str) {
		if (str == null)
			return "X";
		else
			return str;
	}
	
	private static String getPublicIP(String ips) {
		final String multiIndicator = ",";
		String finalIp = "";

		if (ips.indexOf(multiIndicator) == -1)
			return ips;

		String[] iparr = ips.split(multiIndicator);
		/* skip private ip */
		for (int i = 0; i < iparr.length; i++) {
			String trimIp = iparr[i].trim();

			if (!isPrivateIp(trimIp)) {
				finalIp = trimIp;
				//logger.debug("IP " + trimIp + " is public.");
			} else {
				//logger.debug("IP " + trimIp + " is private.");
			}
		}

		/* use the last one if all ip are in private ip format */
		if (finalIp.length() == 0)
			finalIp = iparr[iparr.length - 1].trim();

		return finalIp.trim();
	}
	
	public static String getPublicIP(HttpServletRequest request)
	{
		String addr = request.getHeader("x-forwarded-for");
		if (addr == null || addr.length() == 0
				|| "unknown".equalsIgnoreCase(addr)) {
			addr = request.getHeader("Proxy-Client-IP");
		}
		if (addr == null || addr.length() == 0
				|| "unknown".equalsIgnoreCase(addr)) {
			addr = request.getHeader("WL-Proxy-Client-IP");
		}
		if (addr == null || addr.length() == 0
				|| "unknown".equalsIgnoreCase(addr)) {
			addr = request.getRemoteAddr();
		}
		return getPublicIP(addr);
	}
	
	public static boolean isLocalhostRequest(HttpServletRequest request)
	{		
		String ip = getPublicIP(request);
		String bindaddr = System.getProperty("jboss.bind.address");
		
		logger.info("isLocalhostRequest - REQUEST IP="+ip+"; jboss.bind.address="+bindaddr);
		
		//return (ip.compareToIgnoreCase("127.0.0.1")==0);
		return (ip.compareToIgnoreCase(bindaddr)==0);
	}
	
    private static String getMethodParameter(HttpServletRequest request, String name)
    {
    	if (name == null)
    		return null;
    	
    	String str = request.getParameter(name);
    	
    	if (str==null || str.length()==0)
    	{
    		str = request.getParameter(name.toLowerCase());
    		if (str==null || str.length()==0)
    			return null;
    	}

    	return str;
    }
    
    /* Extract parameters in line, which cannot be done by request.getParameter */
    private static String postMethodParameter(String paramStr, String name)
    {
    	if (paramStr == null || name == null)
    		return null;
    	
    	final String delimGET = "&";
		final String delimAMP = "&AMP;";
		
		String delim = delimGET;
    	
    	String paramStrUpper = paramStr.toUpperCase();
    	
    	if (paramStrUpper.indexOf(delimAMP)!=-1)
    	{
			delim = delimAMP;
    	}		
    	logger.debug("<"+paramStr+"> and "+delim+" is used");
    	
    	int startpos = -1;
    	int endpos = -1;    	
    	String tmpstrUpper = null;
    	String result = null;
    	
    	String lookupStr = name+"=";
    	
    	if (paramStrUpper.contains(lookupStr))
		{
    		startpos = paramStrUpper.indexOf(lookupStr)+lookupStr.length();
    		
    		if (startpos<0)
    			return null;
    		
    		tmpstrUpper = paramStrUpper.substring(startpos);
			endpos = tmpstrUpper.indexOf(delim);
			if (endpos>0){
				result = paramStr.substring(startpos, startpos + endpos);
			}
			else if (endpos==0)
			{
				result = null;
			}
			else
			{
				result = paramStr.substring(startpos);
			}
		}
    	logger.debug(name+"="+result);	
    	
		return result;
    }
    
    public static String getParameter(HttpServletRequest request, String contentStr, String name) throws IOException
    {
    	if (request==null||name==null)
    		return null;    			
		
		if (contentStr!=null && contentStr.trim().length()>0){
			/* POST METHOD FOUND */
			return postMethodParameter(contentStr, name);
		}
		else
		{
			/* GET METHOD FOUND */
			return getMethodParameter(request,name);
		}
    }
    
    public static String getContentStr(HttpServletRequest request) throws IOException
    {
    	BufferedInputStream bis = new BufferedInputStream(request.getInputStream());
		return IOUtils.toString(bis);
    }
    
    public static String createTokenInputString(String time, String compid, String version, String srvid, String channelno)
    {
    	String compSecret = "";
    	
    	logger.debug("size="+Utils.getValidCompidList().size());
    	for (int i=0; i<Utils.getValidCompidList().size(); i++)
    	{
    		logger.debug("i="+i);
    		if (Utils.getValidCompidList().get(i).compareToIgnoreCase(compid)==0)
    		{    			
    			compSecret = Utils.getValidCompSecretList().get(i);
    			logger.debug("secret="+compSecret);
    		}
    	}
    	
    	return String.format("%s%s=%s&%s=%s&%s=%s&%s=%s&%s=%s%s%s", 
				time, Utils.PARAM_COMPID, compid, Utils.PARAM_VERSIOIN, version,
				Utils.PARAM_REQUESTTIME, time, Utils.PARAM_SRVID, srvid, Utils.PARAM_CHANNELNO, channelno, time, compSecret);
    }
	
	public static String ToNullOrLowercase(String str)
	{
		return str==null?null:str.toLowerCase();
	}
	
	public static Map<String, Integer> getSortedMap(Map<String, Integer> unsortMap, final boolean order)
    {

        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());

        Collections.sort(list, new Comparator<Entry<String, Integer>>()
        {
            public int compare(Entry<String, Integer> o1,
                    Entry<String, Integer> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
