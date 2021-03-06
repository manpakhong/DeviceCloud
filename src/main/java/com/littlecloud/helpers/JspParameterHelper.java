package com.littlecloud.helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.jboss.logging.Logger;

import com.littlecloud.utils.ApacheArchiverUtils;
import com.littlecloud.utils.CalendarUtils;
import com.littlecloud.utils.CommonUtils;



public class JspParameterHelper {
	public final static String CALENDAR_MAX_DAY_VALUE = "CALENDAR_MAX_DAY_VALUE";
	public final static String CALENDAR_MIN_DAY_VALUE = "CALENDAR_MIN_DAY_VALUE";
	private static final Logger log = Logger.getLogger(JspParameterHelper.class);
	
	public static Calendar convertMonitorPostDateTimeString2Date(String string, String minOrMaxDateValue){
		Calendar rtnCalendar = null;
		try{
			if (minOrMaxDateValue != null && minOrMaxDateValue.equals(CALENDAR_MAX_DAY_VALUE)){
				rtnCalendar = CalendarUtils.getMaxUtcCalendarToday();
			} else {
				rtnCalendar = CalendarUtils.getMinUtcCalendarToday();
			}
	
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			format.parse(string);
			Calendar formatCalendar = format.getCalendar();
					
			rtnCalendar.set(Calendar.MONTH, formatCalendar.get(Calendar.MONTH));
			rtnCalendar.set(Calendar.DAY_OF_MONTH, formatCalendar.get(Calendar.DAY_OF_MONTH));
			rtnCalendar.set(Calendar.YEAR, formatCalendar.get(Calendar.YEAR));
			rtnCalendar.set(Calendar.HOUR_OF_DAY, formatCalendar.get(Calendar.HOUR_OF_DAY));
			rtnCalendar.set(Calendar.MINUTE, formatCalendar.get(Calendar.MINUTE));
		} catch (Exception e){
			log.error("CaptivePortalActivitiesHelper.convertMonitorPostDateTimeString2Date() - ", e);
		}
		return rtnCalendar;
	}
}
