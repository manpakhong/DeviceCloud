package com.littlecloud.ac.logparser;

import org.jboss.logging.Logger;

import com.littlecloud.pool.utils.PropertyService;

public class AcDbUtils {
	
	private static final Logger log = Logger.getLogger(AcDbUtils.class);	
	private static PropertyService<AcDbUtils> ps = new PropertyService<AcDbUtils>(AcDbUtils.class);
	
	private static final String tableName = ps.getString("tableName");
	
	public void createTable() {
		
		if (tableName == null || tableName.isEmpty())
		{
			log.error("tableName is not found in config!");
			return;
		}
		
//		CREATE TABLE `ac_log_[timestamp]` (
//		  `server_id` VARCHAR(100) NOT NULL,
//		  `last_timestamp_read` VARCHAR(100),
//		  `lastupdate` timestamp,
//		  PRIMARY KEY (`server_id`))
//		ENGINE = INNODB;
		
//		CREATE TABLE `ac_log_[timestamp]` (
//				  `id` INT(11) UNSIGNED NOT NULL,
//		  		  `filename` VARCHAR(100),
//				  `unixtime` INT,
//				  `datetime` DATETIME,	
//				  `src` VARCHAR(45),
//				  `json` TEXT NULL,
//				  `lastupdate` timestamp,
//				  PRIMARY KEY (`id`))
//				ENGINE = INNODB
//				ROW_FORMAT = COMPRESSED;
	}
}
