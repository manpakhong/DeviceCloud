package com.littlecloud.control.webservices.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.ConfigurationPepvpnsDAO;
import com.littlecloud.control.dao.ConfigurationRadioChannelsDAO;
import com.littlecloud.control.dao.ConfigurationRadiosDAO;
import com.littlecloud.control.dao.ConfigurationSsidsDAO;
import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.dao.InformationSchemaDAO;
import com.littlecloud.control.dao.NetworkEmailNotificationsDAO;
import com.littlecloud.control.dao.NetworkSliencePeriodsDAO;
import com.littlecloud.control.dao.branch.DeviceFirmwareSchedulesDAO;
import com.littlecloud.control.dao.branch.HouseKeepLogsDAO;
import com.littlecloud.control.dao.branch.HouseKeepSettingsDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.branch.DeviceFirmwareSchedules;
import com.littlecloud.control.entity.branch.HouseKeepLogs;
import com.littlecloud.control.entity.branch.HouseKeepSettings;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;
import com.peplink.api.db.util.DBUtil;

public class HouseKeepUtils 
{
	private static final Logger logger = Logger.getLogger(HouseKeepUtils.class);
	private static boolean housekeeping = false;
	private static long housekeepTime = 0;
	private static final String VER = "2.0.21.4";
	private static final long TABLE_SKIP_SIZE = 3000000L; // 3M records
	private static final int INIT_ORGS = 10000; // List init parameter of no of orgs
	
	public enum HouseKeep
	{
		MOVE_DEVICE_NETWORK,
		REMOVE_DEVICE,
		REMOVE_NETWORKS
	}
	
	public static void doHouseKeep(String orgId, Integer devId, Integer netId, HouseKeep type)
	{
		ConfigurationRadioChannelsDAO confiRadioDAO = null;
		ConfigurationSsidsDAO configurationSsidsDAO = null;
		ConfigurationRadiosDAO configurationRadiosDAO = null;
		NetworkEmailNotificationsDAO networkEmailNotificationsDAO = null;
		NetworkSliencePeriodsDAO networkSilencePeriodDAO = null;
		ConfigurationPepvpnsDAO configurationPepvpnsDAO = null;
		DevicesDAO devicesDAO = null;
		DeviceFirmwareSchedulesDAO devFwSchedulesDAO = null;
		DeviceFirmwareSchedules sche = null;
		
		try
		{
			switch(type)
			{
				case REMOVE_DEVICE:
					confiRadioDAO = new ConfigurationRadioChannelsDAO(orgId);
					configurationSsidsDAO = new ConfigurationSsidsDAO(orgId);
					devicesDAO = new DevicesDAO(orgId);
					devFwSchedulesDAO = new DeviceFirmwareSchedulesDAO();
					
					if( netId != null && devId != null )
					{
						confiRadioDAO.removeRadioChannel(devId);
						configurationSsidsDAO.deleteByNetworkIdAndDeviceId(netId, devId);
						Devices dev = devicesDAO.findById(devId);
						if( dev != null )
						{
							sche = devFwSchedulesDAO.getUniqueSchedule(dev.getIanaId(), dev.getSn());
							if( sche != null )
								devFwSchedulesDAO.delete(sche);
						}
					}
				break;
				case REMOVE_NETWORKS:
					configurationPepvpnsDAO = new ConfigurationPepvpnsDAO(orgId);
					configurationRadiosDAO = new ConfigurationRadiosDAO(orgId);
					networkEmailNotificationsDAO = new NetworkEmailNotificationsDAO(orgId);
					networkSilencePeriodDAO = new NetworkSliencePeriodsDAO(orgId);
					configurationSsidsDAO = new ConfigurationSsidsDAO(orgId);
					devFwSchedulesDAO = new DeviceFirmwareSchedulesDAO();
					
					if( netId != null )
					{
						configurationPepvpnsDAO.deleteByNetworkId(netId);
						configurationRadiosDAO.deleteByNetworkId(netId);
						networkEmailNotificationsDAO.deleteByNetworkId(netId);
						networkSilencePeriodDAO.deleteByNetworkId(netId);
						configurationSsidsDAO.deleteByNetworkId(netId);
						int del = devFwSchedulesDAO.deleteByNetworkId(orgId, netId);
						logger.info("House keeping finished, " + del + " schedule is cleared.");
					}
				break;
				case MOVE_DEVICE_NETWORK:
					devFwSchedulesDAO = new DeviceFirmwareSchedulesDAO();
					devicesDAO = new DevicesDAO(orgId);
					if( netId != null && devId != null)
					{
						Devices dev = devicesDAO.findById(devId);
						sche = devFwSchedulesDAO.getUniqueSchedule(dev.getIanaId(),dev.getSn());
						devFwSchedulesDAO.delete(sche);
					}
				break;
			}
		}
		catch(Exception e)
		{
			logger.error("Do house keep " + type.toString() + " failed",e);
		}
	}
	
	public static void doHouseKeepScheduler() {
		if(housekeeping) {
			logger.info(VER + " -- SKIP doHouseKeepScheduler as previous housekeeping is still running");
			return;
		}
		
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		long tmpTime = today.getTimeInMillis();

		if(housekeepTime == tmpTime) {
			logger.info(VER + " -- SKIP doHouseKeepScheduler as today's housekeeping has run");
			return;
		}
		
		try {
			housekeeping = true;
			
			logger.info(VER + " -- doHouseKeepScheduler start, housekeepTime = " + tmpTime);
			long tstart = System.currentTimeMillis();
			
			List<String> snsOrganizationsList = new ArrayList<String>();
			HouseKeepSettingsDAO houseKeepSettingsDAO = new HouseKeepSettingsDAO();
			HouseKeepLogsDAO houseKeepLogsDAO = new HouseKeepLogsDAO();
			InformationSchemaDAO infoSchemaDAO = new InformationSchemaDAO();
			
			List <HouseKeepSettings> houseKeepRecordList = houseKeepSettingsDAO.getAllDatas();
			for (HouseKeepSettings hkRecords: houseKeepRecordList)
			{									
				String tableName = hkRecords.get_TableName();
				int keepday = hkRecords.getHouseKeepDay();
				logger.info("-----------doHouseKeepSchedule working on:" + tableName + " with housekeeping day: " + keepday);
				
				if(tableName.equals("client_ip_mac_mappings") || tableName.equals("client_ssid_usages") || tableName.equals("device_ssid_usages") 
						|| tableName.equals("client_usages") || tableName.equals("device_usages") || (tableName.equals("client_infos"))
						|| tableName.equals("daily_client_ssid_usages") || tableName.equals("daily_client_usages") || tableName.equals("daily_device_ssid_usages")
						|| tableName.equals("daily_device_usages") || tableName.equals("device_monthly_usages") || tableName.equals("event_log")) {
//				if(tableName.equals("client_monthly_usages")) { // don't enable this as the tables are very very large
					DBConnection dbConnection = null;
					try {
						snsOrganizationsList = infoSchemaDAO.getAllOrganizations();
						dbConnection = DBUtil.getInstance().getConnection(true, null, false);
						DBQuery dbQuery = dbConnection.createQuery();
						HouseKeepLogs houseKeepLogs = new HouseKeepLogs();
						
						int orgIndex = 1;
						if(snsOrganizationsList != null && snsOrganizationsList.size() > 0)
						{
							ArrayList<String> jobList = new ArrayList<String>(INIT_ORGS); // for holding list of table status
							for( String orgId : snsOrganizationsList )
							{
								long torg = System.currentTimeMillis();
								
								String db_id = "peplink_organization_" + orgId;
								String tbl_id = db_id + "." + tableName;

								long tblSize = estimateTableSize(dbQuery, orgId, tableName);
								if(tblSize == -1 ) {
									logger.error("-----["+orgIndex+"] doHouseKeepSchedule orgId="+orgId+", table="+tableName+" exception and skip !!!!");
									jobList.add(tbl_id+" SKIP ERROR_ESTIMATE -1");
									continue;
								} else if (tblSize > TABLE_SKIP_SIZE) {
									logger.warn("-----["+orgIndex+"] doHouseKeepSchedule orgId="+orgId+", table="+tableName+" ("+tblSize+" > "+TABLE_SKIP_SIZE+") is too large and skip now !!! ");
									jobList.add(tbl_id+" SKIP ERROR_SIZE " + tblSize);
									continue;
								} else {
									logger.info("-----["+orgIndex+"] doHouseKeepSchedule orgId="+orgId+", table="+tableName);
								}
								
								orgIndex ++ ;
								
								Calendar cal = Calendar.getInstance();
								cal.setTimeZone(TimeZone.getTimeZone("GMT"));
								cal.add(Calendar.DAY_OF_MONTH, 2);
								cal.set(Calendar.HOUR_OF_DAY, 23);
								cal.set(Calendar.MINUTE, 59);
								cal.set(Calendar.SECOND, 59);
								int tDayEnd = (int) (cal.getTimeInMillis() / 1000);
								String pDayEnd = "p" + tDayEnd;
								
								Calendar cal2 = Calendar.getInstance();
								cal2.setTimeZone(TimeZone.getTimeZone("GMT"));
								cal2.add(Calendar.DAY_OF_MONTH, -keepday - 1);
								cal2.set(Calendar.HOUR_OF_DAY, 23);
								cal2.set(Calendar.MINUTE, 59);
								cal2.set(Calendar.SECOND, 59);
								int tDayHouseKeep = (int) (cal2.getTimeInMillis() / 1000);
								
								String sql_check = "show create table "+tbl_id;
								boolean skip_future = false;
								boolean skip_past = true;
								boolean skip_convert = true;
								
								List<Integer> partitionDelList = new ArrayList<Integer>(10);  // 10 should be enough
								
								try {
									ResultSet rs_check = dbQuery.executeQuery(sql_check);
									if(rs_check.next()) {
										String record = rs_check.getString(2);
										
										if(record.indexOf("PARTITION BY") ==-1) { // check partitioned table or not
											skip_convert = false; // still not yet partitioned, need convert
										} else { // already partitioned, check the need of housekeep
											String lines[] = record.split("\n");
											for(String line : lines) {
												if(line.indexOf("PARTITION p")!=-1) {
													if(line.indexOf("p_MAXVALUE")!=-1)
														continue;
													int pos = line.indexOf("PARTITION p");
													line = line.substring("PARTITION p".length() + pos);
													pos = line.indexOf(" VALUES");
													line = line.substring(0, pos);
													int partitionTime;
													
													try {
														partitionTime = Integer.parseInt(line);
													} catch(Exception e) { // skip if any error is found
														logger.error("Partition number parsing exception " + e, e);
														skip_future = true;
														skip_past = true;
														break;
													}
		
													if(partitionTime == tDayEnd) { // check for the need to add new future partition
														skip_future = true;
														logger.info("SKIP: Partition " + pDayEnd + " already exists in " + tbl_id);
													}
													
													if(partitionTime <= tDayHouseKeep) { // check for the need to delete outdated partitions
														partitionDelList.add(partitionTime);
														logger.info("Found Housekeeping partition p" + partitionTime + " in " + tbl_id);
														skip_past = false;
													}
												}
											}
										}
									}
									rs_check.close();
								} catch (SQLException sqle) {
									logger.error("show create table exception: "+sqle, sqle);
									jobList.add(tbl_id+" SKIP EX_CREATE "+sqle);
									continue;
								}
								
								if(!skip_convert) {
									String tbl_id_new = tbl_id + "_new";
									long tconvert = System.currentTimeMillis() / 1000;
									
									logger.info("Table "+tbl_id+" is NOT yet partitioned, now convert it");
									
									String sql_create = null;
									String sql_import = "insert ignore " +
											"into " + tbl_id_new + " select * from " + tbl_id + " where unixtime is NOT NULL and unixtime > " + tDayHouseKeep;
									String sql_dropifexists = "DROP TABLE IF EXISTS " + tbl_id_new; 
									
									if(tableName.equals("client_ip_mac_mappings")) {
										sql_create = "CREATE TABLE "+tbl_id_new+" (`network_id` int(10) NOT NULL, `device_id` int(10) NOT NULL, `ip` varchar(45) NOT NULL, `mac` varchar(45) NOT NULL, "
												+ "`unixtime` int(10) unsigned NOT NULL, PRIMARY KEY (`unixtime`,`network_id`,`device_id`,`ip`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 "
												+ "PARTITION BY RANGE(unixtime) (partition `"+pDayEnd+"` values less than ("+tDayEnd+") , partition p_MAXVALUE values less than MAXVALUE)";
									} else if (tableName.equals("client_ssid_usages")) {
										sql_create = "CREATE TABLE "+tbl_id_new+" (`id` varchar(45) NOT NULL, `network_id` int(10) unsigned NOT NULL, `device_id` int(10) unsigned DEFAULT NULL, `mac` varchar(45) NOT NULL, "
												+ "`name` varchar(200) DEFAULT NULL, `bssid` varchar(45) DEFAULT NULL, `essid` varchar(45) DEFAULT NULL, `encryption` varchar(45) DEFAULT NULL, `active` bit(1) DEFAULT NULL, "
												+ "`type` varchar(45) DEFAULT NULL, `datetime` datetime DEFAULT NULL, `unixtime` int(10) unsigned NOT NULL DEFAULT '0', "
												+ "PRIMARY KEY (`id`, `unixtime`), KEY `client_ssid_usages_mac_idx` (`mac`), KEY `client_ssid_usages_devices_fk_idx` (`device_id`), "
												+ "KEY `index_for_client_ssid_usages` (`device_id`,`mac`,`essid`,`encryption`), KEY `report_ssid_usages` (`active`,`type`,`network_id`,`unixtime`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 "
												+ "PARTITION BY RANGE (unixtime) (partition `"+pDayEnd+"` values less than ("+tDayEnd+") , partition p_MAXVALUE values less than MAXVALUE)";

									} else if (tableName.equals("daily_client_ssid_usages")) {
										sql_create = "CREATE TABLE "+tbl_id_new+" (`id` bigint(20) NOT NULL AUTO_INCREMENT, `network_id` int(10) unsigned NOT NULL, "
												+ "`device_id` int(10) unsigned DEFAULT NULL, `name` varchar(200) DEFAULT NULL, `bssid` varchar(45) DEFAULT NULL, "
												+ "`essid` varchar(45) DEFAULT NULL, `encryption` varchar(45) DEFAULT NULL, `mac` varchar(45) DEFAULT NULL, `type` varchar(45) DEFAULT NULL, "
												+ "`datetime` datetime DEFAULT NULL, `unixtime` int(10) unsigned DEFAULT NULL, "
												+ "PRIMARY KEY (`id`, `unixtime`), UNIQUE KEY `unique_idx` (`network_id`,`unixtime`,`device_id`,`mac`,`essid`,`encryption`), "
												+ "KEY `essid_enc_mac` (`essid`,`encryption`,`mac`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 "
												+ "PARTITION BY RANGE (unixtime) (partition `"+pDayEnd+"` values less than ("+tDayEnd+") , partition p_MAXVALUE values less than MAXVALUE)";

									} else if (tableName.equals("daily_client_usages")) {
										sql_create = "CREATE TABLE "+tbl_id_new+" (`id` bigint(20) NOT NULL AUTO_INCREMENT, `network_id` int(10) unsigned NOT NULL, "
												+ "`mac` varchar(45) NOT NULL, `name` varchar(200) DEFAULT NULL, `manufacturer` varchar(150) DEFAULT NULL, `tx` float DEFAULT NULL, "
												+ "`rx` float DEFAULT NULL, `datetime` datetime DEFAULT NULL, `unixtime` int(10) unsigned DEFAULT NULL, `device_id` int(10) DEFAULT NULL, "
												+ "`ip` varchar(32) DEFAULT NULL, `type` varchar(20) DEFAULT NULL, PRIMARY KEY (`id`, `unixtime`), "
												+ "UNIQUE KEY `ut_nid_did_mac_ip` (network_id, device_id, unixtime, mac, ip), KEY `manufacturer` (`manufacturer`), KEY `mac` (`mac`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 "
												+ "PARTITION BY RANGE (unixtime) (partition `"+pDayEnd+"` values less than ("+tDayEnd+") , partition p_MAXVALUE values less than MAXVALUE)";
										sql_import = "insert ignore into " + tbl_id_new + " select id, network_id, mac, name, manufacturer, tx, rx, datetime, unixtime, device_id, ip, type from " + tbl_id + " where unixtime > " + tDayHouseKeep;
										
									} else if (tableName.equals("device_ssid_usages")) {
										sql_create = "CREATE TABLE "+tbl_id_new+" (`id` varchar(45) NOT NULL, `network_id` int(10) unsigned NOT NULL, "
												+ "`device_id` int(10) unsigned NOT NULL, `datetime` datetime NOT NULL, `unixtime` int(10) unsigned NOT NULL, `essid` varchar(45) NOT NULL, "
												+ "`encryption` varchar(45) NOT NULL, `tx` float NOT NULL, `rx` float NOT NULL, PRIMARY KEY (`id`, `unixtime`), "
												+ "KEY `device_ssid_usages_devices_fk_idx` (`device_id`), KEY `daily_device_ssid_usages_datetime_idx` (unixtime, network_id, device_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 "
												+ "PARTITION BY RANGE (unixtime) (partition `"+pDayEnd+"` values less than ("+tDayEnd+") , partition p_MAXVALUE values less than MAXVALUE)";

									} else if (tableName.equals("daily_device_ssid_usages")) {
										sql_create = "CREATE TABLE "+tbl_id_new+" (`id` bigint(20) NOT NULL AUTO_INCREMENT, `network_id` int(10) unsigned NOT NULL, "
												+ "`device_id` int(10) unsigned NOT NULL, `datetime` datetime NOT NULL, `unixtime` int(10) unsigned NOT NULL, `essid` varchar(45) NOT NULL, "
												+ "`encryption` varchar(45) NOT NULL, `tx` float NOT NULL, `rx` float NOT NULL, PRIMARY KEY (`id`, `unixtime`), "
												+ "KEY `report_idx` (unixtime, network_id, device_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 "
												+ "PARTITION BY RANGE (unixtime) (partition `"+pDayEnd+"` values less than ("+tDayEnd+") , partition p_MAXVALUE values less than MAXVALUE)";

									} else if (tableName.equals("device_usages")) {
										sql_create = "CREATE TABLE "+tbl_id_new+" (`id` varchar(45) NOT NULL, `network_id` int(10) unsigned NOT NULL, `device_id` int(10) unsigned NOT NULL, " 
												+ "`datetime` datetime DEFAULT NULL, `unixtime` int(10) unsigned NOT NULL DEFAULT '0', `tx` float DEFAULT NULL, `rx` float DEFAULT NULL, "
												+ "`wan_id` int(10) DEFAULT NULL, `wan_name` varchar(200) DEFAULT NULL, PRIMARY KEY (`id`,`unixtime`), "
												+ "KEY `device_bandwidths_devices_fk_idx` (`device_id`,`unixtime`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 "
												+ "PARTITION BY RANGE (unixtime) (partition `"+pDayEnd+"` values less than ("+tDayEnd+") ENGINE = InnoDB, PARTITION p_MAXVALUE VALUES LESS THAN MAXVALUE ENGINE = InnoDB)";
										sql_import = "insert ignore into " + tbl_id_new + " select id, network_id, device_id, datetime, unixtime, tx, rx, wan_id, wan_name from " + tbl_id + " where unixtime > " + tDayHouseKeep;

									} else if (tableName.equals("device_monthly_usages")) {
										sql_create = "CREATE TABLE "+tbl_id_new+" (`id` varchar(45) NOT NULL, `network_id` int(10) unsigned NOT NULL, `device_id` int(10) unsigned NOT NULL, " 
												+ "`datetime` datetime DEFAULT NULL, `unixtime` int(10) unsigned NOT NULL DEFAULT '0', `tx` float DEFAULT NULL, `rx` float DEFAULT NULL, "
												+ "`wan_id` int(10) DEFAULT NULL, `wan_name` varchar(200) DEFAULT NULL, PRIMARY KEY (`id`,`unixtime`), "
												+ "KEY `index_for_get_device_monthly_usage` (`network_id`,`device_id`,`datetime`,`wan_id`), KEY `nid_did_wid_ut` (`network_id`,`device_id`,`wan_id`,`unixtime`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 "
												+ "PARTITION BY RANGE (unixtime) (partition `"+pDayEnd+"` values less than ("+tDayEnd+") ENGINE = InnoDB, PARTITION p_MAXVALUE VALUES LESS THAN MAXVALUE ENGINE = InnoDB)";
										sql_import = "insert ignore into " + tbl_id_new + " select id, network_id, device_id, datetime, unixtime, tx, rx, wan_id, wan_name from " + tbl_id + " where unixtime > " + tDayHouseKeep;
										
									} else if (tableName.equals("daily_device_usages")) {
										sql_create = "CREATE TABLE "+tbl_id_new+" (`id` bigint(20) NOT NULL AUTO_INCREMENT, `network_id` int(10) unsigned NOT NULL, `device_id` int(10) unsigned NOT NULL, " 
												+ "`datetime` datetime DEFAULT NULL, `unixtime` int(10) unsigned NOT NULL DEFAULT '0', `tx` float DEFAULT NULL, `rx` float DEFAULT NULL, "
												+ "`wan_id` int(10) DEFAULT NULL, `wan_name` varchar(200) DEFAULT NULL, PRIMARY KEY (`id`,`unixtime`), "
												+ "UNIQUE KEY `unique_idx` (`network_id`,`device_id`, `wan_id`, `unixtime`), KEY `report_index` (`network_id`, `wan_id`, `unixtime`), KEY `device_id` (`device_id`), KEY `unixtime` (`unixtime`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 "
												+ "PARTITION BY RANGE (unixtime) (partition `"+pDayEnd+"` values less than ("+tDayEnd+") ENGINE = InnoDB, PARTITION p_MAXVALUE VALUES LESS THAN MAXVALUE ENGINE = InnoDB)";
										sql_import = "insert ignore into " + tbl_id_new + " select id, network_id, device_id, datetime, unixtime, tx, rx, wan_id, wan_name from " + tbl_id + " where unixtime > " + tDayHouseKeep;
										
									} else if (tableName.equals("client_infos")) {
										sql_create = "CREATE TABLE "+tbl_id_new+" (`client_id` varchar(50) CHARACTER SET latin1 NOT NULL, " 
												+ "`client_name` varchar(200) DEFAULT NULL, `last_device_id` int(10) DEFAULT NULL, `last_updated` timestamp NULL DEFAULT NULL, "
												+ "PRIMARY KEY (client_id, last_updated)) ENGINE=InnoDB DEFAULT CHARSET=utf8 "
												+ "PARTITION BY RANGE (UNIX_TIMESTAMP(last_updated)) (partition `"+pDayEnd+"` values less than ("+tDayEnd+") , partition p_MAXVALUE values less than MAXVALUE)";
										sql_import = "insert ignore into " + tbl_id_new + " select client_id, client_name, last_device_id, last_updated from " + tbl_id;
										
									} else if (tableName.equals("client_usages")) {
										sql_create = "CREATE TABLE " + tbl_id_new + " (`id` varchar(45) NOT NULL, `network_id` int(10) unsigned NOT NULL, `device_id` int(10) unsigned DEFAULT NULL, " 
										 		+ "`mac` varchar(45) NOT NULL, `ip` varchar(45) DEFAULT NULL, `name` varchar(200) DEFAULT NULL, `tx` float DEFAULT NULL, `rx` float DEFAULT NULL, "
										 		+ "`datetime` datetime DEFAULT NULL, `unixtime` int(10) unsigned DEFAULT NULL, `type` varchar(20) DEFAULT NULL, "
										 		+ "PRIMARY KEY (`id`, `unixtime`), KEY `idx_for_hourly_client_count` (`network_id`,`unixtime`,`device_id`,`type`), "
										 		+ "UNIQUE KEY `ut_nid_did_mac_ip` (network_id, unixtime, device_id, mac, ip)) ENGINE=InnoDB DEFAULT CHARSET=utf8 "	
										 		+ "PARTITION BY RANGE(unixtime) (partition `"+pDayEnd+"` values less than ("+tDayEnd+") , partition p_MAXVALUE values less than MAXVALUE)";
										sql_import = "insert ignore into " + tbl_id_new + " select id, network_id, device_id, mac, ip, name, tx, rx, datetime, unixtime, type from " + tbl_id + " where unixtime > " + tDayHouseKeep;

//									} else if (tableName.equals("client_monthly_usages")) {	// don't enable this as the tables are very very large
//										sql_create = "CREATE TABLE " + tbl_id_new + " (`id` varchar(45) NOT NULL, `network_id` int(10) unsigned NOT NULL, `device_id` int(11) unsigned DEFAULT NULL, " 
//												+ "`datetime` datetime DEFAULT NULL, `unixtime` int(10) unsigned DEFAULT NULL, `ip` varchar(32) DEFAULT NULL, "
//										 		+ "`mac` varchar(32) NOT NULL, `rx` float DEFAULT NULL, `tx` float DEFAULT NULL, `type` varchar(20) DEFAULT NULL, "
//										 		+ "PRIMARY KEY (`id`, `unixtime`), "
//										 		+ "KEY `index_for_client_usage` (`network_id`,`device_id`,`unixtime`,`mac`,`ip`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 "	
//										 		+ "PARTITION BY RANGE(unixtime) (partition `"+pDayEnd+"` values less than ("+tDayEnd+") , partition p_MAXVALUE values less than MAXVALUE)";
//										sql_import = "insert ignore into " + tbl_id_new + " select id, network_id, device_id, datetime, unixtime, ip, mac, rx, tx, type from " + tbl_id + " where unixtime > " + tDayHouseKeep;

									} else if (tableName.equals("event_log")) {
										sql_create = "CREATE TABLE " + tbl_id_new + " (`id` varchar(45) NOT NULL, `network_id` int(10) unsigned NOT NULL, `datetime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00', " 
												+ "`unixtime` int(10) unsigned DEFAULT NULL, `device_id` int(11) unsigned DEFAULT NULL, `ssid` varchar(45) DEFAULT NULL, "
												+ "`client_name` varchar(45) DEFAULT NULL, `client_mac` varchar(45) DEFAULT NULL, `event_type` varchar(45) DEFAULT NULL, "
												+ "`detail` varchar(500) DEFAULT NULL, `timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP, "
												+ "PRIMARY KEY (`id`, `unixtime`), KEY `event_log1_idx` (`network_id`,`unixtime`,`device_id`,`event_type`), KEY `event_log2_idx` (`network_id`,`device_id`,`unixtime`), "
												+ "KEY `event_log3_idx` (`network_id`,`event_type`,`unixtime`), KEY `event_log4_idx` (`network_id`,`device_id`,`event_type`,`unixtime`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 "	
												+ "PARTITION BY RANGE(unixtime) (partition `"+pDayEnd+"` values less than ("+tDayEnd+") , partition p_MAXVALUE values less than MAXVALUE)";
										
									} else if (tableName.equals("captive_portal_activities")){
										sql_create = "CREATE TABLE" + tbl_id_new + " (`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT, `iana_id` INT(11) DEFAULT NULL, `sn` VARCHAR(255) DEFAULT NULL, `cp_id` INT(11) NOT NULL,"
												+ "`ssid` VARCHAR(30) DEFAULT NULL, `username` VARCHAR(50) DEFAULT NULL, `client_mac` VARCHAR(30) DEFAULT NULL, `bssid` VARCHAR(30) DEFAULT NULL, `bandwidth_used` INT(11) DEFAULT NULL,"
												+ "`time_used` INT(11) DEFAULT NULL, `created_at` DATETIME DEFAULT NULL, `activity_type` VARCHAR(255) DEFAULT NULL COMMENT 'types: user_login, user_logout, login, logout, connect, disconnect, usage',"
												+ "`status` INT(11) DEFAULT NULL, `status_msg` VARCHAR(255) DEFAULT NULL, `unixtime` int(10) unsigned DEFAULT NULL, PRIMARY KEY (`id`, `unixtime`), "
												+ "INDEX cp_activities_idx_ca_at_cm_un (`created_at`, `activity_type`, `client_mac`, `username`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 "
												+ "PARTITION BY RANGE(unixtime) (partition `"+pDayEnd+"` values less than ("+tDayEnd+") , partition p_MAXVALUE values less than MAXVALUE)";
										sql_import = "insert ignore into " + tbl_id_new + " select id, iana_id, sn, cp_id, ssid, username, client_mac, bssid, bandwidth_used, time_used, created_at, activity_type, status, status_msg, null from " + tbl_id + " where unixtime > " + tDayHouseKeep;

									} else if (tableName.equals("captive_portal_access_logs")){
										sql_create = "CREATE TABLE" + tbl_id_new + " (`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT, `network_id` INT(11) DEFAULT NULL, `device_id` INT(11) DEFAULT NULL, `ssid_id` INT(11) DEFAULT NULL,"
												+ "`cp_id` INT(11) DEFAULT NULL, `unixtime` INT(11) DEFAULT NULL, `report_type` VARCHAR(50) DEFAULT NULL, `ssid` VARCHAR(50) DEFAULT NULL,"
												+ "`access_mode` VARCHAR(50) DEFAULT NULL, `client_mac` VARCHAR(50) DEFAULT NULL, `extra_info` VARCHAR(50) DEFAULT NULL,"
												+ "PRIMARY KEY (`id`, `unixtime`), INDEX idx_ut_nid_did_ssid_id_rt (`unixtime`, `network_id`, `device_id`, `ssid_id`, `report_type`)"
												+ "PARTITION BY RANGE(unixtime) (partition `"+pDayEnd+"` values less than ("+tDayEnd+") , partition p_MAXVALUE values less than MAXVALUE)";
										sql_import = "insert ignore into " + tbl_id_new + " select id, network_id, device_id, ssid_id, cp_id, unixtime, report_type, ssid, access_mode, client_mac, extra_info from " + tbl_id + " where unixtime > " + tDayHouseKeep;
										
									} else if (tableName.equals("captive_portal_daily_usage")){
										sql_create = "CREATE TABLE" + tbl_id_new + " (`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT, `report_date` DATETIME DEFAULT NULL, `unixtime` INT(11) NOT NULL,"
												+ "`cp_id` INT UNSIGNED DEFAULT NULL, `bandwidth_used` INT(15) DEFAULT NULL, `time_used` INT(15) DEFAULT NULL, `session_count` INT DEFAULT 0,"
												+ "PRIMARY KEY (`id`, `unixtime`), INDEX id_unixtime_cp_id (`id`, `unixtime`, `cp_id`), UNIQUE INDEX rptdate_cpid (report_date, cp_id),"
												+ "PARTITION BY RANGE(unixtime) (partition `"+pDayEnd+"` values less than ("+tDayEnd+") , partition p_MAXVALUE values less than MAXVALUE)";
										sql_import = "insert ignore into " + tbl_id_new + " select id, report_date, unixtime, ssid_id, cp_id, bandwidth_used, time_used, session_count from " + tbl_id + " where unixtime > " + tDayHouseKeep;
									}
	
									if(sql_create == null) {
										logger.error("Unexpected sql_create is NULL: " + tableName);
										jobList.add(tbl_id+" SKIP ERROR_CREATE NULL");
										continue;
									}
//									logger.info(sql_create);
									dbQuery.executeUpdate(sql_dropifexists);
									logger.info("drop if exists new partitioned table: " + tbl_id_new);
									dbQuery.executeUpdate(sql_create);
									logger.info("created new partitioned table: " + tbl_id_new + " and now import data: "+sql_import);
		
									int nimported = dbQuery.executeUpdate(sql_import);
									logger.info("imported ("+nimported+") rows into partitioned table: " + tbl_id_new + " and now drop & rename the existing table...");
		
									String sql_drop = "drop table " + tbl_id;
									dbQuery.executeUpdate(sql_drop);
		
									String sql_rename = "rename table " + tbl_id_new + " to " + tbl_id;
									dbQuery.executeUpdate(sql_rename);
									
									long tused = System.currentTimeMillis() / 1000 - tconvert;
									logger.info("Table "+tbl_id+" converted to partitioned table in " + tused);
									
									skip_future = true; // already include change in future
									skip_past = true; // just ignore past records
								}
	
								if(!skip_past) {
									String sql_housekeep = "alter table "+tbl_id+" DROP PARTITION ";
									boolean first = true;
									for(Integer partitionNumber: partitionDelList) {
										if(first) {
											sql_housekeep += "p" + partitionNumber;
											first = false;
										}
										else
											sql_housekeep += ", p" + partitionNumber;
									}
									try {
										logger.info("Housekeep outdated partitions sql: " + sql_housekeep);
										dbQuery.executeUpdate(sql_housekeep);
	
										int time_exe = (int) (System.currentTimeMillis() / 1000);
										houseKeepLogs.setTime(time_exe);
										houseKeepLogs.setOrganizationId(orgId);
										houseKeepLogs.setTable_Name(tableName);
										houseKeepLogsDAO.save(houseKeepLogs);
										if(logger.isInfoEnabled())
											logger.info("Housekeep> dropped partition: " + sql_housekeep);
									} catch (SQLException sqle) {
										logger.error("housekeep alter table exception: sql="+sql_housekeep+", e="+sqle, sqle);
										jobList.add(tbl_id+" CONT EX_PAST " + sqle);
									}
								} else 
									logger.warn("No outdated partition is found in "+tbl_id);
								
								if(!skip_future) {
									
									String sql_add_future = "alter table "+tbl_id+" reorganize partition `p_MAXVALUE` into (partition `"+pDayEnd+"` values less than ("+tDayEnd+") , partition p_MAXVALUE values less than MAXVALUE)";
									try {
										logger.info("Add new partition sql: " + sql_add_future);
										dbQuery.executeUpdate(sql_add_future);
									} catch (SQLException sqle) {
										if(sqle.toString().indexOf("Duplicate partition")!=-1) {
											logger.error("Exception future partition "+pDayEnd+" already exists: "+tbl_id);
										} else 
											logger.error("alter table exception: "+sqle, sqle);
										jobList.add(tbl_id+" CONT EX_FUTURE " + sqle);
									}
								} else 
									logger.warn("Future partition "+pDayEnd+" already exists: "+tbl_id);
								
								long tdone = (System.currentTimeMillis() - torg) / 1000;
								logger.info("Partition " + pDayEnd + " created in " + tbl_id + ", time used=" + tdone);
								jobList.add(tbl_id+" DONE " +tblSize+" " + tdone);
							} // end OrgId loop
							for (String job: jobList) {
								logger.info(job);
							}
						}
					} catch (Exception e) {
						logger.error("Error in Housekeep loop " + e, e);
					} finally {
						try {
							if(dbConnection != null)
								dbConnection.close();
						}
						catch( SQLException e ) {
							logger.error("Close DB connection error -", e);
						}
					}
				} // if for selected tables
			}// end loop tables
			Calendar cal3 = Calendar.getInstance();
			cal3.add(Calendar.DATE, -30);
			int clear_time = (int)(cal3.getTimeInMillis() / 1000);
			houseKeepLogsDAO.deleteHouseKeepLogsBeforeTime(clear_time);
			logger.info("doHouseKeepScheduler: cleanup housekeep logs before " + cal3);

			long tused = (System.currentTimeMillis() - tstart) / 1000;
			logger.info("doHouseKeepScheduler end, tused = " + tused);
		}
		catch (Exception e) { 
			logger.error("doHouseKeepScheduler Exception - " + e,e);
		} finally {
			housekeeping = false;
			housekeepTime = tmpTime;
			logger.info(VER + " -- doHouseKeepScheduler end, housekeepTime = " + housekeepTime);
		}
	}
	
	private static long estimateTableSize(DBQuery dbQuery, String orgId, String tableName) {
		long size = -1;

		String sql_estimate = "explain select 1 from peplink_organization_" + orgId + "." + tableName;
		try {
			ResultSet rs_estimate = dbQuery.executeQuery(sql_estimate);
			if(rs_estimate.next()) {
				size  = rs_estimate.getLong("rows");
			}
			rs_estimate.close();
		} catch (Exception e) {
			logger.error("estimateTableSize exception="+e, e);
		} 
		return size;
	}
	
	public static void main(String[] args)
	{
		HouseKeepUtils.doHouseKeepScheduler();
	}
}
