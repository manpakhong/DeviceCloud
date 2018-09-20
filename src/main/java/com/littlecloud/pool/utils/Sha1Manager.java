package com.littlecloud.pool.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class Sha1Manager {

	public static String getSha1Sum(String str)
	{		
		return DigestUtils.sha1Hex(str.getBytes());
	}
	
	public static void main(String[] args) {

		String testStr ="DHCP_SERVER_NETMASK=\"255.255.255.0\"\n"
				+"DHCP_SERVER_POOL_END=\"192.168.50.200\"\n"
				+"DHCP_SERVER_POOL_START=\"192.168.50.10\"\n"
				+"DHCP_STATIC=\"0\"\n"
				+"EQOS_PRIORITY_DSL_ENABLE=\"yes\"\n"
				+"FIREWALL_IDS=\"no\"\n"
				+"FW410_ASSURED=\"1\"\n"
				+"HELPER_H323_SUSPEND=\"yes\"\n"
				+"HELPER_IPSEC=\"enable\"\n"
				+"HELPER_IPSEC_DATA_PORT=\"500 4500 10000\"\n"
				+"HELPER_SIP=\"enable\"\n"
				+"HELPER_SIP_SIGNAL_PORT=\"5060 5061 10000\"\n"
				+"HOSTNAME=\"pepwave\"\n"
				+"LAN_DEV=\"br0\"\n"
				+"LAN_GATEWAY=\"192.168.50.1\"\n"
				+"LAN_IPADDR=\"192.168.50.1\"\n"
				+"LAN_IPBCAST=\"192.168.50.255\"\n"
				+"LAN_MEDIA=\"Auto\"\n"
				+"LAN_MODIFIED=\"yes\"\n"
				+"LAN_NETMASK=\"255.255.255.0\"\n"
				+"LAN_STROUTE_N=\"6.0\"\n"
				+"LDNS_CACHE=\"no\"\n"
				+"LDNS_ORDER=\"\"\n"
				+"LEFTTIME_USAGE=\"yes\"\n"
				+"LIFETIME_USAGE=\"yes\"\n"
				+"MVPN1_CLIENT_SN=\"pegasus_72\"\n"
				+"MVPN1_ENABLE=\"yes\"\n"
				+"MVPN1_ENCRYPT_METHOD=\"aes256\"\n"
				+"MVPN1_L2_ENABLE=\"yes\"\n"
				+"MVPN1_NAME=\"to_wlc50\"\n"
				+"MVPN1_PRIORITY=\"1:1 2:2 3:3\"\n"
				+"MVPN1_SERVER_LIST=\"10.8.99.103\"\n"
				+"MVPN_HC_MODE=\"0\"\n"
				+"MVPN_ORDER=\"1\"\n"
				+"MVPN_SITE_ID=\"MAX_BR1_4AA6\"\n"
				+"MVPN_VLAN0_CONN_METHOD=\"dhcp\"\n"
				+"OUT_CUSTOM1_DEST=\"0.0.0.0/0\"\n"
				+"OUT_CUSTOM1_ENABLE=\"yes\"\n"
				+"OUT_CUSTOM1_MODE=\"lease\"\n"
				+"OUT_CUSTOM1_NAME=\"HTTPS_Persistence\"\n"
				+"OUT_CUSTOM1_PORT=\"443\"\n"
				+"OUT_CUSTOM1_PROTOCOL=\"TCP\"\n"
				+"OUT_CUSTOM1_RANGE=\"1\"\n"
				+"OUT_CUSTOM1_SRC=\"0.0.0.0/0\"\n"
				+"OUT_CUSTOM1_WEIGHT=\"10 10 10\"\n"
				+"OUT_CUSTOM_ORDER=\"1\"\n"
				+"OUT_POLICY=\"custom\"\n"
				+"PCMS_CONFIG_HOST=\"config.pepwave.com\"\n"
				+"PCMS_FIRMWARE_HOST=\"firmware.pepwave.com\"\n"
				+"PCMS_REPORT_HOST=\"report.pepwave.com\"\n"
				+"PCMS_STATUS_HOST=\"status.pepwave.com\"\n"
				+"PIF1_MAC_ADDR=\"\"\n"
				+"PIF1_MEDIA=\"Auto\"\n"
				+"PIF1_MTU=\"1440\"\n"
				+"PIF1_WAN=\"1\"\n"
				+"PIF2_MAC_ADDR=\"\"\n"
				+"PIF2_MEDIA=\"Auto\"\n"
				+"PIF2_MTU=\"1440\"\n"
				+"PIF2_WAN=\"2\"\n"
				+"PIF3_MAC_ADDR=\"\"\n"
				+"PIF3_MEDIA=\"Auto\"\n"
				+"PIF3_MTU=\"1500\"";

		
		System.out.println(testStr + "\n"+getSha1Sum(testStr));
		System.out.println(getSha1Sum(testStr+"\n"));
	}

}
