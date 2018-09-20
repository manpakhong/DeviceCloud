package com.littlecloud.control.json.model.config.util.info;

import java.util.List;
import java.util.Properties;

import com.littlecloud.control.json.model.config.JsonConf.CONFIG_TYPE;
import com.littlecloud.control.json.model.config.JsonConf_SsidProfiles;

public class SsidVlanConfigInfo extends ConfigPutTaskInfo {
	private StringBuilder sbSummary;
	private CONFIG_TYPE configType;
	private Properties fullconfig;
	private List<JsonConf_SsidProfiles> ssidProfileJsonLst;
	private List<JsonConf_SsidProfiles> ssidProfileReservedJsonLst;

	public CONFIG_TYPE getConfigType() {
		return configType;
	}

	public void setConfigType(CONFIG_TYPE configType) {
		this.configType = configType;
	}

	public Properties getFullconfig() {
		return fullconfig;
	}

	public void setFullconfig(Properties fullconfig) {
		this.fullconfig = fullconfig;
	}

	public List<JsonConf_SsidProfiles> getSsidProfileJsonLst() {
		return ssidProfileJsonLst;
	}

	public void setSsidProfileJsonLst(List<JsonConf_SsidProfiles> ssidProfileJsonLst) {
		this.ssidProfileJsonLst = ssidProfileJsonLst;
	}
	
	public List<JsonConf_SsidProfiles> getSsidProfileReservedJsonLst() {
		return ssidProfileReservedJsonLst;
	}

	public void setSsidProfileReservedJsonLst(List<JsonConf_SsidProfiles> ssidProfileReservedJsonLst) {
		this.ssidProfileReservedJsonLst = ssidProfileReservedJsonLst;
	}

	public StringBuilder getSbSummary() {
		return sbSummary;
	}

	public void setSbSummary(StringBuilder sbSummary) {
		this.sbSummary = sbSummary;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SsidVlanConfigInfo [configType=");
		builder.append(configType);
		builder.append(", fullconfig=");
		builder.append(fullconfig);
		builder.append(", ssidProfileJsonLst=");
		builder.append(ssidProfileJsonLst);
		builder.append("]");
		return builder.toString();
	}
}