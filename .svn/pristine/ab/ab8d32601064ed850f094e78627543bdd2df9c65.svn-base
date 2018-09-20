package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.List;

public class ConfigSettingsObject extends PoolObject implements PoolObjectIf, Serializable
{
	/* key */
	private String sn;
	private Integer iana_id;
	/* value */
	List<SsidProfiles> ssidProfilesLst;

	@Override
	public String getKey() 
	{
		return this.getClass().getSimpleName() + "sn_pk" + getSn() + "|" + getIana_id();
	}

	@Override
	public void setKey(Integer iana_id, String sn) 
	{
		this.iana_id = iana_id;
		this.sn = sn;
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

	public List<SsidProfiles> getSsidProfilesLst() {
		return ssidProfilesLst;
	}

	public void setSsidProfilesLst(List<SsidProfiles> ssidProfilesLst) {
		this.ssidProfilesLst = ssidProfilesLst;
	}

	public class SsidProfiles implements Serializable
	{
		private Integer id;
		private String ssid;
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getSsid() {
			return ssid;
		}
		public void setSsid(String ssid) {
			this.ssid = ssid;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("SsidProfile [id=");
			builder.append(id);
			builder.append(", ssid=");
			builder.append(ssid);
			builder.append("]");
			return builder.toString();
		}
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConfigSettingsObject [sn=");
		builder.append(sn);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", ssidProfilesLst=");
		builder.append(ssidProfilesLst);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}
}
