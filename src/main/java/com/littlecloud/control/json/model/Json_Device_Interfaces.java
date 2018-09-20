package com.littlecloud.control.json.model;

import java.util.List;

import com.littlecloud.pool.object.DevDetailObject.Sims;

public class Json_Device_Interfaces {

	private Integer id;
	//private int devices_id;
	
	private String type;
	private String status;
	private String name;
	private String ddns_host;
	private String ip;
//	private String conn_method;
	private String conn_mode;
	private String gateway;
//	private String dns_server;
	private Integer mtu;
//	private String standby_state;
	private String healthcheck;
	
	private List<Sims> sims;
//	private String sims_imsi;
//	private String sims_status;
	
	private String meid_hex;
	private String meid_dec;
	private String esn;
	private String imei;
	private Integer signal;
	private String carrier_name;
	private String carrier_country;
	private List<String> multiple_ip;


	private List<String> dns_servers;
	
	private String status_led;
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
	private String conn_config_method;
	private String standby_mode;
	private Integer mtu_config;
	
	private String adaptor_type;
	private Integer vendor_id;
	private Integer product_id;
	private String modem_name;
	private String modem_manufacturer;
	
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
	private String ddns_name;
	private Boolean ddns_enabled;
	
	public Json_Device_Interfaces()
	{
		
	}
	
	public List<Sims> getSims() {
		return sims;
	}

	public void setSims(List<Sims> sims) {
		this.sims = sims;
	}

	
//	public Json_Device_Interfaces(DeviceInterfaces itf)
//	{
//		if (itf ==null)
//			return;
//		
//		id = itf.getId();
//		//devices_id = itf.getDevicesId();
//		type = itf.getType();
//		status = itf.getStatus();
//		name = itf.getName();
//		ddns_host = itf.getDdnsHost();
//		ip = itf.getIp();
//		conn_method = itf.getConnMethod();
//		conn_mode = itf.getConnMode();
//		gateway = itf.getGateway();
//		dns_server = itf.getDnsServer();
//		mtu = itf.getMtu();
//		standby_state = itf.getStandbyState();
//		healthcheck = itf.getHealthcheck();
//		
//		
////		sims_imsi = itf.getSimsImsi();
////		sims_status = itf.getSimsStatus();
//		
//		meid_hex = itf.getMeidHex();
//		meid_dec = itf.getMeidDec();
//		signal = itf.getSignal();
//		carrier_name = itf.getCarrierName();
//		carrier_country = itf.getCarrierCountry();
//	}
	
	public Boolean getDdns_enabled() {
		return ddns_enabled;
	}

	public void setDdns_enabled(Boolean ddns_enabled) {
		this.ddns_enabled = ddns_enabled;
	}

	public String getDdns_name() {
		return ddns_name;
	}

	public void setDdns_name(String ddns_name) {
		this.ddns_name = ddns_name;
	}

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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	public Integer getConn_method() {
		return conn_method;
	}
	public void setConn_method(Integer conn_method) {
		this.conn_method = conn_method;
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
//	public String getDns_server() {
//		return dns_server;
//	}
//	public void setDns_server(String dns_server) {
//		this.dns_server = dns_server;
//	}
	public Integer getMtu() {
		return mtu;
	}
	public void setMtu(Integer mtu) {
		this.mtu = mtu;
	}
	public Integer getStandby_state() {
		return standby_state;
	}
	public void setStandby_state(Integer standby_state) {
		this.standby_state = standby_state;
	}
	public String getHealthcheck() {
		return healthcheck;
	}
	public void setHealthcheck(String healthcheck) {
		this.healthcheck = healthcheck;
	}
//	public String getSims_imsi() {
//		return sims_imsi;
//	}
//	public void setSims_imsi(String sims_imsi) {
//		this.sims_imsi = sims_imsi;
//	}
//	public String getSims_status() {
//		return sims_status;
//	}
//	public void setSims_status(String sims_status) {
//		this.sims_status = sims_status;
//	}
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
	public String getCarrier_country() {
		return carrier_country;
	}
	public void setCarrier_country(String carrier_country) {
		this.carrier_country = carrier_country;
	}

	public List<String> getMultiple_ip() {
		return multiple_ip;
	}

	public void setMultiple_ip(List<String> multiple_ip) {
		this.multiple_ip = multiple_ip;
	}
	
	public List<String> getDns_servers() {
		return dns_servers;
	}

	public void setDns_servers(List<String> dns_servers) {
		this.dns_servers = dns_servers;
	}

	public String getStatus_led() {
		return status_led;
	}

	public void setStatus_led(String status_led) {
		this.status_led = status_led;
	}

	public Integer getIs_overall_up() {
		return is_overall_up;
	}

	public void setIs_overall_up(Integer is_overall_up) {
		this.is_overall_up = is_overall_up;
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

	public Integer getPort_type() {
		return port_type;
	}

	public void setPort_type(Integer port_type) {
		this.port_type = port_type;
	}

	public String getConn_config_method() {
		return conn_config_method;
	}

	public void setConn_config_method(String conn_config_method) {
		this.conn_config_method = conn_config_method;
	}

	public String getStandby_mode() {
		return standby_mode;
	}

	public void setStandby_mode(String standby_mode) {
		this.standby_mode = standby_mode;
	}

	public Integer getMtu_config() {
		return mtu_config;
	}

	public void setMtu_config(Integer mtu_config) {
		this.mtu_config = mtu_config;
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
}
