package com.littlecloud.ac.handler;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.ac.util.RootBranchRedirectManager;
import com.littlecloud.ac.util.RootBranchRedirectManager.DeviceRedirectInfo;
import com.littlecloud.ac.util.RootBranchRedirectManager.SERVER_MODE;
import com.littlecloud.pool.utils.PropertyService;
import com.littlecloud.utils.CryptoUtils;

public class RootServerRedirectionHandler {
	
	private static final Logger log = Logger.getLogger(OnlineStatusAndCustomEventHandler.class);
	
	private static final PropertyService<RootServerRedirectionHandler> ps = new PropertyService<RootServerRedirectionHandler>(RootServerRedirectionHandler.class);
	private static RootServerRedirectionHandler instance = null;
	
	private final boolean rootRedirectionEnabled = ps.getString("root.server.enabled").equalsIgnoreCase("true")?true:false;  
	private List<String> rootServers = null;	
	private String certPublic = null;
	private String certPrivate = null;
	private String certRoot = null;
	private String certCn = null;

	public static RootServerRedirectionHandler getInstance() 
	{
		if (instance==null)
			instance = new RootServerRedirectionHandler();

		return instance;
	}
	
	private RootServerRedirectionHandler() {
		log.warnf("INFO RootServerRedirectionHandler initializing");
		
		rootServers = initRootServers();
		certRoot = CryptoUtils.readFromFileCharByChar(ps.getString("root.CERT_CA"));
		certPrivate = CryptoUtils.readFromFileCharByChar(ps.getString("root.CERT_PRIVATE"));
		certPublic = CryptoUtils.readFromFileCharByChar(ps.getString("root.CERT_PUBLIC"));
		certCn = ps.getString("root.CERT_PEER_CN");
		
		log.warnf("INFO RootServerRedirectionHandler initialized\n%s\n%s\n%s\n%s\n%s\n%s",
				rootRedirectionEnabled,
				rootServers,
				certRoot,
				certPrivate,
				certPublic,
				certCn);
	}
	
	private List<String> initRootServers() {
		List<String> result = new ArrayList<String>();
		
		String[] rootArr = ps.getString("root.servers").split(",");
		
		for (String rootServer:rootArr)
		{
			result.add(rootServer.trim());
		}
		
		return result;
	}
	
	public boolean redirectToRootIfNotRegistered(Integer ianaId, String sn) {
		if (rootRedirectionEnabled)
		{
			DeviceRedirectInfo info = new DeviceRedirectInfo();
			info.setIana_id(ianaId);
			info.setSn(sn);
			info.setRedirectMode(SERVER_MODE.BRANCH_SERVER_MODE);
			info.setHostList(rootServers);
			return RootBranchRedirectManager.sendRedirection(info);
		}
		
		return true;
	}

	/******** getters and setters ********/
	public boolean isRootRedirectionEnabled() {
		return rootRedirectionEnabled;
	}

	public String getCertPublic() {
		return certPublic;
	}

	public String getCertPrivate() {
		return certPrivate;
	}

	public String getCertRoot() {
		return certRoot;
	}

	public String getCertCn() {
		return certCn;
	}
}
