package com.littlecloud.ac.messagehandler;

import java.util.Arrays;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.ac.json.model.ReportInterval;
import com.littlecloud.ac.json.model.ReportInterval.IntervalObject;
import com.littlecloud.ac.json.model.command.MessageType;
import com.littlecloud.control.entity.Devices;
import com.littlecloud.control.json.model.Json_Devices;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.control.webservices.util.NetworkUtils;
import com.littlecloud.pool.object.utils.OrgInfoUtils;

public class GPSMessageHandler {
	private static final Logger log = Logger.getLogger(GPSMessageHandler.class);
	private static final int DEFAULT_GPS_RPT_INTERVAL_IN_SEC = 60;
	
	public static void suppressJsonDeviceListGpsReport(List<Json_Devices> jsonDevLst) {
		if (log.isDebugEnabled()) log.debugf("devLst %s suppressDeviceListGpsReport", jsonDevLst);
		
		for (Json_Devices jsonDev:jsonDevLst)
		{
			suppressDeviceGpsReport(jsonDev.getIana_id(), jsonDev.getSn());
		}
	}
	
	public static void suppressDeviceListGpsReport(List<Devices> devLst) {
		if (log.isDebugEnabled()) log.debugf("devLst %s suppressDeviceListGpsReport", devLst);
		
		for (Devices dev:devLst)
		{
			suppressDeviceGpsReport(dev.getIanaId(), dev.getSn());
		}
	}
	
	public static void suppressDeviceGpsReport(Integer iana_id, String sn) {
		if (log.isDebugEnabled()) log.debugf("dev %d %s suppressGpsReport", iana_id, sn);
		
		setGpsReportInterval(iana_id, sn, 0);
	}
	
	public static void resumeDeviceGpsReport(Integer iana_id, String sn) {
		if (log.isDebugEnabled()) log.debugf("dev %d %s resumeDeviceGpsReport", iana_id, sn);
		
		setGpsReportInterval(iana_id, sn, DEFAULT_GPS_RPT_INTERVAL_IN_SEC);
	}
	
	public static boolean dropDeviceGpsReport(Integer iana_id, String sn, String org_id)
	{
		/* **** ignore gps data from the group devices **** */
		boolean isdrop = false;
		if (iana_id != null && sn != null && org_id != null)
		{			
			Integer netId = OrgInfoUtils.lookupDevicesNetIdBySn(org_id, iana_id, sn);
			if (netId != null && NetworkUtils.isGpsTrackingDisabled(org_id, netId))
			{
				isdrop = true;
				log.warnf("GpsTrackingDisabled -ignored gps data from the group devices -- orgId %s ianaId %d sn %s", org_id, iana_id, sn);
				suppressDeviceGpsReport(iana_id, sn);
			}
		}
		return isdrop;
	}
 	private static void setGpsReportInterval(Integer iana_id, String sn, Integer interval)
	{
		ReportInterval rptInt = new ReportInterval();
		IntervalObject intObj = new IntervalObject();
		intObj.setReport_id(MessageType.PIPE_INFO_TYPE_DEV_LOCATIONS);
		intObj.setInterval(interval);
		rptInt.setIntervalObjectLst(Arrays.asList(intObj));
		ACService.fetchCommand(MessageType.PIPE_INFO_TYPE_REPORT_INTERVAL, JsonUtils.genServerRef(), iana_id, sn, rptInt);
	}
}
