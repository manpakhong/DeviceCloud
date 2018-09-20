package com.littlecloud.ac.util;

import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.handler.BranchServerRedirectionHandler;
import com.littlecloud.ac.handler.RootServerRedirectionHandler;
import com.littlecloud.ac.json.model.Json_RedirectWtp;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.control.dao.jdbc.BaseDaoInstances.InstanceType;
import com.littlecloud.control.json.util.JsonUtils;
import com.peplink.api.db.util.DBUtil;

public class RootBranchRedirectManager {
	
	private static final Logger log = Logger.getLogger(RootBranchRedirectManager.class);
	
	public static enum SERVER_MODE {
		ROOT_SERVER_MODE, BRANCH_SERVER_MODE, ERROR_MIX_SERVER_MODE
	}
	
	public static DBUtil getDbUtilInstance() throws Exception {
		DBUtil dbUtil = null;
		
		switch(RootBranchRedirectManager.getServerMode())
		{
			case ROOT_SERVER_MODE:	
			{
				/* running in root redirect to branch */
				dbUtil = DBUtil.getInstance(InstanceType.IC2ROOT.getValue());
			}break;
			case BRANCH_SERVER_MODE:	
			{
				/* running in branch redirect to root */
				dbUtil = DBUtil.getInstance();				
			}break;
			default:
			{
				/* running in branch */
				dbUtil = DBUtil.getInstance();
			}break;
		}
		
		return dbUtil;
	}
	

	public static boolean isBranchServerMode() {
		if (getServerMode()==SERVER_MODE.BRANCH_SERVER_MODE)
			return true;
		
		return false;
	}
	
	public static boolean isRootServerMode() {
		if (getServerMode()==SERVER_MODE.ROOT_SERVER_MODE)
			return true;
		
		return false;
	}
	
	public static SERVER_MODE getServerMode() {
		if (BranchServerRedirectionHandler.getInstance().isBranchredirectionenabled() && RootServerRedirectionHandler.getInstance().isRootRedirectionEnabled())
			return SERVER_MODE.ERROR_MIX_SERVER_MODE;
		else if (BranchServerRedirectionHandler.getInstance().isBranchredirectionenabled())
			return SERVER_MODE.ROOT_SERVER_MODE;
		else if (RootServerRedirectionHandler.getInstance().isRootRedirectionEnabled())
			return SERVER_MODE.BRANCH_SERVER_MODE;
		else
			return SERVER_MODE.BRANCH_SERVER_MODE;
	}
	
	public static boolean sendRedirection(DeviceRedirectInfo ctr) {
				
		Json_RedirectWtp jsonRedirectWtp = new Json_RedirectWtp();
		
		switch (ctr.getRedirectMode())
		{
			/* reduce gc */
			case BRANCH_SERVER_MODE:
			{
				RootServerRedirectionHandler rootServer = RootServerRedirectionHandler.getInstance();
				
				jsonRedirectWtp.setClient_cert(rootServer.getCertPublic());
				jsonRedirectWtp.setClient_key(rootServer.getCertPrivate());
				jsonRedirectWtp.setCn(rootServer.getCertCn());
				jsonRedirectWtp.setRoot_cert(rootServer.getCertRoot());
			}break;
			case ROOT_SERVER_MODE:
			{
				BranchServerRedirectionHandler branchServer = BranchServerRedirectionHandler.getInstance();
				
				jsonRedirectWtp.setClient_cert(branchServer.getCertPublic());
				jsonRedirectWtp.setClient_key(branchServer.getCertPrivate());
				jsonRedirectWtp.setCn(branchServer.getCertCn());
				jsonRedirectWtp.setRoot_cert(branchServer.getCertRoot());
			}break;
			default:
			{
				log.errorf("Unknown redirect mode for %s", ctr);
			}break;
		}
		
		jsonRedirectWtp.setHost(ctr.getHostList());		
		jsonRedirectWtp.setVersion(1);		
		jsonRedirectWtp.setPermanent(1);	// false

		if (log.isInfoEnabled() || ctr.getRedirectMode()==SERVER_MODE.ROOT_SERVER_MODE) log.infof("REDIRECT %s", ctr);
		ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_REDIRECT_WTP, JsonUtils.genServerRef(), ctr.getIana_id(), ctr.getSn(), jsonRedirectWtp);

		return true;
	}
	
	public static class DeviceRedirectInfo {
		private Integer iana_id;
		private String sn;
		private List<String> hostList;
		private SERVER_MODE redirectMode;

		public Integer getIana_id() {
			return iana_id;
		}

		public void setIana_id(Integer iana_id) {
			this.iana_id = iana_id;
		}

		public String getSn() {
			return sn;
		}

		public void setSn(String sn) {
			this.sn = sn;
		}

		public List<String> getHostList() {
			return hostList;
		}

		public void setHostList(List<String> hostList) {
			this.hostList = hostList;
		}

		public SERVER_MODE getRedirectMode() {
			return redirectMode;
		}

		public void setRedirectMode(SERVER_MODE redirectMode) {
			this.redirectMode = redirectMode;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("DeviceRedirectInfo [iana_id=");
			builder.append(iana_id);
			builder.append(", sn=");
			builder.append(sn);
			builder.append(", hostList=");
			builder.append(hostList);
			builder.append(", redirectMode=");
			builder.append(redirectMode);
			builder.append("]");
			return builder.toString();
		}
	}
}
