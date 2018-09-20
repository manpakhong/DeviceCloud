package com.littlecloud.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.control.dao.support.DeviceDiagReportDAO;
import com.littlecloud.control.dao.support.criteria.DeviceDiagReportCriteria;
import com.littlecloud.control.entity.support.DeviceDiagReport;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.utils.CalendarUtils;

public class DeviceDiagnosticReportMgr {
	private static final Logger log = Logger.getLogger(DeviceDiagnosticReportMgr.class);

	private DeviceDiagReportDAO deviceDiagReportDao;
	public DeviceDiagnosticReportMgr(){
		init();
	}
	
	private void init(){
		try{
			deviceDiagReportDao = new DeviceDiagReportDAO();
		} catch (Exception e){
			log.error("DIAGRPT20141104 - DeviceDiagnosticReportMgr.init() -  ", e);
		}
	}
	
	public boolean placeDiagReportCommand(Integer ianaId, String sn){
		boolean isCommandPlaced = false;
		try{
			ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_DIAG_REPORT_PLB, JsonUtils.genServerRef(), ianaId, sn);
			isCommandPlaced = true;
		} catch (Exception e){
			log.error("DIAGRPT20141104 - DeviceDiagnosticReportMgr.placeDiagReportCommand() -  ", e);		
		}
		return isCommandPlaced;
	}
	
	public boolean saveDeviceDiagReport(DeviceDiagReport deviceDiagReport){
		boolean isSaved = false;
		try{
			deviceDiagReportDao.save(deviceDiagReport);
			isSaved = true;
		} catch (Exception e){
			log.error("DIAGRPT20141104 - DeviceDiagnosticReportMgr.saveDeviceDiagReport() - ", e);
		}
		return isSaved;
	}
	
	public int deleteNConsequentDaysBeforeRecord(String orgId, Integer ianaId, String sn,Integer noOfConsequentDaysKept){
		int numberOfDeleted = 0;
		try{
			Date today = new Date();
			Date lastDate = getMinNConsequentDate(orgId, ianaId, sn, today, noOfConsequentDaysKept.intValue());
			if (lastDate != null){
				Calendar cal = CalendarUtils.getMinHostCalendarToday();
				cal.setTime(lastDate);
				CalendarUtils.recomputeCalendar(cal);
				
				numberOfDeleted = deviceDiagReportDao.deleteNConsequentDaysBeforeDate(orgId, ianaId, sn, cal);
			}
		} catch (Exception e){
			log.error("DIAGRPT20141104 - DeviceDiagnosticReportMgr.deleteNDaysBeforeRecord() - ", e);
		}
		return numberOfDeleted;
	}
	
	public int deleteNRecordsWithinADay(String orgId, Integer ianaId, String sn, Date date, Integer maxNoOfRecordsKeptADay){
		int numberOfDeleted = 0;
		try{
			
			Calendar calFrom = Calendar.getInstance();
			calFrom.setTime(date);
			CalendarUtils.trimCalendar2Minimum(calFrom);
			CalendarUtils.recomputeCalendar(calFrom);
			
			Calendar calTo = Calendar.getInstance();
			calTo.setTime(date);
			CalendarUtils.trimCalendar2Maximum(calTo);
			CalendarUtils.recomputeCalendar(calTo);
			
			List<DeviceDiagReport> deviceDiagReportList = getDeviceDiagReportList(orgId, ianaId, sn, calFrom.getTime(), calTo.getTime());
			if (deviceDiagReportList != null && deviceDiagReportList.size() > maxNoOfRecordsKeptADay.intValue()){
				for (int i = maxNoOfRecordsKeptADay.intValue(); i < deviceDiagReportList.size(); i++){
					DeviceDiagReport deviceDiagReport = deviceDiagReportList.get(i);
					if (deviceDiagReport != null){
						deviceDiagReportDao.delete(deviceDiagReport);
						numberOfDeleted++;
					}
				}
			}
			
		} catch (Exception e){
			log.error("DIAGRPT20141104 - DeviceDiagnosticReportMgr.deleteNRecordsWithinADay() - ", e);
		}
		return numberOfDeleted;
	}
	
	public Date getMinNConsequentDate(String orgId, Integer ianaId, String sn, Date date, Integer noOfConsequentDate){
		Date minDate = null;
		List<Date> dateList = getNConsequentDateList(orgId, ianaId, sn, date, noOfConsequentDate); 
		if (dateList != null && dateList.size() > 0){
			minDate = dateList.get(0);
			for (Date dateLoop: dateList){
				if (minDate.after(dateLoop)){
					minDate = dateLoop;
				}
			}
		}
		return minDate;
	}
	
	public List<Date> getNConsequentDateList(String orgId, Integer ianaId, String sn, Date date, Integer noOfConsequentDate){
		List<Date> dateList = null;
		try{
			dateList = deviceDiagReportDao.getNConsequentDateList(orgId, ianaId, sn, date, noOfConsequentDate);
		} catch (Exception e){
			log.error("DIAGRPT20141104 - DeviceDiagnosticReportMgr.getNConsequentDateList() - ", e);			
		}
		return dateList;
	}
	
	public List<DeviceDiagReport> getDeviceDiagReportList(String orgId, Integer ianaId, String sn, Date createdDateFrom, Date createdDateTo){
		List<DeviceDiagReport> deviceDiagReportList = null;
		try{
			DeviceDiagReportCriteria criteria = new DeviceDiagReportCriteria();
			criteria.setOrgId(orgId);
			criteria.setIanaId(ianaId);
			criteria.setSn(sn);
			criteria.setCreateDateFrom(createdDateFrom);
			criteria.setCreateDateTo(createdDateTo);
			deviceDiagReportList = deviceDiagReportDao.getDeviceDiagReport(criteria);
		}catch (Exception e){
			log.error("DIAGRPT20141104 - DeviceDiagnosticReportMgr.getDeviceDiagReportList() - ", e);
		}
		return deviceDiagReportList;
	}
}
