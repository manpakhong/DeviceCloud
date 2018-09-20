package com.littlecloud.control.json.model.config.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.json.model.config.JsonConf_SsidProfiles;
import com.littlecloud.control.json.model.config.JsonConf_SsidProfiles.PORTAL_AUTH;
import com.littlecloud.utils.CaptivePortalUtil;

public class PortalConfigUtils {
	
	private static final Logger log = Logger.getLogger(PortalConfigUtils.class);

	/* return true if support */
	public static boolean disablePortalIfNotSupport(PortalSsidConfigInfo ctr) throws Exception {
		if (ctr==null || ctr.getSsidProfileJson()==null)
		{
			log.warnf("ssidProfileJson is null for %s", ctr);
			return false;			
		}
		
		/* remove portal settings if config is not supported */
		if (!ctr.getIsPortalIc2Support()) {
			/* portal disabled */
			PortalConfigUtils.disablePortal(ctr);
			if (log.isInfoEnabled()) log.infof("portal is not supported!!");
			return false;
		}
		
		if (isApplyingFbWifi(ctr))
		{
			if (!ctr.getIsPortalFbWifiSupport()) {
				/* portal disabled */
				PortalConfigUtils.disablePortal(ctr);
				if (log.isInfoEnabled()) log.infof("facebook wifi is not supported!!");
				return false;
			}			
		}
		
		return true;
	}
	
	/* Return false for invalid input */
	public static boolean configSsidPortal(PortalSsidConfigInfo ctr)
	{
		if (ctr==null || ctr.getSsidProfileJson()==null)
			return false;
		
		if (ctr.isPortalEnable!=null && ctr.isPortalEnable==true)
		{
			/* portal enabled */
			if (ctr.getAccessMode()==null || ctr.getAccessMode().isEmpty())
			{			
				log.errorf("access_mode is missing (%s)!", ctr.getAccessMode());
				return false;
			}
			
			if (isSavingFaceBookWifi(ctr))
			{
				if (ctr.getFbGwId()==null || ctr.getFbGwId().isEmpty() || ctr.getFbGwSecret()==null || ctr.getFbGwSecret().isEmpty())
				{
					log.errorf("facebook gateway info is missing (%s)!", ctr);
					return false;
				}				
				PortalConfigUtils.setFbWifiPortal(ctr);							
			}
			else
			{
				PortalConfigUtils.setIc2Portal(ctr);			
			}						
		}
		else
		{
			/* portal disabled */
			PortalConfigUtils.disablePortal(ctr);
		}
		
		return true;		
	}
	
	private static boolean isApplyingFbWifi(PortalSsidConfigInfo ctr) {		
		if (ctr.getSsidProfileJson().getPortal_auth()==null)
		{
			if (log.isDebugEnabled()) log.debugf("portal auth is null");
			return false;
		}
		
		return ctr.getSsidProfileJson().getPortal_auth().equalsIgnoreCase(PORTAL_AUTH.fbwifi_auth.toString());			
	}
	
	private static boolean isSavingFaceBookWifi(PortalSsidConfigInfo ctr) {
		if (ctr.getAccessMode().contains(CaptivePortalUtil.PORTAL_ACCESS_MODE.fb_wifi.toString()))
			return true;		
		
		return false;
	}
	
	private static void setFbWifiPortal(PortalSsidConfigInfo ctr) {
		JsonConf_SsidProfiles ssidProfileJson = ctr.getSsidProfileJson();
		
		ssidProfileJson.setPortal_enabled(true);
		ssidProfileJson.setPortal_auth(PORTAL_AUTH.fbwifi_auth.toString());
		ssidProfileJson.setPortal_cna_bypass(true);
		ssidProfileJson.setFbwifi_gwid(ctr.getFbGwId());
		ssidProfileJson.setFbwifi_secret(ctr.getFbGwSecret());
		ssidProfileJson.setPortal_url(null);
		ssidProfileJson.setPortal_domain_accept(CaptivePortalUtil.getPassThruDomains(ctr.getAccessMode()));
	}

	private static void setIc2Portal(PortalSsidConfigInfo ctr) {
		JsonConf_SsidProfiles ssidProfileJson = ctr.getSsidProfileJson();
		
		ssidProfileJson.setPortal_enabled(true);
		ssidProfileJson.setPortal_auth(PORTAL_AUTH.ic2_auth.toString());
		ssidProfileJson.setPortal_cna_bypass(false);
		ssidProfileJson.setFbwifi_gwid(null);
		ssidProfileJson.setFbwifi_secret(null);
		ssidProfileJson.setPortal_url(RadioConfigUtils.PORTAL_URL);		
		ssidProfileJson.setPortal_domain_accept(CaptivePortalUtil.getPassThruDomains(ctr.getAccessMode()));
	}
	
	private static void disablePortal(PortalSsidConfigInfo ctr) {
		JsonConf_SsidProfiles ssidProfileJson = ctr.getSsidProfileJson();
		
		ssidProfileJson.setPortal_enabled(false);
		ssidProfileJson.setPortal_auth(PORTAL_AUTH.no_auth.toString());
		ssidProfileJson.setPortal_cna_bypass(false);
		ssidProfileJson.setFbwifi_gwid(null);
		ssidProfileJson.setFbwifi_secret(null);
		ssidProfileJson.setPortal_url(null);
		ssidProfileJson.setPortal_domain_accept(null);
	}

	public static class PortalSsidConfigInfo {
		/* config */
		JsonConf_SsidProfiles ssidProfileJson;
		Boolean isPortalEnable;
		List<String> accessMode;
		String FbGwId;
		String FbGwSecret;

		/* apply */
		Boolean isPortalIc2Support;
		Boolean isPortalFbWifiSupport;

		public JsonConf_SsidProfiles getSsidProfileJson() {
			return ssidProfileJson;
		}

		public void setSsidProfileJson(JsonConf_SsidProfiles ssidProfileJson) {
			this.ssidProfileJson = ssidProfileJson;
		}

		public Boolean getIsPortalEnable() {
			return isPortalEnable;
		}

		public void setIsPortalEnable(Boolean isPortalEnable) {
			this.isPortalEnable = isPortalEnable;
		}

		public List<String> getAccessMode() {
			return accessMode;
		}

		public void setAccessMode(String accessMode) {
			String[] modeArr = accessMode.split(",");			
			this.accessMode = new ArrayList<String>(Arrays.asList(modeArr));
		}

		public String getFbGwId() {
			return FbGwId;
		}

		public void setFbGwId(String fbGwId) {
			FbGwId = fbGwId;
		}

		public String getFbGwSecret() {
			return FbGwSecret;
		}

		public void setFbGwSecret(String fbGwSecret) {
			FbGwSecret = fbGwSecret;
		}

		public Boolean getIsPortalIc2Support() {
			return isPortalIc2Support;
		}

		public void setIsPortalIc2Support(Boolean isPortalIc2Support) {
			this.isPortalIc2Support = isPortalIc2Support;
		}

		public Boolean getIsPortalFbWifiSupport() {
			return isPortalFbWifiSupport;
		}

		public void setIsPortalFbWifiSupport(Boolean isPortalFbWifiSupport) {
			this.isPortalFbWifiSupport = isPortalFbWifiSupport;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("PortalSsidConfigInfo [ssidProfileJson=");
			builder.append(ssidProfileJson);
			builder.append(", isPortalEnable=");
			builder.append(isPortalEnable);
			builder.append(", accessMode=");
			builder.append(accessMode);
			builder.append(", FbGwId=");
			builder.append(FbGwId);
			builder.append(", FbGwSecret=");
			builder.append(FbGwSecret);
			builder.append(", isPortalIc2Support=");
			builder.append(isPortalIc2Support);
			builder.append(", isPortalFbWifiSupport=");
			builder.append(isPortalFbWifiSupport);
			builder.append("]");
			return builder.toString();
		}
	}
	
	public static void main(String args[])
	{
		JsonConf_SsidProfiles ssidProfileJson = new JsonConf_SsidProfiles();
		ssidProfileJson.setEnabled(true);
		ssidProfileJson.setSsid("SSIDA");
		ssidProfileJson.setPortal_enabled(true);
		
		/* captive portal */
		PortalSsidConfigInfo ssidCtr = new PortalSsidConfigInfo();
		ssidCtr.setSsidProfileJson(ssidProfileJson);
		ssidCtr.setIsPortalEnable(ssidProfileJson.getPortal_enabled());
		ssidCtr.setAccessMode(CaptivePortalUtil.PORTAL_ACCESS_MODE.fb_wifi.toString());
		ssidCtr.setFbGwId("GWID1234");
		ssidCtr.setFbGwSecret("SECRET1234");
		
		if (!PortalConfigUtils.configSsidPortal(ssidCtr))
		{
			log.errorf("miss portal information %s!", ssidCtr);
		}
		
		log.infof("ssidProfile=%s", ssidProfileJson);
	}
}
