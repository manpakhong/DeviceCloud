package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FeatureGetObject extends PoolObject implements PoolObjectIf, Serializable{
	
	/* key */
	private String sn;
	private Integer iana_id;
	/* value */
	private Integer device_id;
	private Integer network_id;
	private String organization_id;
	private String sid; // caller and server reference
	
	private String features;
	
	private CopyOnWriteArrayList<FeatureRadioConfig> featureRadioConfigLst;
	
	public class FeatureRadioConfig implements Serializable
	{
		private Integer module_id;
		private String frequency_band;
		
		public Integer getModule_id() {
			return module_id;
		}
		public void setModule_id(Integer module_id) {
			this.module_id = module_id;
		}
		public String getFrequency_band() {
			return frequency_band;
		}
		public void setFrequency_band(String frequency_band) {
			this.frequency_band = frequency_band;
		}
		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
		}
	}

	@Override
	public String getKey() {
		return this.getClass().getSimpleName() + "sn_pk" + getSn() + "|" + getIana_id();
	}

	@Override
	public void setKey(Integer iana_id, String sn) {
		this.iana_id = iana_id;
		this.sn = sn;
	}

	public CopyOnWriteArrayList<FeatureRadioConfig> getFeatureRadioConfigLst() {
		return featureRadioConfigLst;
	}

	public void setFeatureRadioConfigLst(
			CopyOnWriteArrayList<FeatureRadioConfig> featureRadioConfigLst) {
		this.featureRadioConfigLst = featureRadioConfigLst;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public Integer getIana_id() {
		return iana_id;
	}

	public void setIana_id(Integer iana_id) {
		this.iana_id = iana_id;
	}

	public Integer getDevice_id() {
		return device_id;
	}

	public void setDevice_id(Integer device_id) {
		this.device_id = device_id;
	}

	public Integer getNetwork_id() {
		return network_id;
	}

	public void setNetwork_id(Integer network_id) {
		this.network_id = network_id;
	}

	public String getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(String organization_id) {
		this.organization_id = organization_id;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	/* features uploaded from devices */
	public String getFeatures() {
		return features;
	}

	public void setFeatures(String features) {
		this.features = features;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FeatureGetObject [sn=");
		builder.append(sn);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", device_id=");
		builder.append(device_id);
		builder.append(", network_id=");
		builder.append(network_id);
		builder.append(", organization_id=");
		builder.append(organization_id);
		builder.append(", sid=");
		builder.append(sid);
		builder.append(", features=");
		builder.append(features);
		builder.append(", featureRadioConfigLst=");
		builder.append(featureRadioConfigLst);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}
	
}
