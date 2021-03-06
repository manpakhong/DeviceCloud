package com.littlecloud.control.json.model;



public class Json_Device_Feature {

//	private int iana_id;
//	private String sn;
//	private String sid;
//	
//	private String type;
//	private Boolean retry;
//	private int status;
//	private int interval;
//	private int duration;
//	private long timestamp;
//	private FeatureData data;
//	
//	public class FeatureData {
	
	
	private int version;
	private Json_Feature_List feature;

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Json_Feature_List getFeature() {
		return feature;
	}

	public void setFeature(Json_Feature_List feature) {
		this.feature = feature;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FeatureData [version=");
		builder.append(version);
		builder.append(", feature=");
		builder.append(feature);
		builder.append("]");
		return builder.toString();
	}

	
	public static class Json_Feature_List {
		
		
		/* **** just including the mvpn part **** */
		private String mvpn_bonding;
		private Boolean mvpn_aggressive;
		private MvpnFeature mvpn;
		private Boolean mvpn_srv;
		private String mvpn_license;	
		private Boolean mvpn_l2vpn;
		
		/* **** add portal_ic2 support **** */
		private Boolean portal_ic2;
		
		
		/* **** Add if needed **** */
//		private Boolean enhanced_qos_bandwidth;
//		private Boolean ap_enterprise;
//		private Boolean setup_wizard;
//		private String config_backup_auto;
//		private String web_tunneling;
//		private String dropin;
//		//private String lan_on_wanport;
//		private int wan_cnt;
//		private Boolean console;
//		private String speedfusion;
//		private Boolean wins;
//		private Boolean frontpanel;
//		private Boolean dhcp_relay;
//		private Boolean portal_ldap;
//		private String ipv6;
//		private Boolean ha;
//		private Boolean web_blocking;
//		private Boolean out_algo_leastused;
//		private String vlan;
//		private Boolean ospf;
//		private String config_update;
//		private Boolean enhanced_qos_gsmb;
//		private Boolean in_llb;
//		private Boolean cli;
//		private Boolean enhanced_qos;
//		private String web_admin_tunnel;
//		private Boolean bonjour_fwd;
//		private String extap;
//		private Boolean enhanced_qos_group;
//		private Boolean enhanced_qos_dpi;
		
		
		public String getMvpn_bonding() {
			return mvpn_bonding;
		}
		public void setMvpn_bonding(String mvpn_bonding) {
			this.mvpn_bonding = mvpn_bonding;
		}
		public Boolean getMvpn_aggressive() {
			return mvpn_aggressive;
		}
		public void setMvpn_aggressive(Boolean mvpn_aggressive) {
			this.mvpn_aggressive = mvpn_aggressive;
		}
		public MvpnFeature getMvpn() {
			return mvpn;
		}
		public void setMvpn(MvpnFeature mvpn) {
			this.mvpn = mvpn;
		}
		public Boolean getMvpn_srv() {
			return mvpn_srv;
		}
		public void setMvpn_srv(Boolean mvpn_srv) {
			this.mvpn_srv = mvpn_srv;
		}
		public String getMvpn_license() {
			return mvpn_license;
		}
		public void setMvpn_license(String mvpn_license) {
			this.mvpn_license = mvpn_license;
		}
		public Boolean getMvpn_l2vpn() {
			return mvpn_l2vpn;
		}
		public void setMvpn_l2vpn(Boolean mvpn_l2vpn) {
			this.mvpn_l2vpn = mvpn_l2vpn;
		}
		
		public Boolean getPortal_ic2() {
			return portal_ic2;
		}
		public void setPortal_ic2(Boolean portal_ic2) {
			this.portal_ic2 = portal_ic2;
		}

		public class MvpnFeature {
			private Integer license;
			private Integer status;
			
			public Integer getLicense() {
				return license;
			}
			public void setLicense(Integer license) {
				this.license = license;
			}
			public Integer getStatus() {
				return status;
			}
			public void setStatus(Integer status) {
				this.status = status;
			}
			
		}
	}

}
