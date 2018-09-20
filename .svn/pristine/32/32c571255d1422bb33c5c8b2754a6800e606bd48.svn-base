package com.littlecloud.ac.messagehandler;

import java.util.Date;

import org.jboss.logging.Logger;

import com.littlecloud.ac.json.model.Json_DiagReport;
import com.littlecloud.control.entity.support.DeviceDiagReport;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.pool.object.DevOnlineObject;
import com.littlecloud.services.DeviceDiagnosticReportMgr;
import com.littlecloud.utils.CommonUtils;

public class DiagnosticReportMessageHandler {
	private static final Logger log = Logger.getLogger(DiagnosticReportMessageHandler.class);
	private final static Integer MAX_NUMBER_OF_RECORDS_KEPT_A_DAY = 300;
	private final static Integer MAX_NUMBER_OF_CONSEQUENT_DAYS_KEPT = 10;
	public static boolean doDiagnosticReportMessage(DevOnlineObject devOnlineO, Json_DiagReport jsonDiagReport){
		boolean result = false;
		try{
			if (devOnlineO != null && jsonDiagReport != null){
				if (log.isDebugEnabled()){
					log.debugf("DIAGRPT20141104 - DiagnosticReportMessageHandler.doDiagnosticReportMessage() - ianaId:%s, sn:%s, jsonDiagReport:%s", devOnlineO.getIana_id(), devOnlineO.getSn(), jsonDiagReport);
				}
				
				boolean isFileExisted = false;
				if (jsonDiagReport.getFilepath() != null && !jsonDiagReport.getFilepath().isEmpty()){
					isFileExisted = CommonUtils.isFileExisted(jsonDiagReport.getFilepath());
				}
				if (isFileExisted){
					
					byte[] reportContent = CommonUtils.readFile2ByteArray(jsonDiagReport.getFilepath());
					
					DeviceDiagReport deviceDiagReport = new DeviceDiagReport();
					deviceDiagReport.setCreatedDate(new Date());
					deviceDiagReport.setIanaId(devOnlineO.getIana_id());
					deviceDiagReport.setSn(devOnlineO.getSn());
					deviceDiagReport.setUnixtime(DateUtils.getUtcUnixtime());
					deviceDiagReport.setReportContent(reportContent);
					deviceDiagReport.setOrgId(devOnlineO.getOrganization_id());
					DeviceDiagnosticReportMgr deviceDiagnosticReportMgr = new DeviceDiagnosticReportMgr();
					result = deviceDiagnosticReportMgr.saveDeviceDiagReport(deviceDiagReport);
					if (result){
						boolean isDeleted = CommonUtils.deleteFileIfExisted(jsonDiagReport.getFilepath());
						if (!isDeleted){
							log.warnf("DIAGRPT20141104 - DiagnosticReportMessageHandler.doDiagnosticReportMessage() - file: %s cannot be deleted for some reason!", jsonDiagReport.getFilepath());
						}
					}
					
					// House Keep jobs
					// 1. Delete N ConsequentDays
					int noOfConsequentDaysDeleted = deviceDiagnosticReportMgr.deleteNConsequentDaysBeforeRecord(devOnlineO.getOrganization_id(), devOnlineO.getIana_id(), devOnlineO.getSn(), MAX_NUMBER_OF_CONSEQUENT_DAYS_KEPT);
					
					if (log.isDebugEnabled()){
						log.debugf("DIAGRPT20141104 - DiagnosticReportMessageHandler.doDiagnosticReportMessage() - noOfConsequentDaysDelete: %s -  HouseKeep %s of consequentDays --> device: %s",noOfConsequentDaysDeleted, MAX_NUMBER_OF_CONSEQUENT_DAYS_KEPT, devOnlineO);
					}
					// 2. delete N records a day
					Date date = DateUtils.getUtcDate();
					int noOfDayRecordsDeleted = deviceDiagnosticReportMgr.deleteNRecordsWithinADay(devOnlineO.getOrganization_id(), devOnlineO.getIana_id(), devOnlineO.getSn(), date, MAX_NUMBER_OF_RECORDS_KEPT_A_DAY);
					
					if (log.isDebugEnabled()){
						log.debugf("DIAGRPT20141104 - DiagnosticReportMessageHandler.doDiagnosticReportMessage() - noOfDayRecordsDeleted: %s - HouseKeep %s of records kept a day --> device: %s", noOfDayRecordsDeleted, MAX_NUMBER_OF_RECORDS_KEPT_A_DAY, devOnlineO);
					}
					
				} else {
					log.warnf("DIAGRPT20141104 - DiagnosticReportMessageHandler.doDiagnosticReportMessage() - file: %s is not existed!", jsonDiagReport.getFilepath());
				}
	//			result = DeviceNeighborUtils.updateDevNeighborListByJsonDeviceNeighborList(devOnlineO.getIana_id(), devOnlineO.getSn(), jsonDeviceNeighborList);
			} else {
				if (log.isDebugEnabled()){
					log.debugf("DIAGRPT20141104 -DiagnosticReportMessageHandler.doDiagnosticReportMessage() - devOnlineO != null && jsonDiagReport != null");
				}
			}
		} catch (Exception e){
			log.error("DIAGRPT20141104 -DiagnosticReportMessageHandler.doDiagnosticReportMessage() - exception -", e);
		}
		return result;
	}
}
