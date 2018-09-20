package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.logging.Logger;

public class DevDetailJsonObject extends PoolObject implements PoolObjectIf, Serializable {

	private static final Logger logger = Logger.getLogger(DevDetailJsonObject.class);
	
	/* key */
	private String sn;
	private Integer iana_id;
	/* value */
	private Integer device_id;
	private Integer network_id;
	private String organization_id;
	private String sid; // caller and server reference
	
	private DevInfo dev_info;
	private GPS gps;
	private XmlWanList xml_wan_list;
	private CopyOnWriteArrayList<LanList> lan_list;
	private CopyOnWriteArrayList<WlanMacList> wlan_mac_list;
	private StationZ stationz;
	private CopyOnWriteArrayList<WanList> wan_list;
	

	private CopyOnWriteArrayList<JsonDeviceLocations> locations;
	private JsonDeviceLocations point;
	private CopyOnWriteArrayList<JsonDeviceInterfaces> interfaces;
	private CopyOnWriteArrayList<JsonDeviceVlanInterfaces> vlan_interfaces;
	
	public JsonDeviceLocations getPoint() {
		return point;
	}

	public void setPoint(JsonDeviceLocations point) {
		this.point = point;
	}

	public CopyOnWriteArrayList<JsonDeviceLocations> getLocations() {
		return locations;
	}

	public void setLocations(CopyOnWriteArrayList<JsonDeviceLocations> locations) {
		this.locations = locations;
	}

	public CopyOnWriteArrayList<JsonDeviceInterfaces> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(CopyOnWriteArrayList<JsonDeviceInterfaces> interfaces) {
		this.interfaces = interfaces;
	}

	public CopyOnWriteArrayList<JsonDeviceVlanInterfaces> getVlan_interfaces() {
		return vlan_interfaces;
	}

	public void setVlan_interfaces(
			CopyOnWriteArrayList<JsonDeviceVlanInterfaces> vlan_interfaces) {
		this.vlan_interfaces = vlan_interfaces;
	}

	public class DevInfo implements Serializable{
		private String product_name;
		private String model;
		private String variant;
		private String fw_ver;
		private String product_code;
		public String getProduct_name() {
			return product_name;
		}
		public void setProduct_name(String product_name) {
			this.product_name = product_name;
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
		public String getFw_ver() {
			return fw_ver;
		}
		public void setFw_ver(String fw_ver) {
			this.fw_ver = fw_ver;
		}
		public String getProduct_code() {
			return product_code;
		}
		public void setProduct_code(String product_code) {
			this.product_code = product_code;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("DevInfo [product_name=");
			builder.append(product_name);
			builder.append(", model=");
			builder.append(model);
			builder.append(", variant=");
			builder.append(variant);
			builder.append(", fw_ver=");
			builder.append(fw_ver);
			builder.append(", product_code=");
			builder.append(product_code);
			builder.append(", getClass()=");
			builder.append(getClass());
			builder.append(", hashCode()=");
			builder.append(hashCode());
			builder.append(", toString()=");
			builder.append(super.toString());
			builder.append("]");
			return builder.toString();
		}		
	}
	
	public class GPS implements Serializable{
		private Float latitude;
		private Float longtitude;
		private Integer timestamp;
		
		public Float getLatitude() {
			return latitude;
		}
		public void setLatitude(Float latitude) {
			this.latitude = latitude;
		}
		public Float getLongtitude() {
			return longtitude;
		}
		public void setLongtitude(Float longtitude) {
			this.longtitude = longtitude;
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
			builder.append("GPS [latitude=");
			builder.append(latitude);
			builder.append(", longtitude=");
			builder.append(longtitude);
			builder.append(", timestamp=");
			builder.append(timestamp);
			builder.append(", getClass()=");
			builder.append(getClass());
			builder.append(", hashCode()=");
			builder.append(hashCode());
			builder.append(", toString()=");
			builder.append(super.toString());
			builder.append("]");
			return builder.toString();
		}
	}
	
	public class XmlWanList implements Serializable{
		private String xml;

		public String getXml() {
			return xml;
		}

		public void setXml(String xml) {
			this.xml = xml;
		}
	}
	
	public class LanList implements Serializable{
		private Integer vlan_id;
		private String vlan_ip;
		private String netmask;		
		
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
			builder.append(", getClass()=");
			builder.append(getClass());
			builder.append(", hashCode()=");
			builder.append(hashCode());
			builder.append(", toString()=");
			builder.append(super.toString());
			builder.append("]");
			return builder.toString();
		}
		
	}
	
	public class WlanMacList implements Serializable{
		private String bssid;
		private String essid;
		private boolean enabled;
		private String encryption;
		
		public String getBssid() {
			return bssid;
		}
		public void setBssid(String bssid) {
			this.bssid = bssid;
		}
		public String getEssid() {
			return essid;
		}
		public void setEssid(String essid) {
			this.essid = essid;
		}
		public boolean isEnabled() {
			return enabled;
		}
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
		public String getEncryption() {
			return encryption;
		}
		public void setEncryption(String encryption) {
			this.encryption = encryption;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("WlanMacList [bssid=");
			builder.append(bssid);
			builder.append(", essid=");
			builder.append(essid);
			builder.append(", enabled=");
			builder.append(enabled);
			builder.append(", encryption=");
			builder.append(encryption);
			builder.append(", getClass()=");
			builder.append(getClass());
			builder.append(", hashCode()=");
			builder.append(hashCode());
			builder.append(", toString()=");
			builder.append(super.toString());
			builder.append("]");
			return builder.toString();
		}
	}
	
	public class StationZ implements Serializable{
		private Integer total;
		private Integer eth;
		private Integer wlan;
		private Integer mvpn;
		private Integer stat_ro;
		private Integer pptp;
		private Integer others;
		public Integer getTotal() {
			return total;
		}
		public void setTotal(Integer total) {
			this.total = total;
		}
		public Integer getEth() {
			return eth;
		}
		public void setEth(Integer eth) {
			this.eth = eth;
		}
		public Integer getWlan() {
			return wlan;
		}
		public void setWlan(Integer wlan) {
			this.wlan = wlan;
		}
		public Integer getMvpn() {
			return mvpn;
		}
		public void setMvpn(Integer mvpn) {
			this.mvpn = mvpn;
		}
		public Integer getStat_ro() {
			return stat_ro;
		}
		public void setStat_ro(Integer stat_ro) {
			this.stat_ro = stat_ro;
		}
		public Integer getPptp() {
			return pptp;
		}
		public void setPptp(Integer pptp) {
			this.pptp = pptp;
		}
		public Integer getOthers() {
			return others;
		}
		public void setOthers(Integer others) {
			this.others = others;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("StationZ [total=");
			builder.append(total);
			builder.append(", eth=");
			builder.append(eth);
			builder.append(", wlan=");
			builder.append(wlan);
			builder.append(", mvpn=");
			builder.append(mvpn);
			builder.append(", stat_ro=");
			builder.append(stat_ro);
			builder.append(", pptp=");
			builder.append(pptp);
			builder.append(", others=");
			builder.append(others);
			builder.append(", getClass()=");
			builder.append(getClass());
			builder.append(", hashCode()=");
			builder.append(hashCode());
			builder.append(", toString()=");
			builder.append(super.toString());
			builder.append("]");
			return builder.toString();
		}
	}
	public class WanList implements Serializable{
		private Integer id;
		private String type;
		private String status_message;
		private String status_led;
		private Integer mtu;
		private Integer is_overall_up;
		private Integer standby_state;
		private Integer mtu_state;
		private Integer healthy_state;
		private Integer connection_state;
		private Integer physical_state;
		private Integer is_backup;
		private Integer is_quota_exceed;
		private Integer is_manual_disconnect;
		private Integer is_enable;
		private Integer conn_method;
		private Integer port_type;
		
		private String mac;
		private String name;
		private String ddns_host;
		private String ip;
		private String conn_config_method;
		private String conn_mode;
		private String gateway;
		private String dns_server;
		private Integer mtu_config;
		private String standby_mode;
		private String healthcheck;
		private List<Sims> sims;
		private String meid_hex;
		private String meid_dec;
		private String esn;
		private String imei;
		private Integer signal;
		private String carrier_name;
		private String adaptor_type;
		private Integer vendor_id;
		private Integer product_id;
		private String modem_name;
		private String modem_manufacturer;
		private String carrier_country;
		
		private String apn_auto;
		private String username_auto;
		private String password_auto;
		private String dialnum_auto;
		
		private String apn_custom;
		private String username_custom;
		private String password_custom;
		private String dialnum_custom;
		
		private String s2g3glte;
		
		private Integer group;
		
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}		
		public String getStatus_message() {
			return status_message;
		}
		public void setStatus_message(String status_message) {
			this.status_message = status_message;
		}
		public String getStatus_led() {
			return status_led;
		}
		public void setStatus_led(String status_led) {
			this.status_led = status_led;
		}
		public Integer getMtu() {
			return mtu;
		}
		public void setMtu(Integer mtu) {
			this.mtu = mtu;
		}
		public Integer getIs_overall_up() {
			return is_overall_up;
		}
		public void setIs_overall_up(Integer is_overall_up) {
			this.is_overall_up = is_overall_up;
		}
		public Integer getStandby_state() {
			return standby_state;
		}
		public void setStandby_state(Integer standby_state) {
			this.standby_state = standby_state;
		}
		public Integer getMtu_state() {
			return mtu_state;
		}
		public void setMtu_state(Integer mtu_state) {
			this.mtu_state = mtu_state;
		}
		public Integer getHealthy_state() {
			return healthy_state;
		}
		public void setHealthy_state(Integer healthy_state) {
			this.healthy_state = healthy_state;
		}
		public Integer getConnection_state() {
			return connection_state;
		}
		public void setConnection_state(Integer connection_state) {
			this.connection_state = connection_state;
		}
		public Integer getPhysical_state() {
			return physical_state;
		}
		public void setPhysical_state(Integer physical_state) {
			this.physical_state = physical_state;
		}
		public Integer getIs_backup() {
			return is_backup;
		}
		public void setIs_backup(Integer is_backup) {
			this.is_backup = is_backup;
		}
		public Integer getIs_quota_exceed() {
			return is_quota_exceed;
		}
		public void setIs_quota_exceed(Integer is_quota_exceed) {
			this.is_quota_exceed = is_quota_exceed;
		}
		public Integer getIs_manual_disconnect() {
			return is_manual_disconnect;
		}
		public void setIs_manual_disconnect(Integer is_manual_disconnect) {
			this.is_manual_disconnect = is_manual_disconnect;
		}
		public Integer getIs_enable() {
			return is_enable;
		}
		public void setIs_enable(Integer is_enable) {
			this.is_enable = is_enable;
		}
		public Integer getConn_method() {
			return conn_method;
		}
		public void setConn_method(Integer conn_method) {
			this.conn_method = conn_method;
		}
		public Integer getPort_type() {
			return port_type;
		}
		public void setPort_type(Integer port_type) {
			this.port_type = port_type;
		}
		public String getMac() {
			return mac;
		}
		public void setMac(String mac) {
			this.mac = mac;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDdns_host() {
			return ddns_host;
		}
		public void setDdns_host(String ddns_host) {
			this.ddns_host = ddns_host;
		}
		public String getIp() {
			return ip;
		}
		public void setIp(String ip) {
			this.ip = ip;
		}
		public String getConn_config_method() {
			return conn_config_method;
		}
		public void setConn_config_method(String conn_config_method) {
			this.conn_config_method = conn_config_method;
		}
		public String getConn_mode() {
			return conn_mode;
		}
		public void setConn_mode(String conn_mode) {
			this.conn_mode = conn_mode;
		}
		public String getGateway() {
			return gateway;
		}
		public void setGateway(String gateway) {
			this.gateway = gateway;
		}
		public String getDns_server() {
			return dns_server;
		}
		public void setDns_server(String dns_server) {
			this.dns_server = dns_server;
		}
		public Integer getMtu_config() {
			return mtu_config;
		}
		public void setMtu_config(Integer mtu_config) {
			this.mtu_config = mtu_config;
		}
		public String getStandby_mode() {
			return standby_mode;
		}
		public void setStandby_mode(String standby_mode) {
			this.standby_mode = standby_mode;
		}
		public String getHealthcheck() {
			return healthcheck;
		}
		public void setHealthcheck(String healthcheck) {
			this.healthcheck = healthcheck;
		}
		public List<Sims> getSims() {
			return sims;
		}
		public void setSims(List<Sims> sims) {
			this.sims = sims;
		}
		public String getMeid_hex() {
			return meid_hex;
		}
		public void setMeid_hex(String meid_hex) {
			this.meid_hex = meid_hex;
		}
		public String getMeid_dec() {
			return meid_dec;
		}
		public void setMeid_dec(String meid_dec) {
			this.meid_dec = meid_dec;
		}
		public String getEsn() {
			return esn;
		}
		public void setEsn(String esn) {
			this.esn = esn;
		}
		public String getImei() {
			return imei;
		}
		public void setImei(String imei) {
			this.imei = imei;
		}
		public Integer getSignal() {
			return signal;
		}
		public void setSignal(Integer signal) {
			this.signal = signal;
		}
		public String getCarrier_name() {
			return carrier_name;
		}
		public void setCarrier_name(String carrier_name) {
			this.carrier_name = carrier_name;
		}
		public String getAdaptor_type() {
			return adaptor_type;
		}
		public void setAdaptor_type(String adaptor_type) {
			this.adaptor_type = adaptor_type;
		}
		public Integer getVendor_id() {
			return vendor_id;
		}
		public void setVendor_id(Integer vendor_id) {
			this.vendor_id = vendor_id;
		}
		public Integer getProduct_id() {
			return product_id;
		}
		public void setProduct_id(Integer product_id) {
			this.product_id = product_id;
		}
		public String getModem_name() {
			return modem_name;
		}
		public void setModem_name(String modem_name) {
			this.modem_name = modem_name;
		}
		public String getModem_manufacturer() {
			return modem_manufacturer;
		}
		public void setModem_manufacturer(String modem_manufacturer) {
			this.modem_manufacturer = modem_manufacturer;
		}
		public String getCarrier_country() {
			return carrier_country;
		}
		public void setCarrier_country(String carrier_country) {
			this.carrier_country = carrier_country;
		}
		public String getApn_auto() {
			return apn_auto;
		}
		public void setApn_auto(String apn_auto) {
			this.apn_auto = apn_auto;
		}
		public String getUsername_auto() {
			return username_auto;
		}
		public void setUsername_auto(String username_auto) {
			this.username_auto = username_auto;
		}
		public String getPassword_auto() {
			return password_auto;
		}
		public void setPassword_auto(String password_auto) {
			this.password_auto = password_auto;
		}
		public String getDialnum_auto() {
			return dialnum_auto;
		}
		public void setDialnum_auto(String dialnum_auto) {
			this.dialnum_auto = dialnum_auto;
		}
		public String getApn_custom() {
			return apn_custom;
		}
		public void setApn_custom(String apn_custom) {
			this.apn_custom = apn_custom;
		}
		public String getUsername_custom() {
			return username_custom;
		}
		public void setUsername_custom(String username_custom) {
			this.username_custom = username_custom;
		}
		public String getPassword_custom() {
			return password_custom;
		}
		public void setPassword_custom(String password_custom) {
			this.password_custom = password_custom;
		}
		public String getDialnum_custom() {
			return dialnum_custom;
		}
		public void setDialnum_custom(String dialnum_custom) {
			this.dialnum_custom = dialnum_custom;
		}
		public String getS2g3glte() {
			return s2g3glte;
		}
		public void setS2g3glte(String s2g3glte) {
			this.s2g3glte = s2g3glte;
		}
		public Integer getGroup() {
			return group;
		}
		public void setGroup(Integer group) {
			this.group = group;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("WanList [id=");
			builder.append(id);
			builder.append(", type=");
			builder.append(type);
			builder.append(", status_message=");
			builder.append(status_message);
			builder.append(", status_led=");
			builder.append(status_led);
			builder.append(", mtu=");
			builder.append(mtu);
			builder.append(", is_overall_up=");
			builder.append(is_overall_up);
			builder.append(", standby_state=");
			builder.append(standby_state);
			builder.append(", mtu_state=");
			builder.append(mtu_state);
			builder.append(", healthy_state=");
			builder.append(healthy_state);
			builder.append(", connection_state=");
			builder.append(connection_state);
			builder.append(", physical_state=");
			builder.append(physical_state);
			builder.append(", is_backup=");
			builder.append(is_backup);
			builder.append(", is_quota_exceed=");
			builder.append(is_quota_exceed);
			builder.append(", is_manual_disconnect=");
			builder.append(is_manual_disconnect);
			builder.append(", is_enable=");
			builder.append(is_enable);
			builder.append(", conn_method=");
			builder.append(conn_method);
			builder.append(", port_type=");
			builder.append(port_type);
			builder.append(", mac=");
			builder.append(mac);
			builder.append(", name=");
			builder.append(name);
			builder.append(", ddns_host=");
			builder.append(ddns_host);
			builder.append(", ip=");
			builder.append(ip);
			builder.append(", conn_config_method=");
			builder.append(conn_config_method);
			builder.append(", conn_mode=");
			builder.append(conn_mode);
			builder.append(", gateway=");
			builder.append(gateway);
			builder.append(", dns_server=");
			builder.append(dns_server);
			builder.append(", mtu_config=");
			builder.append(mtu_config);
			builder.append(", standby_mode=");
			builder.append(standby_mode);
			builder.append(", healthcheck=");
			builder.append(healthcheck);
			builder.append(", sims=");
			builder.append(sims);
			builder.append(", meid_hex=");
			builder.append(meid_hex);
			builder.append(", meid_dec=");
			builder.append(meid_dec);
			builder.append(", esn=");
			builder.append(esn);
			builder.append(", imei=");
			builder.append(imei);
			builder.append(", signal=");
			builder.append(signal);
			builder.append(", carrier_name=");
			builder.append(carrier_name);
			builder.append(", adaptor_type=");
			builder.append(adaptor_type);
			builder.append(", vendor_id=");
			builder.append(vendor_id);
			builder.append(", product_id=");
			builder.append(product_id);
			builder.append(", modem_name=");
			builder.append(modem_name);
			builder.append(", modem_manufacturer=");
			builder.append(modem_manufacturer);
			builder.append(", carrier_country=");
			builder.append(carrier_country);
			builder.append(", apn_auto=");
			builder.append(apn_auto);
			builder.append(", username_auto=");
			builder.append(username_auto);
			builder.append(", password_auto=");
			builder.append(password_auto);
			builder.append(", dialnum_auto=");
			builder.append(dialnum_auto);
			builder.append(", apn_custom=");
			builder.append(apn_custom);
			builder.append(", username_custom=");
			builder.append(username_custom);
			builder.append(", password_custom=");
			builder.append(password_custom);
			builder.append(", dialnum_custom=");
			builder.append(dialnum_custom);
			builder.append(", s2g3glte=");
			builder.append(s2g3glte);
			builder.append(", group=");
			builder.append(group);
			builder.append(", getClass()=");
			builder.append(getClass());
			builder.append(", hashCode()=");
			builder.append(hashCode());
			builder.append(", toString()=");
			builder.append(super.toString());
			builder.append("]");
			return builder.toString();
		}
				
	}
	
	public class Sims implements Serializable{
		private String sims_imsi;
		private String sims_status;
		
		public String getSims_imsi() {
			return sims_imsi;
		}
		public void setSims_imsi(String sims_imsi) {
			this.sims_imsi = sims_imsi;
		}
		public String getSims_status() {
			return sims_status;
		}
		public void setSims_status(String sims_status) {
			this.sims_status = sims_status;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Sims [sims_imsi=");
			builder.append(sims_imsi);
			builder.append(", sims_status=");
			builder.append(sims_status);
			builder.append(", getClass()=");
			builder.append(getClass());
			builder.append(", hashCode()=");
			builder.append(hashCode());
			builder.append(", toString()=");
			builder.append(super.toString());
			builder.append("]");
			return builder.toString();
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

	public DevInfo getDev_info() {
		return dev_info;
	}

	public void setDev_info(DevInfo dev_info) {
		this.dev_info = dev_info;
	}

	public GPS getGps() {
		return gps;
	}

	public void setGps(GPS gps) {
		this.gps = gps;
	}

	public XmlWanList getXml_wan_list() {
		return xml_wan_list;
	}

	public void setXml_wan_list(XmlWanList xml_wan_list) {
		this.xml_wan_list = xml_wan_list;
	}

	public StationZ getStationz() {
		return stationz;
	}

	public void setStationz(StationZ stationz) {
		this.stationz = stationz;
	}

	public CopyOnWriteArrayList<LanList> getLan_list() {
		return lan_list;
	}

	public void setLan_list(CopyOnWriteArrayList<LanList> lan_list) {
		this.lan_list = lan_list;
	}

	public CopyOnWriteArrayList<WlanMacList> getWlan_mac_list() {
		return wlan_mac_list;
	}

	public void setWlan_mac_list(CopyOnWriteArrayList<WlanMacList> wlan_mac_list) {
		this.wlan_mac_list = wlan_mac_list;
	}

	public CopyOnWriteArrayList<WanList> getWan_list() {
		return wan_list;
	}

	public void setWan_list(CopyOnWriteArrayList<WanList> wan_list) {
		this.wan_list = wan_list;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DevDetailJsonObject [sn=");
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
		builder.append(", dev_info=");
		builder.append(dev_info);
		builder.append(", gps=");
		builder.append(gps);
		builder.append(", xml_wan_list=");
		builder.append(xml_wan_list);
		builder.append(", lan_list=");
		builder.append(lan_list);
		builder.append(", wlan_mac_list=");
		builder.append(wlan_mac_list);
		builder.append(", stationz=");
		builder.append(stationz);
		builder.append(", wan_list=");
		builder.append(wan_list);
		builder.append(", locations=");
		builder.append(locations);
		builder.append(", point=");
		builder.append(point);
		builder.append(", interfaces=");
		builder.append(interfaces);
		builder.append(", vlan_interfaces=");
		builder.append(vlan_interfaces);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}
	
	public void fillWanListByXml(String Xml){
		if (Xml == null || Xml.isEmpty()){
			return;
		}
		
		CopyOnWriteArrayList<WanList> newWanList  = new CopyOnWriteArrayList<WanList>();
		String[] singleConnectionList = Xml.split("</connection>");
		int pos = 0;
		for (String singleConnection:singleConnectionList){
			
			String portPart = null;
			String configPart = null;
			String statusPart = null;
			String connectTestPart = null;
			
			Pattern pattern = Pattern.compile("connection id=");
			Matcher matcher = pattern.matcher(singleConnection);
			if (!matcher.find()){
				continue;
			}
			logger.debug("wan list xml = "+singleConnection);
			
			pattern = Pattern.compile("<port id=\'[\\d]+\'>(.+)</port>");
			matcher = pattern.matcher(singleConnection);
			if (matcher.find()){
				portPart = matcher.group(1);
			}else{
				continue;
			}	
			
			pattern = Pattern.compile("<config>(.+)</config>");
			matcher = pattern.matcher(singleConnection);
			if (matcher.find()){
				configPart = matcher.group(1);
			}else{
				continue;
			}
			
			pattern = Pattern.compile("<status>(.+)</status>");
			matcher = pattern.matcher(singleConnection);
			if (matcher.find()){
				statusPart = matcher.group(1);
			}else{
				continue;
			}
			
			pattern = Pattern.compile("<connstatus_v[\\d]+_test>(.+)</connstatus_v[\\d]+_test>");
			matcher = pattern.matcher(singleConnection);
			if (matcher.find()){
				connectTestPart = matcher.group(1);
			}else{
				continue;
			}
			
			WanList wan = new WanList();
			
			pos++;
			wan.setId(pos);
			
			pattern = Pattern.compile("<type>(\\w+)</type>");
			matcher = pattern.matcher(portPart);
			if (matcher.find()){
				wan.setType(matcher.group(1));
			}
			
			pattern = Pattern.compile("<status_message>([^<]+)");
			matcher = pattern.matcher(connectTestPart);
			if (matcher.find()){
				/*switch (matcher.group(1)){
					case "Standby":
					case "Connected":
						wan.setStatus("Online");
						break;
					case "Not Connected":					
					case "Link Down":
						wan.setStatus("Offline");
						break;
					case "Disabled":
						wan.setStatus("Disabled");
						break;
				}*/
				wan.setStatus_message(matcher.group(1));
			}
			
			pattern = Pattern.compile("<status_led>([^<]+)");
			matcher = pattern.matcher(connectTestPart);
			if (matcher.find()){
				wan.setStatus_led(matcher.group(1));
			}
			
			pattern = Pattern.compile("<mtu>([^<]+)</mtu>");
			matcher = pattern.matcher(connectTestPart);
			if (matcher.find()){
				wan.setMtu(Integer.valueOf(matcher.group(1)));
			}
			
			pattern = Pattern.compile("<is_overall_up>([^<]+)</is_overall_up>");
			matcher = pattern.matcher(connectTestPart);
			if (matcher.find()){
				wan.setIs_overall_up(Integer.valueOf(matcher.group(1)));
			}
			
			pattern = Pattern.compile("<standby_state>([^<]+)</standby_state>");
			matcher = pattern.matcher(connectTestPart);
			if (matcher.find()){
				wan.setStandby_state(Integer.valueOf(matcher.group(1)));
			}
			
			pattern = Pattern.compile("<mtu_state>([^<]+)</mtu_state>");
			matcher = pattern.matcher(connectTestPart);
			if (matcher.find()){
				wan.setMtu_state(Integer.valueOf(matcher.group(1)));
			}
			
			pattern = Pattern.compile("<healthy_state>([^<]+)</healthy_state>");
			matcher = pattern.matcher(connectTestPart);
			if (matcher.find()){
				wan.setHealthy_state(Integer.valueOf(matcher.group(1)));
			}
			
			pattern = Pattern.compile("<connection_state>([^<]+)</connection_state>");
			matcher = pattern.matcher(connectTestPart);
			if (matcher.find()){
				wan.setConnection_state(Integer.valueOf(matcher.group(1)));
			}
			
			pattern = Pattern.compile("<physical_state>([^<]+)</physical_state>");
			matcher = pattern.matcher(connectTestPart);
			if (matcher.find()){
				wan.setPhysical_state(Integer.valueOf(matcher.group(1)));
			}
			
			pattern = Pattern.compile("<is_backup>([^<]+)</is_backup>");
			matcher = pattern.matcher(connectTestPart);
			if (matcher.find()){
				wan.setIs_backup(Integer.valueOf(matcher.group(1)));
			}
			
			pattern = Pattern.compile("<is_quota_exceed>([^<]+)</is_quota_exceed>");
			matcher = pattern.matcher(connectTestPart);
			if (matcher.find()){
				wan.setIs_quota_exceed(Integer.valueOf(matcher.group(1)));
			}
			
			pattern = Pattern.compile("<is_manual_disconnect>([^<]+)</is_manual_disconnect>");
			matcher = pattern.matcher(connectTestPart);
			if (matcher.find()){
				wan.setIs_manual_disconnect(Integer.valueOf(matcher.group(1)));
			}
			
			pattern = Pattern.compile("<is_enable>([^<]+)</is_enable>");
			matcher = pattern.matcher(connectTestPart);
			if (matcher.find()){
				wan.setIs_enable(Integer.valueOf(matcher.group(1)));
			}
			
			pattern = Pattern.compile("<conn_method>([^<]+)</conn_method>");
			matcher = pattern.matcher(connectTestPart);
			if (matcher.find()){
				wan.setConn_method(Integer.valueOf(matcher.group(1)));
			}
			
			pattern = Pattern.compile("<port_type>([^<]+)</port_type>");
			matcher = pattern.matcher(connectTestPart);
			if (matcher.find()){
				wan.setPort_type(Integer.valueOf(matcher.group(1)));
			}
			
			pattern = Pattern.compile("<mac_addr default=\'((\\w{2}:){5}\\w{2})\'>");
			matcher = pattern.matcher(configPart);
			if (matcher.find()){
				wan.setMac(matcher.group(1));
			}
			
			pattern = Pattern.compile("<name>([^<]+)</name>");
			matcher = pattern.matcher(configPart);
			if (matcher.find()){
				wan.setName(matcher.group(1));
			}
			
			pattern = Pattern.compile("<host id=\"\\d\">([^<]+)");
			matcher = pattern.matcher(configPart);
			if (matcher.find()){
				wan.setDdns_host(matcher.group(1));
			}
			
			pattern = Pattern.compile("<ipaddr>([^<]+)</ipaddr>");
			matcher = pattern.matcher(statusPart);
			if (matcher.find()){
				wan.setIp(matcher.group(1));
			}
			
			pattern = Pattern.compile("<connection_config>(.+)</connection_config>");
			matcher = pattern.matcher(configPart);
			if (matcher.find()){
				Pattern innerPattern = Pattern.compile("<method>([^<]+)</method>");
				Matcher innerMatcher = innerPattern.matcher(matcher.group(1));
				if (innerMatcher.find()){
					wan.setConn_config_method(innerMatcher.group(1));
				}
			}
			
			wan.setConn_mode("NAT");
			
			pattern = Pattern.compile("<ip_forward/>");
			matcher = pattern.matcher(configPart);
			if (matcher.find()){
				wan.setConn_mode("IP Forward");
			}
			
			pattern = Pattern.compile("<dropin/>");
			matcher = pattern.matcher(configPart);
			if (matcher.find()){
				wan.setConn_mode("Dropin");
			}
			
			pattern = Pattern.compile("<gateway>([^<]+)</gateway>");
			matcher = pattern.matcher(statusPart);
			if (matcher.find()){
				wan.setGateway(matcher.group(1));
			}
			
			pattern = Pattern.compile("<dns>([^<]+)</dns>");
			matcher = pattern.matcher(statusPart);
			if (matcher.find()){
				wan.setDns_server(matcher.group(1));
			}
			
			pattern = Pattern.compile("<mtu>([^<]+)</mtu>");
			matcher = pattern.matcher(configPart);
			if (matcher.find()){
				wan.setMtu_config(Integer.valueOf(matcher.group(1)));
			}
			
			pattern = Pattern.compile("<hot_standby[/]*>");
			matcher = pattern.matcher(configPart);
			if (matcher.find()){
				wan.setStandby_mode("hot standby");
			}
			
			pattern = Pattern.compile("<healthcheck>(.+)</healthcheck>");
			matcher = pattern.matcher(configPart);
			if (matcher.find()){
				Pattern innerPattern = Pattern.compile("<method>([^<]+)</method>");
				Matcher innerMatcher = innerPattern.matcher(matcher.group(1));
				if (innerMatcher.find()){
					wan.setHealthcheck(innerMatcher.group(1));
				}
			}
			
			pattern = Pattern.compile("<sim id=\'[\\d]+\'>(.+)</sim>");
			matcher = pattern.matcher(portPart);
			if (matcher.find()){
				List<Sims> sims = new ArrayList<Sims>();
				String[] simList = matcher.group(1).split("</sim>");
				for (String sim:simList){
					Sims simItem = new Sims();
					Pattern innerPattern = Pattern.compile("<imsi>([^<]+)</imsi>");
					Matcher innerMatcher = innerPattern.matcher(sim);
					if (innerMatcher.find()){
						simItem.setSims_imsi(innerMatcher.group(1));			
					}			
					
					innerPattern = Pattern.compile("<active[/]*>");
					innerMatcher = innerPattern.matcher(sim);
					if (innerMatcher.find()){
						simItem.setSims_status("Active");
					}else{
						simItem.setSims_status("Inactive");
					}
					
					sims.add(simItem);
				}
				
				wan.setSims(sims);
			}
			
			pattern = Pattern.compile("<modem_details id='\\d'>(.+)</modem_details>");
			matcher = pattern.matcher(portPart);
			if (matcher.find()){
				List<Sims> sims = new ArrayList<Sims>();
				Sims simItem = new Sims();
				Pattern innerPattern = Pattern.compile("<imsi>([^<]+)</imsi>");
				Matcher innerMatcher = innerPattern.matcher(matcher.group(1));
				if (innerMatcher.find()){
					simItem.setSims_imsi(innerMatcher.group(1));			
				}			
				sims.add(simItem);
				wan.setSims(sims);				
			}
			

			
			pattern = Pattern.compile("<meid_hex>([^<]+)</meid_hex>");
			matcher = pattern.matcher(portPart);
			if (matcher.find()){
				wan.setMeid_hex(matcher.group(1));
			}
			
			pattern = Pattern.compile("<signal_strength>([^<]+)</signal_strength>");
			matcher = pattern.matcher(statusPart);
			if (matcher.find()){
				wan.setSignal(Integer.valueOf(matcher.group(1)));
			}
			
			pattern = Pattern.compile("<esn>([^<]+)</esn>");
			matcher = pattern.matcher(portPart);
			if (matcher.find()){
				wan.setEsn(matcher.group(1));
			}
			
			pattern = Pattern.compile("<imei>([^<]+)</imei>");
			matcher = pattern.matcher(portPart);
			if (matcher.find()){
				wan.setImei(matcher.group(1));
			}
			
			pattern = Pattern.compile("<carrier_name>([^<]+)</carrier_name>");
			matcher = pattern.matcher(portPart);
			if (matcher.find()){
				wan.setCarrier_name(matcher.group(1));
			}
			
			pattern = Pattern.compile("<carrier_country>([^<]+)</carrier_country>");
			matcher = pattern.matcher(portPart);
			if (matcher.find()){
				wan.setCarrier_country(matcher.group(1));
			}
			
			pattern = Pattern.compile("<adaptor_type id='\\d+'>([^<]+)</adaptor_type>");
			matcher = pattern.matcher(portPart);
			if (matcher.find()){
				wan.setAdaptor_type(matcher.group(1));
			}
			
			pattern = Pattern.compile("<vendor_id>([^<]+)</vendor_id>");
			matcher = pattern.matcher(portPart);
			if (matcher.find()){
				wan.setVendor_id(Integer.valueOf(matcher.group(1)));
			}
			
			pattern = Pattern.compile("<product_id>([^<]+)</product_id>");
			matcher = pattern.matcher(portPart);
			if (matcher.find()){
				wan.setProduct_id(Integer.valueOf(matcher.group(1)));
			}
			
			pattern = Pattern.compile("<modem_name>([^<]+)</modem_name>");
			matcher = pattern.matcher(portPart);
			if (matcher.find()){
				wan.setModem_name(matcher.group(1));
			}
			
			pattern = Pattern.compile("<modem_manufacturer>([^<]+)</modem_manufacturer>");
			matcher = pattern.matcher(portPart);
			if (matcher.find()){
				wan.setModem_manufacturer(matcher.group(1));
			}
			
			pattern = Pattern.compile("<s2g3glte>([^<]+)</s2g3glte>");
			matcher = pattern.matcher(portPart);
			if (matcher.find()){
				wan.setS2g3glte(matcher.group(1));
			}
			
			pattern = Pattern.compile("<carrier_auto>(.+)</carrier_auto>");
			matcher = pattern.matcher(portPart);
			if (matcher.find()){
				Pattern innerPattern = Pattern.compile("<apn>(.*)</apn>");
				Matcher innerMatcher = innerPattern.matcher(matcher.group(1));
				if (innerMatcher.find()){
					wan.setApn_auto(innerMatcher.group(1));
				}
				
				innerPattern = Pattern.compile("<username>(.*)</username>");
				innerMatcher = innerPattern.matcher(matcher.group(1));
				if (innerMatcher.find()){
					wan.setUsername_auto(innerMatcher.group(1));
				}
				
				innerPattern = Pattern.compile("<password>(.*)</password>");
				innerMatcher = innerPattern.matcher(matcher.group(1));
				if (innerMatcher.find()){
					wan.setPassword_auto(innerMatcher.group(1));
				}
				
				innerPattern = Pattern.compile("<dialnum>(.*)</dialnum>");
				innerMatcher = innerPattern.matcher(matcher.group(1));
				if (innerMatcher.find()){
					wan.setDialnum_auto(innerMatcher.group(1));
				}
			}
			
			pattern = Pattern.compile("<carrier_custom>(.+)</carrier_custom>");
			matcher = pattern.matcher(portPart);
			if (matcher.find()){
				Pattern innerPattern = Pattern.compile("<apn>(.*)</apn>");
				Matcher innerMatcher = innerPattern.matcher(matcher.group(1));
				if (innerMatcher.find()){
					wan.setApn_custom(innerMatcher.group(1));
				}
				
				innerPattern = Pattern.compile("<username>(.*)</username>");
				innerMatcher = innerPattern.matcher(matcher.group(1));
				if (innerMatcher.find()){
					wan.setUsername_custom(innerMatcher.group(1));
				}
				
				innerPattern = Pattern.compile("<password>(.*)</password>");
				innerMatcher = innerPattern.matcher(matcher.group(1));
				if (innerMatcher.find()){
					wan.setPassword_custom(innerMatcher.group(1));
				}
				
				innerPattern = Pattern.compile("<dialnum>(.*)</dialnum>");
				innerMatcher = innerPattern.matcher(matcher.group(1));
				if (innerMatcher.find()){
					wan.setDialnum_custom(innerMatcher.group(1));
				}
			}
			
			pattern = Pattern.compile("<group>(\\d*)</group>");
			matcher = pattern.matcher(configPart);
			if (matcher.find()){
				wan.setGroup(Integer.valueOf(matcher.group(1)));
			}
			
			newWanList.add(wan);
			
		}
		
		this.setWan_list(newWanList);
	}

	
}
