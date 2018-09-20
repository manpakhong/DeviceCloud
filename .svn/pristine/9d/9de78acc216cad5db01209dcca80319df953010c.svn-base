package com.littlecloud.ac.messagehandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.logging.Logger;

import com.littlecloud.ac.json.model.Json_WanIpInfo;
import com.littlecloud.ac.json.model.util.JsonMessageParser;
import com.littlecloud.control.entity.DdnsRecords;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.pool.object.DevUsageObject;
import com.littlecloud.pool.object.DevUsageObject.UsageList;
import com.littlecloud.pool.object.eventlog.JsonWanObject;
import com.littlecloud.pool.object.utils.DdnsUtils;
import com.littlecloud.pool.utils.PropertyService;

public class DdnsMessageHandler {
	private static final Logger log = Logger.getLogger(DdnsMessageHandler.class);
	private static PropertyService<DdnsMessageHandler> ps = new PropertyService<DdnsMessageHandler>(DdnsMessageHandler.class);
	// regular expressions for ddns mapping
	private static final String ddnsRegExpNotDnsAcceptedChars = ps.getString("DDNS_REG_EXP_NOT_DNS_ACCEPTED_CHARS");
	private static final String ddnsRegExpFirstLastCharHypen = ps.getString("DDNS_REG_EXP_FIRST_LAST_CHAR_HYPEN");
	private static final String ddnsRegExpContinueReptHypen = ps.getString("DDNS_REG_EXP_CONTINUE_REPT_HYPEN");
	private static final String ddnsRegExpNonEngWords = ps.getString("DDNSREG_EXP_NON_ENG_WORDS");
	private static final String ddnsSuffixName = ps.getString("DDNS_SUFFIX_NAME");
	private static final String DDNS_SUFFIX_NAME = getDdnsSuffixName();
	
	public static String getDdnsSuffixName() {
		return ddnsSuffixName;
	}

	public static String getDdnsRegExpNotDnsAcceptedChars() {
		return ddnsRegExpNotDnsAcceptedChars;
	}

	public static String getDdnsRegExpFirstLastCharHypen() {
		return ddnsRegExpFirstLastCharHypen;
	}

	public static String getDdnsRegExpContinueReptHypen() {
		return ddnsRegExpContinueReptHypen;
	}

	public static String getDdnsRegExpNonEngWords() {
		return ddnsRegExpNonEngWords;
	}
	
/*	
	// json in json format
	public static boolean doDdnsMessageHandle(DevOnlineObject devOnlineO, JsonWanObject jsonWanObject){
		boolean result = false;
		if (devOnlineO != null && jsonWanObject != null && devOnlineO.getDdns_enabled() != null && 
				devOnlineO.getDdns_enabled()){
			List<DdnsRecords> ddnsRecordsList = DdnsUtils.getDdnsRecordsList(devOnlineO.getIana_id(), devOnlineO.getSn());

			String wanName = jsonWanObject.getName();
			String ip = jsonWanObject.getIp();
			Integer wanId = jsonWanObject.getWan_id();
			
			if (wanName != null && !wanName.isEmpty() && ip != null && !ip.isEmpty() && wanId != null){
				boolean anyChange = checkAndUpdateDdnsRecord(ddnsRecordsList, devOnlineO, wanName, ip, wanId);
				if (log.isDebugEnabled()){
					log.debugf("DDNS20140402 - doDdnsEventMessageHandle(), iana: %s, sn: %s, newIp: %s", devOnlineO.getIana_id(), devOnlineO.getSn(), ip);
				}
				result = anyChange;
			}
		}
		return result;
	}
	
	public static boolean doDdnsWanIpInfoMessageHandler(DevOnlineObject devOnlineO, String wanIpInfoJson){
		boolean result = false;
		try{
			if (devOnlineO != null && wanIpInfoJson != null && !wanIpInfoJson.isEmpty() && devOnlineO.getDdns_enabled() != null && 
					devOnlineO.getDdns_enabled()){
				List<Json_WanIpInfo> jsonWanIpInfoList = JsonMessageParser.parseWanIpInfo(wanIpInfoJson);
				if (jsonWanIpInfoList != null && jsonWanIpInfoList.size() > 0){
					if (log.isDebugEnabled()){
						log.debugf("DDNS20140402 - doDdnsWanIpInfoMessageHandler() - wanIpInfoJson, iana: %s, sn: %s, jsonWanIpInfoList: %s", devOnlineO.getIana_id(), devOnlineO.getSn(), jsonWanIpInfoList);
					}
					if (devOnlineO.getIana_id() != null && devOnlineO.getSn() != null){
						for (Json_WanIpInfo jsonWanIpInfo: jsonWanIpInfoList){
							String wanName = jsonWanIpInfo.getName();
							String ip = jsonWanIpInfo.getIp();
							Integer wanId = jsonWanIpInfo.getId();
							if (wanName != null && !wanName.isEmpty() && ip != null && !ip.isEmpty() && wanId != null){
								List<DdnsRecords> ddnsRecordsList = DdnsUtils.getDdnsRecordsList(devOnlineO.getIana_id(), devOnlineO.getSn());
								boolean anyChange = checkAndUpdateDdnsRecord(ddnsRecordsList, devOnlineO, wanName, ip, wanId);
								if (log.isDebugEnabled()){
									log.debugf("DDNS20140402 - doDdnsWanIpInfoMessageHandler() - wanIpInfoJson, iana: %s, sn: %s, newIp: %s, change: %s", devOnlineO.getIana_id(), devOnlineO.getSn(), ip, anyChange);
								}
								result = anyChange;
							}
						} // end for
					} // end if (devOnlineO.getIana_id() != null && devOnlineO.getSn() != null) 
				} else {
					if (log.isDebugEnabled()){
						log.debugf("DDNS20140402 - doDdnsWanIpInfoMessageHandler()- jsonWanIpInfoList null - wanIpInfoJson, iana: %s, sn: %s, jsonWanIpInfoList: %s", devOnlineO.getIana_id(), devOnlineO.getSn(), jsonWanIpInfoList);
					}
				}
			} else {
				if (devOnlineO == null){
					log.warn("DDNS20140402 - doDdnsWanIpInfoMessageHandler(), devOnlineO is null");
				}
				if (devOnlineO!= null && (wanIpInfoJson == null || wanIpInfoJson.isEmpty())){
					log.warnf("DDNS20140402 - doDdnsWanIpInfoMessageHandler(), iana: %s, sn: %s, devOnlineO is null", devOnlineO.getIana_id(), devOnlineO.getSn());
				}
			}
		} catch (Exception e) {
			log.error("DDNS20140402 - doDdnsWanIpInfoMessageHandler()",e);
		}
		return result;
	}
	
	// old message: free text format
	public static boolean doDdnsMessageHandle(DevOnlineObject devOnlineO, String eventMessage){
		// eventMessage sample: WAN: WAN3 connected (10.8.8.247)
		boolean result = false;
		try{
			if (devOnlineO != null && eventMessage != null && !eventMessage.isEmpty() && devOnlineO.getDdns_enabled() != null && 
					devOnlineO.getDdns_enabled()){
				if (devOnlineO.getIana_id() != null && devOnlineO.getSn() != null){
					Pattern pattern = Pattern.compile(".*:.*connected\\s\\(\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}\\)");
					Matcher matcher = pattern.matcher(eventMessage);

					if (matcher.find()) {
						String wanName = getWanNameFromEventMessage(eventMessage);
						String ip = getIpFromEventMessage(eventMessage);
						Integer wanId = getWanIdFromEventMessage(eventMessage);
						if (wanName != null && !wanName.isEmpty() && ip != null && !ip.isEmpty() && wanId != null){
							List<DdnsRecords> ddnsRecordsList = DdnsUtils.getDdnsRecordsList(devOnlineO.getIana_id(), devOnlineO.getSn());
							boolean anyChange = checkAndUpdateDdnsRecord(ddnsRecordsList, devOnlineO, wanName, ip, wanId);
							log.debugf("DDNS20140402 - doDdnsEventMessageHandle(), iana: %s, sn: %s, newIp: %s", devOnlineO.getIana_id(), devOnlineO.getSn(), devOnlineO.getWan_ip());
							result = anyChange;
						}
					}
				}
			} else {
				log.warn("DDNS20140402 - doDdnsEventMessageHandle(), devOnlineO is null");
			}
		} catch (Exception e) {
			log.error("DDNS20140402 - doDdnsEventMessageHandle()",e);
		}
		return result;
	}
	
	public static boolean doDdnsMessageHandle(DevUsageObject devUsageObject, DevOnlineObject devOnlineO){
		boolean result = false;
		try{			
			if (devUsageObject != null && devOnlineO != null && devOnlineO.getDdns_enabled() != null && 
					devOnlineO.getDdns_enabled()){
				if (log.isDebugEnabled()){
					log.debugf("DDNS20140402 - doDdnsEventMessageHandle() - devUsageObject, iana: %s, sn: %s", devOnlineO.getIana_id(), devOnlineO.getSn());
				}

				List<DdnsRecords> ddnsRecordsList = DdnsUtils.getDdnsRecordsList(devOnlineO.getIana_id(), devOnlineO.getSn());
				
				List <UsageList> usageListList = getTheLatestUniqueUsageListRecords(devUsageObject);

				if (usageListList != null && usageListList.size() > 0){
					if (log.isDebugEnabled()){
						log.debugf("DDNS20140402 - doDdnsEventMessageHandle() - devUsageObject - usageListList.size:%s, iana: %s, sn: %s",usageListList.size(), devOnlineO.getIana_id(), devOnlineO.getSn());
					}
					for (UsageList usageList: usageListList){
						String wanName = usageList.getWan_name();
						String ip = usageList.getIp();
						Integer wanId = usageList.getId();
						
						if (wanName != null && !wanName.isEmpty() && ip != null && !ip.isEmpty() && wanId != null){		
							boolean anyChange = checkAndUpdateDdnsRecord(ddnsRecordsList, devOnlineO, wanName, ip, wanId);
							log.debugf("DDNS20140402 - doDdnsEventMessageHandle() - checkAndUpdateDdnsRecord, anyChange:%s, iana: %s, sn: %s",anyChange, devOnlineO.getIana_id(), devOnlineO.getSn());
							result = anyChange;
						} else {
							log.debugf("DDNS20140402 - doDdnsEventMessageHandle() - devUsageObject, iana: %s, sn: %s - wanName, ip, wanId - null?", devOnlineO.getIana_id(), devOnlineO.getSn());
						}
					}
				}
			} else {
				log.warn("DDNS20140402 - doDdnsEventMessageHandle() - DevDetailObject, devOnlineO is null");
			}
		} catch (Exception e){
			log.error("DDNS20140402 - doDdnsEventMessageHandle() - DevDetailObject",e);
		}
		return result;
	}
	
	private static List <UsageList> getTheLatestUniqueUsageListRecords(DevUsageObject devUsageObject){
		Map<Integer, UsageList> usageListMap = null;
		
		if (devUsageObject != null){
			List<UsageList> usageListList = devUsageObject.getUsage_list();
			if (usageListList != null && usageListList.size() > 0){
				usageListMap = new HashMap<Integer, UsageList>();
				for (UsageList usageList: usageListList){
					if (usageList != null && usageList.getTimestamp() != null){
						if (usageListMap.containsKey(usageList.getId())){
							UsageList usageListTmp = usageListMap.get(usageList.getId());
							if (usageListTmp != null){
								if (usageListTmp.getTimestamp() <= usageList.getTimestamp()){
									usageListMap.remove(usageList.getId());
									usageListMap.put(usageList.getId(), usageList);
								}
							}
						} else { // HashMap does not have Id of usageList exist
							usageListMap.put(usageList.getId(), usageList);
						}
					} 
				} // end for (UsageList usageList: usageListList)
			}
		}
		
		// After finding the latestUsageLists
		// ===================================
		List <UsageList> latestUsageListList = null;		
		if (usageListMap != null && usageListMap.size() > 0){
			latestUsageListList = new ArrayList<UsageList>();
			for (Map.Entry<Integer, UsageList> entry: usageListMap.entrySet()){
				latestUsageListList.add(entry.getValue());
			}
		}
		
		return latestUsageListList;
	}
	
	private static String getWanNameFromEventMessage(String eventMessage){
		String wanName = "";
		int indexStartOfWanName = eventMessage.indexOf(":");
		int indexEndOfWanName = eventMessage.indexOf("connected");
		
		if (indexStartOfWanName > -1 && indexEndOfWanName > -1){
			indexStartOfWanName += 1;
			wanName = eventMessage.substring(indexStartOfWanName, indexEndOfWanName);
			wanName = wanName.trim();
			if (log.isDebugEnabled()){
				log.debugf("DDNS20140402 - getWanNameFromEventMessage(), wanName: %s", wanName);
			}
		}
		return wanName;
	}
	
	private static String convert2PeplinkDdnsNameFormat(List<DdnsRecords> currentDdnsRecordsList, DevOnlineObject devOnlineO, String wanName, String ip, Integer wanId){
		String peplinkDdnsNameFormat = "";
		
		String basicPeplinkDdnsNameFormat = convert2BasicPeplinkDdnsNameFormat(wanName);
		
		if (basicPeplinkDdnsNameFormat.isEmpty()){
			basicPeplinkDdnsNameFormat = wanId.toString();
		}
		
		if (currentDdnsRecordsList != null){
			boolean isDuplicatedName = false;
			for (DdnsRecords ddnsRecords: currentDdnsRecordsList){
//				int maxNumberOfTheDdnsName = returnMaxNumberOfTheDdnsName(currentDdnsRecordsList, basicPeplinkDdnsNameFormat);
				String currentDdnsNameWithoutSuffix = ddnsRecords.getDdnsName().replace("." + DDNS_SUFFIX_NAME, "");
				if (basicPeplinkDdnsNameFormat.equals(currentDdnsNameWithoutSuffix)){
					isDuplicatedName = true;
					break;
				}
			}
			if (isDuplicatedName){
				peplinkDdnsNameFormat = basicPeplinkDdnsNameFormat + "_" + wanId;
			} else {
				peplinkDdnsNameFormat = basicPeplinkDdnsNameFormat;
			}
		} else {
			peplinkDdnsNameFormat = basicPeplinkDdnsNameFormat;
		}
		return peplinkDdnsNameFormat.toLowerCase();
	}
	
	private static int returnMaxNumberOfTheDdnsName(List<DdnsRecords> currentDdnsRecordsList, String basicPeplinkDdnsNameFormat){
		int maxNumberOfTheDdnsName = -1;
		try{
			for (DdnsRecords currentDdnsRecords: currentDdnsRecordsList){
				if (currentDdnsRecords != null && currentDdnsRecords.getDdnsName() != null && currentDdnsRecords.getWanIp() != null){
					if(currentDdnsRecords.getDdnsName() != null){
						if (currentDdnsRecords.getDdnsName().trim().indexOf(basicPeplinkDdnsNameFormat) >= 0){
							if (Math.abs(currentDdnsRecords.getDdnsName().length() - basicPeplinkDdnsNameFormat.length()) == 2){
								String numberSubStr = currentDdnsRecords.getDdnsName().trim().substring(currentDdnsRecords.getDdnsName().length() -2);
								int numberSubStrInteger = Integer.parseInt(numberSubStr);
								if (numberSubStrInteger >= maxNumberOfTheDdnsName){
									maxNumberOfTheDdnsName = numberSubStrInteger + 1;
								}
							} else {
								maxNumberOfTheDdnsName = 2;
							}
						}
					}
				}
			}
		} catch (Exception e){
			log.error("DDNS20140402 - returnMaxNumberOfTheDdnsName()", e);
		}
		return maxNumberOfTheDdnsName;
	}
	
	private static String convert2BasicPeplinkDdnsNameFormat(String wanName){
		final String REG_EXP_NOT_DNS_ACCEPTED_CHARS = getDdnsRegExpNotDnsAcceptedChars();
		final String REG_EXP_FIRST_LAST_CHAR_HYPEN = getDdnsRegExpFirstLastCharHypen();
		final String REG_EXP_CONTINUE_REPT_HYPEN = getDdnsRegExpContinueReptHypen();
		final String REG_EXP_NON_ENG_WORDS = getDdnsRegExpNonEngWords();
		String peplinkBasicDdnsName ="";
		
		wanName = wanName.replaceAll(REG_EXP_NOT_DNS_ACCEPTED_CHARS, "-");
//		System.out.println("1:" + dnsNameArray[i]);
		wanName = wanName.replaceAll(REG_EXP_FIRST_LAST_CHAR_HYPEN, "");
		wanName = wanName.replaceAll(REG_EXP_NON_ENG_WORDS, "");  
		wanName = wanName.replaceAll(REG_EXP_CONTINUE_REPT_HYPEN, "-");
		peplinkBasicDdnsName = wanName;
		return peplinkBasicDdnsName;
	}
	
	private static Integer getWanIdFromEventMessage(String eventMessage){
		Integer wanId = null;
		// TODO: 
		return wanId;
	}
	
	private static String getIpFromEventMessage(String eventMessage){
		String ip = "";
		int indexStartOfIp = eventMessage.indexOf("(");
		int indexEndOfIp = eventMessage.indexOf(")");
		if (indexStartOfIp > -1 && indexEndOfIp > -1){
			indexStartOfIp += 1;
			ip = eventMessage.substring(indexStartOfIp, indexEndOfIp);
			ip = ip.trim();
			if (log.isDebugEnabled()){
				log.debugf("DDNS20140402 - getIpFromEventMessage(), ip: %s", ip);
			}
		}
		return ip;
	}
	
	private static boolean checkAndUpdateDdnsRecord(List<DdnsRecords> currentDdnsRecordsList, DevOnlineObject devOnlineO, String wanName, String ip, Integer wanId){
		boolean updatedAndChanged = false;
		if (currentDdnsRecordsList == null){
			DdnsRecords newDdnsRecords = new DdnsRecords();
			String pepLinkDdnsName = convert2PeplinkDdnsNameFormat(currentDdnsRecordsList, devOnlineO, wanName, ip, wanId);
			
			StringBuilder ddnsNameSb = new StringBuilder();
			ddnsNameSb.append(pepLinkDdnsName);
			ddnsNameSb.append(".");
			ddnsNameSb.append(devOnlineO.getSn());
			ddnsNameSb.append(".");
			ddnsNameSb.append(DDNS_SUFFIX_NAME);

			newDdnsRecords.setDdnsName(ddnsNameSb.toString());
			newDdnsRecords.setIanaId(devOnlineO.getIana_id());
			newDdnsRecords.setSn(devOnlineO.getSn());
			newDdnsRecords.setOrganizationId(devOnlineO.getOrganization_id());
			newDdnsRecords.setWanId(wanId.toString());
			newDdnsRecords.setWanIp(ip);
			newDdnsRecords.setLastUpdated(DateUtils.getUtcDate());
			DdnsUtils.insertOrUpdateDdnsRecords(newDdnsRecords);
			if (log.isDebugEnabled()){
				log.debugf("DDNS20140402 - checkAndUpdateDdnsRecord() - insert new record, iana:%s, sn:%s, wanId:%s, ip:%s", devOnlineO.getIana_id(), devOnlineO.getSn(), wanId, ip);
			}
			updatedAndChanged = true;
		} else {
			boolean isInsertCase = true;
			for (DdnsRecords currentDdnsRecords: currentDdnsRecordsList){
				if (currentDdnsRecords != null){
					if(currentDdnsRecords.getDdnsName() != null && currentDdnsRecords.getWanIp() != null){
						// if found the same interface, updated
						
						if (currentDdnsRecords.getWanId().equals(wanId.toString())){
							if (log.isDebugEnabled()){
								log.debugf("DDNS20140402 - checkAndUpdateDdnsRecord() record found at cache list! - record, iana:%s, sn:%s, wanId:%s, ip:%s", devOnlineO.getIana_id(), devOnlineO.getSn(), wanId, ip);
							}

							String pepLinkDdnsName = convert2PeplinkDdnsNameFormat(currentDdnsRecordsList, devOnlineO, wanName, ip, wanId);
							
							StringBuilder ddnsNameSb = new StringBuilder();
							ddnsNameSb.append(pepLinkDdnsName);
							ddnsNameSb.append(".");
							ddnsNameSb.append(devOnlineO.getSn());
							ddnsNameSb.append(".");
							ddnsNameSb.append(DDNS_SUFFIX_NAME);
							
							if (!currentDdnsRecords.getDdnsName().trim().equals(ddnsNameSb.toString()) || !currentDdnsRecords.getWanIp().trim().equalsIgnoreCase(ip)){
								currentDdnsRecords.setWanIp(ip);
								currentDdnsRecords.setDdnsName(ddnsNameSb.toString());
								currentDdnsRecords.setWanId(wanId.toString());
								currentDdnsRecords.setLastUpdated(DateUtils.getUtcDate());
								if (log.isDebugEnabled()){
									log.debugf("DDNS20140402 - checkAndUpdateDdnsRecord()- change!!! - record found at cache list! - record, iana:%s, sn:%s, wanId:%s, ip:%s", devOnlineO.getIana_id(), devOnlineO.getSn(), wanId, ip);				
								}
								DdnsUtils.insertOrUpdateDdnsRecords(currentDdnsRecords);
								updatedAndChanged = true;
							} else {
								if (log.isDebugEnabled()){
									log.debugf("DDNS20140402 - checkAndUpdateDdnsRecord()- same ip found!!! no change - record found at cache list! - record, iana:%s, sn:%s, wanId:%s, ip:%s", devOnlineO.getIana_id(), devOnlineO.getSn(), wanId, ip);	
								}

							}
							isInsertCase = false;
						}
					}
				}
			} // end for (DdnsRecords currentDdnsRecords: currentDdnsRecordsList)
			
			// check if existing list have not such interface data for update insert it
			if (isInsertCase){
				DdnsRecords newDdnsRecords = new DdnsRecords();
				String pepLinkDdnsName = convert2PeplinkDdnsNameFormat(currentDdnsRecordsList, devOnlineO, wanName, ip, wanId);
				
				StringBuilder ddnsNameSb = new StringBuilder();
				ddnsNameSb.append(pepLinkDdnsName);
				ddnsNameSb.append(".");
				ddnsNameSb.append(devOnlineO.getSn());
				ddnsNameSb.append(".");
				ddnsNameSb.append(DDNS_SUFFIX_NAME);

				newDdnsRecords.setDdnsName(ddnsNameSb.toString());
				newDdnsRecords.setIanaId(devOnlineO.getIana_id());
				newDdnsRecords.setSn(devOnlineO.getSn());
				newDdnsRecords.setOrganizationId(devOnlineO.getOrganization_id());
				newDdnsRecords.setWanId(wanId.toString());
				newDdnsRecords.setWanIp(ip);
				newDdnsRecords.setLastUpdated(DateUtils.getUtcDate());
				DdnsUtils.insertOrUpdateDdnsRecords(newDdnsRecords);
				updatedAndChanged = true;
				if (log.isDebugEnabled()){
					log.debugf("DDNS20140402 - checkAndUpdateDdnsRecord()- insert!!! - record list found but no such record! - record, iana:%s, sn:%s, wanId:%s, ip:%s", devOnlineO.getIana_id(), devOnlineO.getSn(), wanId, ip);								
				}
			}
		} // end if (currentDdnsRecordsList == null) ... else ...
		return updatedAndChanged;
	}
*/	

} // end class
