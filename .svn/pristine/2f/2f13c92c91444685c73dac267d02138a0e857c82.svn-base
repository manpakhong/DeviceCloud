package com.littlecloud.helpers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.littlecloud.control.entity.CaptivePortalDailyUsage;
import com.littlecloud.control.entity.CaptivePortalDailyUserUsage;

public class CaptivePortalDailyUserUsagesHelper {
	private Map<String, CaptivePortalDailyUserUsage> objectMap;
	public CaptivePortalDailyUserUsagesHelper(){
		objectMap = new HashMap<String, CaptivePortalDailyUserUsage>();
	}
	
	public int getHashMapSize(){
		int size = 0;
		if (objectMap != null){
			size = objectMap.size();
		}
		return size;
	}
	
	// Zero for database record indicate It has been consolidated without data
	private void setStatus2One(CaptivePortalDailyUserUsage captivePortalDailyUserUsage){
		captivePortalDailyUserUsage.setStatus(true);
	}
	
	public boolean addOrUpdateCalculateFields(CaptivePortalDailyUserUsage captivePortalDailyUserUsage){
		boolean isAddOrUpdate = false;
		if (captivePortalDailyUserUsage != null && captivePortalDailyUserUsage.getCpId() != null &&
				captivePortalDailyUserUsage.getUsername() != null && captivePortalDailyUserUsage.getUserGroupId() != null){
			String key = getKey(captivePortalDailyUserUsage.getCpId(), captivePortalDailyUserUsage.getUsername(), captivePortalDailyUserUsage.getUserGroupId());
			setStatus2One(captivePortalDailyUserUsage);
			if (isIdExisted(key)){
				CaptivePortalDailyUserUsage existCpDailyUserUsage = objectMap.get(key);
				BigDecimal bandwidthUsed = existCpDailyUserUsage.getBandwidthUsed();
				Integer sessionCount = existCpDailyUserUsage.getSessionCount();
				BigDecimal timeUsed = existCpDailyUserUsage.getTimeUsed();
				
				if (captivePortalDailyUserUsage.getBandwidthUsed() != null){
					if (bandwidthUsed != null && bandwidthUsed.intValue() != -1){
						bandwidthUsed = bandwidthUsed.add(captivePortalDailyUserUsage.getBandwidthUsed());
					} else {
						bandwidthUsed = captivePortalDailyUserUsage.getBandwidthUsed();
					}
					existCpDailyUserUsage.setBandwidthUsed(bandwidthUsed);
				}

				
				if (captivePortalDailyUserUsage.getSessionCount() != null){
					if (sessionCount != null && sessionCount.intValue() != -1){
						sessionCount += captivePortalDailyUserUsage.getSessionCount().intValue();
					} else {
						sessionCount = captivePortalDailyUserUsage.getSessionCount();
					}
					existCpDailyUserUsage.setSessionCount(sessionCount);
				}
				
				if (captivePortalDailyUserUsage.getTimeUsed() != null){
					if (timeUsed != null && bandwidthUsed.intValue() != -1){
						timeUsed = timeUsed.add(captivePortalDailyUserUsage.getTimeUsed());
					} else {
						timeUsed = captivePortalDailyUserUsage.getTimeUsed();
					}
					existCpDailyUserUsage.setTimeUsed(timeUsed);
				}
				
			} else {
				addElement(key, captivePortalDailyUserUsage);
			}
		}
		
		return isAddOrUpdate;
	}
	
	private boolean addElement(String key, CaptivePortalDailyUserUsage captivePortalDailyUserUsage){
		boolean isAdded = false;
		if (objectMap.put(key, captivePortalDailyUserUsage) != null){
			isAdded = true;
		}
		return isAdded;
	}
	
	private boolean isIdExisted(String key){
		boolean isExisted = false;
		if (key != null){
			CaptivePortalDailyUserUsage objElement = objectMap.get(key);
			if (objElement != null){
				isExisted = true;
			}
		}
		return isExisted;
	}
	
	private String getKey(Integer cpId, String username, String userGroupId){
		if (cpId != null && username != null && !username.isEmpty() && userGroupId != null && !userGroupId.isEmpty()){
			return "C" + cpId + "U" + username + "I" + userGroupId;
		}
		return null;
	}
	public CaptivePortalDailyUserUsage getElement(Integer cpId, String username, String userGroupId){
		String comboId = getKey(cpId, username, userGroupId);
		if (comboId != null){
			CaptivePortalDailyUserUsage objElement = objectMap.get(comboId);
			return objElement;
		} else {
			return null;
		}
	}
	
	public List<CaptivePortalDailyUserUsage> getElementList(){
		List<CaptivePortalDailyUserUsage> elementList = null;
		if (objectMap.size() > 0){
			elementList = new ArrayList<CaptivePortalDailyUserUsage>();
			for (Map.Entry<String, CaptivePortalDailyUserUsage> entry: objectMap.entrySet()){
				elementList.add(entry.getValue());
			}
		}
		return elementList;
	}
	
}
