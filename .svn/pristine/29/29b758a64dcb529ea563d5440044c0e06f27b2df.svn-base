package com.littlecloud.pool.utils;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.ac.util.RootBranchRedirectManager;
import com.littlecloud.control.dao.jdbc.BaseDaoInstances.InstanceType;
import com.peplink.api.db.util.DBUtil;

public class DBUtilManager {
	private static final Logger log = Logger.getLogger(DBUtilManager.class);
	
	public static List<DBUtil> getAllDbUtilInstance() throws Exception {
		List<DBUtil> dbUtilList = new ArrayList<DBUtil>();
		DBUtil dbUtil = null, dbUtilCap = null, dbUtilSup = null;
		
		switch (RootBranchRedirectManager.getServerMode()) {
		case ROOT_SERVER_MODE:	
			if (RootBranchRedirectManager.isRootServerMode()) {
				/* running in root redirect to branch */
				dbUtil = DBUtil.getInstance(InstanceType.IC2ROOT.getValue());
				log.infof("ROOT_SERVER_MODE getAllDbUtilInstance() ");
			}
			break;
		case BRANCH_SERVER_MODE:
			if (RootBranchRedirectManager.isBranchServerMode()) {
				/* running in branch redirect to root */
				dbUtil = DBUtil.getInstance(InstanceType.IC2CORE.getValue());
				
				String propertyCap = System.getProperties().getProperty(InstanceType.CAPTIVEPORTAL.getValue());
				if (propertyCap != null && !propertyCap.trim().equals(""))
				{
					dbUtilCap = DBUtil.getInstance(InstanceType.CAPTIVEPORTAL.getValue());//
					log.infof("BRANCH_SERVER_MODE getAllDbUtilInstance() %s", InstanceType.CAPTIVEPORTAL.getValue());
				}
				
				String propertySup = System.getProperties().getProperty(InstanceType.SUPPORTDB.getValue());
				if (propertySup != null && !propertySup.trim().equals(""))
				{
					dbUtilSup = DBUtil.getInstance(InstanceType.SUPPORTDB.getValue());
					log.infof("BRANCH_SERVER_MODE getAllDbUtilInstance() %s", InstanceType.SUPPORTDB.getValue());
				}
			}
			break;
		default:
			{
				/* running in branch */
				dbUtil = DBUtil.getInstance();
			}break;
		}
		
		if (dbUtil != null) dbUtilList.add(dbUtil);
		if (dbUtilCap != null) dbUtilList.add(dbUtilCap);
		if (dbUtilSup != null) dbUtilList.add(dbUtilSup);

		return dbUtilList;
	}
	
	public static void main(){
		StringBuilder sb = new StringBuilder();
		List<DBUtil> dbUtilList= null;
		try {
			dbUtilList = DBUtilManager.getAllDbUtilInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sb.append("<p style=\"font-family:Arial, sans-serif;font-size:14px;\">");
		sb.append("<font color=\"red\">******************** DBUtil Instances: ");
		
		if (dbUtilList != null){
			
			sb.append(dbUtilList.size());
			sb.append(" ******************** <br>");
			sb.append("*******************************************************************</font><br><br>");

			for (DBUtil dbUtil : dbUtilList) {
				sb.append(dbUtil.dumpDBPool());
				sb.append("<br><font color=\"red\">*******************************************************************</font><br>");
			}
		}
		sb.append("</p>");
		
		System.out.print(sb.toString());
	}
}
