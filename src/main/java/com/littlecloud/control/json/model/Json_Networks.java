package com.littlecloud.control.json.model;

public class Json_Networks 
{
	private Integer id;
	private String name;
	private Integer online_device_count;
	private Integer offline_device_count;
	private Integer client_count;
	private Integer expiry_count;
	private Integer expiry_soon_count;
	private Integer usage;
	private String network_type;
	private String features;
	private String country;
	private String address;
	private Float longitude;
	private Float latitude;
	private Integer master_device_id;
	private String product_types;
	private String incontrol_host1;
	private String incontrol_host2;
	private String custom_common_name;
	private String custom_certificate;
	private Boolean custom_cert_perm;
	

	public Boolean getCustom_cert_perm() {
		return custom_cert_perm;
	}
	public void setCustom_cert_perm(Boolean custom_cert_perm) {
		this.custom_cert_perm = custom_cert_perm;
	}
	public String getIncontrol_host1() {
		return incontrol_host1;
	}
	public void setIncontrol_host1(String incontrol_host1) {
		this.incontrol_host1 = incontrol_host1;
	}
	public String getIncontrol_host2() {
		return incontrol_host2;
	}
	public void setIncontrol_host2(String incontrol_host2) {
		this.incontrol_host2 = incontrol_host2;
	}
	public String getCustom_common_name() {
		return custom_common_name;
	}
	public void setCustom_common_name(String custom_common_name) {
		this.custom_common_name = custom_common_name;
	}
	public String getCustom_certificate() {
		return custom_certificate;
	}
	public void setCustom_certificate(String custom_certificate) {
		this.custom_certificate = custom_certificate;
	}
	public Integer getMaster_device_id() {
		return master_device_id;
	}
	public void setMaster_device_id(Integer master_device_id) {
		this.master_device_id = master_device_id;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getOnline_device_count() {
		return online_device_count;
	}
	public void setOnline_device_count(Integer online_device_count) {
		this.online_device_count = online_device_count;
	}
	public Integer getOffline_device_count() {
		return offline_device_count;
	}
	public void setOffline_device_count(Integer offline_device_count) {
		this.offline_device_count = offline_device_count;
	}
	public Integer getClient_count() {
		return client_count;
	}
	public void setClient_count(Integer client_count) {
		this.client_count = client_count;
	}
	public Integer getExpiry_count() {
		return expiry_count;
	}
	public void setExpiry_count(Integer expiry_count) {
		this.expiry_count = expiry_count;
	}
	public Integer getExpiry_soon_count() {
		return expiry_soon_count;
	}
	public void setExpiry_soon_count(Integer expiry_soon_count) {
		this.expiry_soon_count = expiry_soon_count;
	}
	public Integer getUsage() {
		return usage;
	}
	public void setUsage(Integer usage) {
		this.usage = usage;
	}
	public String getNetwork_type() {
		return network_type;
	}
	public void setNetwork_type(String network_type) {
		this.network_type = network_type;
	}
	public String getFeatures() {
		return features;
	}
	public void setFeatures(String features) {
		this.features = features;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Float getLongitude() {
		return longitude;
	}
	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}
	public Float getLatitude() {
		return latitude;
	}
	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}
	public String getProduct_types() {
		return product_types;
	}
	public void setProduct_types(String product_types) {
		this.product_types = product_types;
	}
}
