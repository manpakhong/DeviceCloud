package com.littlecloud.services;

import java.sql.SQLException;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.DevicesDAO;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.json.model.config.util.AdminConfigGroupLevel;
import com.littlecloud.pool.object.utils.NetUtils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.util.DBUtil;

public class DeviceMgr {
	private static final Logger log = Logger.getLogger(DeviceMgr.class);
	private String orgId;
	private DevicesDAO devDAO; 

	public DeviceMgr(String orgId) {
		this.orgId = orgId;
		init();
	}

	private void init() {
		try {
			devDAO = new DevicesDAO(orgId);
		} catch (Exception e) {
			log.error("init ", e);
		}
	}
	
	public Devices findById(Integer id) throws SQLException {
		return devDAO.findById(id);
	}
	
	public void update(Devices dev) throws SQLException {
		devDAO.update(dev);
	}

	public List<Devices> getDevicesList(Integer networkId) throws SQLException {
		return devDAO.getDevicesList(networkId);
	}
	
	public List<Integer> getDevicesIdList(Integer networkId) throws SQLException {
		return devDAO.getDevicesIdList(networkId);
	}

	public static class PasswordGenerateAction {
		public boolean isGenAdmin;
		public boolean isResetAdmin;
		public boolean isGenUser;
		public boolean isResetUser;
		
		public boolean isGenAdmin() {
			return isGenAdmin;
		}
		public void setGenAdmin(boolean isGenAdmin) {
			this.isGenAdmin = isGenAdmin;
		}
		public boolean isResetAdmin() {
			return isResetAdmin;
		}
		public void setResetAdmin(boolean isResetAdmin) {
			this.isResetAdmin = isResetAdmin;
		}
		public boolean isGenUser() {
			return isGenUser;
		}
		public boolean isResetUser() {
			return isResetUser;
		}
		public void setGenUser(boolean isGenUser) {
			this.isGenUser = isGenUser;
		}
		public void setResetUser(boolean isResetUser) {
			this.isResetUser = isResetUser;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("PasswordGenerateAction [isGenAdmin=");
			builder.append(isGenAdmin);
			builder.append(", isResetAdmin=");
			builder.append(isResetAdmin);
			builder.append(", isGenUser=");
			builder.append(isGenUser);
			builder.append(", isResetUser=");
			builder.append(isResetUser);
			builder.append("]");
			return builder.toString();
		}
	}

	public boolean generateRandomPasswordForNetwork(Integer netId, PasswordGenerateAction passGenAction)
	{
		DBUtil dbUtil = null;
		DBConnection batchConn = null;
		
		if (!passGenAction.isGenAdmin && !passGenAction.isGenUser && !passGenAction.isResetUser)
			return true;
		
		log.warnf("INFO generateRandomPasswordForNetwork org %s net %d passGenAction %s", this.orgId, netId, passGenAction);
		
		try {
			dbUtil = DBUtil.getInstance();
			dbUtil.startSession();			
			batchConn = dbUtil.getConnection(false, this.orgId, false);
			batchConn.setBatchMode();
			
			List<Devices> devLst = devDAO.getDevicesList(netId);
			
			for (Devices dev:devLst)
			{				
				if (passGenAction.isGenAdmin)
					dev.setWebadmin_password(AdminConfigGroupLevel.generatePassword());
				
				if (passGenAction.isGenUser)
					dev.setWebadmin_user_password(AdminConfigGroupLevel.generatePassword());
				
				if (passGenAction.isResetUser)
					dev.setWebadmin_user_password("");
				
				dev.update();				
				batchConn.addBatch(dev);
			}
			
			NetUtils.reload(orgId, netId);
			
			return true;
		} catch (Exception e) {
			log.error("generateRandomPasswordForNetwork ",e);
			return false;
		} finally {
			if (batchConn!=null)
			{
				try {
					batchConn.commit();
					batchConn.close();
				} catch (Exception e)
				{
					log.error("fail to commit/close batchConn", e);
					return false;
				}
			}
			
			if( dbUtil.isSessionStarted() )
				dbUtil.endSession();
		}
	}
}
