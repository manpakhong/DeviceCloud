package com.littlecloud.control.json.model.config.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.jboss.logging.Logger;

import com.littlecloud.control.dao.ConfigurationPepvpnsDAO;
import com.littlecloud.control.dao.DeviceFeaturesDAO;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.entity.ConfigurationPepvpns;
import com.littlecloud.control.entity.DeviceFeatures;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.Devices.ONLINE_STATUS;
import com.littlecloud.control.json.model.config.JsonConf_PepvpnProfiles;
import com.littlecloud.control.json.model.config.JsonConf_PepvpnProfilesNew;
import com.littlecloud.control.json.model.pepvpn.PepvpnConnection;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.utils.DeviceUtils;

public class PepvpnConfigUtils {

	private static final Logger log = Logger.getLogger(PepvpnConfigUtils.class);
	
	/* Important: One network only has one pepvpn hub and at most one more ha hub */
	public static JsonConf_PepvpnProfiles getDatabasePepvpnFullConfig(String param_orgId, int param_networkId) throws Exception 
	{
		log.debug("getDatabasePepvpnFullConfig is called");
		
		/* if no existing configuration from db, create default */
		DevicesDAO devDAO = new DevicesDAO(param_orgId);
		ConfigurationPepvpnsDAO pepvpnDAO = new ConfigurationPepvpnsDAO(param_orgId);		
		DeviceFeaturesDAO dfDAO = new DeviceFeaturesDAO(param_orgId);		
		//ConfigurationPepvpnCertificatesDAO certDAO = new ConfigurationPepvpnCertificatesDAO(param_orgId);
		
		DeviceFeatures feature = null;
		JsonConf_PepvpnProfiles pepvpnJson = null;
		int maxLicense = -1;
		try {

			/* get network endpoint list */
			List<Integer> hubIdLst = pepvpnDAO.getHubandHahubDeviceIdFromNetworkId(param_networkId, false);
			if (hubIdLst!=null && hubIdLst.size()!=0)
				feature = dfDAO.findById(hubIdLst.get(0));
			
			if (feature!=null)
				maxLicense = (feature.getMvpnLicense()==0?RadioConfigUtils.MVPN_INFINITE_LICENSE_NUM:feature.getMvpnLicense());
						
			List<Devices> devList = PepvpnConfigUtils.getAssignedEndptLst(param_orgId, param_networkId, maxLicense);
			if (devList==null)
				devList = new ArrayList<Devices>();
			
			log.debugf("devList.size=%d, maxLicense=%d", devList.size(), maxLicense);
			
			/* seek network config */
			ConfigurationPepvpns pepvpn = pepvpnDAO.findById(param_networkId);
			if (pepvpn != null)
			{
				/* parse config */
				pepvpnJson = JsonConf_PepvpnProfiles.parseConfigurationPepvpns(pepvpn);

				if (pepvpnJson == null)
					throw new Exception("Configuration does not exist in database");
				
				/* seek hub certificates */
				/* suppose ha cert must exists if hub certificate exists in db */
				int hub_device_id;
				int hub_network_id;
				try {
					hub_device_id = pepvpnJson.getHub_id();
					hub_network_id = pepvpnJson.getHub_net_id();
				} catch (Exception e)
				{
					log.debug("what null pointer here!!", e);
					throw e;
				}

				Devices hub = devDAO.findById(hub_device_id);
				log.infof("hub_device_id, hub_name = %d, %s", hub_device_id, hub.getName());
								
//				List<Certificate> hubCertList = new ArrayList<Certificate>();
//
//				/* check certificate record from database */
//				ConfigurationPepvpnCertificates cert = certDAO.findById(new ConfigurationPepvpnCertificatesId(param_networkId, hub_device_id));
//				Certificate certJson = pepvpnJson.new Certificate();
//				certJson.parseCertificates(cert, hub);
//
//				/* set json custom field */
//				if (certJson.getPublic_key_cert_pem() != null && certJson.getPublic_key_cert_pem().trim().length() > 0)
//					certJson.setExisted(true);
//				else
//					certJson.setExisted(false);
//
//				/* hide/clear certificates from users */
//				certJson.setPrivate_key_pem("");
//				certJson.setPublic_key_cert_pem("");
//				certJson.setPrivate_key_pem_passphrase("");
//
//				hubCertList.add(certJson);

				/* loop all devices in the network (except hub) to seek endpoint certificates */
				pepvpnJson = JsonUtils.fromJson(pepvpn.getConfig(), JsonConf_PepvpnProfiles.class);
				if (pepvpnJson == null)
					throw new Exception("config is empty in db configuration_pepvpns for network_id ");
								
				/* if no record, default true; if no field value, default false */
				log.debugf("pepvpnJson.getNat_enabled()=%s", pepvpnJson.getNat_enabled());
				if (pepvpnJson.getNat_enabled()==null)
				{
					pepvpnJson.setNat_enabled(false);
				}
				
				/* PVPN_10001 */
				pepvpnJson.setHub_name(hub.getName());	// use sn instead
				pepvpnJson.setHub_sn(hub.getSn());
				//pepvpnJson.setHub_cert_list(hubCertList);
				

				/* check endpoint certificate record from database */
//				List<Certificate> endpointCertLst = pepvpnJson.getEndpoint_cert_list();
//				if (endpointCertLst == null)
//					endpointCertLst = new ArrayList<Certificate>();
//				ConfigurationPepvpnCertificates certEndpoint;
//				Certificate certEndpointJson;

				// List<Devices> devList = devDAO.getDevicesHubList(param_networkId, false);
				List<JsonConf_PepvpnProfiles.Endpoint> endptList = new ArrayList<JsonConf_PepvpnProfiles.Endpoint>();
				for (Devices dev : devList)
				{
					log.debug("getting endpoint " + dev.getId());
					//certEndpoint = certDAO.findById(new ConfigurationPepvpnCertificatesId(param_networkId, dev.getId()));
					//certEndpointJson = pepvpnJson.new Certificate();
					//certEndpointJson.parseCertificates(certEndpoint, dev);
					
					//... duplicate list issue. 
//					if (!endpointCertLst.contains(certEndpointJson))
//					{
//						endpointCertLst.add(certEndpointJson);
//					}
//					else
//					{
//						endpointCertLst.remove(certEndpointJson);
//						endpointCertLst.add(certEndpointJson);
//					}
					
					JsonConf_PepvpnProfiles.Endpoint endpt = pepvpnJson.new Endpoint();
					endpt.setId(dev.getId());
					endpt.setName(dev.getName());		
					endpt.setSn(dev.getSn());
					endptList.add(endpt);
				}
				//pepvpnJson.setEndpoint_cert_list(endpointCertLst);
				pepvpnJson.setEndptNameLst(endptList);
			}
			else
			{
				log.info("no record found! Generate default set");
				pepvpnJson = JsonConf_PepvpnProfiles.generateDefaultInstance(param_networkId, devList);
			}

		} catch (Exception e) {
			log.error("Exception - " + e, e);
			throw e;
		}

		return pepvpnJson;
	}

	private static String getDevSnLstString(List<Devices> devLst)
	{
		if (devLst == null || devLst.size() == 0)
			return "";
	
		StringBuilder sb = new StringBuilder();
		for (Devices dev : devLst)
		{
			sb.append(dev.getSn());
			sb.append(",");
		}
	
		return sb.toString();
	}

	private static List<String> getDevSnLst(List<Devices> devLst)
	{
		List<String> result = new ArrayList<String>();
	
		if (devLst == null || devLst.size() == 0)
			return result;
	
		StringBuilder sb = new StringBuilder();
		for (Devices dev : devLst)
		{
			result.add(dev.getSn());
		}
	
		return result;
	}

	public static List<PepvpnConnection> genPepvpnConnectionLst(List<Devices> devLst)
	{
		List<PepvpnConnection> result = new ArrayList<PepvpnConnection>();
		// List<Integer> connLst = new ArrayList<Integer>();
	
		if (devLst == null)
			return result;
	
		int id = 0;
		for (Devices dev : devLst)
		{
			id++;
			PepvpnConnection pepconn = new PepvpnConnection();
			pepconn.setId(id);
			pepconn.setDevice_id(dev.getId());
			pepconn.setDevice_name(dev.getName());
			pepconn.setDevice_network_id(dev.getNetworkId());
	
			DevOnlineObject devO = DeviceUtils.getDevOnlineObject(dev);
			if (devO != null && devO.isOnline())
				pepconn.setMain_state("Starting...");
			else
				pepconn.setMain_state("");
	
			// pepconn.setName(dev.getName() + "_");
			// pepconn.setName(formatSiteId(dev.getSn()));
			pepconn.setName(PepvpnConfigUtils.formatProfileNameFromDevnameSn(dev.getName(), dev.getSn()));
			pepconn.setSn(dev.getSn());
			result.add(pepconn);
		}
	
		return result;
	}

	public static Devices getPepvpnHubDevice (String orgId, Integer netId, Integer maxHubLicense)
	{		
		boolean bRdonly = true;
		Devices selectedHub = null;
		List<Devices> hubList = new ArrayList<Devices>();
		
		try {
		DevicesDAO deviceDAO = new DevicesDAO(orgId, bRdonly);
		
		ConfigurationPepvpnsDAO pepvpnConfDao = new ConfigurationPepvpnsDAO(orgId, bRdonly);
		ConfigurationPepvpns pepvpnConf = pepvpnConfDao.findById(netId);
		
		if (pepvpnConf != null && pepvpnConf.isEnabled() == true)
		{
			log.debug("finding hub " + pepvpnConf.getHubDeviceId());
			Devices hub = deviceDAO.findById(pepvpnConf.getHubDeviceId());
			hubList.add(hub);
			if (pepvpnConf.isHaHubEnabled())
			{
				if (pepvpnConf.getHaHubDeviceId() != null)
				{
					log.debug("finding ha hub " + pepvpnConf.getHaHubDeviceId());
					Devices hahub = deviceDAO.findById(pepvpnConf.getHaHubDeviceId());
					hubList.add(hahub);
				}
			}
		}
		
		for (Devices dev : hubList)
		{
			if (ONLINE_STATUS.statusOf(dev.getOnline_status())==ONLINE_STATUS.ONLINE)
			{				
				selectedHub = dev;
			}
		}
	
		}catch (SQLException e) {
			log.infof("RadioConfigUtils.getPepvpnHubDevice -  maxHubLicense = %d for orgId %s netId %d" ,maxHubLicense ,orgId, netId);
			e.printStackTrace();
		}
		return selectedHub;
	}

	public static List<Devices> getAssignedEndptLst(String orgId, Integer netId, Integer maxHubLicense) throws SQLException
	{
		if (maxHubLicense <= 0)
		{
			log.warnf("maxHubLicense = %d for orgId %s netId %d", maxHubLicense, orgId, netId);
			return new ArrayList<Devices>();
		}
	
		DevicesDAO deviceDAO = new DevicesDAO(orgId, true);
	
		List<Devices> result = deviceDAO.getEverOnlineSpeedFusionEndptDevicesList(netId, maxHubLicense);
		if (result == null)
			result = new ArrayList<Devices>();
		log.debugf("result(1)=%s", getDevSnLstString(result));
	
		List<String> devSnLst = getDevSnLst(result);
	
		int allowEndpt = maxHubLicense - result.size();
		if (allowEndpt >= 0)
		{
			/* exceptional case */
			List<Devices> ptDevLst = deviceDAO.getPrioritizedEndptDevicesList(netId, maxHubLicense - result.size());
			if (ptDevLst != null)
			{
				for (Devices dev : ptDevLst)
				{
					if (allowEndpt > 0 && !devSnLst.contains(dev.getSn()))
						if (!devSnLst.contains(dev.getSn()))
						{
							result.add(dev);
							allowEndpt--;
						}
				}
			}
		}
		else
		{
			log.warnf("Unknown exception result.size()>maxHubLicense ");// 2.0.10
		}
		log.debugf("result(2)=%s", getDevSnLstString(result));
		return result;
	}

	private static boolean isValidProfileChar(char ch)
	{
		if (Character.UnicodeBlock.of(ch) != Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS && (Character.isLetter(ch) || Character.isDigit(ch) || ch == '-'))
			return true;
		else
			return false;
	}

	public static String getSnFromProfileName(String name)
	{
		return getInfoFromProfileName(name).get(1);
	}

	public static String getDevNameFromProfileName(String name)
	{
		return getInfoFromProfileName(name).get(0); 
	}

	/* 
	 * result:
	 * 	0 - device name
	 * 	1 - sn
	 */
	private static List<String> getInfoFromProfileName(String name)
	{
		List<String> result = new ArrayList<String>();
		String devName = null;
		String sn = null;
		
		name = StringEscapeUtils.escapeHtml(StringEscapeUtils.escapeJava(name)).replace("'", "\\'");
	
		if (name == null)
		{
			log.warn("profile name is null");
		}
		else if (name.length() == 12)
		{
			/* version 1 pepvpn profile name as sn e.g. 123412341234 */
			log.warnf("version 1 profile name as sn is found '%s'", name);
			sn = profileNameToSn(name);
		}
		else if (name.length() < 15)
		{
			log.infof("Unmanaged profile name is found '%s' with length < 15.", name);
			sn = name;
		}
		else
		{
			Pattern pattern = Pattern.compile("^(.*)-([0-9A-Z]{4}-[0-9A-Z]{4}-[0-9A-Z]{4})$");
		    Matcher matcher = pattern.matcher(name);
			if (matcher.find()) {
				devName = matcher.group(1)==null?"":matcher.group(1);
				sn = matcher.group(2)==null?"":matcher.group(2);
			
				if (sn!=null && sn.length()!=14)
				{
					log.infof("Unmanaged profile name is found '%s'", name);
					sn = name;
				}
			}
		}
		
		devName = (devName==null?"DEVNAME_CUSTOM_DEVICENAME_DETECTED":devName);
		sn = (sn==null?"SN_CUSTOM_DEVICENAME_DETECTED":sn);
		
		log.debugf("devName %s sn %s", devName, sn);
		result.add(devName);
		result.add(sn);
		return result;
	}

	/* version 1 profile name */
	public static String profileNameToSn(String name)
	{
		name = StringEscapeUtils.escapeHtml(StringEscapeUtils.escapeJava(name)).replace("'", "\\'");
	
		if (name == null)
		{
			log.warn("profile name is null");
			return "";
		}
	
		if (name.length() < 12)
		{
			log.warnf("Unmanaged profile name is found with length less than 12 (%s)", name);
			return "";
		}
	
		StringBuilder sn = new StringBuilder();
		sn.append(name.substring(0, 4));
		sn.append("-");
		sn.append(name.substring(4, 8));
		sn.append("-");
		sn.append(name.substring(8, 12));
	
		return sn.toString();
	}

	public static String formatProfileNameFromDevnameSn(String devName, String sn)
	{
		if (sn==null)
		{
			log.error("Null sn is found!");
			return "NULL_SN_IS_FOUND";
		}
		
		if (devName==null)
		{
			log.warn("Null devName is found!");
			devName = sn;
		}
		
		sn = sn.toUpperCase();
		
		if (devName.equalsIgnoreCase(sn))
			return sn;
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < devName.length(); i++)
		{
			if (isValidProfileChar(devName.charAt(i)))
			{
				sb.append(devName.charAt(i));
			}
			else
			{
				if (i > 0 && isValidProfileChar(devName.charAt(i - 1)))
					sb.append("-");
			}
		}
	
		while (sb.length() > 1 && sb.charAt(0) == '-')
		{
			sb.deleteCharAt(0);
		}
	
		while (sb.length() > 1 && sb.charAt(sb.length() - 1) == '-')
		{
			sb.deleteCharAt(sb.length() - 1);
		}
	
		if (sb.length()!=0)
			sb.append("-");
		
		sb.append(sn);
	
		return sb.toString();
	}
	
	public static List<JsonConf_PepvpnProfilesNew> collectEndptConfigLst(String orgId, Integer netId, int maxHubLicense) {
		log.debugf("memTrace - collectEndptConfigLst is called for (orgId, netId, maxHubLicense) = (%s, %d, %d)", orgId, netId, maxHubLicense);

		final boolean rdonly = true;
		boolean pepEnabled = false;
		JsonConf_PepvpnProfiles pepvpnProfile = null;

		List<JsonConf_PepvpnProfilesNew> endptConfigLst = new ArrayList<JsonConf_PepvpnProfilesNew>();
		List<Devices> allNetDevLst;

		if (maxHubLicense <= 0)
			return endptConfigLst;

		try {
			ConfigurationPepvpnsDAO pepDAO = new ConfigurationPepvpnsDAO(orgId, rdonly);

			ConfigurationPepvpns confPep = pepDAO.findById(netId);
			if (confPep != null)
				pepEnabled = confPep.isEnabled();

			allNetDevLst = PepvpnConfigUtils.getAssignedEndptLst(orgId, netId, maxHubLicense);
			if (allNetDevLst.size() == 0)
			{
				log.warn("empty endpoint list");
				return endptConfigLst;
			}

			if (pepEnabled) {
				pepvpnProfile = PepvpnConfigUtils.getDatabasePepvpnFullConfig(orgId, netId);
				log.debugf("pepProf = %s", pepvpnProfile);

				if (allNetDevLst != null) {
					log.debugf("net %d devLst[].size=%d", netId, allNetDevLst.size());
					for (Devices dev : allNetDevLst)
					{
						log.debugf("net dev sn=%s", dev.getSn());
						if (!pepDAO.isEnabledHubOrHaHubInAnyNetwork(dev.getId()))
						{
							JsonConf_PepvpnProfilesNew pepEndptConfNew = JsonConf_PepvpnProfilesNew.parseJsonConf_PepvpnProfilesAsEndpoint(pepvpnProfile, dev.getId(), dev.getSn(), dev.getName(), null, null, null);
							log.debugf("dev %d %s pepconf = \n%s", dev.getId(), dev.getSn(), pepEndptConfNew);

							if (pepEndptConfNew != null) {
								endptConfigLst.add(pepEndptConfNew);
							}

							log.debugf("endptConfigLst.size = %d", endptConfigLst.size());
						}
						else
						{
							log.debugf("dev %d %s is a hub ", dev.getId(), dev.getSn());
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("transaction is rollback - " + e, e);
			return endptConfigLst;
		}

		return endptConfigLst;
	}
}
