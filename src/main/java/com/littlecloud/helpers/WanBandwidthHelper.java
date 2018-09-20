package com.littlecloud.helpers;

import java.util.Calendar;
import java.util.Date;

import com.littlecloud.utils.CalendarUtils;

public class WanBandwidthHelper {
	public static int convert2UnixTime(Date date){
		long now_timemillis =date.getTime();
		int now_unixtime = (int)(now_timemillis/1000);
		return now_unixtime;
	}
	public static int calculateDateOneDayBeforeAndConvert2UnixTime(Calendar cal){
		Calendar calCopy = (Calendar) cal.clone();
		calCopy.add(Calendar.HOUR_OF_DAY, -24);
		long timemillis =calCopy.getTimeInMillis();
		int oneDayBeforeUnixtime = (int)(timemillis/1000);
		return oneDayBeforeUnixtime;
	}
	public static int calculateDateOneDayBeforeAndConvert2UnixTime(Date date){
		Calendar utTime = Calendar.getInstance();
		utTime.setTime(date);
		utTime.add(Calendar.HOUR_OF_DAY, -24);

		long timemillis =utTime.getTimeInMillis();
		int oneDayBeforeUnixtime = (int)(timemillis/1000);
		return oneDayBeforeUnixtime;
	}
	public static int trimMinuteSecondAndConvert2UnixTime(Calendar cal){
		Calendar calCopy = (Calendar) cal.clone();
		CalendarUtils.trimCalendarMinuteSecond(calCopy);
		int trimMinuteSecondUnixtime = convert2UnixTime(calCopy.getTime());
		return trimMinuteSecondUnixtime;
	}
	public static int trimMinuteSecondAndConvert2UnixTime(Date date){
		Date trimMinuteSecondDate = CalendarUtils.trimDate2Minimum(date);
		int trimMinuteSecondUnixtime = convert2UnixTime(trimMinuteSecondDate);
		return trimMinuteSecondUnixtime;
	}
}
