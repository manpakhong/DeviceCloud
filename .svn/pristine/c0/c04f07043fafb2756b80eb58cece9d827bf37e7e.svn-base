package com.littlecloud.control.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.jboss.logging.Logger;

import com.littlecloud.control.entity.report.DevicesChannelUtilizations;
import com.littlecloud.control.dao.jdbc.BaseDAO;


public class DevicesChannelUtilizationsDAO extends BaseDAO<DevicesChannelUtilizations, Integer>{

	private static final Logger log = Logger.getLogger(DevicesChannelUtilizationsDAO.class);

	public DevicesChannelUtilizationsDAO() throws SQLException {
		super(DevicesChannelUtilizations.class);
	}

	public DevicesChannelUtilizationsDAO(String orgId) throws SQLException {
		super(DevicesChannelUtilizations.class, orgId);
	}
	
	// No reference
//	public List<DevicesChannelUtilizations> getByDevicesIdAndStarttime(Integer devId, Date start)
//	{
//		/* Return 24 hours data from start time, or latest device location if start time is not specified. */
//		// DeviceLocations locExample = new DeviceLocations();
//		// locExample.setDevices(dev);
//		// DeviceLocations loc = findMaxByExample(locExample);
//		//
//		// HashSet<DeviceLocations> resultSet = new HashSet<DeviceLocations>();
//		// resultSet.addAll(Arrays.asList(loc));
//		// return resultSet;
//
//		try {
//			Criteria cri = session.createCriteria(this.persistentClass);
//			cri.add(Restrictions.eq("device_id", devId));
//
//			if (start != null)
//			{
//				log.debug("start param=" + start);
//				cri.add(Restrictions.ge("datetime", start));
//			}
//
//			List<DevicesChannelUtilizations> results = cri.list();
//
//			log.debug("find by example successful, result size: " + results.size());
//			return results;
//		} catch (RuntimeException re) {
//			log.error("find by example failed", re);
//			throw re;
//		}
//	} 
	
}
