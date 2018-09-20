package com.littlecloud.control.dao;

// Generated Apr 15, 2013 12:32:59 PM by Hibernate Tools 4.0.0

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.control.dao.jdbc.BaseDAO;
import com.littlecloud.control.entity.ConfigurationRadioChannels;
import com.littlecloud.control.entity.ConfigurationRadioChannelsId;
import com.littlecloud.control.entity.ConfigurationSsids;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.pool.object.utils.DeviceUtils;
import com.littlecloud.pool.utils.Utils;
import com.peplink.api.db.DBConnection;
import com.peplink.api.db.DBQuery;

/**
 * Home object for domain model class ConfigurationRadioChannels.
 * 
 * @see com.littlecloud.control.entity.ConfigurationRadioChannels
 * @author Hibernate Tools
 */
public class ConfigurationRadioChannelsDAO extends BaseDAO<ConfigurationRadioChannels, ConfigurationRadioChannelsId> {

	private static final Logger log = Logger.getLogger(ConfigurationRadioChannelsDAO.class);

	public ConfigurationRadioChannelsDAO() throws SQLException {
		super(ConfigurationRadioChannels.class);
	}

	public ConfigurationRadioChannelsDAO(String orgId) throws SQLException {
		super(ConfigurationRadioChannels.class, orgId);
	}

	public ConfigurationRadioChannelsDAO(String orgId, boolean readonly) throws SQLException {
		super(ConfigurationRadioChannels.class, orgId, readonly);
	}

	public HashMap<ConfigurationRadioChannelsId, ConfigurationRadioChannels> getFromDevIdLst(List<Integer> devIdLst) throws SQLException
	{
		HashMap<ConfigurationRadioChannelsId, ConfigurationRadioChannels> crMap = new HashMap<ConfigurationRadioChannelsId, ConfigurationRadioChannels>();
		List<ConfigurationRadioChannels> crList = new ArrayList<ConfigurationRadioChannels>();
		if (devIdLst == null || devIdLst.size() == 0)
			return crMap;

		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();
			query.setQueryClass(persistentClass);
			String strSQL = "select * from " + query.getDBName() + ".configuration_radio_channels where device_id in (" + intListToParam(devIdLst) + " ) ";

			crList = query.executeQueryAsObject(strSQL);
			log.debug("query successful, result size: " + crList.size());

			for (ConfigurationRadioChannels cr : crList)
			{
				crMap.put(cr.getId(), cr);
			}

			return crMap;

		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session != null)
				session.close();
		}
	}

	@Deprecated
	public List<ConfigurationRadioChannels> getListFromDevLst(List<Devices> devLst) throws SQLException
	{
		List<Integer> devIdLst = DeviceUtils.getDevIdLstFromDevLst(devLst);
		List<ConfigurationRadioChannels> result = new ArrayList<ConfigurationRadioChannels>();

		DBConnection session = getSession();
		try
		{
			DBQuery query = session.createQuery();

			StringBuilder sb = new StringBuilder();
			sb.append("select c1.* from ");
			sb.append(query.getDBName());
			sb.append(".");
			sb.append("configuration_radio_channels c1 ");
			sb.append("where c1.device_id in (" + intListToParam(devIdLst) + ") order by c1.module_id ");

			query.setQueryClass(persistentClass);
			List<ConfigurationSsids> results = query.executeQueryAsObject(sb.toString());
			log.debug("SQL=" + sb.toString());
			return result;
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session != null)
				session.close();
		}
	}

	public void removeRadioChannel(Integer deviceId) throws SQLException {
		log.debug("removing radio channel setting for deviceId: " + deviceId);
		DBConnection session = getSession();
		// String dbPrefix = Utils.PARAM_ORGANIZATION_DB_PREFIX;
		// String dbname = dbPrefix + session.getOrgId();

		try {
			DBQuery query = session.createQuery();
			query.executeUpdate("delete from " + query.getDBName() + ".configuration_radio_channels where device_id = " + deviceId);
		} catch (SQLException e)
		{
			throw e;
		} finally
		{
			if (session != null)
				session.close();
		}
	}
}
