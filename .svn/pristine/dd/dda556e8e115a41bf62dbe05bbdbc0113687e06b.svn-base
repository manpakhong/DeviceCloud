package com.littlecloud.control.json.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;

import com.littlecloud.pool.utils.Utils;

public class DateUtils {

	public static final String UNIX_TIME_OPERATOR_ADD = "ADD";
	public static final String UNIX_TIME_OPERATOR_SUBSTRACT = "SUBSTRACT";
	public static final String UNIX_TIME_UNIT_DAY = "DAY";
	public static final String UNIX_TIME_UNIT_HOUR = "HR";
	public static final String UNIX_TIME_UNIT_MINUTE = "MIN";
	public static final String UNIX_TIME_UNIT_SECOND = "SEC";
	
	private static final Logger log = Logger.getLogger(DateUtils.class);
	private static final String generalDateTimeFormat = "yyyy-MM-dd HH:mm:ss";
	
	private static boolean isLoaded=false;
	
	private static Properties timezone = new Properties();
	private static Properties timezoneMappingMax2Ap = new Properties();

	//private static PropertyService<DateUtils> ps = new PropertyService<DateUtils>(DateUtils.class);
	public static String getCurrentDateTimeInUtcGeneralFormat()
	{
		SimpleDateFormat sdf = new SimpleDateFormat(generalDateTimeFormat);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(new Date());
	}
	
	public static Date convertUnixtime2Date(int unixTime){
		Date date = new Date(unixTime * (long) 1000);
		return date;
	}
	
	public static int getUnixTimeByDifferent(int unixtime, String unixTimeUnit, int diffValue, String unixTimeOperator){

		int result = 0;
		int operator = 1;
		
		if (unixTimeOperator.equals(UNIX_TIME_OPERATOR_SUBSTRACT)){
			operator = -1;
		} 
		
		switch(unixTimeUnit){
			case UNIX_TIME_UNIT_SECOND:
				result = unixtime + (diffValue * operator);
				break;
			case UNIX_TIME_UNIT_MINUTE:
				result = unixtime + (diffValue * 60 * operator);
				break;
			case UNIX_TIME_UNIT_HOUR:
				result = unixtime + (diffValue * 60 * 60 * operator);
				break;
			case UNIX_TIME_UNIT_DAY:
				result = unixtime + (diffValue * 60 * 60 * 24 * operator);
				break;
		}
		
		return result;
		
	}
	
	public static Properties getTimezone() {
		return timezone;
	}
	
	public static Properties getTimezoneMappingMax2Ap() {
		return timezoneMappingMax2Ap;
	}

	public static int getUnixtime() {
		return (int) (System.currentTimeMillis() / 1000L);
	}
	
	public static int getUnixtime(Calendar calendar){
		long now_timemillis =calendar.getTimeInMillis();
		int now_unixtime = (int)(now_timemillis/1000);		
		return now_unixtime;		
	}
	
	public static Date trimDate2Minimum(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		Date rtnDate = trimCalendar2Minimum(cal).getTime();
		return rtnDate;
	}
	
	public static Calendar trimCalendar2Minimum(Calendar cal){
		if (cal != null){
			cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
			cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
			cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));
		}
		return cal;
	}
	
	public static Date trimDate2Maximum(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		Date rtnDate = trimCalendar2Maximum(cal).getTime();
		return rtnDate;
	}
	
	public static Calendar trimCalendar2Maximum(Calendar cal){
		if (cal != null){
			cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
			cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
			cal.set(Calendar.MILLISECOND, cal.getActualMaximum(Calendar.MILLISECOND));
		}
		return cal;
	}
	
	public static Date getUtcDate() 
	{
		return offsetTimeZone(new Date(), TimeZone.getDefault().getID(), "Utc");
	}
	
	public static Date getUtcDate(Date date) 
	{
		return offsetTimeZone(date, TimeZone.getDefault().getID(), "Utc");
	}
	
	public static Date getUtcDateFromUnixTime(int unixtime) 
	{
		Date date = new Date(unixtime * 1000L);
		return DateUtils.getUtcDate(date);
	}
	
	public static Date getUtcDate(Date date, String tz) 
	{
		return offsetTimeZone(date, tz, "Utc");
	}
	
	public static Date offsetFromUtcTimeZoneId(Date date, String id) {
		//log.debugf("date %s id %s", date, id);
		return offsetTimeZone(date, "Utc", getTimezoneFromId(Integer.valueOf(id))); 
	}
	
	public static Date offsetFromUtcTimeZone(Date date, String toTZ) {
		return offsetTimeZone(date, "Utc", toTZ); 
	}
	
	public static Date offsetTimeZone(Date date, String fromTZ, String toTZ)
	{
		//log.debugf("offsetTimeZone date %s fromTZ %s toTZ %s", date, fromTZ, toTZ);
		
		// Construct FROM and TO TimeZone instances
		TimeZone fromTimeZone = TimeZone.getTimeZone(fromTZ);
		TimeZone toTimeZone = TimeZone.getTimeZone(toTZ);

		// Get a Calendar instance using the default time zone and locale.
		Calendar calendar = Calendar.getInstance();

		// Set the calendar's time with the given date
		calendar.setTimeZone(fromTimeZone);
		calendar.setTime(date);

		// FROM TimeZone to UTC
		calendar.add(Calendar.MILLISECOND, fromTimeZone.getRawOffset() * -1);

		if (fromTimeZone.inDaylightTime(calendar.getTime())) {
			calendar.add(Calendar.MILLISECOND, calendar.getTimeZone().getDSTSavings() * -1);
		}

		// UTC to TO TimeZone
		calendar.add(Calendar.MILLISECOND, toTimeZone.getRawOffset());

		if (toTimeZone.inDaylightTime(calendar.getTime())) {
			calendar.add(Calendar.MILLISECOND, toTimeZone.getDSTSavings());
		}

		return calendar.getTime();
	}

	public static void loadTimeZones()
	{
		if (isLoaded)
			return;
		
		isLoaded = true;
		
		InputStreamReader reader = null;
		FileInputStream fis = null;
		
		class CustomStringComparator implements Comparator<String> {

			@Override
			public int compare(String str1, String str2) {

				int num1;
				int num2;

				try {
					num1 = Integer.valueOf(str1);
				} catch (NumberFormatException e) {
					return 1;
				}

				try {
					num2 = Integer.valueOf(str2);
				} catch (NumberFormatException e) {
					return -1;
				}

				return num1 - num2;
			}
		}

		log.warnf("INFO ==============loadTimeZones==============");		
		String timezoneFilepath = System.getProperty("littlecloud.timezones.config");
		try {
			log.info("Loading timezone property " + timezoneFilepath + " ...");
			File file = new File(timezoneFilepath);

			if (file.exists() && file.isFile()) { // Also checks for existance
				fis = new FileInputStream(file);
				timezone.load(fis);
			}
			else
			{
				log.fatal("Timezone config is not found! "+timezoneFilepath);
			}					
			log.warnf("INFO \n"+Utils.getPropertiesInOrder(timezone, new CustomStringComparator()));
		} catch (FileNotFoundException e) {
			log.fatal("Timezone config is not found! "+timezoneFilepath, e);
		} catch (IOException e) {
			log.fatal("Timezone config is unable to read! "+timezoneFilepath, e);
		} catch (Exception e) {
			log.fatal("Timezone config load exception! "+timezoneFilepath, e);
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(fis);
		}
		
		
		log.warnf("INFO ==============loadTimeZones mapping: max to ap ==============");
		String timezoneMax2ApFilepath = System.getProperty("timezones.max2ap.config");
		try {
			log.info("Loading timezone mapping: max to ap property " + timezoneMax2ApFilepath + " ...");
			File file = new File(timezoneMax2ApFilepath);

			if (file.exists() && file.isFile()) { // Also checks for existance
				fis = new FileInputStream(file);
				timezoneMappingMax2Ap.load(fis);
			}
			else
			{
				log.fatal("Timezone mapping: max to ap config is not found! "+timezoneMax2ApFilepath);
			}					
			log.warnf("INFO \n"+Utils.getPropertiesInOrder(timezoneMappingMax2Ap, new CustomStringComparator()));
			
		} catch (FileNotFoundException e) {
			log.fatal("Timezone config is not found! "+timezoneFilepath, e);
		} catch (IOException e) {
			log.fatal("Timezone config is unable to read! "+timezoneFilepath, e);
		} catch (Exception e) {
			log.fatal("Timezone config load exception! "+timezoneFilepath, e);
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(fis);
		}		
	}

	public static String getTimezoneFromId(Integer id)
	{
		return timezone.getProperty(String.valueOf(id));
	}
	
	public static int getUtcUnixtime() 
	{
		return (int) (DateUtils.getUtcDate().getTime() / 1000);
	}
	
	public static Date getUtcDateBeforeMin(int beforeMin) {
		//return DateUtils.getUtcDateFromUnixTime(DateUtils.getUtcUnixtime() - (beforeMin * 60));
		return new Date((DateUtils.getUtcUnixtime() - (beforeMin * 60))*1000L);
	}
	
	public static Date getUtcDateStartOfDay() {		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd 00:00:00");
		Date result = null;
		try {
			result = sdf.parse(sdf.format(DateUtils.getUtcDate()));
		} catch (ParseException e) {
			log.error("ParseException",e);
		}
		return result;
	}
	
	public static void main(String args[])
	{
		log.info(DateUtils.getUtcDateStartOfDay().getTime()/1000);
	}
}
