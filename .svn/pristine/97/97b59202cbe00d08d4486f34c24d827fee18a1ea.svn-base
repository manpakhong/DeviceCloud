package com.littlecloud.ac.messagehandler;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.json.model.Json_RedirectWtp;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.ac.json.model.command.QueryInfo;
import com.littlecloud.ac.util.ACUtil;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.entity.Networks;
import com.littlecloud.control.entity.branch.SnsOrganizations;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.utils.BranchUtils;
import com.littlecloud.pool.object.utils.NetUtils;
import com.littlecloud.pool.object.utils.OrgInfoUtils;
import com.littlecloud.pool.utils.PropertyService;
import com.littlecloud.utils.CryptoUtils;

public class RedirectWtpMessageHandler {
	private static final Logger log = Logger.getLogger(RedirectWtpMessageHandler.class);
	private static PropertyService<RedirectWtpMessageHandler> ps = new PropertyService<RedirectWtpMessageHandler>(RedirectWtpMessageHandler.class);
	public static final int acRedirectServerRetryInterval = 180; // 3 minutes
	// cert location
	private static final String certRootPath = ps.getString("CERT_CA");
	private static final String certPrivatePath = ps.getString("CERT_PRIVATE");
	private static final String certPublicPath = ps.getString("CERT_PUBLIC");
	private static final String certCn = ps.getString("CERT_PEER_CN");
	private static String certPublic;
	private static String certPrivate;
	
	public static String getCertrootpath() {
		return certRootPath;
	}

	public static String getCertprivatepath() {
		return certPrivatePath;
	}

	public static String getCertpublicpath() {
		return certPublicPath;
	}

	public static String getCertcn() {
		return certCn;
	}
	
	public static boolean placeACRedirectCommand(List<String> hostList, Integer ianaId, String sn){
		boolean isRedirected = false;
		try{					
			String certRoot = null;
//			String certPrivate = null;
//			String certPublic = null;
			String certCn = null;
			
			if (getCertrootpath()!= null && !getCertrootpath().isEmpty()){
				certRoot = CryptoUtils.readFromFileCharByChar(getCertrootpath());
			} else {
				log.warnf("REDIRECTCMD20140616 - placeACRedirectCommand, certRoot is not read from properties file!!!  sn:%s ", sn);
			}
			
			if (getCertprivatepath() != null && !getCertprivatepath().isEmpty()){
				if (certPrivate == null || certPrivate.isEmpty()){
					certPrivate = CryptoUtils.readFromFileCharByChar(getCertprivatepath());
				}
			} else {
				log.warnf("REDIRECTCMD20140616 - placeACRedirectCommand, certPrivate is not read from properties file!!!  sn:%s ", sn);
			}
			
			if (getCertpublicpath() != null && ! getCertpublicpath().isEmpty()){
				if (certPublic == null || certPublic.isEmpty()){
					certPublic = CryptoUtils.readFromFileCharByChar(getCertpublicpath());
				}
			} else {
				log.warnf("REDIRECTCMD20140616 - placeACRedirectCommand, certPublic is not read from properties file!!!  sn:%s ", sn);
			}
			
			if (getCertcn() != null && !getCertcn().isEmpty()){
				certCn = getCertcn();
			} else {
				log.warnf("REDIRECTCMD20140616 - placeACRedirectCommand, certCn is not read from properties file!!!  sn:%s ", sn);
			}
			
			if (certRoot != null && certPrivate != null && certPublic != null && certCn != null){
				
				String data = "";
				
				Json_RedirectWtp jsonRedirectWtp = new Json_RedirectWtp();
				jsonRedirectWtp.setClient_cert(certPublic);
				jsonRedirectWtp.setClient_key(certPrivate);
				jsonRedirectWtp.setCn(certCn);
				
				jsonRedirectWtp.setHost(hostList);
				jsonRedirectWtp.setRoot_cert(certRoot);
				jsonRedirectWtp.setVersion(1);
								
				data = JsonUtils.toJson(jsonRedirectWtp);

				log.infof("REDIRECTCMD20140616 - placeACRedirectCommand,  sn:%s, json:%s", sn, data);
				ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_REDIRECT_WTP, JsonUtils.genServerRef(), ianaId, sn, jsonRedirectWtp);
				isRedirected = true;
			} else {
				log.warnf("REDIRECTCMD20140616 - placeACRedirectCommand, one of which is/are null or empty! certRoot:%s, certPrivate:%s, certPublic:%s, certCn:%s, ianaId:%s, sn:%s", certRoot, certPrivate, certPublic, certCn, ianaId, sn);
			}
		} catch (Exception e){
			log.error("REDIRECTCMD20140616 - placeACRedirectCommand", e);
			return isRedirected;
		}
		return isRedirected;
	}
	
	private static boolean placeACRedirectCommand(Networks networks, Integer ianaId, String sn){
		boolean isRedirected = false;
		
		try{
			if (networks != null && ianaId > 0 && sn != null && !sn.isEmpty()){
				if ((networks.getIncontrolHost1() != null && 
					!networks.getIncontrolHost1().isEmpty()) || 
					(networks.getIncontrolHost2() != null && 
					!networks.getIncontrolHost2().isEmpty())){	
					
					String certRoot = null;
					String certPrivate = null;
					String certPublic = null;
					String certCn = null;
					
					if (networks.getCustomCertificate() != null && !networks.getCustomCertificate().isEmpty()){
						certRoot = networks.getCustomCertificate();
					}else if (getCertrootpath()!= null && !getCertrootpath().isEmpty()){
						certRoot = CryptoUtils.readFromFileCharByChar(getCertrootpath());
					}
					
					if (getCertprivatepath() != null && !getCertprivatepath().isEmpty()){
						certPrivate = CryptoUtils.readFromFileCharByChar(getCertprivatepath());
					}
					if (getCertpublicpath() != null && ! getCertpublicpath().isEmpty()){
						certPublic = CryptoUtils.readFromFileCharByChar(getCertpublicpath());
					}
					
					if (networks.getCustomCommonName() != null && !networks.getCustomCommonName().isEmpty()){
						certCn = networks.getCustomCommonName();
					} else if (getCertcn() != null && !getCertcn().isEmpty()){
						certCn = getCertcn();
					}
					
					if (certRoot != null && certPrivate != null && certPublic != null && certCn != null){
						
						String data = "";
						
						Json_RedirectWtp jsonRedirectWtp = new Json_RedirectWtp();
						jsonRedirectWtp.setClient_cert(certPublic);
						jsonRedirectWtp.setClient_key(certPrivate);
						jsonRedirectWtp.setCn(certCn);
						
						List<String> hostList = new ArrayList<String>();
						if ((networks.getIncontrolHost1() != null && 
								!networks.getIncontrolHost1().isEmpty())){
							hostList.add(networks.getIncontrolHost1());
						}
						
						if ((networks.getIncontrolHost2() != null && 
								!networks.getIncontrolHost2().isEmpty())){
							hostList.add(networks.getIncontrolHost2());
						}
						
						jsonRedirectWtp.setHost(hostList);
						jsonRedirectWtp.setRoot_cert(certRoot);
						jsonRedirectWtp.setVersion(1);
						
						if (networks.getCustomCertPerm() != null){
							if (networks.getCustomCertPerm()){ // true
								jsonRedirectWtp.setPermanent(0);
							} else { // false
								jsonRedirectWtp.setPermanent(1);
							}
						}
						
						data = JsonUtils.toJson(jsonRedirectWtp);
	
						log.infof("LT10001 REDIRECT201403141212 - placeACRedirectCommand,  sn:%s, json:%s", sn, data);
						ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_REDIRECT_WTP, JsonUtils.genServerRef(), ianaId, sn, jsonRedirectWtp);
						isRedirected = true;
					} else {
						log.warnf("REDIRECT201403141212 - placeACRedirectCommand, one of which is/are null or empty! certRoot:%s, certPrivate:%s, certPublic:%s, certCn:%s, networks:%s, ianaId:%s, sn:%s", certRoot, certPrivate, certPublic, certCn, networks, ianaId, sn);
					}
				}
			} else {
				if (log.isDebugEnabled()){
					log.debugf("REDIRECT201403141212 - placeACRedirectCommand, one of which is/are null or empty! networks:%s, ianaId:%s, sn:%s", networks, ianaId, sn);
				}
			}
		} catch (Exception e){
			log.error("REDIRECT201403141212 - placeACRedirectCommand", e);
			return isRedirected;
		}
		return isRedirected;
	}
	
	public static boolean doCheckAndRedirectWtpMessageHandler(DevOnlineObject devOnlineO, List<String> hostList){
		
		boolean result = false;
		try{
			if (devOnlineO != null && hostList != null && hostList.size() > 0){
				Integer currentUnixTime = DateUtils.getUnixtime();
				if (devOnlineO.getOrganization_id() != null && devOnlineO.getNetwork_id() != null){
					Networks networks = OrgInfoUtils.getNetwork(devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());

					devOnlineO.setAcRedirectedHostTime(currentUnixTime);
					ACUtil.<DevOnlineObject> cachePoolObjectBySn(devOnlineO, DevOnlineObject.class);
					if (networks != null){
						result = placeACRedirectCommand(hostList, devOnlineO.getIana_id(), devOnlineO.getSn());	
					}
				} // end if (devOnlineO.getOrganization_id() != null && devOnlineO.getNetwork_id() != null)
			} else {
				if (log.isDebugEnabled()){
					log.debugf("REDIRECTCMD20140616 - doCheckAndRedirectWtpmessageHandler, devOnlineO is null! or hostList is null!");
				}
			} // end if (devOnlineO != null) ... else ...
		}catch (Exception e){
			log.error("REDIRECTCMD20140616 - doCheckAndRedirectWtpMessageHandler", e);
		} // end try ... catch (Exception e)
		return result;
	} // end doCheckAndRedirectWtpMessageHandler
	
	public static boolean doCheckAndRedirectWtpMessageHandler(QueryInfo<Object> info){
		boolean result = false;
		try{
			if (info.getType() == MessageType.PIPE_INFO_TYPE_DEV_ONLINE){
				if (info.getIana_id() > 0 && info.getSn() != null && !info.getSn().isEmpty()){
					SnsOrganizations snsOrg = BranchUtils.getSnsOrganizationsByIanaIdSn(info.getIana_id(), info.getSn());
					if (snsOrg!=null && snsOrg.getOrganizationId() != null){
						Devices devices = NetUtils.getDevices(snsOrg.getOrganizationId(), info.getIana_id(), info.getSn());
						if (devices != null && devices.getNetworkId() > 0){
							Networks networks = OrgInfoUtils.getNetwork(snsOrg.getOrganizationId(), devices.getNetworkId());
							if (networks != null){
								result = placeACRedirectCommand(networks, info.getIana_id(), info.getSn());
							}
						} else {
							if (log.isDebugEnabled()){
								log.debugf("REDIRECT201403141212 - doCheckAndRedirectWtpMessageHandler, devices is null! or networkId ==0, ianaId:%s, sn:%s, orgId:%s", info.getIana_id(), info.getSn(), snsOrg.getOrganizationId());
							}
						}
					} else {
						if (log.isDebugEnabled()){
							log.debugf("REDIRECT201403141212 - doCheckAndRedirectWtpMessageHandler, snsOrg/ snsOrg.getOrganizationId() is null! ianaId:%s, sn:%s", info.getIana_id(), info.getSn());
						}
					}
				}
			}
		} catch(Exception e){
			log.error("REDIRECT201403141212 - doCheckAndRedirectWtpMessageHandler", e);
			return result;
		}
		return result;
	} // end doCheckAndRedirectWtpMessageHandler(QueryInfo<Object> info)
	
	public static boolean doCheckAndRedirectWtpMessageHandler(DevOnlineObject devOnlineO){
		
		boolean result = false;
		try{
			if (devOnlineO != null){
				Integer currentUnixTime = DateUtils.getUnixtime();
				
				if (devOnlineO.getAcRedirectedHostTime() == null || 
					(currentUnixTime - devOnlineO.getAcRedirectedHostTime()) >= acRedirectServerRetryInterval ){
					
					if (devOnlineO.getOrganization_id() != null && devOnlineO.getNetwork_id() != null){
						Networks networks = OrgInfoUtils.getNetwork(devOnlineO.getOrganization_id(), devOnlineO.getNetwork_id());
						if (networks != null){
							devOnlineO.setAcRedirectedHostTime(currentUnixTime);
							boolean isRedirectEnabled = isRedirectEnabled(networks);
							if (isRedirectEnabled){
								ACUtil.<DevOnlineObject> cachePoolObjectBySn(devOnlineO, DevOnlineObject.class);
								result = placeACRedirectCommand(networks, devOnlineO.getIana_id(), devOnlineO.getSn());
							}
						}
					} // end if (devOnlineO.getOrganization_id() != null && devOnlineO.getNetwork_id() != null)
				} // end if (devOnlineO.getAcRedirectedHost() == null || devOnlineO.getAcRedirectedHost().isEmpty())
			} else {
				if (log.isDebugEnabled()){
					log.debugf("REDIRECT201403141212 - doCheckAndRedirectWtpmessageHandler, devOnlineO is null!");
				}
			} // end if (devOnlineO != null) ... else ...
		}catch (Exception e){
			log.error("REDIRECT201403141212 - doCheckAndRedirectWtpMessageHandler", e);
		} // end try ... catch (Exception e)
		return result;
	} // end doCheckAndRedirectWtpMessageHandler
	
	private static boolean isRedirectEnabled(Networks network){
		boolean isRedirectEnabled = false;
		if (network != null && network.getIncontrolHost1() != null && !network.getIncontrolHost1().isEmpty()){
			isRedirectEnabled = true;
		}
		return isRedirectEnabled;
	}
	
} // end class

