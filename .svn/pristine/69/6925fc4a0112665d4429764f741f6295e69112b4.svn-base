package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.WtpMsgHandler;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.Devices.ONLINE_STATUS;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.pool.object.utils.DeviceUtils;

public class DevOnlineObject extends PoolObject implements PoolObjectIf, Serializable {

	/* key */
	private String sn;
	private Integer iana_id;
	/* value */
	private String machine_id;
	private Integer device_id;
	private Integer network_id;
	private String organization_id;
	private String sid; // caller and server reference
	
	private String model;
	private String variant;
	private String product_code;
	private String product_name;
	private String fw_ver;
	private String lan_mac;
	private String wan_ip;
	private boolean isDevDetailJson = false;
	
	private CopyOnWriteArrayList<LanList> lan_list;
	private GpsLocation gps_location;
	private String conf_checksum;
	private String cert_checksum;
	private Long uptime;
	private Date updateUptimeDate;
	//private ConfChecksum conf_checksum_class;
	
	private Integer lastAcTime;
	private Date lastUpdateTime;
	private Date lastUpdateTime_DevUsage;
	
	private boolean isOnline;
	private Integer acRedirectedHostTime;
	private Boolean ddns_enabled;
	private boolean isRebuild = false;
	private String wtp_ip = "";
	private BootFlash bootflash;
	
	private ONLINE_STATUS status = ONLINE_STATUS.UNKNOWN;

	public void retainPropertiesFromExistingOnlineObject(DevOnlineObject devO)
	{
		this.iana_id = devO.getIana_id();
		this.sn = devO.getSn();
		this.machine_id = ACService.getServerName();
		this.device_id = devO.getDevice_id();
		this.network_id = devO.getNetwork_id();
		this.organization_id = devO.getOrganization_id();
		this.lastUpdateTime = DateUtils.getUtcDate();
		this.status = devO.getStatus();
		this.isOnline = devO.isOnline;
		this.lastAcTime = devO.getLastAcTime();
		this.isRebuild = false;
	}
	
	public void rebuildInfo(String orgId, Devices dev)
	{
		if (dev==null)
			return;
		
		this.iana_id = dev.getIanaId();
		this.sn = dev.getSn();
		this.machine_id = ACService.getServerName();
		this.device_id = dev.getId();
		this.network_id = dev.getNetworkId();
		this.organization_id = orgId;
		
		this.fw_ver = dev.getFw_ver();
		this.lastUpdateTime = DateUtils.getUtcDate();
		this.conf_checksum = dev.getConfigChecksum();
		this.status = DeviceUtils.getDevOnlineStatus(dev);
		this.lastAcTime = 0;
		
		if (this.status == ONLINE_STATUS.UNKNOWN)
		{
			if (!WtpMsgHandler.isDeviceWarrantValid(dev)) 
			{			
				this.status = ONLINE_STATUS.WARRANTY_EXPIRED;
			}
			else if (dev.getFirstAppear()==null)
			{
				this.status = ONLINE_STATUS.NEVER_ONLINE;
			}
		}
		
		this.isRebuild = true;
	}
	
	public void updateCoreInfo(String orgId, Devices dev)
	{
		this.iana_id = dev.getIanaId();
		this.sn = dev.getSn();
		this.machine_id = ACService.getServerName();
		this.device_id = dev.getId();
		this.network_id = dev.getNetworkId();
		this.organization_id = orgId;
	}
	
	public Devices.ONLINE_STATUS getStatus() {
		return status;
	}

	public void setStatus(Devices.ONLINE_STATUS status) {
		this.status = status;
	}
	
	public class BootFlash implements Serializable {
		private String fw0;
		private String fw1;
		Integer active;

		public String getFw0() {
			return fw0;
		}

		public void setFw0(String fw0) {
			this.fw0 = fw0;
		}

		public String getFw1() {
			return fw1;
		}

		public void setFw1(String fw1) {
			this.fw1 = fw1;
		}

		public Integer getActive() {
			return active;
		}

		public void setActive(Integer active) {
			this.active = active;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("BootFlash [fw0=");
			builder.append(fw0);
			builder.append(", fw1=");
			builder.append(fw1);
			builder.append(", active=");
			builder.append(active);
			builder.append("]");
			return builder.toString();
		}
	}

	public class LanList implements Serializable {
		private Integer vlan_id;
		private String vlan_ip;
		private String netmask;
//		private Integer stationz;
		public Integer getVlan_id() {
			return vlan_id;
		}
		public void setVlan_id(Integer vlan_id) {
			this.vlan_id = vlan_id;
		}
		public String getVlan_ip() {
			return vlan_ip;
		}
		public void setVlan_ip(String vlan_ip) {
			this.vlan_ip = vlan_ip;
		}
		public String getNetmask() {
			return netmask;
		}
		public void setNetmask(String netmask) {
			this.netmask = netmask;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("LanList [vlan_id=");
			builder.append(vlan_id);
			builder.append(", vlan_ip=");
			builder.append(vlan_ip);
			builder.append(", netmask=");
			builder.append(netmask);
			builder.append("]");
			return builder.toString();
		}		
	}
	
	public class GpsLocation implements Serializable{
		private Float latitude;
		private Float longitude;
		private Integer timestamp;
		
		public Float getLatitude() {
			return latitude;
		}
		public void setLatitude(Float latitude) {
			this.latitude = latitude;
		}
		public Float getLongitude() {
			return longitude;
		}
		public void setLongitude(Float longitude) {
			this.longitude = longitude;
		}
		public Integer getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(Integer timestamp) {
			this.timestamp = timestamp;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("GpsLocation [latitude=");
			builder.append(latitude);
			builder.append(", longitude=");
			builder.append(longitude);
			builder.append(", timestamp=");
			builder.append(timestamp);
			builder.append("]");
			return builder.toString();
		}
	}
	
	public class ConfChecksum implements Serializable{
		private String type;
		private String conf_checksum;
		private String cert_checksum;
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getConf_checksum() {
			return conf_checksum;
		}
		public void setConf_checksum(String conf_checksum) {
			this.conf_checksum = conf_checksum;
		}
		public String getCert_checksum() {
			return cert_checksum;
		}
		public void setCert_checksum(String cert_checksum) {
			this.cert_checksum = cert_checksum;
		}
		
	}
	
	@Override
	public void setKey(Integer iana_id, String sn) {
		this.iana_id = iana_id;
		this.sn = sn;
	}
	
	@Override
	public String getKey()
	{
		return this.getClass().getSimpleName() + "sn_pk" + getSn() + "|" + getIana_id();
	}

	public boolean isDevDetailJson() {
		return isDevDetailJson;
	}

	public void setDevDetailJson(boolean isDevDetailJson) {
		this.isDevDetailJson = isDevDetailJson;
	}

	public Integer getNetwork_id() {
		return network_id;
	}

	public void setNetwork_id(Integer network_id) {
		this.network_id = network_id;
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

	public String getMachine_id() {
		return machine_id;
	}

	public void setMachine_id(String machine_id) {
		this.machine_id = machine_id;
	}

	public Integer getDevice_id() {
		return device_id;
	}

	public void setDevice_id(Integer device_id) {
		this.device_id = device_id;
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

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getVariant() {
		return variant;
	}

	public void setVariant(String variant) {
		this.variant = variant;
	}

	public String getProduct_code() {
		return product_code;
	}

	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getFw_ver() {
		return fw_ver;
	}

	public void setFw_ver(String fw_ver) {
		this.fw_ver = fw_ver;
	}

	public String getLan_mac() {
		return lan_mac;
	}

	public void setLan_mac(String lan_mac) {
		this.lan_mac = lan_mac;
	}

	public String getWan_ip() {
		return wan_ip;
	}

	public void setWan_ip(String wan_ip) {
		this.wan_ip = wan_ip;
	}

	public CopyOnWriteArrayList<LanList> getLan_list() {
		return lan_list;
	}

	public void setLan_list(CopyOnWriteArrayList<LanList> lan_list) {
		this.lan_list = lan_list;
	}

	public GpsLocation getGps_location() {
		return gps_location;
	}

	public void setGps_location(GpsLocation gps_location) {
		this.gps_location = gps_location;
	}
	
	public String getConf_checksum() {
		return conf_checksum;
	}

	public void setConf_checksum(String conf_checksum) {
		this.conf_checksum = conf_checksum;
	}

	public String getCert_checksum() {
		return cert_checksum;
	}

	public void setCert_checksum(String cert_checksum) {
		this.cert_checksum = cert_checksum;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	
	public Integer getLastAcTime() {
		return lastAcTime;
	}

	public void setLastAcTime(Integer lastAcTime) {
		this.lastAcTime = lastAcTime;
	}
	
	public boolean isOnline() {
		return isOnline;
	}
	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}
	public Date getLastUpdateTime_DevUsage() {
		return lastUpdateTime_DevUsage;
	}

	public void setLastUpdateTime_DevUsage(Date lastUpdateTime_DevUsage) {
		this.lastUpdateTime_DevUsage = lastUpdateTime_DevUsage;
	}


	public Long getUptime() {
		return uptime;
	}

	public void setUptime(Long uptime) {
		this.uptime = uptime;
	}

	public Date getUpdateUptimeDate() {
		return updateUptimeDate;
	}

	public void setUpdateUptimeDate(Date updateUptimeDate) {
		this.updateUptimeDate = updateUptimeDate;
	}

	public Boolean getDdns_enabled() {
		return ddns_enabled;
	}

	public void setDdns_enabled(Boolean ddns_enabled) {
		this.ddns_enabled = ddns_enabled;
	}
	
	public Integer getAcRedirectedHostTime() {
		return acRedirectedHostTime;
	}

	public void setAcRedirectedHostTime(Integer acRedirectedHostTime) {
		this.acRedirectedHostTime = acRedirectedHostTime;
	}
	
	public boolean isRebuild() {
		return isRebuild;
	}

	public void setRebuild(boolean isRebuild) {
		this.isRebuild = isRebuild;
	}
	
	public String getWtp_ip() {
		return wtp_ip;
	}

	public void setWtp_ip(String wtp_ip) {
		this.wtp_ip = wtp_ip;
	}

	public BootFlash getBootflash() {
		return bootflash;
	}

	public void setBootflash(BootFlash bootflash) {
		this.bootflash = bootflash;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DevOnlineObject [sn=");
		builder.append(sn);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", machine_id=");
		builder.append(machine_id);
		builder.append(", device_id=");
		builder.append(device_id);
		builder.append(", network_id=");
		builder.append(network_id);
		builder.append(", organization_id=");
		builder.append(organization_id);
		builder.append(", sid=");
		builder.append(sid);
		builder.append(", model=");
		builder.append(model);
		builder.append(", variant=");
		builder.append(variant);
		builder.append(", product_code=");
		builder.append(product_code);
		builder.append(", product_name=");
		builder.append(product_name);
		builder.append(", fw_ver=");
		builder.append(fw_ver);
		builder.append(", lan_mac=");
		builder.append(lan_mac);
		builder.append(", wan_ip=");
		builder.append(wan_ip);
		builder.append(", isDevDetailJson=");
		builder.append(isDevDetailJson);
		builder.append(", lan_list=");
		builder.append(lan_list);
		builder.append(", gps_location=");
		builder.append(gps_location);
		builder.append(", conf_checksum=");
		builder.append(conf_checksum);
		builder.append(", cert_checksum=");
		builder.append(cert_checksum);
		builder.append(", uptime=");
		builder.append(uptime);
		builder.append(", updateUptimeDate=");
		builder.append(updateUptimeDate);
		builder.append(", lastAcTime=");
		builder.append(lastAcTime);
		builder.append(", lastUpdateTime=");
		builder.append(lastUpdateTime);
		builder.append(", lastUpdateTime_DevUsage=");
		builder.append(lastUpdateTime_DevUsage);
		builder.append(", isOnline=");
		builder.append(isOnline);
		builder.append(", acRedirectedHostTime=");
		builder.append(acRedirectedHostTime);
		builder.append(", ddns_enabled=");
		builder.append(ddns_enabled);
		builder.append(", isRebuild=");
		builder.append(isRebuild);
		builder.append(", wtp_ip=");
		builder.append(wtp_ip);
		builder.append(", bootflash=");
		builder.append(bootflash);
		builder.append(", status=");
		builder.append(status);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}

}
