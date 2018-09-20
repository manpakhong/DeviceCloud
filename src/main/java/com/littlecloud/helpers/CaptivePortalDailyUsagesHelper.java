package com.littlecloud.helpers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.littlecloud.control.entity.CaptivePortalDailyUsage;

public class CaptivePortalDailyUsagesHelper {
	private Map<String, CaptivePortalDailyUsage> objectMap;
	public CaptivePortalDailyUsagesHelper(){
		objectMap = new HashMap<String, CaptivePortalDailyUsage>();
	}
	
	public int getHashMapSize(){
		int size = 0;
		if (objectMap != null){
			size = objectMap.size();
		}
		return size;
	}
	
	// Zero for database record indicate It has been consolidated without data
	private void setStatus2One(CaptivePortalDailyUsage captivePortalDailyUsage){
		captivePortalDailyUsage.setStatus(true);
	}
	
	public boolean addOrUpdateCalculateFields(CaptivePortalDailyUsage captivePortalDailyUsageReport){
		boolean isAddOrUpdate = false;
		if (captivePortalDailyUsageReport != null && captivePortalDailyUsageReport.getCpId() != null){
			String key = getKey(captivePortalDailyUsageReport.getCpId());
			setStatus2One(captivePortalDailyUsageReport);
			if (isIdExisted(key)){
				CaptivePortalDailyUsage existCpDailyUsageReport = objectMap.get(key);
				BigDecimal bandwidthUsed = existCpDailyUsageReport.getBandwidthUsed();
				Integer sessionCount = existCpDailyUsageReport.getSessionCount();
				BigDecimal timeUsed = existCpDailyUsageReport.getTimeUsed();
				
				if (captivePortalDailyUsageReport.getBandwidthUsed() != null){
					if (bandwidthUsed != null && bandwidthUsed.intValue() != -1){
						bandwidthUsed = bandwidthUsed.add(captivePortalDailyUsageReport.getBandwidthUsed());
					} else {
						bandwidthUsed = captivePortalDailyUsageReport.getBandwidthUsed();
					}
					existCpDailyUsageReport.setBandwidthUsed(bandwidthUsed);
				}

				
				if (captivePortalDailyUsageReport.getSessionCount() != null){
					if (sessionCount != null && sessionCount.intValue() != -1){
						sessionCount += captivePortalDailyUsageReport.getSessionCount().intValue();
					} else {
						sessionCount = captivePortalDailyUsageReport.getSessionCount();
					}
					existCpDailyUsageReport.setSessionCount(sessionCount);
				}
				
				if (captivePortalDailyUsageReport.getTimeUsed() != null){
					if (timeUsed != null && bandwidthUsed.intValue() != -1){
						timeUsed = timeUsed.add(captivePortalDailyUsageReport.getTimeUsed());
					} else {
						timeUsed = captivePortalDailyUsageReport.getTimeUsed();
					}
					existCpDailyUsageReport.setTimeUsed(timeUsed);
				}

			} else {
				addElement(key, captivePortalDailyUsageReport);
			}
		}
		
		return isAddOrUpdate;
	}
	
	private boolean addElement(String key, CaptivePortalDailyUsage captivePortalDailyUsageReport){
		boolean isAdded = false;
		if (objectMap.put(key, captivePortalDailyUsageReport) != null){
			isAdded = true;
		}
		return isAdded;
	}
	
	private boolean isIdExisted(String key){
		boolean isExisted = false;
		if (key != null){
			CaptivePortalDailyUsage objElement = objectMap.get(key);
			if (objElement != null){
				isExisted = true;
			}
		}
		return isExisted;
	}
	
	private String getKey(Integer cpId){
		if (cpId != null){
			return "C" + cpId;
		}
		return null;
	}
	public CaptivePortalDailyUsage getElement(Integer cpId){
		String comboId = getKey(cpId);
		if (comboId != null){
			CaptivePortalDailyUsage objElement = objectMap.get(comboId);
			return objElement;
		} else {
			return null;
		}
	}
	
	public List<CaptivePortalDailyUsage> getElementList(){
		List<CaptivePortalDailyUsage> elementList = null;
		if (objectMap.size() > 0){
			elementList = new ArrayList<CaptivePortalDailyUsage>();
			for (Map.Entry<String, CaptivePortalDailyUsage> entry: objectMap.entrySet()){
				elementList.add(entry.getValue());
			}
		}
		return elementList;
	}
	
}
