package com.littlecloud.fusionhub;

import java.util.Arrays;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.pool.utils.PropertyService;

public class FusionHubProperties {

	private static final Logger log = Logger.getLogger(FusionHubProperties.class);
	
	private static final PropertyService<FusionHubProperties> ps = new PropertyService<FusionHubProperties>(FusionHubProperties.class);
	private static FusionHubProperties fprop = null;
	private static final String COMMA = ",";
	
	public final Integer FusionHubIanaId = 23695;	
	public String FusionHubVerifyLicURL;	
	public List<String> FusionHubDefaultSnLst;
	public List<String> FusionHubDefaultModelLst;
	
	private FusionHubProperties() {
		FusionHubVerifyLicURL = ps.getString("FusionHubVerifyLicURL");
		FusionHubDefaultSnLst = Arrays.asList(ps.getString("FusionHubDefaultSn").split(COMMA));
		FusionHubDefaultModelLst = Arrays.asList(ps.getString("FusionHubDefaultModel").split(COMMA));
		
		log.info("FusionHubVerifyLicURL="+FusionHubVerifyLicURL);
		log.info("FusionHubDefaultSnLst="+FusionHubDefaultSnLst);
		log.info("FusionHubDefaultModelLst="+FusionHubDefaultModelLst);
	}
	
	public synchronized static FusionHubProperties getInstance() {
		if (fprop == null) {
			try {
				fprop = new FusionHubProperties();
			} catch (Exception e) {
				log.error("Failed to init FusionHubProperties. " + e, e);
				return null;
			}
		}
		return fprop;
	}
}
