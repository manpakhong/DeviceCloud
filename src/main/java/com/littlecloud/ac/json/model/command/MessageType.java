package com.littlecloud.ac.json.model.command;

import java.util.concurrent.ConcurrentHashMap;

/* 
 * Important: This ReportType name must match those define in ACDataTransition.h in CAPWAP 
 * 
 * AcWtpMsgHandler.c
 * AcWtpMsgHandler.h
 * 
 * */

public enum MessageType {
	
	PIPE_INFO_TYPE_DEV_ONLINE,
	PIPE_INFO_TYPE_DEV_OFFLINE,
	PIPE_INFO_TYPE_EVENT_LOG,
	PIPE_INFO_TYPE_DEV_DETAIL,
	PIPE_INFO_TYPE_DEV_BANDWIDTH,
	//PIPE_INFO_TYPE_NET_INFO,
	PIPE_INFO_TYPE_DEV_USAGE,
	PIPE_INFO_TYPE_DEV_LOCATIONS,
	PIPE_INFO_TYPE_DEV_SSID_USAGES,
	PIPE_INFO_TYPE_DEV_CHANNEL_UTIL,
	PIPE_INFO_TYPE_DEV_WLAN,
	PIPE_INFO_TYPE_DEV_TCPDUMP,
	PIPE_INFO_TYPE_DEV_USAGE_HIST,
	PIPE_INFO_TYPE_STATION_LIST,
	PIPE_INFO_TYPE_STATION_LIST_DELTA,
	PIPE_INFO_TYPE_STATION_BANDWIDTH_LIST,
	PIPE_INFO_TYPE_STATION_USAGE,
	PIPE_INFO_TYPE_STATION_Z,
	PIPE_INFO_TYPE_STATION_USAGE_HIST,	
	PIPE_INFO_TYPE_PEPVPN_ENDPOINT,
	PIPE_INFO_TYPE_PEPVPN_PEER_LIST, // add
	PIPE_INFO_TYPE_PEPVPN_PEER_UPDATE, // add
	PIPE_INFO_TYPE_PEPVPN_PEER_DETAIL, // add
	PIPE_INFO_TYPE_PEPVPN_TUNNEL_STAT, // add
	PIPE_INFO_TYPE_CONFIG_CHECKSUM,
	PIPE_INFO_TYPE_CONFIG_MD5,
	PIPE_INFO_TYPE_CONFIG_GET,
	PIPE_INFO_TYPE_CONFIG_GET_TEXT,
	PIPE_INFO_TYPE_CONFIG_BACKUP_TEXT,
	PIPE_INFO_TYPE_CONFIG_BACKUP,
	PIPE_INFO_TYPE_CONFIG_PUT,	
	PIPE_INFO_TYPE_CONFIG_PUT_MASTER,
	PIPE_INFO_TYPE_CONFIG_PUT_TEXT,
	PIPE_INFO_TYPE_FIRMWARE_PUT,
	PIPE_INFO_TYPE_FEATURE_GET,
	PIPE_INFO_TYPE_UPDATE_AUTO_CHANNEL,
	PIPE_INFO_TYPE_REDIRECT_DEV_ROOT,
	PIPE_INFO_TYPE_OP_MODE_PUT,
	PIPE_INFO_TYPE_EVENT_ACT_FH_LIC,
	PIPE_INFO_TYPE_CONFIG_ICMG_PUT,
	PIPE_INFO_TYPE_INTERNAL_CMD,
	PIPE_INFO_TYPE_DUMMY,
	PIPE_INFO_TYPE_DUMMY_TEST,
	PIPE_INFO_TYPE_NDPI,
	//PIPE_INFO_TYPE_SET_TIMER,	/* 2014-05-09 - should be an obsolete message. previous discussion to specify the interval in every on demand request */
	//PIPE_INFO_TYPE_REG_LIST,
	PIPE_INFO_TYPE_DEV_DISCONNECT,
	PIPE_INFO_TYPE_WEB_ADMIN_TUNNEL,
	PIPE_INFO_TYPE_WEB_TUNNELING,
	PIPE_INFO_TYPE_DEV_SYSINFO,	
	PIPE_INFO_AC_REG_LIST,
	PIPE_INFO_TYPE_REDIRECT_WTP,
	PIPE_INFO_TYPE_DEV_USAGE_CONSOLIDATE,
	PIPE_INFO_TYPE_WAN_IP_INFO, // for ddns, periodic from device 1 hr
	PIPE_INFO_TYPE_AC_STATUS,
	PIPE_INFO_AC_INFO_UPDATE, 		// no need to handle
	PIPE_INFO_AC_LICENSE,
	PIPE_INFO_TYPE_SSID_DISCOVERY,
	PIPE_INFO_TYPE_PORTAL,
	PIPE_INFO_TYPE_REPORT_INTERVAL,	// no need to handle
	REPEAT_SN,						// no need to handle
	AC_KICK,						// no need to handle
	CORE_KICK,						// no need to handle
	PIPE_INFO_TYPE_DIAG_REPORT_PLB,
	UNDEFINED;
	
	private static ConcurrentHashMap<String,MessageType> lookupFromStrMap = new ConcurrentHashMap<String,MessageType>(); 
	
	static
	{
		for (MessageType m: MessageType.values())
		{
			lookupFromStrMap.put(m.name(), m);
		}
	}
	
	public static MessageType getMessageType(String m)
	{
		if (m==null)
			return MessageType.UNDEFINED;
		
		return lookupFromStrMap.get(m)==null?MessageType.UNDEFINED:lookupFromStrMap.get(m);
	}
	
	public static void main(String args[])
	{
		System.out.println(MessageType.getMessageType("PIPE_INFO_TYPE_FIRMWARE_PUT"));
		System.out.println(MessageType.PIPE_INFO_TYPE_CONFIG_CHECKSUM);
	}
}
