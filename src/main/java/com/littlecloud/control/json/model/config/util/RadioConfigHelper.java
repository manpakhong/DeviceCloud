package com.littlecloud.control.json.model.config.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.json.JSONObject;

import org.jboss.logging.Logger;

import com.google.gson.JsonSyntaxException;
import com.littlecloud.ac.ACService;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.control.dao.ConfigurationRadioChannelsDAO;
import com.littlecloud.control.dao.ConfigurationRadiosDAO;
import com.littlecloud.control.dao.DeviceFeaturesDAO;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.dao.branch.ProductsDAO;
import com.littlecloud.control.entity.ConfigurationRadioChannels;
import com.littlecloud.control.entity.ConfigurationRadioChannelsId;
import com.littlecloud.control.entity.ConfigurationRadios;
import com.littlecloud.control.entity.ConfigurationRadiosId;
import com.littlecloud.control.entity.ConfigurationSettings;
import com.littlecloud.control.entity.ConfigurationSettingsId;
import com.littlecloud.control.entity.DeviceFeatures;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.branch.Products;
import com.littlecloud.control.json.model.config.JsonConf_RadioSettings;
import com.littlecloud.control.json.model.config.JsonConf_RadioSettings.AP_RADIO_POLICY;
import com.littlecloud.control.json.model.config.JsonConf_RadioSettings.Modules;
import com.littlecloud.control.json.model.config.JsonConf_RadioSettingsWeb;
import com.littlecloud.control.json.model.config.util.exception.FeatureNotFoundException;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.FeatureGetObject;
import com.littlecloud.pool.object.FeatureGetObject.FeatureRadioConfig;
import com.littlecloud.pool.object.utils.DeviceUtils;
import com.littlecloud.pool.object.utils.FeatureGetUtils;
import com.littlecloud.pool.object.utils.ProductUtils;
import com.littlecloud.pool.object.utils.FeatureGetUtils.RADIO_PROTOCOL;

public class RadioConfigHelper {

	private static final Logger log = Logger.getLogger(RadioConfigHelper.class);

	private String orgId = null;
	private Integer netId = null;
	private List<Devices> devList = null;	

	private HashMap<Integer, DeviceFeatures> dfMap = null;
	//private HashMap<Integer, Products> prodMap = null;
	private ConfigurationRadios radio = null;
	private boolean isForApplyConfig = false;	
	private JsonConf_RadioSettingsWeb radioJson;
	private StringBuilder sbSummary;
	
	private static final Map<RADIO_PROTOCOL, AP_RADIO_POLICY> AP_RADIO_POLICY_LookupMap;
    static
    {
    	AP_RADIO_POLICY_LookupMap = new HashMap<RADIO_PROTOCOL, AP_RADIO_POLICY>();
    	AP_RADIO_POLICY_LookupMap.put(RADIO_PROTOCOL.wifi_802dot11ac, AP_RADIO_POLICY.ac);
    	AP_RADIO_POLICY_LookupMap.put(RADIO_PROTOCOL.wifi_802dot11na, AP_RADIO_POLICY.na);
    	AP_RADIO_POLICY_LookupMap.put(RADIO_PROTOCOL.wifi_802dot11ng, AP_RADIO_POLICY.ng);
    }
    
    private static final Map<AP_RADIO_POLICY, String> PROTOCOL_5GZ_LookupMap;
    static
    {
    	PROTOCOL_5GZ_LookupMap = new HashMap<AP_RADIO_POLICY, String>();
    	PROTOCOL_5GZ_LookupMap.put(AP_RADIO_POLICY.ac, JsonConf_RadioSettingsWeb.STRING_80211AC_PROTOCOL);
    	PROTOCOL_5GZ_LookupMap.put(AP_RADIO_POLICY.na, JsonConf_RadioSettingsWeb.STRING_80211NA_PROTOCOL);
    }

	public RadioConfigHelper(String orgId, Integer netId, List<Devices> devList, boolean isForApplyConfig, StringBuilder sbSummary)
			throws Exception {
		super();
		this.orgId = orgId;
		this.netId = netId;
		this.devList = devList;
		this.isForApplyConfig = isForApplyConfig;
		this.sbSummary = (sbSummary==null?new StringBuilder():sbSummary);
		init();
	}

	public void init() throws Exception {

		if (log.isInfoEnabled())
			log.info("DeviceRadioFeaturesHelper.init() start");
		
		//ProductsDAO productsDAO = new ProductsDAO(true);
		DeviceFeaturesDAO dfDAO = new DeviceFeaturesDAO(this.orgId, true);
		ConfigurationRadiosDAO radioDAO = new ConfigurationRadiosDAO(orgId, true);

		List<Integer> devIdLst = DeviceUtils.getDevIdLstFromDevLst(devList);
		dfMap = dfDAO.getFromDevIdLst(devIdLst);
		//prodMap = productsDAO.getAllProductsMap();
		
		ConfigurationRadiosId radioId = new ConfigurationRadiosId();
		radioId.setNetworkId(netId);
		radioId.setDeviceId(0);	// only network setting is available
		this.radio = radioDAO.findById(radioId);
		if (this.radio!=null){
			radioJson = new JsonConf_RadioSettingsWeb();
			radioJson.parseRadio(radio);
		}
		else
		{
			List<Integer> modIdList = new ArrayList<Integer>();
			for (int i = 1; i <= Modules.DEFAULT_MODULES; i++)
				modIdList.add(i);
			radioJson = new JsonConf_RadioSettingsWeb().fillDefaultValues(netId, devIdLst, modIdList, isForApplyConfig);
		}
		
		if (log.isInfoEnabled())
			log.info("DeviceRadioFeaturesHelper.init() end");
	}
	
	public HashMap<Integer, DeviceFeatures> getFeatureMap() {
		return dfMap;
	}
	
	public JsonConf_RadioSettingsWeb getRadioJson() {
		return radioJson;
	}

	public List<Modules> getModuleLst() throws Exception {		
		sbSummary.append("\n>>> Radio config >>>");
		
		List<Modules> radiomsLst = null;		
		
		HashMap<Integer, FeatureGetObject> pfMap = null;
		HashMap<ConfigurationRadioChannelsId, ConfigurationRadioChannels> crMap = null;	
		HashMap<Integer, Devices> devMap = null;
		
		ConfigurationSettingsUtils settingUtils = null;
		ConfigurationSettings devSet = null;
		ConfigurationSettings netSet = null;
		ConfigurationSettingsId netSettingsId = null;
		ConfigurationSettingsId settingsId = null;
				
		DeviceFeaturesDAO dfDAO = new DeviceFeaturesDAO(this.orgId, true);
		DevicesDAO devicesDAO = new DevicesDAO(orgId, true);

		ConfigurationRadioChannelsDAO channelDAO = new ConfigurationRadioChannelsDAO(this.orgId, true);
		ConfigurationRadiosDAO radioDAO = new ConfigurationRadiosDAO(this.orgId, true);

		ConfigurationRadiosId radioId = new ConfigurationRadiosId();
		radioId.setNetworkId(this.netId);
		radio = radioDAO.findById(radioId);

		List<Integer> devIdLst = DeviceUtils.getDevIdLstFromDevLst(devList);				
		dfMap = dfDAO.getFromDevIdLst(devIdLst);		
		crMap = channelDAO.getFromDevIdLst(devIdLst);		
		pfMap = getDefaultProductFeatures(orgId, devList);
		radiomsLst = new ArrayList<Modules>();

		for (Devices dev : devList) {
			if (log.isDebugEnabled()) log.debugf("dev[x]=%s", dev.getSn());

			/** search for device capabilities **/
			Integer numOfModules = Modules.DEFAULT_MODULES;
			List<FeatureRadioConfig> featureRadConfLst = null;
			FeatureGetObject featureGetObject = null;
			try {
				// DeviceFeatures feature = dfDAO.findById(dev.getId());
				DeviceFeatures feature = dfMap.get(dev.getId());
				if (feature != null) {
					//featureGetObject = JsonUtils.<FeatureGetObject> fromJson(feature.getRadioSupport(), FeatureGetObject.class);
					featureGetObject = FeatureGetUtils.createFeatureGetObject(dev.getIanaId(), dev.getSn(), feature);
					sbSummary.append(String.format("\n* - %d %s device features found",dev.getIanaId(), dev.getSn()));
				} else {
					
					if (!isForApplyConfig)
					{
						sbSummary.append(String.format("\n* - %d %s no device features, use product features",dev.getIanaId(), dev.getSn()));
						featureGetObject = pfMap.get(dev.getId());
					}
					else
					{
						sbSummary.append(String.format("\n* - %d %s no device or product features! stop apply!",dev.getIanaId(), dev.getSn()));
						ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_FEATURE_GET, JsonUtils.genServerRef(), dev.getIanaId(), dev.getSn());
						throw new FeatureNotFoundException(String.format("FeatureNotFoundException - dev %d %s RadioConfigHelper.getModuleLst()", dev.getIanaId(), dev.getSn()));						
					}
				}

				if (featureGetObject != null) {
					featureRadConfLst = featureGetObject.getFeatureRadioConfigLst();
					if (featureRadConfLst != null) {
						numOfModules = featureRadConfLst.size();
						sbSummary.append(String.format("\n* - %d %s no. of modules = %d",dev.getIanaId(), dev.getSn(), numOfModules));
					}
				} else {
					log.warnf("dev %s has no default product features");
					sbSummary.append(String.format("\n* - %d %s no default product features!",dev.getIanaId(), dev.getSn()));
				}
			} catch (JsonSyntaxException jsone) {
				log.warnf("JsonSyntaxException for device features of sn %s", dev.getSn());
			}
							
			
			//Products product = prodMap.get(dev.getProductId());
			Products product = ProductUtils.getProducts(dev.getProductId());
			if (product == null)
			{
				log.warnf("Product %d is not found in ProductUtils", dev.getProductId());
				sbSummary.append(String.format("\n* - %d %s no related product (%d) is found!",dev.getIanaId(), dev.getSn(), dev.getProductId()));
				throw new FeatureNotFoundException(String.format("dev %d %s has no related product %d!", dev.getIanaId(), dev.getSn(), dev.getProductId()));
			}
			
			Map<Integer, List<AP_RADIO_POLICY>> protocolSupportMap = getRadioProtocolSupport(featureGetObject, numOfModules);			
			if (log.isDebugEnabled()) log.debugf("protocolSupportMap=%s", protocolSupportMap);
			for (int i = 1; i <= numOfModules; i++) {
				String bandDefault = null;
				String bandSupportLst = null;
				String supportDefaultBand = null;
				String actualBandSelected = null;
				
				String protocolDefault = null;
				List<AP_RADIO_POLICY> protocolSupportLst = protocolSupportMap.get(i);
				AP_RADIO_POLICY supportDefaultProtocol = null;
				
				if (log.isDebugEnabled()) log.debug("seek radio_channels of devId " + dev.getId() + " modules id " + i);				
				
				ConfigurationRadioChannelsId channelId = new ConfigurationRadioChannelsId();
				channelId.setDeviceId(dev.getId());
				channelId.setModuleId(i);
				ConfigurationRadioChannels channels = crMap.get(channelId);
				Modules radioms = new JsonConf_RadioSettings().new Modules();
				radioms.setProduct_name(product.getName());
				
				/** set frequency_band from features or product **/
				if (featureRadConfLst!=null && featureRadConfLst.size()>=i && featureRadConfLst.get(i - 1)!=null)
				{
					if (log.isDebugEnabled()) log.debugf("set featureRadConfLst.get(%d).getFrequency_band()=%s", i - 1, featureRadConfLst.get(i - 1).getFrequency_band());					
					radioms.setFrequency_band_capability(featureRadConfLst.get(i - 1).getFrequency_band());
					bandSupportLst = getRadioSupportBandFromFeature(featureRadConfLst.get(i - 1).getFrequency_band(), product, i);
					bandDefault = getRadioDefaultBandFromFeature(featureRadConfLst.get(i - 1).getFrequency_band(), product, i);
				}
				else
				{	
					bandSupportLst = getRadioSupportBandFromFeature(null, product, i);
					bandDefault = getRadioDefaultBandFromFeature(null, product, i);
				}
				if (log.isDebugEnabled())
					log.debugf("radioms.getFrequency_band_capability()=%s bandSupportLst=%s bandDefault=%s", 
							radioms.getFrequency_band_capability(), bandSupportLst, bandDefault);

				if (radio!=null)
				{
					if (!isBandSupport(bandSupportLst, radio.getDefaultBand()))
					{
						if (log.isDebugEnabled())
							log.debugf("band not support");
						sbSummary.append(String.format("\n* - %d %s module %d band %s not support (support list:%s)",dev.getIanaId(), dev.getSn(), i, radio.getDefaultBand(), bandSupportLst));
						supportDefaultBand = bandDefault;
					}
					else
					{
						if (log.isDebugEnabled())
							log.debugf("band support");
						supportDefaultBand = radio.getDefaultBand();
					}
				}
				else
				{
					if (log.isDebugEnabled())
						log.debugf("radio record not found. band default");
					supportDefaultBand = bandDefault;
				}
								
				if (channels == null) {
					// log.debug("no module found!");
					/* generate default if not exist */
										
					if (log.isDebugEnabled())
						log.debugf("generate default channels for sn %s", dev.getSn());
					
					radioms.fillDefault(dev.getId(), i);
					
					if (isForApplyConfig)						
						radioms.setFrequency_band(supportDefaultBand);
					
				} else {
					/* parse config if exist */
					if (log.isDebugEnabled())
						log.debugf("(a) channels=%s modules=%s", channels, radioms);
					
					radioms.parseModules(channels);
					
					if (isForApplyConfig)
					{
						if (radioms.getFrequency_band().equalsIgnoreCase(JsonConf_RadioSettingsWeb.STRING_DEFAULT_BAND))
							radioms.setFrequency_band(supportDefaultBand);
					}
					
					if (log.isDebugEnabled())
						log.debugf("(b) channels=%s modules=%s", channels, radioms);					
				}
				
				if (log.isDebugEnabled())
					log.debugf("featureDefaultBand %s bandSupport %s supportDefaultBand %s radioms.getFrequency_band() %s", bandDefault, bandSupportLst, supportDefaultBand, radioms.getFrequency_band());
								
				/** check if existing channel record has band not supported**/
				actualBandSelected = radioms.getFrequency_band();
				if (!isBandSupport(bandSupportLst, radioms.getFrequency_band()))
				{
					if (isForApplyConfig)
					{
						/* including "default" frequency_band */
						radioms.setFrequency_band(supportDefaultBand);
					}
					actualBandSelected = supportDefaultBand;
				}
				
				if (log.isDebugEnabled())
					log.debugf("radioms.getFrequency_band() %s", radioms.getFrequency_band());
				
				/** set protocol after frequency_band is finalized **/
				if (radio==null || radio.getProtocol()==null || radio.getProtocol().isEmpty())
					protocolDefault = JsonConf_RadioSettingsWeb.STRING_80211AC_PROTOCOL;
				else
					protocolDefault = radio.getProtocol();
				supportDefaultProtocol = getRadioDefaultProtocolFromFeature(actualBandSelected, protocolSupportLst, protocolDefault, product, i);
				radioms.setProtocol(supportDefaultProtocol.toString());
				radioms.setProtocol_5gz_capability(get5GzProtocolCapabilityFromProductFeature(protocolSupportLst, product, i));
				log.debugf("supportDefaultProtocol=%s", supportDefaultProtocol);

				radioms.setDevice_name(dev.getName());
				radioms.setProduct_id(dev.getProductId());
				radiomsLst.add(radioms);
			}
		}
		
		if (!isForApplyConfig)
		{
			settingUtils = new ConfigurationSettingsUtils(orgId, netId, 0);			
			netSet = settingUtils.getNetworkConfigSettings();
			if (netSet == null)
			{
				netSettingsId = new ConfigurationSettingsId(netId, 0);
				netSet = new ConfigurationSettings();
				netSet.setId(netSettingsId);
				netSet.fillDefaultValues();
			}
			
			devMap = devicesDAO.devlistToMap(devList);
			Map<Integer, ConfigurationSettings> devSettingsMap = settingUtils.getDevConfigSettingsOfNetwork();			
			for (Modules m:radiomsLst)
			{
				if (log.isDebugEnabled())
					log.debugf("processing modules devId = %s", m.getDevice_id());
				
				int devId = m.getDevice_id();
				Devices dev = devMap.get(devId);				
				devSet = devSettingsMap.get(m.getDevice_id());
				if (devSet == null)
				{						
					devSet = new ConfigurationSettings();
					settingsId = new ConfigurationSettingsId(netId, devId);
					devSet.setId(settingsId);
					devSet.fillDefaultValues();
				}
				
				m.setWifi_cfg(RadioConfigUtils.getDevWifiManaged(dev, netSet, devSet));				
			}
		}
		
		return radiomsLst;
	}
	
	public static Map<String, String> getModFreqRadioMap(List<Modules> radiomsLst) {
		
		Map<String, String> modFreqRadioMap = new HashMap<String, String>();
		
		for (Modules mod:radiomsLst)
		{
			modFreqRadioMap.put(JsonConf_RadioSettings.STRING_DUAL_BAND, "3");
			
			//dual|5ghz|2_4ghz
			switch(mod.getFrequency_band())
			{				
			case "2400":
				modFreqRadioMap.put("2_4ghz", String.valueOf(mod.getModule_id()));
				break;
			case "5000":
				modFreqRadioMap.put("5ghz", String.valueOf(mod.getModule_id()));
				break;
			default:
				log.warnf("default unhandled case (%s)!", mod.getFrequency_band());
				break;
			}				
		}		
		return modFreqRadioMap;
	}

	private HashMap<Integer, FeatureGetObject> getDefaultProductFeatures(String orgId, List<Devices> devLst) {
		/* use when device_features does not exist */
		
		HashMap<Integer, FeatureGetObject> result = new HashMap<Integer, FeatureGetObject>();

		if (devLst == null || devLst.size() == 0)
			return null;

		try {			
			for (Devices dev : devLst) {
				log.debugf("getDefaultDeviceFeatures %d", dev.getId());
				FeatureGetObject featureGetObj = new FeatureGetObject();
				//Devices dev = devicesDAO.findById(devId);
				//Devices dev = devMap.get(devId);
				if (dev!=null)
				{
					//Products product = prodMap.get(dev.getProductId());
					Products product = ProductUtils.getProducts(dev.getProductId());
					List<FeatureRadioConfig> featureRadioConfigLst = new ArrayList<FeatureRadioConfig>();
					FeatureRadioConfig featureRadioConfig = null;
	
					if (product != null) {
						if (product.getRadio1_24G() || product.getRadio1_5G()) {
							featureRadioConfig = featureGetObj.new FeatureRadioConfig();
							featureRadioConfig.setModule_id(1);
							if (product.getRadio1_24G() == true && product.getRadio1_5G() == false)
								featureRadioConfig.setFrequency_band("2400");
							if (product.getRadio1_24G() == false && product.getRadio1_5G() == true)
								featureRadioConfig.setFrequency_band("5000");
							if (product.getRadio1_24G() == true && product.getRadio1_5G() == true)
								featureRadioConfig.setFrequency_band("2400|5000");
							featureRadioConfigLst.add(featureRadioConfig);
						}
	
						if (product.getRadio2_24G() || product.getRadio2_5G()) {
							featureRadioConfig = featureGetObj.new FeatureRadioConfig();
							featureRadioConfig.setModule_id(2);
							if (product.getRadio2_24G() == true && product.getRadio2_5G() == false)
								featureRadioConfig.setFrequency_band("2400");
							if (product.getRadio2_24G() == false && product.getRadio2_5G() == true)
								featureRadioConfig.setFrequency_band("5000");
							if (product.getRadio2_24G() == true && product.getRadio2_5G() == true)
								featureRadioConfig.setFrequency_band("2400|5000");
							featureRadioConfigLst.add(featureRadioConfig);
						}
	
						featureGetObj.setIana_id(dev.getIanaId());
						featureGetObj.setSn(dev.getSn());
						featureGetObj.setDevice_id(dev.getId());
						featureGetObj.setNetwork_id(dev.getNetworkId());
						featureGetObj.setOrganization_id(orgId);
						featureGetObj.setFeatureRadioConfigLst(new CopyOnWriteArrayList<FeatureRadioConfig>(featureRadioConfigLst));
						result.put(dev.getId(), featureGetObj);
					} else {
						log.warnf("getDefaultProductFeatures - no related product is found for %s", dev.getSn());
					}
				}
				else
				{
					log.warnf("getDefaultProductFeatures - orgId %s devId %d is not found!", orgId, dev.getId());
				}
			}

		} catch (Exception e) {
			log.error("getDefaultProductFeatures - get Default Device Features Failed", e);
		}

		return result;
	}
		
	public String get5GzProtocolCapabilityFromProductFeature(List<AP_RADIO_POLICY> protocolSupportFromFeature, Products product, Integer radioId)
	{
		if (log.isDebugEnabled())
			log.debugf("getRadioCapabilityFromProductFeature - protocolSupportFromFeature %s product %s radioId %d", protocolSupportFromFeature, product, radioId);
		
		StringBuilder sb = new StringBuilder();
		if (protocolSupportFromFeature!=null && protocolSupportFromFeature.size()!=0)
		{
			log.debugf("getRadioCapabilityFromProductFeature - load from features");
			for (AP_RADIO_POLICY rp:protocolSupportFromFeature)
			{
				String protocol = PROTOCOL_5GZ_LookupMap.get(rp);
				if (protocol!=null)
				{
					sb.append(protocol);
					sb.append("|");
				}else
				{
					if (log.isDebugEnabled())
						log.debugf("AP_RADIO_POLICY %s is not found in 5Gz map!!", rp);
				}
			}
		}
		else
		{
			log.debugf("getRadioCapabilityFromProductFeature - load from products");
			switch (radioId)
			{
			case 1:
				if (product.getRadio1_5G_na())
					sb.append(JsonConf_RadioSettingsWeb.STRING_80211NA_PROTOCOL+"|");
				
				if (product.getRadio1_5G_ac())
					sb.append(JsonConf_RadioSettingsWeb.STRING_80211AC_PROTOCOL+"|");
				
				break;
			case 2:
				if (product.getRadio2_5G_na())
					sb.append(JsonConf_RadioSettingsWeb.STRING_80211NA_PROTOCOL+"|");
				
				if (product.getRadio2_5G_ac())
					sb.append(JsonConf_RadioSettingsWeb.STRING_80211AC_PROTOCOL+"|");
				
				break;
			}
		}
				
		if (sb.length()>1)
			sb.setLength(sb.length()-1);
		
		return sb.toString();
	}
	
	public AP_RADIO_POLICY getRadioDefaultProtocolFromFeature(String selectedSupportBand, 
			List<AP_RADIO_POLICY> protocolSupportFromFeature, String prefer_5gz_protocol, Products product, int radioId)
	{		
		if (log.isDebugEnabled())
			log.debugf("getRadioDefaultProtocolFromFeature - selectedSupportBand %s protocolSupportFromFeature %s prefer_5gz_protocol %s product %s radioId %d",
				selectedSupportBand, protocolSupportFromFeature, prefer_5gz_protocol, product, radioId);
		
		
		if (protocolSupportFromFeature!=null && protocolSupportFromFeature.size()!=0)
		{
			if (log.isDebugEnabled()) log.debugf("load from features");
			switch (selectedSupportBand)
			{
				case JsonConf_RadioSettingsWeb.STRING_BAND2400:
					return AP_RADIO_POLICY.ng;			
				case JsonConf_RadioSettingsWeb.STRING_BAND5000:
					if (prefer_5gz_protocol.equalsIgnoreCase(JsonConf_RadioSettingsWeb.STRING_80211AC_PROTOCOL) 
							&& (isProtocolSupport(protocolSupportFromFeature, AP_RADIO_POLICY.ac)))
							return AP_RADIO_POLICY.ac;
					else if (prefer_5gz_protocol.equalsIgnoreCase(JsonConf_RadioSettingsWeb.STRING_80211NA_PROTOCOL) 
							&& (isProtocolSupport(protocolSupportFromFeature, AP_RADIO_POLICY.na)))
						return AP_RADIO_POLICY.na;
					else if (prefer_5gz_protocol.equalsIgnoreCase(JsonConf_RadioSettingsWeb.STRING_DEFAULT_PROTOCOL) 
							&& (isProtocolSupport(protocolSupportFromFeature, AP_RADIO_POLICY.ac)))
						return AP_RADIO_POLICY.ac; 
					else
					{
						if (log.isInfoEnabled())
							log.infof("load from features - prefer_5gz_protocol not supported,  default to na !! (selectedBand %s protocol_support %s prefer_protocol %s)", 
								selectedSupportBand, protocolSupportFromFeature, prefer_5gz_protocol.toString());
						return AP_RADIO_POLICY.na;				
					}
				default:
					log.errorf("load from features - Unknown band is given %s !! default to ng", selectedSupportBand);
					return AP_RADIO_POLICY.ng;
			}
		}
		else if (product!=null)
		{
			log.debugf("load from product");
			switch (selectedSupportBand)
			{
				case JsonConf_RadioSettingsWeb.STRING_BAND2400:
					return AP_RADIO_POLICY.ng;			
				case JsonConf_RadioSettingsWeb.STRING_BAND5000:
					log.debugf("load from product - STRING_BAND5000 prefer_5gz_protocol %s JsonConf_RadioSettingsWeb.STRING_80211AC_PROTOCOL %s product %s",
							prefer_5gz_protocol, JsonConf_RadioSettingsWeb.STRING_80211AC_PROTOCOL, product);
					switch(radioId)
					{
						case 1:
							{
								if (prefer_5gz_protocol.equalsIgnoreCase(JsonConf_RadioSettingsWeb.STRING_80211AC_PROTOCOL) 
										&& product.getRadio1_5G_ac())
										return AP_RADIO_POLICY.ac;
								else if (prefer_5gz_protocol.equalsIgnoreCase(JsonConf_RadioSettingsWeb.STRING_80211NA_PROTOCOL) 
										&& product.getRadio1_5G_na())
									return AP_RADIO_POLICY.na;
								else if (prefer_5gz_protocol.equalsIgnoreCase(JsonConf_RadioSettingsWeb.STRING_DEFAULT_PROTOCOL) 
										&& product.getRadio1_5G_ac())
									return AP_RADIO_POLICY.ac; 
								else
								{
									if (log.isInfoEnabled())
										log.infof("load from product (1) - prefer_5gz_protocol not supported, default to na !! (selectedBand %s product %s prefer_protocol %s)",
											selectedSupportBand, product, prefer_5gz_protocol.toString());
									return AP_RADIO_POLICY.na;				
								}
							}
						case 2:
							{
								if (prefer_5gz_protocol.equalsIgnoreCase(JsonConf_RadioSettingsWeb.STRING_80211AC_PROTOCOL) 
										&& product.getRadio2_5G_ac())
										return AP_RADIO_POLICY.ac;
								else if (prefer_5gz_protocol.equalsIgnoreCase(JsonConf_RadioSettingsWeb.STRING_80211NA_PROTOCOL) 
										&& product.getRadio2_5G_na())
									return AP_RADIO_POLICY.na;
								else if (prefer_5gz_protocol.equalsIgnoreCase(JsonConf_RadioSettingsWeb.STRING_DEFAULT_PROTOCOL) 
										&& product.getRadio2_5G_ac())
									return AP_RADIO_POLICY.ac; 
								else
								{
									if (log.isInfoEnabled())
										log.infof("load from product (2) - prefer_5gz_protocol not supported, default to na !! (selectedBand %s product %s prefer_protocol %s)", 
											selectedSupportBand, product, prefer_5gz_protocol.toString());
									return AP_RADIO_POLICY.na;				
								}
							}
						default:
							log.errorf("unknown radio id %d!", radioId);
							break;
					}
				default:
					log.errorf("load from product -Unknown band is given %s !! default to ng", selectedSupportBand);
					return AP_RADIO_POLICY.ng;
			}
		} else
		{
			log.errorf("Feature and product are null! selectedBand %s prefer_protocol %s default to ng", selectedSupportBand, prefer_5gz_protocol);
			return AP_RADIO_POLICY.ng;
		}
	}
	
	private String getRadioDefaultBandFromFeature(String band_support, Products product, int radioId) 
	{
		//final String bandDefault="default";	// error case
		final String band2400="2400";
		final String band5000="5000";
		
		if (band_support!=null)
		{
			if (band_support.contains(band2400))
				return band2400;
			
			if (band_support.contains(band5000))
				return band5000;
		}
		else if (product!=null)
		{
			switch (radioId)
			{
			case 1:
				if (product.getRadio1_24G())
					return band2400;
				
				if (product.getRadio1_5G())
					return band5000;
				
				break;
			case 2:
				if (product.getRadio2_24G())
					return band2400;
				
				if (product.getRadio2_5G())
					return band5000;
				break;
			}
		}
		
		log.warnf("default feature band is not found from either feature or product tables!! 2400 is used");
		return band2400;
	}
	
	private String getRadioSupportBandFromFeature(String band_support, Products product, int radioId) 
	{
		StringBuilder sb = new StringBuilder();
		
		final String band2400="2400";
		final String band5000="5000";
		
		if (band_support!=null)
		{
			if (band_support.contains(band2400))
				sb.append(band2400+"|");
			
			if (band_support.contains(band5000))
				sb.append(band5000+"|");
		}
		else if (product!=null)
		{
			switch (radioId)
			{
			case 1:
				if (product.getRadio1_24G())
					sb.append(band2400+"|");
				
				if (product.getRadio1_5G())
					sb.append(band5000+"|");
				
				break;
			case 2:
				if (product.getRadio2_24G())
					sb.append(band2400+"|");
				
				if (product.getRadio2_5G())
					sb.append(band5000+"|");
				break;
			}
		}
		
		return sb.toString();
	}
	
	private static Map<Integer, List<AP_RADIO_POLICY>> getRadioProtocolSupport(FeatureGetObject fgo, Integer numOfRadio) {
		//log.debugf("fgo=%s numOfRadio=%d", fgo, numOfRadio);
		
		Map <Integer, List<AP_RADIO_POLICY>> radioSupportMap = new HashMap<Integer, List<AP_RADIO_POLICY>>();		
				
		String prefix = null;
		String key = null;
		String value = null;
		List<AP_RADIO_POLICY> radioSupportLst = null;
						
		if (numOfRadio==null || numOfRadio==0 || fgo == null)
			return radioSupportMap;
		
		JSONObject object = JSONObject.fromObject(fgo.getFeatures());
				
		for (int i=1; i<=numOfRadio;i++)
		{
			key = null;
			value = null;
			radioSupportLst = new ArrayList<AP_RADIO_POLICY>();
			
			if (i==1)
				prefix="";
			else
				prefix=String.format("r%d_", i);
			
			for (RADIO_PROTOCOL rp:RADIO_PROTOCOL.values())
			{
				key = String.format("%s%s", prefix, rp.toString());
				//log.debugf("finding key=%s", key);
				try {
					value = object.getString(key.toString());
					if (value!=null && !value.isEmpty())
					{						
						radioSupportLst.add(AP_RADIO_POLICY_LookupMap.get(rp));
					}
				} catch (Exception e)
				{
					if (log.isInfoEnabled())
						log.infof("key %s is not found in feature json!", key);
				}
			}
			
			if (radioSupportLst!=null && radioSupportLst.size()!=0)
				radioSupportMap.put(i, radioSupportLst);
		}
		
		return radioSupportMap;
	}
	
	private boolean isBandSupport(String supportBand, String setBand)
	{
		if (supportBand.contains(setBand))
			return true;
		else
			return false;
	}
	
	private boolean isProtocolSupport(List<AP_RADIO_POLICY> supportProtocol, AP_RADIO_POLICY setProtocol)
	{
		if (supportProtocol.contains(setProtocol))
			return true;
		else
			return false;
	}
}
