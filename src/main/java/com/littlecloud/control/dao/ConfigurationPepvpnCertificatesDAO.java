package com.littlecloud.control.dao;

// Generated Apr 15, 2013 12:32:59 PM by Hibernate Tools 4.0.0

import java.sql.SQLException;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.ConfigurationPepvpnCertificates;
import com.littlecloud.control.entity.ConfigurationPepvpnCertificatesId;

/**
 * Home object for domain model class Devices.
 * 
 * @see com.littlecloud.control.entity.Devices
 * @author Hibernate Tools
 */
public class ConfigurationPepvpnCertificatesDAO extends BaseDAO<ConfigurationPepvpnCertificates, ConfigurationPepvpnCertificatesId> {

	private static final Logger log = Logger.getLogger(ConfigurationPepvpnCertificatesDAO.class);

	public ConfigurationPepvpnCertificatesDAO() throws SQLException {
		super(ConfigurationPepvpnCertificates.class);
	}

	public ConfigurationPepvpnCertificatesDAO(String orgId) throws SQLException {
		super(ConfigurationPepvpnCertificates.class, orgId);
	}
}
