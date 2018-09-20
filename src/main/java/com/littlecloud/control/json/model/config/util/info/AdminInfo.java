package com.littlecloud.control.json.model.config.util.info;

import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.json.model.config.JsonConf;
import com.littlecloud.control.json.model.config.util.ConfigurationSettingsUtils;
import com.littlecloud.pool.object.FeatureGetObject;

public class AdminInfo extends ConfigPutTaskInfo {

	private JsonConf.CONFIG_TYPE configType;
	private Devices dev;	
	private ConfigurationSettingsUtils settingsUtils;
	private FeatureGetObject fgo;
	private StringBuilder sbSummary;

	public JsonConf.CONFIG_TYPE getConfigType() {
		return configType;
	}

	public void setConfigType(JsonConf.CONFIG_TYPE configType) {
		this.configType = configType;
	}
	
	public Devices getDev() {
		return dev;
	}

	public void setDev(Devices dev) {
		this.dev = dev;
	}
	
	public ConfigurationSettingsUtils getSettingsUtils() {
		return settingsUtils;
	}

	public void setSettingsUtils(ConfigurationSettingsUtils settingsUtils) {
		this.settingsUtils = settingsUtils;
	}

	public StringBuilder getSbSummary() {
		return sbSummary;
	}

	public void setSbSummary(StringBuilder sbSummary) {
		this.sbSummary = sbSummary;
	}
	
	public FeatureGetObject getFgo() {
		return fgo;
	}

	public void setFgo(FeatureGetObject fgo) {
		this.fgo = fgo;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AdminInfo [configType=");
		builder.append(configType);
		builder.append(", dev=");
		builder.append(dev);
		builder.append(", settingsUtils=");
		builder.append(settingsUtils);
		builder.append(", sbSummary=");
		builder.append(sbSummary);
		builder.append("]");
		return builder.toString();
	}
}