package com.littlecloud.services;

import java.sql.SQLException;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.DeviceGpsRecordsDAO;
import com.littlecloud.control.entity.report.DeviceGpsRecords;
import com.littlecloud.control.entity.report.DeviceGpsRecordsId;

public class DeviceGpsRecordsMgr {
	
	private static final Logger log = Logger.getLogger(DeviceGpsRecordsMgr.class);
	private String orgId;
	private DeviceGpsRecordsDAO gpsDAO;
	
	public DeviceGpsRecordsMgr(String orgId){
		this.orgId = orgId;
		init();
	}
	
	private void init(){
		try{
			gpsDAO = new DeviceGpsRecordsDAO(orgId);
		}catch (Exception e){
			log.error("init ", e);
		}
	}

	public void save(DeviceGpsRecords transientInstance) throws SQLException {
		gpsDAO.save(transientInstance);
	}

	public void update(DeviceGpsRecords transientInstance) throws SQLException {
		gpsDAO.update(transientInstance);
	}

	public void saveOrUpdate(DeviceGpsRecords transientInstance) throws SQLException {
		gpsDAO.saveOrUpdate(transientInstance);
	}

	public void delete(DeviceGpsRecords persistentInstance) throws SQLException {
		gpsDAO.delete(persistentInstance);
	}

	public void delete(DeviceGpsRecords[] persistentInstanceArray) throws SQLException {
		gpsDAO.delete(persistentInstanceArray);
	}

	public DeviceGpsRecords findById(DeviceGpsRecordsId id) throws SQLException {
		return gpsDAO.findById(id);
	}
	
	
}
